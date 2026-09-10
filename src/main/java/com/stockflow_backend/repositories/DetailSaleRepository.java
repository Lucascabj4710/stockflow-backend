package com.stockflow_backend.repositories;

import com.stockflow_backend.entities.DetailSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailSaleRepository extends JpaRepository<DetailSale, Long> {

    List<DetailSale> findBySale_Id(Long saleId);

    @Query("""
    SELECT ds
    FROM DetailSale ds
    WHERE ds.sale.id IN :saleIds
""")
    List<DetailSale> findBySaleIds(@Param("saleIds") List<Long> saleIds);

}
