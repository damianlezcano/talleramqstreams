package ar.com.redhat;
import java.util.Properties;
import java.util.Arrays;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class SimpleConsumerExample {
	
   public static void main(String[] args) throws Exception {
      
      String topic = "my-topic";
      String consumerGroup = "group1";
      String bootstrapServers = "my-cluster-kafka-tls-bootstrap-amq-streams.apps.talleramqstreams.b1a2.sandbox1080.opentlc.com:443";
	  	  
      Properties prop = new Properties();
      
      prop.put("clien.id", "client-java-consumer-example");
      
      prop.put("bootstrap.servers", bootstrapServers);
      
      prop.put("group.id", consumerGroup);
      
      prop.put("enable.auto.commit", "true");
      
      prop.put("auto.commit.interval.ms", "1000");
      
      prop.put("session.timeout.ms", "30000");
      
      prop.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      prop.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      
      prop.put("security.protocol", "SSL");
      prop.put("ssl.truststore.location", "/Users/damianlezcano/rh/kafka_2.13-2.8.0/truststore.jks");
      prop.put("ssl.truststore.password", "redhat01");
      
      KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(prop);
      
      consumer.subscribe(Arrays.asList(topic));
      System.out.println("Subscription to topic complete " + topic);
         
      while (true) {
         ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
               System.out.printf("offset value = %d, key = %s, value = %s\n", 
               record.offset(), record.key(), record.value());
      }     
   }  
}