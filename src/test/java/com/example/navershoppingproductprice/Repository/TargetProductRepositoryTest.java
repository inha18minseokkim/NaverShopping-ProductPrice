package com.example.navershoppingproductprice.Repository;

import com.example.navershoppingproductprice.Entity.TargetProduct;
import com.example.navershoppingproductprice.Job.JobConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TargetProductRepositoryTest {
    @Autowired
    private TargetProductRepository repository;
    @MockBean
    private JobConfiguration jobConfiguration;

    @Test
    public void insertTest() {
        TargetProduct testEntity = TargetProduct.builder()
                .productId(42238216039L)
                .productName("돼지 뒷다리")
                .brand("")
                .maker("도드람푸드")
                .category1("식품")
                .category2("축산물")
                .category3("돼지고기")
                .category4("국내산돼지고기")
                .queryString("도드람 돼지 뒷다리")
                .build();
        repository.save(testEntity);
        assertEquals(testEntity,repository.findById(42238216039L).get());
    }
    @Test
    public void findAllTest() {
        System.out.println(repository.findAll());
    }

}