package com.epiis.app.business;

import com.epiis.app.dto.DtoSalesReport;
import com.epiis.app.dto.DtoSalesReport.*;
import com.epiis.app.entity.Order;
import com.epiis.app.entity.Payment;
import com.epiis.app.entity.User;
import com.epiis.app.repository.PaymentRepository;
import com.epiis.app.repository.SalesRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

@Service
public class SalesBusiness {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public DtoSalesReport getSalesReport(Date startDate, Date endDate, int page, int size) {
        DtoSalesReport report = new DtoSalesReport();

        // 1. Summary
        BigDecimal totalSales = salesRepository.getTotalSales(startDate, endDate);
        Long totalOrders = salesRepository.getTotalOrders(startDate, endDate);
        report.setSummary(new DtoSalesSummary(totalSales != null ? totalSales : BigDecimal.ZERO,
                totalOrders != null ? totalOrders : 0L));

        // 2. Product Performance (Limit to Top 10 to prevent massive payload)
        List<DtoProductPerformance> topProducts = salesRepository.getProductPerformance(startDate, endDate,
                PageRequest.of(0, 10));
        report.setTopProducts(topProducts);

        // 3. Transactions (Paginated)
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> ordersPage = salesRepository.getOrdersInRange(startDate, endDate, pageable);

        List<DtoSalesTransaction> transactions = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Optimize: Fetch all payments for these orders in one query
        List<String> orderIds = ordersPage.getContent().stream()
                .map(Order::getIdOrder)
                .collect(java.util.stream.Collectors.toList());

        java.util.Map<String, String> paymentMap = new java.util.HashMap<>();
        if (!orderIds.isEmpty()) {
            List<Payment> payments = paymentRepository.findByOrder_IdOrderIn(orderIds);
            for (Payment p : payments) {
                paymentMap.put(p.getOrder().getIdOrder(), p.getPaymentMethod());
            }
        }

        for (Order order : ordersPage.getContent()) {
            DtoSalesTransaction tx = new DtoSalesTransaction();
            tx.setOrderNumber(order.getOrderNumber());

            // Customer Name
            User user = order.getUser();
            tx.setCustomerName(user.getFirstName() + " " + user.getLastName());

            tx.setOrderDate(sdf.format(order.getOrderDate()));
            tx.setTotalAmount(order.getTotalAmount()); // Using TotalAmount from Order

            // Payment Method (O(1) lookup)
            String method = paymentMap.getOrDefault(order.getIdOrder(), "N/A");
            tx.setPaymentMethod(method);

            // Product Summary (Simple count for now)
            int productCount = order.getOrderItems() != null ? order.getOrderItems().size() : 0;
            tx.setProductSummary(productCount + " Products");

            transactions.add(tx);
        }
        report.setTransactions(transactions);

        // 4. Pagination Metadata
        report.setCurrentPage(ordersPage.getNumber());
        report.setTotalPages(ordersPage.getTotalPages());
        report.setTotalElements(ordersPage.getTotalElements());

        return report;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public byte[] generatePdfReport(Date startDate, Date endDate) throws Exception {
        // For PDF, we might want all transactions or just a summary.
        // Typically PDFs show all data, but if it's too large, it might crash.
        // For now, let's fetch a large number of records for PDF (e.g., 1000) or assume
        // pagination is for UI only.
        // Let's use a very large page size for export to capture "all" logic for now.
        DtoSalesReport report = getSalesReport(startDate, endDate, 0, 10000);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Font styles
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Title
            Paragraph title = new Paragraph("Reporte de Venta", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Date Range
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            document.add(new Paragraph("Periodo: " + sdf.format(startDate) + " to " + sdf.format(endDate), normalFont));
            document.add(new Paragraph("Generated on: " + sdf.format(new Date()), normalFont));
            document.add(new Paragraph(" ")); // Spacer

            // Executive Summary
            document.add(new Paragraph("Resumen Ejecutivo", headerFont));
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(10);

            addCell(summaryTable, "Total de ventas", headerFont);
            addCell(summaryTable, "S/. " + report.getSummary().getTotalSales(), normalFont);
            addCell(summaryTable, "Total Pedidos", headerFont);
            addCell(summaryTable, String.valueOf(report.getSummary().getTotalOrders()), normalFont);
            document.add(summaryTable);
            document.add(new Paragraph(" "));

            // Product Performance
            document.add(new Paragraph("Top Productos", headerFont));
            PdfPTable productTable = new PdfPTable(3);
            productTable.setWidthPercentage(100);
            productTable.setSpacingBefore(10);

            addCell(productTable, "Producto", headerFont);
            addCell(productTable, "Cantidad Vendida", headerFont);
            addCell(productTable, "Ingresos", headerFont);

            for (DtoProductPerformance p : report.getTopProducts()) {
                addCell(productTable, p.getProductName(), normalFont);
                addCell(productTable, String.valueOf(p.getQuantitySold()), normalFont);
                addCell(productTable, "S/. " + p.getTotalRevenue(), normalFont);
            }
            document.add(productTable);
            document.add(new Paragraph(" "));

            // Transactions
            document.add(new Paragraph("Transacciones", headerFont));
            PdfPTable txTable = new PdfPTable(5);
            txTable.setWidthPercentage(100);
            txTable.setSpacingBefore(10);

            addCell(txTable, "Order #", headerFont);
            addCell(txTable, "Fecha y Hora", headerFont);
            addCell(txTable, "Cliente", headerFont);
            addCell(txTable, "Metodo de Pago", headerFont);
            addCell(txTable, "Monto", headerFont);

            for (DtoSalesTransaction tx : report.getTransactions()) {
                addCell(txTable, tx.getOrderNumber(), normalFont);
                addCell(txTable, tx.getOrderDate(), normalFont);
                addCell(txTable, tx.getCustomerName(), normalFont);
                addCell(txTable, tx.getPaymentMethod(), normalFont);
                addCell(txTable, "S/. " + tx.getTotalAmount(), normalFont);
            }
            document.add(txTable);

            document.close();
            return out.toByteArray();
        }
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }
}
