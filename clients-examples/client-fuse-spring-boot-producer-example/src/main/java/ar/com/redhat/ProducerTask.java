package ar.com.redhat;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ProducerTask {

	private static final Logger logger = LogManager.getLogger();
	
    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    @Value("${app.kafka.topic}")
    private String topic;

    public void sendMessage(String value) {
			
    	logger.info("# Enviando mensajes: " + value);
    	
		kafkaProducer.initTransactions();
		
		ProducerRecord<String, String> deliveryMessage = new ProducerRecord<String, String>(topic, value);
		this.kafkaProducer.send(deliveryMessage);
		
	    kafkaProducer.commitTransaction();

    }


}
