package ar.com.redhat;

import java.util.Properties;


import org.apache.kafka.clients.producer.Producer;

import org.apache.kafka.clients.producer.KafkaProducer;

import org.apache.kafka.clients.producer.ProducerRecord;

public class SimpleProducerExample {
   
   public static void main(String[] args) throws Exception{
      
      String topic = "my-topic";
      
      String bootstrapServers = "my-cluster-kafka-tls-bootstrap-amq-streams.apps.talleramqstreams.d8ab.sandbox1168.opentlc.com:443";
  
      Properties prop = new Properties();
      
      prop.put("clien.id", "client-java-producer-example");
      
      prop.put("bootstrap.servers", bootstrapServers);
      
      prop.put("acks", "all");
      
      prop.put("retries", 0);
      
      prop.put("batch.size", 16384);
      
      prop.put("linger.ms", 1);
      
      prop.put("buffer.memory", 33554432);
      
      prop.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");         
      prop.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      
      prop.put("security.protocol", "SSL");
      prop.put("ssl.truststore.location", "/Users/damianlezcano/rh/kafka_2.13-2.8.0/truststore.jks");
      prop.put("ssl.truststore.password", "redhat01");
      
      Producer<String, String> producer = new KafkaProducer<String, String>(prop);

      for(int i = 0; i < 10; i++) {
    	  producer.send(new ProducerRecord<String, String>(topic, Integer.toString(i), Integer.toString(i)));
      }
      System.out.println("Message sent successfully");
      producer.close();    	  
   }
}