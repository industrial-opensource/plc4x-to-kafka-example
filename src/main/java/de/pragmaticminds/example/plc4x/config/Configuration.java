package de.pragmaticminds.example.plc4x.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Configuration.
 */
public class Configuration {

    private List<JobConfiguration> jobs;
    private KafkaConfiguration kafka;

    public Configuration() {
        // For Jackson
    }

    public Configuration(List<JobConfiguration> jobs, KafkaConfiguration kafka) {
        this.jobs = jobs;
        this.kafka = kafka;
    }

    public List<JobConfiguration> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobConfiguration> jobs) {
        this.jobs = jobs;
    }

    public KafkaConfiguration getKafka() {
        return kafka;
    }

    public void setKafka(KafkaConfiguration kafka) {
        this.kafka = kafka;
    }

    public static final class ConfigurationBuilder {

        private List<JobConfiguration> scrapeConfigurations;
        private KafkaConfiguration kafkaConfiguration;

        private ConfigurationBuilder() {
        }

        public static ConfigurationBuilder builder() {
            return new ConfigurationBuilder();
        }

        public ConfigurationBuilder withScrapeConfigurations(List<JobConfiguration> scrapeConfigurations) {
            this.scrapeConfigurations = scrapeConfigurations;
            return this;
        }

        public ConfigurationBuilder withKafkaConfiguration(KafkaConfiguration kafkaConfiguration) {
            this.kafkaConfiguration = kafkaConfiguration;
            return this;
        }

        public Configuration build() {
            return new Configuration(scrapeConfigurations, kafkaConfiguration);
        }
    }

    @Override
    public String toString() {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
