package pl.java.scalatech.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@EnableBatchProcessing
@Configuration
@Profile(value = { "java" })
public class BatchConfig {

}
