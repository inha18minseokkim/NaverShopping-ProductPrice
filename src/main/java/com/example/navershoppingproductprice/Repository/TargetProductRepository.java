package com.example.navershoppingproductprice.Repository;

import com.example.navershoppingproductprice.Entity.TargetProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetProductRepository extends PagingAndSortingRepository<TargetProduct,Long> {
}
