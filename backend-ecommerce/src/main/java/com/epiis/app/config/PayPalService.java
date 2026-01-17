package com.epiis.app.config;

import org.springframework.stereotype.Service;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;

import jakarta.annotation.PostConstruct;

@Service
public class PayPalService {

    private PayPalHttpClient payPalHttpClient;
    private String environment;

    @PostConstruct
    public void initializeClient() {
        try {
            // Leer variables de entorno del sistema (Render / Docker)
            String clientId = System.getenv("PAYPAL_CLIENT_ID");
            String clientSecret = System.getenv("PAYPAL_CLIENT_SECRET");
            String mode = System.getenv().getOrDefault("PAYPAL_MODE", "sandbox");

            if (clientId == null || clientSecret == null) {
                throw new RuntimeException(
                        "PayPal credentials not found. Please configure PAYPAL_CLIENT_ID and PAYPAL_CLIENT_SECRET");
            }

            PayPalEnvironment payPalEnvironment;

            if ("live".equalsIgnoreCase(mode)) {
                payPalEnvironment = new PayPalEnvironment.Live(clientId, clientSecret);
                this.environment = "live";
            } else {
                payPalEnvironment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
                this.environment = "sandbox";
            }

            this.payPalHttpClient = new PayPalHttpClient(payPalEnvironment);

            System.out.println("✅ PayPal SDK initialized in " + this.environment + " mode");

        } catch (Exception e) {
            System.err.println("❌ Error initializing PayPal SDK: " + e.getMessage());
            throw new RuntimeException("Failed to initialize PayPal SDK", e);
        }
    }

    public PayPalHttpClient getPayPalHttpClient() {
        return payPalHttpClient;
    }

    public String getEnvironment() {
        return environment;
    }
}
