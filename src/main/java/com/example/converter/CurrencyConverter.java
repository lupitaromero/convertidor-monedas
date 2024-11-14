package com.example.converter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

// Clase para almacenar los registros de conversión
class ConversionRecord {
    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double convertedAmount;
    private LocalDateTime timestamp;

    public ConversionRecord(String fromCurrency, String toCurrency, double amount, double convertedAmount) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
        this.timestamp = LocalDateTime.now(); // Marca de tiempo actual
    }

    @Override
    public String toString() {
        return String.format("De %s a %s: %.2f -> %.2f en %s",
                fromCurrency, toCurrency, amount, convertedAmount, timestamp);
    }
}

public class CurrencyConverter {
    private static final String API_KEY = "266d25f09c1b5c1e9497f1e5";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

    // Conjunto de códigos de moneda permitidos
    private static final Set<String> ALLOWED_CURRENCIES = new HashSet<>(Arrays.asList(
            "ARS", "BOB", "BRL", "CLP", "COP", "USD", "EUR", "GBP", "JPY", "MXN", "CAD"
    ));

    // Lista para el historial de conversiones
    private static final List<ConversionRecord> conversionHistory = new ArrayList<>();

    public static void main(String[] args) {
        try {
            // Paso 1:
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Paso 2: Análisis del JSON
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");

            // Obtener las tasas de conversión para las monedas filtradas
            double arsRate = conversionRates.get("ARS").getAsDouble();
            double bobRate = conversionRates.get("BOB").getAsDouble();
            double brlRate = conversionRates.get("BRL").getAsDouble();
            double clpRate = conversionRates.get("CLP").getAsDouble();
            double copRate = conversionRates.get("COP").getAsDouble();
            double usdRate = conversionRates.get("USD").getAsDouble();
            double eurRate = conversionRates.get("EUR").getAsDouble();
            double gbpRate = conversionRates.get("GBP").getAsDouble();
            double jpyRate = conversionRates.get("JPY").getAsDouble();
            double mxnRate = conversionRates.get("MXN").getAsDouble();
            double cadRate = conversionRates.get("CAD").getAsDouble();

            // Mostrar las opciones de conversión
            Scanner scanner = new Scanner(System.in);
            String fromCurrency, toCurrency;

            // Filtrar la moneda de origen
            do {
                System.out.println("Selecciona la moneda de origen (USD, ARS, BOB, BRL, CLP, COP, EUR, GBP, JPY, MXN, CAD): ");
                fromCurrency = scanner.nextLine().toUpperCase();
            } while (!ALLOWED_CURRENCIES.contains(fromCurrency));

            // Filtrar la moneda de destino
            do {
                System.out.println("Selecciona la moneda de destino (USD, ARS, BOB, BRL, CLP, COP, EUR, GBP, JPY, MXN, CAD): ");
                toCurrency = scanner.nextLine().toUpperCase();
            } while (!ALLOWED_CURRENCIES.contains(toCurrency));

            System.out.println("Ingresa la cantidad a convertir: ");
            double amount = scanner.nextDouble();

            // Paso 3: Realizar la conversión
            double fromRate = getRateForCurrency(fromCurrency, arsRate, bobRate, brlRate, clpRate, copRate, usdRate, eurRate, gbpRate, jpyRate, mxnRate, cadRate);
            double toRate = getRateForCurrency(toCurrency, arsRate, bobRate, brlRate, clpRate, copRate, usdRate, eurRate, gbpRate, jpyRate, mxnRate, cadRate);

            double convertedAmount = (amount / fromRate) * toRate;
            System.out.printf("Resultado: %.2f %s = %.2f %s%n", amount, fromCurrency, convertedAmount, toCurrency);

            // Almacenar el registro en el historial
            ConversionRecord record = new ConversionRecord(fromCurrency, toCurrency, amount, convertedAmount);
            conversionHistory.add(record);

            // Mostrar el historial de conversiones
            showConversionHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Función para obtener la tasa de la moneda elegida
    private static double getRateForCurrency(String currency, double arsRate, double bobRate, double brlRate,
                                             double clpRate, double copRate, double usdRate, double eurRate,
                                             double gbpRate, double jpyRate, double mxnRate, double cadRate) {
        return switch (currency) {
            case "ARS" -> arsRate;
            case "BOB" -> bobRate;
            case "BRL" -> brlRate;
            case "CLP" -> clpRate;
            case "COP" -> copRate;
            case "USD" -> usdRate;
            case "EUR" -> eurRate;
            case "GBP" -> gbpRate;
            case "JPY" -> jpyRate;
            case "MXN" -> mxnRate;
            case "CAD" -> cadRate;
            default -> throw new IllegalArgumentException("Moneda no soportada: " + currency);
        };
    }

    // Método para mostrar el historial de conversiones
    private static void showConversionHistory() {
        System.out.println("\nHistorial de conversiones:");
        for (ConversionRecord record : conversionHistory) {
            System.out.println(record);
        }
    }
}
