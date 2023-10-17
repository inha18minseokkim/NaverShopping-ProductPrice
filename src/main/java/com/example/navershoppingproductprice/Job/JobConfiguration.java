package com.example.navershoppingproductprice.Job;

import com.example.navershoppingproductprice.Business.NaverApiReceive;
import com.example.navershoppingproductprice.DTO.ApiReceiveItem;
import com.example.navershoppingproductprice.DTO.ApiReceiveResponse;
import com.example.navershoppingproductprice.Entity.ApiReceiveRaw;
import com.example.navershoppingproductprice.Entity.TargetProduct;
import com.example.navershoppingproductprice.Mapper.TargetProductMapper;
import com.example.navershoppingproductprice.Repository.TargetProductRepository;
import com.example.navershoppingproductprice.Utility.JpaListWriter;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
    private final NaverApiReceive apiReceive;
    private final TargetProductMapper targetProductMapper;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final TargetProductRepository targetProductRepository;
    private final EntityManagerFactory entityManagerFactory;

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
                        .start(targetProductReceiveStep(jobRepository,platformTransactionManager,targetProductRepository))
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


    @Bean
    public Step targetProductReceiveStep(JobRepository jobRepository,
                                         PlatformTransactionManager platformTransactionManager,
                                         TargetProductRepository targetProductRepository
                                         ) {
        return new StepBuilder("TargetProductReceiveStep",jobRepository)
                .<TargetProduct,List<ApiReceiveRaw>>chunk(100,platformTransactionManager)
                .reader(targetProductItemReader(targetProductRepository))
                .processor(compositeProcessor())
                .writer(targetItemWriter())
                .build();
    }

    @Bean
    public ItemReader<TargetProduct> targetProductItemReader(TargetProductRepository repository) {
        RepositoryItemReader<TargetProduct> itemReader = new RepositoryItemReader<>();
        itemReader.setRepository(repository);
        itemReader.setMethodName("findAll");
        itemReader.setPageSize(1);
        HashMap<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("productId", Sort.Direction.DESC);
        itemReader.setSort(sorts);
        return itemReader;
    }

    @Bean
    public ItemProcessor<TargetProduct,List<ApiReceiveRaw>> compositeProcessor() {
        CompositeItemProcessor<TargetProduct, List<ApiReceiveRaw>> objectObjectCompositeItemProcessor = new CompositeItemProcessor<>();
        objectObjectCompositeItemProcessor.setDelegates(Arrays.asList(targetItemReceiveProcessor(),targetItemMappingProcessor()));
        return objectObjectCompositeItemProcessor;
    }

    @Bean
    public ItemProcessor<TargetProduct, List<ApiReceiveItem>> targetItemReceiveProcessor() {
        return (TargetProduct target) -> {
            ApiReceiveResponse apiReceiveResponse = apiReceive.requestFromEntity(target, 1);
            return apiReceiveResponse.getItems();
        };
    }

    @Bean
    public ItemProcessor<List<ApiReceiveItem>, List<ApiReceiveRaw>> targetItemMappingProcessor() {
        return (List<ApiReceiveItem> target) -> target.stream().map(targetProductMapper::from).toList();
    }
    @Bean
    public ItemWriter<List<ApiReceiveRaw>> targetItemWriter() {
        JpaItemWriter<List<ApiReceiveRaw>> objectJpaListWriter = new JpaListWriter<>(new JpaItemWriter<>(),entityManagerFactory);
        return objectJpaListWriter;
    }
}
