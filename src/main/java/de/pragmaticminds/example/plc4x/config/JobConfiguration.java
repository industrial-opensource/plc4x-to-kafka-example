package de.pragmaticminds.example.plc4x.config;

import java.util.Map;

public class JobConfiguration {

    private String alias = "unknown";
    private String kafkaTopic = "plc4x";
    private String connectionString;
    private int scrapeRateMs;
    private Map<String, String> fieldAdresses;

    public JobConfiguration() {
        // Jackson
    }

    public static JobConfigurationBuilder builder() {
        return JobConfigurationBuilder.aJobConfiguration();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public int getScrapeRateMs() {
        return scrapeRateMs;
    }

    public void setScrapeRateMs(int scrapeRateMs) {
        this.scrapeRateMs = scrapeRateMs;
    }

    public Map<String, String> getFieldAdresses() {
        return fieldAdresses;
    }

    public void setFieldAdresses(Map<String, String> fieldAdresses) {
        this.fieldAdresses = fieldAdresses;
    }


    public static final class JobConfigurationBuilder {
        private String alias = "unknown";
        private String kafkaTopic = "plc4x";
        private String connectionString;
        private int scrapeRateMs;
        private Map<String, String> fieldAdresses;

        private JobConfigurationBuilder() {
        }

        public static JobConfigurationBuilder aJobConfiguration() {
            return new JobConfigurationBuilder();
        }

        public JobConfigurationBuilder withAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public JobConfigurationBuilder withKafkaTopic(String kafkaTopic) {
            this.kafkaTopic = kafkaTopic;
            return this;
        }

        public JobConfigurationBuilder withConnectionString(String connectionString) {
            this.connectionString = connectionString;
            return this;
        }

        public JobConfigurationBuilder withScrapeRateMs(int scrapeRateMs) {
            this.scrapeRateMs = scrapeRateMs;
            return this;
        }

        public JobConfigurationBuilder withFieldAdresses(Map<String, String> fieldAdresses) {
            this.fieldAdresses = fieldAdresses;
            return this;
        }

        public JobConfiguration build() {
            JobConfiguration jobConfiguration = new JobConfiguration();
            jobConfiguration.setAlias(alias);
            jobConfiguration.setKafkaTopic(kafkaTopic);
            jobConfiguration.setConnectionString(connectionString);
            jobConfiguration.setScrapeRateMs(scrapeRateMs);
            jobConfiguration.setFieldAdresses(fieldAdresses);
            return jobConfiguration;
        }
    }

    @Override
    public String toString() {
        return "JobConfiguration{" +
            "alias='" + alias + '\'' +
            ", kafkaTopic='" + kafkaTopic + '\'' +
            ", connectionString='" + connectionString + '\'' +
            ", scrapeRateMs=" + scrapeRateMs +
            ", fieldAdresses=" + fieldAdresses +
            '}';
    }
}
