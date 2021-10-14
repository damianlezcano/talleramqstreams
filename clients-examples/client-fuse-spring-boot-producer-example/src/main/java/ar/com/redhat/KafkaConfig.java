package ar.com.redhat;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.brokerHosts}")
    String brokerHosts;

    @Value("${app.kafka.producer.clienId}")
    String producerClienId;
    @Value("${app.kafka.producer.requestRequiredAcks}")
    String producerRequestRequiredAcks;
    @Value("${app.kafka.producer.lingerMs}")
    Long producerLingerMs;
    @Value("${app.kafka.producer.producerBatchSize}")
    int producerBatchSize;
    @Value("${app.kafka.producer.transactionalId}")
    String producerTransactionalId;

    @Value("${app.kafka.producer.compressionTypeConfig}")
    String producerCompressionTypeConfig;

    @Value("${app.kafka.producer.bufferMemoryConfig}")
    Integer producerBufferMemoryConfig;
    @Value("${app.kafka.producer.maxBlockMsConfig}")
    Integer producerMaxBlockMsConfig;
    @Value("${app.kafka.producer.maxInFlightRequestsPerConnection}")
    Integer producerMaxInFlightRequestsPerConnection;
    @Value("${app.kafka.producer.enableIdempotenceConfig}")
    boolean producerEnableIdempotenceConfig;
    @Value("${app.kafka.producer.partitioner}")
    String producerPartitioner;
    
    @Value("${app.kafka.producer.security.protocol}")
    String securityProtocol;
    @Value("${app.kafka.producer.ssl.truststore.location}")
    String sslTruststoreLocation;
    @Value("${app.kafka.producer.ssl.truststore.password}")
    String sslTruststorePassword;
   
	@Bean
	<T> KafkaProducer<String, T> createKafkaProducer() {
        Properties configProps = new Properties();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerHosts);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, producerClienId);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, producerLingerMs);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, producerBatchSize);
        
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producerBufferMemoryConfig);
        configProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, producerMaxBlockMsConfig);
        if (producerMaxInFlightRequestsPerConnection > 0) {
            configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, producerMaxInFlightRequestsPerConnection);
        }
        configProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, producerPartitioner);

        if(!producerCompressionTypeConfig.isEmpty()) {
            configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, producerCompressionTypeConfig);
        }
        
//        configProps.put(ProducerConfig.ACKS_CONFIG, producerRequestRequiredAcks);
//        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producerEnableIdempotenceConfig);
//
//        if(!producerTransactionalId.isEmpty()) {
//            configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, producerTransactionalId);
//        }
        
        configProps.put("security.protocol", securityProtocol);
        configProps.put("ssl.truststore.location", sslTruststoreLocation);
        configProps.put("ssl.truststore.password", sslTruststorePassword);
        
        return new KafkaProducer<String, T>(configProps);
	    
	}

}