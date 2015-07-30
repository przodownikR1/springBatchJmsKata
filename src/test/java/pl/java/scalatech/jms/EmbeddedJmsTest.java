package pl.java.scalatech.jms;

import javax.jms.TextMessage;

import lombok.extern.slf4j.Slf4j;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.java.scalatech.config.EmbeddedJmsConfig;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EmbeddedJmsConfig.class })
@ActiveProfiles("embedded")
public class EmbeddedJmsTest {
    @Autowired
    private JmsTemplate jmsTemplate;
    
    @Test
    public void shouldJmsWork() {
        Assertions.assertThat(jmsTemplate).isNotNull();
        jmsTemplate.send(session ->
        {
            log.info("send text !!!!!!!!!!!!!!!!!!!!!!");
            TextMessage message = session.createTextMessage("Hello World: Spring - JMS");  
            return message;  
            
        });
    
    }
   
    
}
