package ar.com.redhat;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProducerToKafkaProcess implements Processor {

    @Autowired
    private ProducerTask producerTask;

    @Override
    public void process(Exchange exchange) throws Exception {
    	String message = new Date().toString();
    	producerTask.sendMessage(message);
    }
}
