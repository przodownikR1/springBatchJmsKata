package pl.java.scalatech.camel.route;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pl.java.scalatech.camel.beans.SimpleBean;

@Component
@Profile("timer")
@ComponentScan(basePackages = "pl.java.scalatech.camel.beans")
public class TimerRoute extends SpringRouteBuilder {

    @Autowired
    private SimpleBean bean;

    @Override
    public void configure() throws Exception {
        from("timer://ping?fixedRate=true&period=2000").transform().simple(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd : HH:mm:ss")))
                .to("activemq:queue.A");
        //from("activemq:queue.A?concurrentConsumers=5").to("log:myCamel?level=INFO");
        from("activemq:queue.A?concurrentConsumers=5").bean(bean).to("stream:out");

    }
}
