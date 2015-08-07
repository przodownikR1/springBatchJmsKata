package pl.java.scalatech.config.camel.jms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import lombok.extern.slf4j.Slf4j;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.usage.SystemUsage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
@Profile("embedded")
@Slf4j
public class EmbeddedJmsConfig {

    @Bean(initMethod = "start")
    public BrokerService brokerService() throws Exception {

        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName("testBroker");
        brokerService.setPersistent(false);
        brokerService.setUseShutdownHook(true);
        brokerService.setEnableStatistics(true);
        brokerService.addConnector("vm://localhost");
        SystemUsage systemUsage = brokerService.getSystemUsage();
        systemUsage.getStoreUsage().setLimit(1024 * 1024 * 128);
        systemUsage.getTempUsage().setLimit(1024 * 1024 * 128);
        log.info("+++++++++++++++++++++++++++++++++++++++                         {}", brokerService);
        return brokerService;
    } 

    @Bean
    public ConnectionFactory amqConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("vm://localhost?broker.persistent=false");

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
        final JmsTemplate jmsTemplate = new JmsTemplate(amqConnectionFactory());
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
                    log.info(" $$$$  Object:  received : {}",((ObjectMessage) message).getObject().toString());
                } catch (final JMSException e) {

                }
            }else if(message instanceof  TextMessage) {
                try {
                    log.info(" $$$$ Text : received : {}",((TextMessage) message).getText());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return messageListener;
    }

   @Bean(destroyMethod = "destroy")
    public DefaultMessageListenerContainer jmsContainer() {
        final DefaultMessageListenerContainer jmsContainer = new DefaultMessageListenerContainer();
        jmsContainer.setDestination(destination());
        jmsContainer.setConnectionFactory(amqConnectionFactory());
        jmsContainer.setMessageListener(messageListener());
        jmsContainer.setConcurrency("5-10");
        jmsContainer.setReceiveTimeout(5000);
        jmsContainer.afterPropertiesSet();
        return jmsContainer;
    }
}
