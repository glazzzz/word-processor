package org.liaonau.task.processor;

import io.reactivex.rxjava3.core.Flowable;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.apache.logging.log4j.util.Strings;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ProcessorRoutes extends RouteBuilder {

    @Override
    public void configure() {
        //configure in/out routes
        from("{{producer.kafka-uri}}")
                //can add filtering  if required
//                .choice().when(new Predicate() {
//            @Override
//            public boolean matches(Exchange exchange) {
//                String payload = exchange.getIn().getBody().toString();
//                return !payload.matches("^.*[^a-zA-Z0-9 ].*$");
//            }
//        })
                .to("reactive-streams:words").log("Got new word - ${body}");

        from("reactive-streams:sentences")
                .choice().when(body().isNotEqualTo(""))
                .to("{{consumer.kafka-uri}}").log("Sent new sentence - ${body}");

        //configure stream processor
        CamelReactiveStreamsService camel = CamelReactiveStreams.get(getContext());
        Publisher<String> words = camel.fromStream("words", String.class);

        Flowable.fromPublisher(words).buffer(60, TimeUnit.SECONDS)
                .map(list -> Strings.join(list, ' '))
                .subscribe(camel.streamSubscriber("sentences", String.class));
    }
}
