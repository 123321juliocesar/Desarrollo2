package com.epiis.app.business;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class PricingBusiness {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("129.90");
    private static final BigDecimal DEFAULT_SHIPPING_COST = new BigDecimal("9.90");

    /**
     * Calcula el costo de envío basado en el subtotal
     * 
     * @param subtotal El subtotal del carrito
     * @return El costo de envío (0 si supera el umbral, sino costo por defecto)
     */
    public BigDecimal calculateShippingCost(BigDecimal subtotal) {
        if (subtotal == null)
            return BigDecimal.ZERO;

        if (subtotal.compareTo(FREE_SHIPPING_THRESHOLD) > 0) {
            return BigDecimal.ZERO;
        }
        return DEFAULT_SHIPPING_COST;
    }

    /**
     * Calcula el total final sumando subtotal y envío
     * 
     * @param subtotal     El subtotal del carrito
     * @param shippingCost El costo de envío
     * @return El total final
     */
    public BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal shippingCost) {
        if (subtotal == null)
            return BigDecimal.ZERO;
        BigDecimal safeShipping = shippingCost != null ? shippingCost : BigDecimal.ZERO;
        return subtotal.add(safeShipping);
    }
}
