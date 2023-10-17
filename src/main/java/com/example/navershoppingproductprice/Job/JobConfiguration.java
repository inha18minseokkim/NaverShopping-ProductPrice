package com.example.navershoppingproductprice.Job;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.time.LocalDateTime;

@Configuration
public class JobConfiguration {
    private final JobRepository jobRepository;

//    @Qualifier("targetProductReceiveStep")
    private final Step firstStep;
//    @Qualifier("extractFromRawStep")
    private final Step secondStep;

    public JobConfiguration(JobRepository jobRepository, @Qualifier("targetProductReceiveStep") Step firstStep, @Qualifier("extractFromRawStep") Step secondStep) {
        this.jobRepository = jobRepository;
        this.firstStep = firstStep;
        this.secondStep = secondStep;
    }


    @Bean
    public JobParameters jobParameters() {
        return new JobParametersBuilder()
                .addLocalDateTime("processDate", LocalDateTime.now())
                .toJobParameters();
    }
    @Bean
    public JobExecution jobExecution() throws Exception {
        return jobLauncher().run(
                new JobBuilder("NaverReceiveJob",jobRepository)
                        .start(firstStep)
                        .next(secondStep)
                        .build()
                ,jobParameters());
    }
    @Bean
    public JobLauncher jobLauncher() throws Exception {
        TaskExecutorJobLauncher taskExecutorJobLauncher = new TaskExecutorJobLauncher();
        taskExecutorJobLauncher.setJobRepository(jobRepository);
        taskExecutorJobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        taskExecutorJobLauncher.afterPropertiesSet();
        return taskExecutorJobLauncher;
    }



}
