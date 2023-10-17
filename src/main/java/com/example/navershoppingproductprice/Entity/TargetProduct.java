package com.example.navershoppingproductprice.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TargetProduct {
    @Id
    @Column(name = "productId")
    private Long productId;//상품아이디
    private String productName;//상품이름
    private String queryString;//상품검색 문자열
    private String maker;//제조사이름
    private String brand;//브랜드이름
    private String category1;//카테고리1
    private String category2;//카테고리2
    private String category3;//카테고리3
    private String category4;//카테고리4

}
