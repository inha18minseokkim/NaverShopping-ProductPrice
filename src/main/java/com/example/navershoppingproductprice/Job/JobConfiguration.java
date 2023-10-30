package com.example.navershoppingproductprice.Job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

@Configuration
public class JobConfiguration {
    @Value("${isGo}")
    private String isGo;
    private final JobRepository jobRepository;

//    @Qualifier("targetProductReceiveStep")
    private final Step firstStep;
//    @Qualifier("extractFromRawStep")
    private final Step secondStep;
    private final PlatformTransactionManager transactionManager;

    public JobConfiguration(JobRepository jobRepository, @Qualifier("targetProductReceiveStep") Step firstStep,
                            @Qualifier("extractFromRawStep") Step secondStep, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.firstStep = firstStep;
        this.secondStep = secondStep;
        this.transactionManager = transactionManager;
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
                        .start(preTasklet()).on("COMPLETED")
                        .to(firstStep)
                        .from(preTasklet()).on("*").end()
                        .next(secondStep)
                        .build().build()
                ,jobParameters());
    }
    @Bean
    public Step preTasklet() {
        return new StepBuilder("preStep",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if(isGo.equals("N")){
                        contribution.setExitStatus(ExitStatus.FAILED);
                        return RepeatStatus.FINISHED;
                    }
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
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
