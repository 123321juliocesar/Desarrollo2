package com.epiis.app.business;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class CurrencyBusiness {

    // Tasa de cambio fija (Soles a Dólares)
    // En el futuro esto podría venir de una BD o una API externa
    private static final BigDecimal PEN_TO_USD_RATE = new BigDecimal("3.75");

    /**
     * Convierte un monto de PEN a USD
     * 
     * @param amountPen Monto en Soles
     * @return Monto en Dólares
     */
    public BigDecimal convertPenToUsd(BigDecimal amountPen) {
        if (amountPen == null) {
            return BigDecimal.ZERO;
        }
        return amountPen.divide(PEN_TO_USD_RATE, 2, RoundingMode.HALF_UP);
    }

    /**
     * Obtiene la tasa de cambio actual
     * 
     * @return Tasa de cambio
     */
    public BigDecimal getExchangeRate() {
        return PEN_TO_USD_RATE;
    }
}
