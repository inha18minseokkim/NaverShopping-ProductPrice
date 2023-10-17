package com.example.navershoppingproductprice.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(
        uniqueConstraints = @UniqueConstraint(name = "RawDataUnique", columnNames = {"productId", "receiveDate"})
)
public class ApiReceiveRaw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;//: [<b>도드람</b>한돈] <b>뒷다리</b> 다짐육 500g,
    private String link;//: https;//://search.shopping.naver.com/gate.nhn?id=6469374548,
    private String image;//: https;//://shopping-phinf.pstatic.net/main_6469374/6469374548.1.jpg,
    private Integer lprice;//: 6500,
    private Integer hprice;//: ,
    private String mallName;//: 도드람한돈,
    private String productId;//: 6469374548,
    private String productType;//: 2,
    private String brand;//: 도드람,
    private String maker;//: ,
    private String category1;//: 식품,
    private String category2;//: 축산물,
    private String category3;//: 돼지고기,
    private String category4;//: 국내산돼지고기
    private LocalDate receiveDate;
}
