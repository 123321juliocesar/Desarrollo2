package com.epiis.app.business;


import com.epiis.app.dto.DtoDashboardSummary;
import com.epiis.app.repository.OrderRepository;
import com.epiis.app.repository.ProductRepository;
import com.epiis.app.repository.SalesRepository;
import com.epiis.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Service
public class DashboardBusiness {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Transactional(readOnly = true)
    public DtoDashboardSummary getDashboardSummary() {
        // 1. Total Users
        long totalUsers = userRepository.count();

        // 2. Pending Orders
        long pendingOrders = orderRepository.countByStatus("pending");

        // 3. Total Products
        long totalProducts = productRepository.count();

        // 4. Today's Revenue
        // Calculate start and end of today
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date endDate = cal.getTime();

        BigDecimal todaysRevenue = salesRepository.getTotalSales(startDate, endDate);
        if (todaysRevenue == null) {
            todaysRevenue = BigDecimal.ZERO;
        }

        return new DtoDashboardSummary(totalUsers, todaysRevenue, pendingOrders, totalProducts);
    }
}
