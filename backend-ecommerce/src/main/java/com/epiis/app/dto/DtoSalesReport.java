package com.epiis.app.dto;


import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoSalesReport {
    private DtoSalesSummary summary;
    private List<DtoSalesTransaction> transactions;
    private List<DtoProductPerformance> topProducts;

    // Pagination Metadata
    private int currentPage;
    private int totalPages;
    private long totalElements;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DtoSalesSummary {
        private BigDecimal totalSales;
        private Long totalOrders;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DtoSalesTransaction {
        private String orderNumber;
        private String customerName;
        private String orderDate;
        private String paymentMethod;
        private String productSummary; // e.g. "Product A, Product B..." or "3 Products"
        private BigDecimal totalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DtoProductPerformance {
        private String productName;
        private Long quantitySold;
        private BigDecimal totalRevenue;
    }
}
