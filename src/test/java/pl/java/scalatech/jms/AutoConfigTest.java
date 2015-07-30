package pl.java.scalatech.jms;

import javax.jms.TextMessage;

import lombok.extern.slf4j.Slf4j;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.java.scalatech.config.ActiveMQConfig;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ActiveMQConfig.class })
@ActiveProfiles("autoConfig")
public class AutoConfigTest {
    @Autowired
    @Qualifier("jmsTemplateC")
    private JmsTemplate jmsTemplateC;
    
  
    
    @Test
    public void shouldJmsWork() {
        Assertions.assertThat(jmsTemplateC).isNotNull();
        jmsTemplateC.send(session ->
        {
            log.info("send text !!!!!!!!!!!!!!!!!!!!!!");
            TextMessage message = session.createTextMessage("Hello World: przodownik");  
            return message;  
            
        });
    
    }
   
    @Test
    public void shouldRedeliveryJmsWork() throws InterruptedException {
        Assertions.assertThat(jmsTemplateC).isNotNull();
        for(int i =0 ; i< 10;i++) {
        jmsTemplateC.send(session ->
        {
            log.info("send text !!!!!!!!!!!!!!!!!!!!!!");
            TextMessage message = session.createTextMessage("Hello World: przodownik ");  
            return message;  
            
        });
        }
    Thread.sleep(10000);
    }
}
