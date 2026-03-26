package com.stockflow_backend.repositories;

import com.stockflow_backend.entities.DetailSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailSaleRepository extends JpaRepository<DetailSale, Long> {

    List<DetailSale> findBySale_Id(Long saleId);

}
