package pl.java.scalatech.config;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import lombok.extern.slf4j.Slf4j;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
@Slf4j
@ComponentScan(basePackages="pl.java.scalatech.jms")
public class JmsAmqConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("tcp://localhost:61616");

        cachingConnectionFactory.setTargetConnectionFactory(activeMQConnectionFactory);
        cachingConnectionFactory.setSessionCacheSize(10);
        cachingConnectionFactory.setCacheProducers(true);
        cachingConnectionFactory.setReconnectOnException(true);
        return cachingConnectionFactory;
    }

    @Bean
    public ActiveMQQueue destination() {
        return new ActiveMQQueue("queue.A");
    }

    @Bean
    @Lazy
    public JmsTemplate jmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setDefaultDestination(destination());
        jmsTemplate.setReceiveTimeout(1000);
        return jmsTemplate;
    }

    @Bean
    public MessageListener messageListener() {
        final MessageListener messageListener = message ->
        {
            if (message instanceof ObjectMessage) {
                try {
                    log.info(" $$$$  Object:  received : {}", ((ObjectMessage) message).getObject().toString());
                } catch (final JMSException e) {

                }
            } else if (message instanceof TextMessage) {
                try {
                    log.info(" $$$$ Text : received : {}", ((TextMessage) message).getText());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return messageListener;
    }

    @Bean(destroyMethod = "destroy")
    @Profile("receiver")
    public DefaultMessageListenerContainer jmsContainer() {
        final DefaultMessageListenerContainer jmsContainer = new DefaultMessageListenerContainer();
        jmsContainer.setDestination(destination());
        jmsContainer.setConnectionFactory(connectionFactory());
        jmsContainer.setMessageListener(messageListener());
        jmsContainer.setConcurrency("5-10");
        jmsContainer.setReceiveTimeout(5000);
        jmsContainer.afterPropertiesSet();
        return jmsContainer;
    }
}
