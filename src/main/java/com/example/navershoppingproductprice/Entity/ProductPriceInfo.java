package com.example.navershoppingproductprice.Entity;

import jakarta.annotation.Nonnull;
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
        uniqueConstraints = @UniqueConstraint(name = "ProductPriceInfoConstraint", columnNames = {"productId", "receiveDate"})
)
public class ProductPriceInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nonnull
    private Long productId;
    @Nonnull
    private LocalDate receiveDate;
    private Integer lprice;
    private Integer hprice;


}
