package com.epiis.app.config;

import org.springframework.stereotype.Service;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Service
public class PayPalService {

    private PayPalHttpClient payPalHttpClient;
    private String environment;

    @PostConstruct
    public void initializeClient() {
        try {
            // Cargar variables de entorno desde .env
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            String clientId = dotenv.get("PAYPAL_CLIENT_ID");
            String clientSecret = dotenv.get("PAYPAL_CLIENT_SECRET");
            String mode = dotenv.get("PAYPAL_MODE", "sandbox");

            // Validar que las credenciales existan
            if (clientId == null || clientSecret == null) {
                throw new RuntimeException(
                        "PayPal credentials not found. Please configure PAYPAL_CLIENT_ID and PAYPAL_CLIENT_SECRET in .env file");
            }

            // Configurar ambiente de PayPal
            PayPalEnvironment payPalEnvironment;
            if ("live".equalsIgnoreCase(mode)) {
                payPalEnvironment = new PayPalEnvironment.Live(clientId, clientSecret);
                this.environment = "live";
            } else {
                payPalEnvironment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
                this.environment = "sandbox";
            }

            // Inicializar cliente HTTP de PayPal
            this.payPalHttpClient = new PayPalHttpClient(payPalEnvironment);

            System.out.println("PayPal SDK initialized successfully in " + this.environment + " mode");

        } catch (Exception e) {
            System.err.println("Error initializing PayPal SDK: " + e.getMessage());
            throw new RuntimeException("Failed to initialize PayPal SDK", e);
        }
    }

    /**
     * Obtiene el cliente HTTP de PayPal configurado
     * 
     * @return PayPalHttpClient
     */
    public PayPalHttpClient getPayPalHttpClient() {
        if (payPalHttpClient == null) {
            throw new RuntimeException("PayPal client not initialized");
        }
        return payPalHttpClient;
    }

    /**
     * Obtiene el ambiente actual (sandbox o live)
     * 
     * @return String con el ambiente
     */
    public String getEnvironment() {
        return environment;
    }
}
