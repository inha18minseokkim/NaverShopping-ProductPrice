package com.example.navershoppingproductprice.Mapper;

import com.example.navershoppingproductprice.Entity.ApiReceiveRaw;
import com.example.navershoppingproductprice.Entity.ProductPriceInfo;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring", // 빌드 시 구현체 만들고 빈으로 등록
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, // 생성자 주입 전략
        unmappedTargetPolicy = ReportingPolicy.ERROR // 일치하지 않는 필드가 있으면 빌드 시 에러
)
public interface ApiReceiveRawMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "receiveDate", expression = "java(java.time.LocalDate.now())")
    })
    ProductPriceInfo from(ApiReceiveRaw element);
}
