package com.example.navershoppingproductprice.Job;

import com.example.navershoppingproductprice.DTO.ExtractFromRawReaderDTO;
import com.example.navershoppingproductprice.Entity.ProductPriceInfo;
import com.example.navershoppingproductprice.Mapper.ExtractFromRawDataDTOMapper;
import com.example.navershoppingproductprice.Repository.ProductPriceInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ExtractFromRawStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private final ProductPriceInfoRepository productPriceInfoRepository;


    @Bean
    public Step extractFromRawStep(){
        return new StepBuilder("ExtractFromRawStep",jobRepository)
                .<ExtractFromRawReaderDTO, ProductPriceInfo>chunk(100,platformTransactionManager)
                .reader(extractFromRawReaderDTOItemReader())
                .processor(extractFromRawReaderDTOProductPriceInfoItemProcessor())
                .writer(productPriceInfoItemWriter())
                .build();
    }
    @Bean
    public ItemReader<ExtractFromRawReaderDTO> extractFromRawReaderDTOItemReader() {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sql = "select\n" +
                "    a.productId\n" +
                "     ,receiveDate\n" +
                "     ,lprice\n" +
                "     ,hprice\n" +
                "from\n" +
                "    ApiReceiveRaw a\n" +
                "   ,TargetProduct b\n" +
                "where\n" +
                "    b.productId = a.productId\n" +
                "    and a.receiveDate = str_to_date(\"" +currentDate+ "\",\"%Y%m%d\")";
        return new JdbcCursorItemReaderBuilder<ExtractFromRawReaderDTO>()
                .name("extractFromRawReaderDTOItemReader")
                .fetchSize(100)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(ExtractFromRawReaderDTO.class))
                .sql(sql)
                .build();
    }
    @Bean
    public ItemProcessor<ExtractFromRawReaderDTO,ProductPriceInfo> extractFromRawReaderDTOProductPriceInfoItemProcessor() {
        return new FunctionItemProcessor<>(ExtractFromRawDataDTOMapper.INSTANCE::from);
    }
    @Bean
    public ItemWriter<ProductPriceInfo> productPriceInfoItemWriter() {
        RepositoryItemWriter<ProductPriceInfo> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setMethodName("save");
        itemWriter.setRepository(productPriceInfoRepository);
        return itemWriter;
    }
}
