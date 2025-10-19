package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.Order;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class PdfService {

    private final VoucherService voucherService;

    public PdfService(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    public byte[] generateOrderReceipt(Order order) throws IOException, DocumentException {
        log.info("Starting PDF generation for order ID: {}", order.getId());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("Order Receipt"));
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("Customer: " + order.getCustomerName()));
            document.add(new Paragraph("Email: " + order.getCustomerEmail()));
            document.add(new Paragraph("Status: " + order.getStatus()));

            log.debug("Fetching voucher details for voucher ID: {}", order.getVoucher().getId());

            VoucherDTO voucher = voucherService.findById(order.getVoucher().getId().toString());

            document.add(new Paragraph("Voucher Title: " + voucher.getTitle()));
            document.add(new Paragraph("Description: " + voucher.getDescription()));
            document.add(new Paragraph("Price: " + voucher.getPrice() + " USD"));
            document.add(new Paragraph("Tour Type: " + voucher.getTourType()));
            document.add(new Paragraph("Transfer: " + voucher.getTransferType()));
            document.add(new Paragraph("Hotel: " + voucher.getHotelType()));
            document.add(new Paragraph("Arrival Date: " + voucher.getArrivalDate()));
            document.add(new Paragraph("Eviction Date: " + voucher.getEvictionDate()));

            log.info("PDF successfully generated for order ID: {}", order.getId());
        } catch (Exception e) {
            log.error("Error generating PDF for order ID: {}", order.getId(), e);
            throw e;
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }
}
