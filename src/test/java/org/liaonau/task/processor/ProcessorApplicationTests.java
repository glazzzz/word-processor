package org.liaonau.task.processor;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@MockEndpoints
class ProcessorApplicationTests {

    @DirtiesContext
    @Test
    void contextLoads() {
    }

    @Produce("{{producer.kafka-uri}}")
    private ProducerTemplate producerTemplate;

    @EndpointInject("{{consumer.kafka-uri}}")
    private MockEndpoint mockConsumer;

    @DirtiesContext
    @Test
    public void test() throws InterruptedException {
        mockConsumer.expectedMessageCount(1);

        producerTemplate.sendBody("Hello");
        producerTemplate.sendBody("World");
        Thread.sleep(62000);
        mockConsumer.assertIsSatisfied();
    }

}
