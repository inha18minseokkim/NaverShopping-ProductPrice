package com.example.navershoppingproductprice.Business;

import com.example.navershoppingproductprice.DTO.ApiReceiveResponse;
import com.example.navershoppingproductprice.Entity.TargetProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class NaverApiReceive {
    @Value("${naver.id}")
    private String naverId;
    @Value("${naver.secret}")
    private String secret;

    private static final String ENDPOINTURL = "https://openapi.naver.com/v1/search/shop.json";
    public ApiReceiveResponse requestFromEntity(TargetProduct targetProduct,Integer offset) {
        log.info(naverId);
        log.info(secret);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(ENDPOINTURL)
                .queryParam("display",100)
                .queryParam("start",offset)
                .queryParam("query",targetProduct.getQueryString().replaceAll(" ","%20"))
                .queryParam("dataType","JSON");

        UriComponents uriComponents = uriComponentsBuilder.build();

        String finalUrl = uriComponents.toUriString();
        log.info(finalUrl);

        ApiReceiveResponse block = WebClient.create()
                .get()
                .uri(finalUrl)
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.add("X-Naver-Client-Id", naverId);
                    httpHeaders.add("X-Naver-Client-Secret", secret);
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ApiReceiveResponse.class)
                .block();
        return block;

    }
}
