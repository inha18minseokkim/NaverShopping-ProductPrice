package com.example.navershoppingproductprice.Job;

import com.example.navershoppingproductprice.Business.NaverApiReceive;
import com.example.navershoppingproductprice.DTO.ApiReceiveItem;
import com.example.navershoppingproductprice.DTO.ApiReceiveResponse;
import com.example.navershoppingproductprice.Entity.ApiReceiveRaw;
import com.example.navershoppingproductprice.Entity.TargetProduct;
import com.example.navershoppingproductprice.Mapper.TargetProductMapper;
import com.example.navershoppingproductprice.Repository.TargetProductRepository;
import com.example.navershoppingproductprice.Utility.BatchListWriter;
import com.example.navershoppingproductprice.Utility.DuplicateKeySkipper;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.navershoppingproductprice.Code.RequestCommonCode.PAGING_COUNT;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApiReceiveStepConfiguration {
    private final NaverApiReceive apiReceive;
    private final TargetProductMapper targetProductMapper;
    private final DataSource dataSource;
    @Bean
    public Step targetProductReceiveStep(JobRepository jobRepository,
                                         PlatformTransactionManager platformTransactionManager,
                                         TargetProductRepository targetProductRepository
    ) {
        return new StepBuilder("TargetProductReceiveStep",jobRepository)
                .<TargetProduct, List<ApiReceiveRaw>>chunk(100,platformTransactionManager)
                .reader(targetProductItemReader(targetProductRepository))
                .processor(compositeProcessor())
                .writer(targetItemWriter())
                .faultTolerant()
                .skipPolicy(new DuplicateKeySkipper())
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
            List<ApiReceiveItem> resultList = new ArrayList<>();
            int maxPageLength = 1;
            int offsetIndex = 1;
            while(true){
                if(maxPageLength < offsetIndex || offsetIndex > 10) break; //api 호출 수 제한 관계로 그냥 10페이지까지만 받기로함
                ApiReceiveResponse apiReceiveResponse = apiReceive.requestFromEntity(target, offsetIndex);
                int totalCount = Integer.parseInt(apiReceiveResponse.getTotal());
                resultList.addAll(apiReceiveResponse.getItems());
                maxPageLength = (int)Math.ceil((double)totalCount / PAGING_COUNT);
                log.info("총 건수 : {} 현재 offset : {} 페이징건수 : {} 최대 페이지수 : {}",totalCount,offsetIndex,PAGING_COUNT,maxPageLength);
                offsetIndex++;
                Thread.sleep(500); //혹시모른 스레드 블록을 위해
            }

            return resultList;
        };
    }

    @Bean
    public ItemProcessor<List<ApiReceiveItem>, List<ApiReceiveRaw>> targetItemMappingProcessor() {
        return (List<ApiReceiveItem> target) -> target.stream().map(targetProductMapper::from).toList();
    }
    @Bean
    public JdbcBatchItemWriter<ApiReceiveRaw> batchItemWriter() { //public으로 나와있지 않으면 안됨. 빈 매핑 안됨.
        JdbcBatchItemWriter<ApiReceiveRaw> build = new JdbcBatchItemWriterBuilder<ApiReceiveRaw>()
                .dataSource(dataSource)
                .sql("INSERT INTO ApiReceiveRaw (\n" +
                        "    brand, category1, category2, category3, category4, hprice, image, link, lprice, maker, mallName, productId, productType, receiveDate, title\n" +
                        ") VALUES (\n" +
                        "    :brand, :category1, :category2, :category3, :category4, :hprice, :image, :link, :lprice, :maker, :mallName, :productId, :productType, :receiveDate, :title\n" +
                        ") ON DUPLICATE KEY UPDATE\n" +
                        "    hprice = VALUES(hprice),\n" +
                        "    image = VALUES(image),\n" +
                        "    link = VALUES(link),\n" +
                        "    lprice = VALUES(lprice),\n" +
                        "    maker = VALUES(maker),\n" +
                        "    mallName = VALUES(mallName),\n" +
                        "    productType = VALUES(productType),\n" +
                        "    receiveDate = VALUES(receiveDate),\n" +
                        "    title = VALUES(title)")
                .beanMapped()
                .build();
        return build;
    }
    @Bean
    public ItemWriter<List<ApiReceiveRaw>> targetItemWriter() {

        ItemWriter<List<ApiReceiveRaw>> objectJpaListWriter = new BatchListWriter<>(batchItemWriter());

        return objectJpaListWriter;
    }


}

