package com.example.navershoppingproductprice.Repository;

import com.example.navershoppingproductprice.Entity.ProductPriceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPriceInfoRepository extends JpaRepository<ProductPriceInfo,Long> {
}
