package ar.com.redhat;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerRouteBuilder extends RouteBuilder {

    @Autowired
    ProducerToKafkaProcess producerToKafka;

    @Value("${app.timer.kafkaProducer.period}")
    private long timerKafkaProducerPeriod;
    @Value("${app.timer.kafkaProducer.delay}")
    private long timerKafkaProducerDelay;

    @Override
    public void configure() throws Exception {
        fromF("timer://produceToKafka?fixedRate=true&period=%d&delay=%d&synchronous=true", timerKafkaProducerPeriod, timerKafkaProducerDelay)
            .routeId("produceToKafka")
            .process(producerToKafka)
        ;

    }

}
