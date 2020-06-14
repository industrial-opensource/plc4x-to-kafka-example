package de.pragmaticminds.example.plc4x;

import com.alibaba.fastjson.JSONObject;
import de.pragmaticminds.example.plc4x.config.JobConfiguration;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A single job execution which includes
 * - fetch data from PLC4X
 * - Send all valid fields to Kafka
 */
class JobExecution implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(JobExecution.class);

    private final JobConfiguration config;
    private final PlcDriverManager driverManager;
    private final Producer<String, String> producer;

    public JobExecution(JobConfiguration config, PlcDriverManager driverManager, Producer<String, String> producer) {
        this.config = config;
        this.driverManager = driverManager;
        this.producer = producer;
    }

    @Override
    public void run() {
        logger.info("Fetching data for job {}", config.getAlias());

        try (PlcConnection connection = driverManager.getConnection(config.getConnectionString())) {
            final PlcReadRequest.Builder builder = connection.readRequestBuilder();
            for (Map.Entry<String, String> entry : config.getFieldAdresses().entrySet()) {
                builder.addItem(entry.getKey(), entry.getValue());
            }
            final PlcReadRequest request = builder.build();

            final PlcReadResponse response = request.execute().get(5, TimeUnit.SECONDS);

            // Warn for all fields that are not OK
            response.getFieldNames().stream()
                .filter(name -> response.getResponseCode(name) != PlcResponseCode.OK)
                .forEach(name -> logger.warn("Fetching field with name '{}' and address '{}' returned not-OK Status {}", name, config.getFieldAdresses().get(name), response.getResponseCode(name)));

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
    }
}
