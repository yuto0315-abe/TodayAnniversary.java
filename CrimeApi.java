import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class CrimeApi {

    static Map<String, Double> crimeRates = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // データ
        crimeRates.put("tokyo", 2.5);
        crimeRates.put("osaka", 3.8);
        crimeRates.put("nagoya", 1.2);
        crimeRates.put("fukuoka", 2.0);

        // サーバー起動
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/crime", CrimeApi::handleRequest);
        server.setExecutor(null);
        server.start();

        System.out.println("Server started at http://localhost:8000/crime?city=osaka");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String city = null;

        if (query != null && query.contains("city=")) {
            city = query.split("=")[1];
        }

        String response;
        if (city != null && crimeRates.containsKey(city.toLowerCase())) {
            double rate = crimeRates.get(city.toLowerCase());
            response = "{\"city\": \"" + city + "\", \"crime_rate\": " + rate + "}";
            System.out.println("Request: city=" + city + " → Response: " + response);
        } else {
            response = "{\"error\": \"City not found\"}";
            System.out.println("Request: city=" + city + " → Response: " + response);
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}