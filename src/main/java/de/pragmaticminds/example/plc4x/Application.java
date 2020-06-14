package de.pragmaticminds.example.plc4x;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.pragmaticminds.example.plc4x.config.Configuration;
import de.pragmaticminds.example.plc4x.config.JobConfiguration;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.utils.connectionpool.PooledPlcDriverManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main Application
 */
public class Application {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
        // Read Configuration via Jackson
        final ObjectMapper mapper = new ObjectMapper();

        if (args.length < 1) {
            System.out.println("Please provide path to configuration as first Argument!");
            System.exit(1);
        }

        logger.info("Starting Application...");
        logger.info("Reading Config from Path {}", args[0]);

        final Configuration configuration = mapper.readValue(new File(args[0]), Configuration.class);

        logger.info("Using Configuration:\n{}", configuration);

        BasicThreadFactory factory = new BasicThreadFactory.Builder()
            .namingPattern("scrape-thread-%d")
            .daemon(false)
            .priority(Thread.MAX_PRIORITY)
            .build();
        final ScheduledExecutorService pool = Executors.newScheduledThreadPool(4, factory);

        // Prepare the Kafka Connection


        // create instance for properties to access producer configs
        Properties props = new Properties();

        props.put("bootstrap.servers", configuration.getKafka().getBootstrapServers());
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("key.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        // Close kafka producer...
        Runtime.getRuntime().addShutdownHook(new Thread(producer::close));

        // Prepare the Connection Pool
        final PlcDriverManager driverManager = new PooledPlcDriverManager();

        // Set up all fetchers
        logger.info("Starting Thead Pool...");
        for (JobConfiguration config : configuration.getJobs()) {
            logger.info("Registering Config:\n{}", config);
            pool.scheduleAtFixedRate(() -> {
                logger.info("Fetching data for job {}", config.getAlias());

                try (PlcConnection connection = driverManager.getConnection(config.getConnectionString())) {
                    final PlcReadRequest.Builder builder = connection.readRequestBuilder();
                    for (Map.Entry<String, String> entry : config.getFieldAdresses().entrySet()) {
                        builder.addItem(entry.getKey(), entry.getValue());
                    }
                    final PlcReadRequest request = builder.build();

                    final PlcReadResponse response = request.execute().get(5, TimeUnit.SECONDS);

                    // Assemble all "valid" responses and send them as JSON
                    final Map<String, Object> results = response.getFieldNames().stream()
                        .filter(name -> response.getResponseCode(name) == PlcResponseCode.OK)
                        .collect(Collectors.toMap(Function.identity(), response::getObject));

                    // Make to JSON
                    final String json = (new JSONObject(results)).toJSONString();
                    logger.info("Sending JSON String to Kafka: {}", json);

                    producer.send(new ProducerRecord<>(config.getKafkaTopic(), json));

                } catch (Exception e) {
                    logger.warn("Unable to scrape from {} in job {}", config.getConnectionString(), config.getAlias(), e);
                }
            }, config.getScrapeRateMs(), config.getScrapeRateMs(), TimeUnit.MILLISECONDS);
        }
    }

}
