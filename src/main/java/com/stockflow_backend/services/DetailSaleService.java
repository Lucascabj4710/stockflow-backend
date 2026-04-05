package com.stockflow_backend.services;

import com.stockflow_backend.dto.request.DetailSaleRequestDTO;
import com.stockflow_backend.entities.DetailSale;
import com.stockflow_backend.entities.Product;
import com.stockflow_backend.entities.Sale;
import com.stockflow_backend.mapper.DetailSaleMapper;
import com.stockflow_backend.repositories.DetailSaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DetailSaleService {

    private final DetailSaleRepository detailSaleRepository;

    // Services
    private final ProductService productService;

    // Mapper
    private final DetailSaleMapper detailSaleMapper;

    public DetailSaleService(DetailSaleRepository detailSaleRepository, ProductService productService, DetailSaleMapper detailSaleMapper) {
        this.detailSaleRepository = detailSaleRepository;
        this.productService = productService;
        this.detailSaleMapper = detailSaleMapper;
    }

    @Transactional
    public List<DetailSale> getDetailsSalesByID(Long saleID){
        return detailSaleRepository.findBySale_Id(saleID);
    }



    @Transactional
    public List<DetailSale> createDetailSale(Sale sale, List<DetailSaleRequestDTO> detailSaleRequestDTOS){

        List<DetailSale> detailSaleList = new ArrayList<>();

        for (DetailSaleRequestDTO detailSaleRequestDTO : detailSaleRequestDTOS) {
            DetailSale detailSale = detailSaleMapper.toDetailSale(detailSaleRequestDTO);

            Product product = productService.getProductById(detailSaleRequestDTO.getProductId());

            productService.discountStock(product ,detailSale.getQuantity());

            detailSale.setSale(sale);
            detailSale.setProduct(product);
            detailSale.setUnitPrice(product.getPrice());

            detailSaleList.add(detailSale);
        }

        detailSaleRepository.saveAll(detailSaleList);
        return detailSaleList;
    }

    @Transactional
    public void deleteDetailSaleByID(Long detailSaleID){
        detailSaleRepository.deleteById(detailSaleID);
    }


}
