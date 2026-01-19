package com.epiis.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


public class DtoDashboardSummary {
    private long totalUsers;
    private BigDecimal todaysRevenue;
    private long pendingOrders;
    private long totalProducts;

    public DtoDashboardSummary() {
    }

    public DtoDashboardSummary(long totalUsers, BigDecimal todaysRevenue, long pendingOrders, long totalProducts) {
        this.totalUsers = totalUsers;
        this.todaysRevenue = todaysRevenue;
        this.pendingOrders = pendingOrders;
        this.totalProducts = totalProducts;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public BigDecimal getTodaysRevenue() {
        return todaysRevenue;
    }

    public void setTodaysRevenue(BigDecimal todaysRevenue) {
        this.todaysRevenue = todaysRevenue;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }
}
