package com.example.navershoppingproductprice.Mapper;

import com.example.navershoppingproductprice.DTO.ExtractFromRawReaderDTO;
import com.example.navershoppingproductprice.Entity.ApiReceiveRaw;
import com.example.navershoppingproductprice.Entity.ProductPriceInfo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "default")
public interface ExtractFromRawDataDTOMapper {
    ExtractFromRawDataDTOMapper INSTANCE = Mappers.getMapper(ExtractFromRawDataDTOMapper.class);
    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    ProductPriceInfo from(ExtractFromRawReaderDTO element);
}
