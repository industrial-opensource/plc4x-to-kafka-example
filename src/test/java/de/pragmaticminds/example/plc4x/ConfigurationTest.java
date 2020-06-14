package de.pragmaticminds.example.plc4x;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.pragmaticminds.example.plc4x.config.Configuration;
import de.pragmaticminds.example.plc4x.config.JobConfiguration;
import de.pragmaticminds.example.plc4x.config.KafkaConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

class ConfigurationTest {

    @Test
    @Disabled
    void saveToFile() throws IOException {
        final Configuration configuration = Configuration.ConfigurationBuilder.builder()
            .withKafkaConfiguration(KafkaConfiguration.builder().withBootstrapServers(Collections.singletonList("localhost:2991")).build())
            .withScrapeConfigurations(Collections.singletonList(JobConfiguration.builder().withConnectionString("s7://").withScrapeRateMs(1_000).withFieldAdresses(Collections.singletonMap("temp", "%DB...")).build()))
            .build();

        final ObjectMapper mapper = new ObjectMapper();

        mapper
            .writerWithDefaultPrettyPrinter()
            .writeValue(new File("src/main/resources/config.json"), configuration);
    }
}