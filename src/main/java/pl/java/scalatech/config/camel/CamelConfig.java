package pl.java.scalatech.config.camel;

import javax.jms.ConnectionFactory;

import lombok.extern.slf4j.Slf4j;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.processor.interceptor.Tracer;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@EnableAutoConfiguration
@Configuration
@Slf4j
@ComponentScan(basePackages = { "pl.java.scalatech.camel.route", "pl.java.scalatetech.camel.processor", "pl.java.scalatech.camel.beans" }, includeFilters = { @Filter(Component.class) })
@PropertySource("classpath:camel.properties")
public class CamelConfig extends CamelConfiguration {

    @Autowired
    private CamelContext camelContext;

    @Value("${camel.traceFlag}")
    private Boolean traceFlag;

    @Bean
    public Tracer camelTracer() {
        Tracer tracer = new Tracer();
        tracer.setEnabled(true);
        tracer.setTraceExceptions(false);
        tracer.setTraceInterceptors(true);
        tracer.setLogName("myTrace");
        return tracer;
    }

    @Override
    protected void setupCamelContext(CamelContext camelContext) throws Exception {
        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation("classpath:application.properties");
        camelContext.addComponent("properties", pc);
        log.info("+++  beforeApplicationStart");
        camelContext.addRoutePolicyFactory(new MetricsRoutePolicyFactory());
        camelContext.setUseMDCLogging(true);
        camelContext.setUseBreadcrumb(true);
        camelContext.setTracing(true);
        intQueue(camelContext);
        super.setupCamelContext(camelContext);

    }

    public void intQueue(CamelContext context) {
        //context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));
        context.addComponent("activemq", activemq());
    }

    public ActiveMQComponent activemq() {
        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConfiguration(jmsConfiguration());
        return activeMQComponent;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory connectionFactory = new PooledConnectionFactory();
        connectionFactory.setMaxConnections(10);
        connectionFactory.setConnectionFactory(connectionFactory());

        return connectionFactory;
    }

    private ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("tcp://localhost:61616");
        connectionFactory.setMaxThreadPoolSize(50);
        connectionFactory.setOptimizeAcknowledge(true);
        connectionFactory.setOptimizedMessageDispatch(true);
        connectionFactory.setUseCompression(true);
        return connectionFactory;
    }

    private JmsConfiguration jmsConfiguration() {
        JmsConfiguration jmsConfig = new JmsConfiguration(pooledConnectionFactory());
        jmsConfig.setMaxConcurrentConsumers(10);
        return jmsConfig;
    }

}
