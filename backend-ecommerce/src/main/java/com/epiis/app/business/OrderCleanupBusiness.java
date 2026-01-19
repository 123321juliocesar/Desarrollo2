package com.epiis.app.business;


import com.epiis.app.entity.Order;
import com.epiis.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class OrderCleanupBusiness {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Ejecuta cada hora para limpiar órdenes pendientes antiguas.
     * Limpia órdenes 'pending' creadas hace más de 2 horas.
     */
    @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 hora
    @Transactional
    public void cancelAbandonedOrders() {
        System.out.println("Ejecutando limpieza de órdenes abandonadas...");

        // Calcular fecha límite (hace 2 horas)
        LocalDateTime limitTime = LocalDateTime.now().minusHours(2);
        Timestamp limitTimestamp = Timestamp.valueOf(limitTime);


        List<Order> pendingOrders = orderRepository.findByStatus("pending");
        int cancelledCount = 0;

        for (Order order : pendingOrders) {
            // Verificar si es más antigua que el límite
            // order.getCreatedAt() es Timestamp
            if (order.getCreatedAt().before(limitTimestamp)) {
                order.setStatus("cancelled");
                order.setUpdatedAt(new Timestamp(new Date().getTime()));
                orderRepository.save(order);
                cancelledCount++;
            }
        }

        if (cancelledCount > 0) {
            System.out.println("Se cancelaron " + cancelledCount + " órdenes abandonadas.");
        }
    }
}
