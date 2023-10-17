package com.example.navershoppingproductprice.Business;

import com.example.navershoppingproductprice.DTO.ApiReceiveResponse;
import com.example.navershoppingproductprice.Entity.TargetProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NaverApiReceiveTest {
    @Autowired
    private NaverApiReceive apiReceive;

    @Test
    public void ApiReceiveTest(){
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
        ApiReceiveResponse apiReceiveResponse = apiReceive.requestFromEntity(testEntity,1);
        System.out.println(apiReceiveResponse);
    }
}