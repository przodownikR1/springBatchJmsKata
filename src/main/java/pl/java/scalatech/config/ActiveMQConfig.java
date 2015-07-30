package pl.java.scalatech.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import lombok.extern.slf4j.Slf4j;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@Configuration
@EnableAutoConfiguration
@EnableJms
@Profile("autoConfig")
@Slf4j
@ComponentScan(basePackages = "pl.java.scalatech.jms")
public class ActiveMQConfig {
    private static final long RECEIVE_TIMEOUT = 10000;
    Random r = new Random();
    int allCount, successCount, failureCount;
    @Bean
    public Queue queueB() {
        return new ActiveMQQueue("queue.B");
    }
    
    @Bean
    public Queue queueC() {
        return new ActiveMQQueue("queue.C");
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy());
        activeMQConnectionFactory.setMaxThreadPoolSize(50);
        activeMQConnectionFactory.setExceptionListener(exception ->
        {
            log.error(exception.getLocalizedMessage());
        });
        activeMQConnectionFactory.setStatsEnabled(true);
        activeMQConnectionFactory.setBrokerURL("tcp://localhost:61616");
        activeMQConnectionFactory.setAlwaysSessionAsync(true);
        return activeMQConnectionFactory;
    }

    @Bean
    public RedeliveryPolicy redeliveryPolicy() {
         RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(1000);
        redeliveryPolicy.setMaximumRedeliveries(5);
        redeliveryPolicy.setUseExponentialBackOff(true);
        redeliveryPolicy.setBackOffMultiplier(2);
        return redeliveryPolicy;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setCacheConsumers(true);
        connectionFactory.setReconnectOnException(true);
        connectionFactory.setTargetConnectionFactory(connectionFactory());
        return connectionFactory;
    }

    @Bean(name = "defaultJmsListenerContainerFactory")
    public DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        defaultJmsListenerContainerFactory.setConnectionFactory(cachingConnectionFactory());
        defaultJmsListenerContainerFactory.setConcurrency("5-10");
       // defaultJmsListenerContainerFactory.setTaskExecutor(executor());
        defaultJmsListenerContainerFactory.setReceiveTimeout(RECEIVE_TIMEOUT);
        defaultJmsListenerContainerFactory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

        return defaultJmsListenerContainerFactory;
    }


    @JmsListener(destination = "queue.C", containerFactory = "defaultJmsListenerContainerFactory")
    public void messageReceiver(TextMessage textMessage) throws JMSException {
        doSomethingWithMessageContent(textMessage.getText());
        log.info("allCount  {},successCount {} , failure {}",allCount,successCount,failureCount);
        
    }

   
   

    private void doSomethingWithMessageContent(String text) {
        allCount++;
        if (r.nextBoolean()) {
            failureCount++;
            throw new RuntimeException(text + ". FAILURE");
        }
        successCount++;
    }
    
  /*  @Bean
    Executor executor() {
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setMaxPoolSize(50);
        threadPoolExecutor.setQueueCapacity(100);
        threadPoolExecutor.setCorePoolSize(80);
        threadPoolExecutor.setKeepAliveSeconds(600);
        return threadPoolExecutor;
    }*/

  /*  @Bean(name="jmsTemplateB")
    JmsTemplate jmsTemplateB(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setDefaultDestinationName("queue.B");
        return jmsTemplate;
    }*/
    @Bean(name="jmsTemplateC")
    JmsTemplate jmsTemplateC(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setDefaultDestinationName("queue.C");
        return jmsTemplate;
    }
}