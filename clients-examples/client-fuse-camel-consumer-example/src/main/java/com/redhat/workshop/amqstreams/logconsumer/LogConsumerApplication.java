package com.redhat.workshop.amqstreams.logconsumer;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LogConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogConsumerApplication.class, args);
    }

    @Bean
    public RouteBuilder routeBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("kafka:{{input.topic}}"
                		+ "?brokers={{kafka.configuration.brokers}}"
                		+ "&sslProtocol={{kafka.ssl-protocol}}"
                		+ "&securityProtocol={{kafka.security-protocol}}"
                		+ "&sslTruststoreLocation={{kafka.ssl-truststore-location}}"
                		+ "&sslTruststorePassword={{kafka.ssl-truststore-password}}")
                        .log("Received from '${in.headers[kafka.TOPIC]}': ${in.body}");

            }
        };
    }

}

