package pl.java.scalatech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;

@SpringBootApplication(exclude = { HypermediaAutoConfiguration.class, MultipartAutoConfiguration.class })
public class BatchBootJmsSimple {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BatchBootJmsSimple.class, args);

        int x = System.in.read();
        System.err.println("+++ " + x);
        if (x == 113) {
            System.exit(0);
        }
    }

}
