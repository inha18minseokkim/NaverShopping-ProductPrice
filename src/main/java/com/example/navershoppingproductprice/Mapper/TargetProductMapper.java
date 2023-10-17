package com.example.navershoppingproductprice.Mapper;


import com.example.navershoppingproductprice.DTO.ApiReceiveItem;
import com.example.navershoppingproductprice.Entity.ApiReceiveRaw;
import com.example.navershoppingproductprice.Entity.TargetProduct;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring", // 빌드 시 구현체 만들고 빈으로 등록
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, // 생성자 주입 전략
        unmappedTargetPolicy = ReportingPolicy.ERROR // 일치하지 않는 필드가 있으면 빌드 시 에러
)
public interface TargetProductMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "lprice", expression = "java(java.lang.Integer.parseInt(element.getLprice()))"),
            @Mapping(target = "receiveDate", expression = "java(java.time.LocalDate.now())")
    })
    ApiReceiveRaw from(ApiReceiveItem element);
}
