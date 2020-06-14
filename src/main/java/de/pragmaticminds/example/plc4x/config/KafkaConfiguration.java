package de.pragmaticminds.example.plc4x.config;

import java.util.List;

public class KafkaConfiguration {

    private List<String> bootstrapServers;

    public KafkaConfiguration() {
        // Jackson
    }

    public static KafkaConfigurationBuilder builder() {
        return KafkaConfigurationBuilder.aKafkaConfiguration();
    }

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public static final class KafkaConfigurationBuilder {
        private List<String> bootstrapServers;

        private KafkaConfigurationBuilder() {
        }

        public static KafkaConfigurationBuilder aKafkaConfiguration() {
            return new KafkaConfigurationBuilder();
        }

        public KafkaConfigurationBuilder withBootstrapServers(List<String> bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
            return this;
        }

        public KafkaConfiguration build() {
            KafkaConfiguration kafkaConfiguration = new KafkaConfiguration();
            kafkaConfiguration.bootstrapServers = this.bootstrapServers;
            return kafkaConfiguration;
        }
    }
}
