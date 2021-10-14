package kafka.examples;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class ConsumerStandarWithoutTrxExample {

    private final static String TOPIC = "entity-traceability-output";
    private final static String BOOTSTRAP_SERVERS = "192.168.1.12:29092,localhost:39092,localhost:49092";
	
    public static void main(String... args) throws Exception {
        runConsumer();
    }
    
	  private static Consumer<String, Object> createConsumer() {
	      final Properties props = new Properties();
	      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
	      props.put(ConsumerConfig.GROUP_ID_CONFIG, ConsumerStandarWithoutTrxExample.class.getName());
	      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
	      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

	      // Create the consumer using props.
	      final Consumer<String, Object> consumer = new KafkaConsumer<>(props);

	      // Subscribe to the topic.
	      consumer.subscribe(Collections.singletonList(TOPIC));
	      return consumer;
	  }
	  
	    static void runConsumer() throws InterruptedException {
	        final Consumer<String, Object> consumer = createConsumer();

	        final int giveUp = 100000;   int noRecordsCount = 0;

	        while (true) {
	            final ConsumerRecords<String, Object> consumerRecords = consumer.poll(1000);

	            if (consumerRecords.count()==0) {
	                noRecordsCount++;
	                if (noRecordsCount > giveUp) break;
	                else continue;
	            }

	            consumerRecords.forEach(record -> {
	                System.out.printf("Consumer Record:(%s, %s, %d, %d)\n",
	                        record.key(), record.value(),
	                        record.partition(), record.offset());
	            });

	            consumer.commitAsync();
	        }
	        consumer.close();
	        System.out.println("DONE");
	    }

}