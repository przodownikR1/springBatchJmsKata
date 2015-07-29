package pl.java.scalatech.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import pl.java.scalatech.processor.SimpleStringProcessor;
import pl.java.scalatech.reader.SimpleStringReader;
import pl.java.scalatech.writer.SimpleStringWriter;

@Configuration
@Profile("simpleString")
@ComponentScan(basePackages = "pl.java.scalatech.listener")
public class SimpleStringProcessingJob {
    @Autowired
    private JobBuilderFactory jobs;
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExecutionListener simpleJobLogger;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Bean
    public Job job(JobExecutionListener listener, Step stepProcess) {
        return jobs.get("simpleStringProcessorTask").incrementer(new RunIdIncrementer()).flow(stepProcess).end().listener(simpleJobLogger).build();
    }

    @Bean
    public Step stepProcess(StepBuilderFactory stepBuilderFactory, ItemReader<String> reader, ItemWriter<String> writer, ItemProcessor<String, String> processor) {
        return stepBuilderFactory.get("stringStep").<String, String> chunk(3).reader(reader).processor(processor).writer(writer).build();
    }

    @Bean
    public ItemReader<String> reader() {
        return new SimpleStringReader();
    }

    @Bean
    public ItemWriter<String> writer() {
        return new SimpleStringWriter();
    }

    @Bean
    public ItemProcessor<String, String> processor() {
        return new SimpleStringProcessor();
    }

}
