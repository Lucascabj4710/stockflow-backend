package com.stockflow_backend.services;

import com.stockflow_backend.dto.request.SaleRequestDTO;
import com.stockflow_backend.dto.response.SaleResponseDto;
import com.stockflow_backend.entities.DetailSale;
import com.stockflow_backend.entities.Sale;
import com.stockflow_backend.entities.SaleStatus;
import com.stockflow_backend.exceptions.InsufficientPaymentException;
import com.stockflow_backend.exceptions.InvalidSaleStatusException;
import com.stockflow_backend.exceptions.SaleNotFoundException;
import com.stockflow_backend.mapper.SaleMapper;
import com.stockflow_backend.repositories.SaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;

    // Services
    private final DetailSaleService detailSaleService;
    private final ProductService productService;


    public SaleService(SaleRepository saleRepository, SaleMapper saleMapper, DetailSaleService detailSaleService, ProductService productService) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
        this.detailSaleService = detailSaleService;
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public Sale findSaleByID(Long saleID){
        return saleRepository.findById(saleID)
                .orElseThrow(()-> new SaleNotFoundException("The requested sale does not exist"));
    }

    @Transactional(readOnly = true)
    public Page<SaleResponseDto> getAllSales(Integer pageNumber){

        Pageable pageable = PageRequest.of(pageNumber,10);

        return saleRepository.findAll(pageable).map(saleMapper::toSaleResponseDto);
    }


    @Transactional
    public void createSale(SaleRequestDTO saleRequestDTO){
        Sale sale = saleMapper.toSale(saleRequestDTO);
        saleRepository.save(sale);

        List<DetailSale> detailSaleList = detailSaleService.createDetailSale(sale, saleRequestDTO.getDetailSaleRequestDTOList());
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal changeAmount = BigDecimal.ZERO;

        for (DetailSale detailSale : detailSaleList){
            total = total.add(detailSale.getSubtotal());
        }
        sale.setTotal(total);

        changeAmount = sale.getAmountPaid().subtract(total);

        if (changeAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientPaymentException("Insufficient payment amount. Missing: " + changeAmount.abs());
        }

        sale.setChangeAmount(changeAmount);
        sale.setSaleDate(LocalDateTime.now());
        sale.setStatus(SaleStatus.IN_PROGRESS);

        saleRepository.save(sale);
    }

    @Transactional
    public void editSaleStatus(Long saleID, String status) {

        findSaleByID(saleID);
        String upperStatus = status.toUpperCase();

        switch (upperStatus) {
            case "COMPLETED":
                completedStatus(saleID);
                break;

            case "CANCELED":
                canceledStatus(saleID);
                break;

            default:
                throw new InvalidSaleStatusException("The status '" + status + "' is not valid. " +
                        "Allowed: COMPLETED, CANCELED.");
        }
    }

    @Transactional
    public void completedStatus(Long saleID){
        Sale sale = findSaleByID(saleID);
        sale.setSaleDate(LocalDateTime.now());
        sale.setStatus(SaleStatus.COMPLETED);
    }

    @Transactional
    public void canceledStatus(Long saleID){
        Sale sale = findSaleByID(saleID);

        List<DetailSale> detailSaleList = detailSaleService.getDetailsSalesByID(saleID);

        for (DetailSale detailSale : detailSaleList){
            productService.addStock(detailSale.getProduct().getId(),
                    detailSale.getQuantity());
        }

        sale.setStatus(SaleStatus.CANCELED);
        saleRepository.save(sale);
    }

    @Transactional
    public void deleteProductSaleBySaleID(Long saleID, Long productID){
        Sale sale = findSaleByID(saleID);

        List<DetailSale> detailSaleList = detailSaleService.getDetailsSalesByID(saleID);

        for (DetailSale detailSale : detailSaleList){
            if (detailSale.getProduct().getId().equals(productID)){
                Long detailSaleID = detailSale.getId();
                detailSaleService.deleteDetailSaleByID(detailSaleID);
                sale.setTotal(sale.getTotal().subtract(detailSale.getSubtotal()));
            }
        }
        saleRepository.save(sale);

    }

}