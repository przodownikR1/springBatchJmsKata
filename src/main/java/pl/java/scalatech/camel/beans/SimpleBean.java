package pl.java.scalatech.camel.beans;

import org.apache.camel.Body;
import org.springframework.stereotype.Component;

@Component
public class SimpleBean {

    public String sayText(@Body String text) {
        return "+$+ " + text;
    }

}
