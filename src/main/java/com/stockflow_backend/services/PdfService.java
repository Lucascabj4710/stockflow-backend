package com.stockflow_backend.services;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.stockflow_backend.entities.DetailSale;
import com.stockflow_backend.entities.Sale;
import com.stockflow_backend.exceptions.SaleNotFoundException;
import com.stockflow_backend.repositories.DetailSaleRepository;
import com.stockflow_backend.repositories.SaleRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private final SaleRepository saleRepository;
    private final DetailSaleRepository detailSaleRepository;

    public PdfService(SaleRepository saleRepository, DetailSaleRepository detailSaleRepository) {
        this.saleRepository = saleRepository;
        this.detailSaleRepository = detailSaleRepository;
    }

    public byte[] createPdf(Long saleId) {

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() ->
                        new SaleNotFoundException(
                                "Venta no encontrada con el ID: " + saleId
                        )
                );

        List<DetailSale> detailSaleList =
                detailSaleRepository.findBySale_Id(saleId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.setMargins(30, 36, 30, 36);

        // Fuentes estándar para regular y negrita
        PdfFont fontRegular;
        PdfFont fontBold;
        try {
            fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar las fuentes para el PDF", e);
        }

        document.setFont(fontRegular);

        // =========================
        // ENCABEZADO Y BRANDING
        // =========================

        Paragraph title = new Paragraph("STOCKFLOW")
                .setFont(fontBold)
                .setFontSize(22)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(2);

        Paragraph subtitle = new Paragraph("COMPROBANTE NO FISCAL DE VENTA")
                .setFontSize(11)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15);

        document.add(title);
        document.add(subtitle);

        // =========================
        // DATOS DE LA VENTA
        // =========================

        Paragraph saleDataTitle = new Paragraph("DATOS DE LA OPERACIÓN")
                .setFont(fontBold)
                .setFontSize(11)
                .setMarginBottom(4);

        document.add(saleDataTitle);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = sale.getSaleDate().format(dateFormatter);

        String status = switch (sale.getStatus()) {
            case IN_PROGRESS -> "Venta en progreso";
            case COMPLETED -> "Venta completada";
            case CANCELED -> "Venta cancelada";
        };

        Table saleInfoTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .useAllAvailableWidth()
                .setMarginBottom(12);

        saleInfoTable.addCell(new Cell().add(new Paragraph("Nº Comprobante").setFont(fontBold).setFontSize(9)));
        saleInfoTable.addCell(new Cell().add(new Paragraph("#" + String.format("%06d", sale.getId())).setFontSize(9)));

        saleInfoTable.addCell(new Cell().add(new Paragraph("Fecha y Hora").setFont(fontBold).setFontSize(9)));
        saleInfoTable.addCell(new Cell().add(new Paragraph(fechaFormateada).setFontSize(9)));

        saleInfoTable.addCell(new Cell().add(new Paragraph("Estado").setFont(fontBold).setFontSize(9)));
        saleInfoTable.addCell(new Cell().add(new Paragraph(status).setFontSize(9)));

        saleInfoTable.addCell(new Cell().add(new Paragraph("Método de Pago").setFont(fontBold).setFontSize(9)));
        saleInfoTable.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getPaymentMethod())).setFontSize(9)));

        document.add(saleInfoTable);

        // =========================
        // DETALLE DE VENTA
        // =========================

        Paragraph detailTitle = new Paragraph("DETALLE DE ARTÍCULOS")
                .setFont(fontBold)
                .setFontSize(11)
                .setMarginBottom(4);

        document.add(detailTitle);

        Table detailTable = new Table(UnitValue.createPercentArray(new float[]{46, 14, 20, 20}))
                .useAllAvailableWidth()
                .setMarginBottom(12);

        // Encabezados
        detailTable.addHeaderCell(new Cell().add(new Paragraph("Producto").setFont(fontBold).setFontSize(9)));
        detailTable.addHeaderCell(new Cell().add(new Paragraph("Cant.").setFont(fontBold).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));
        detailTable.addHeaderCell(new Cell().add(new Paragraph("Precio Unit.").setFont(fontBold).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));
        detailTable.addHeaderCell(new Cell().add(new Paragraph("Subtotal").setFont(fontBold).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));

        int totalItems = 0;

        for (DetailSale detailSale : detailSaleList) {
            totalItems += detailSale.getQuantity();

            detailTable.addCell(new Cell().add(new Paragraph(detailSale.getProduct().getName()).setFontSize(9)));
            detailTable.addCell(new Cell().add(new Paragraph(String.valueOf(detailSale.getQuantity())).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));
            detailTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", detailSale.getUnitPrice())).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));
            detailTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", detailSale.getSubtotal())).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(detailTable);

        // =========================
        // RESUMEN Y TOTALES
        // =========================

        Paragraph summaryTitle = new Paragraph("RESUMEN DE PAGO")
                .setFont(fontBold)
                .setFontSize(11)
                .setMarginBottom(4);

        document.add(summaryTitle);

        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        summaryTable.addCell(new Cell().add(new Paragraph("Total de Unidades").setFontSize(9)));
        summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalItems)).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));

        summaryTable.addCell(new Cell().add(new Paragraph("Monto Total").setFont(fontBold).setFontSize(10)));
        summaryTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", sale.getTotal())).setFont(fontBold).setFontSize(10)).setTextAlignment(TextAlignment.RIGHT));

        summaryTable.addCell(new Cell().add(new Paragraph("Monto Recibido").setFontSize(9)));
        summaryTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", sale.getAmountPaid())).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));

        summaryTable.addCell(new Cell().add(new Paragraph("Vuelto").setFontSize(9)));
        summaryTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", sale.getChangeAmount())).setFontSize(9)).setTextAlignment(TextAlignment.RIGHT));

        document.add(summaryTable);

        // =========================
        // PIE Y MENSAJE PERSONALIZADO
        // =========================

        Paragraph separator = new Paragraph("----------------------------------------------------------------------------------------------------")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.LIGHT_GRAY)
                .setMarginBottom(6);

        document.add(separator);

        Paragraph mainGreeting = new Paragraph("¡Muchas gracias por confiar en nosotros!")
                .setFont(fontBold)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);

        Paragraph personalNote = new Paragraph("Conserve este comprobante para cualquier reclamo o cambio dentro de los 30 días.")
                .setFontSize(8)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER);

        String printDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        Paragraph printedAt = new Paragraph("Documento emitido electrónicamente por StockFlow el " + printDate)
                .setFontSize(7)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(4);

        document.add(mainGreeting);
        document.add(personalNote);
        document.add(printedAt);

        // =========================
        // CERRAR
        // =========================

        document.close();

        return baos.toByteArray();
    }
}