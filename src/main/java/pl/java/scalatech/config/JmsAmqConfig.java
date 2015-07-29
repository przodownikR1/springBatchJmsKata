package pl.java.scalatech.config;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@Slf4j
public class JmsAmqConfig {
    @Bean
    public ActiveMQConnectionFactory amqConnectionFactory() {
        return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }

  /*  @Bean
    public PooledConnectionFactory amqPoolConnectionFactory() {
        return new PooledConnectionFactory(amqConnectionFactory());
    }*/

    // A cached connection to wrap the ActiveMQ connection
    @Bean
    public CachingConnectionFactory connectionFactory() {
        final CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(amqConnectionFactory());
        cachingConnectionFactory.setSessionCacheSize(100);
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
                    log.info("{}",((ObjectMessage) message).getObject().toString());
                } catch (final JMSException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return messageListener;
    }

  /*  @Bean(destroyMethod = "destroy")
    public DefaultMessageListenerContainer jmsContainer() {
        final DefaultMessageListenerContainer jmsContainer = new DefaultMessageListenerContainer();
        jmsContainer.setDestination(destination());
        jmsContainer.setConnectionFactory(connectionFactory());
        jmsContainer.setMessageListener(messageListener());
        jmsContainer.setConcurrency("5-10");
        jmsContainer.setReceiveTimeout(5000);
        jmsContainer.afterPropertiesSet();
        return jmsContainer;
    }*/
}
