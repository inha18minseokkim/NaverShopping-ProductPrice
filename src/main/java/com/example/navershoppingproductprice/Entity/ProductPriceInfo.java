package com.example.navershoppingproductprice.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductPriceInfo {
    @Id
    public Long id;

}
