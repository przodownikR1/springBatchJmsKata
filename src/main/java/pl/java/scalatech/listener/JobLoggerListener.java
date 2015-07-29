package pl.java.scalatech.listener;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component("simpleJobLogger")
public class JobLoggerListener implements JobExecutionListener {
    private StopWatch stopWatch;

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        stopWatch = new StopWatch();
        log.info("beginning exec : {} ", jobExecution.getJobInstance().getJobName());
        stopWatch.start("Processing start");

    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        stopWatch.stop();
        log.info("has completed job {} -> stastus {}", jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
        long duration = stopWatch.getLastTaskTimeMillis();
        log.info("Job took: {} minutes, {} seconds, {} miliseconds", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)), TimeUnit.MILLISECONDS.toMillis(duration));
    }
}