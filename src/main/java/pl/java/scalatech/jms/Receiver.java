package pl.java.scalatech.jms;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@Profile("autoConfig")
public class Receiver {
	
	@JmsListener(destination="queue.B")
	public void receiveMessage(String message) {
	   log.info("Received {}",message);
	}

}