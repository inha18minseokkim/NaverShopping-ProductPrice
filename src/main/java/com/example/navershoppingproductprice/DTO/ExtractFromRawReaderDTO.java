package com.example.navershoppingproductprice.DTO;

import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ExtractFromRawReaderDTO {
    private Long productId;
    private LocalDate receiveDate;
    private Integer lprice;
    private Integer hprice;
}
