import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherApiFetcher {
    // Open-Meteoの天気予報APIを使う例（APIキー不要）
    public static String getWeatherForecast(double latitude, double longitude) {
        String apiUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m,weathercode&current_weather=true",
                latitude, longitude);
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "データの取得に失敗しました！レスポンスコード：" + responseCode;
            }
            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
            }
            JSONObject json = new JSONObject(responseBody.toString());
            JSONObject current = json.getJSONObject("current_weather");
            double temp = current.getDouble("temperature");
            int code = current.getInt("weathercode");
            String weather = getWeatherDescription(code);
            return String.format("現在の気温: %.1f℃\n天気: %s (コード: %d)", temp, weather, code);
        } catch (Exception e) {
            return "エラー: " + e.getMessage();
        }
    }

    // 天気コードを日本語の説明に変換
    private static String getWeatherDescription(int code) {
        switch (code) {
            case 0:
                return "快晴";
            case 1:
            case 2:
            case 3:
                return "主に晴れ・一部曇り";
            case 45:
            case 48:
                return "霧";
            case 51:
            case 53:
            case 55:
                return "霧雨";
            case 56:
            case 57:
                return "凍結霧雨";
            case 61:
            case 63:
            case 65:
                return "雨";
            case 66:
            case 67:
                return "凍結雨";
            case 71:
            case 73:
            case 75:
                return "雪";
            case 77:
                return "雪粒";
            case 80:
            case 81:
            case 82:
                return "にわか雨";
            case 85:
            case 86:
                return "にわか雪";
            case 95:
                return "雷雨";
            case 96:
            case 99:
                return "雷雨（ひょうを伴う）";
            default:
                return "不明";
        }
    }

    public static void main(String[] args) {
        // 東京の緯度経度
        double lat = 35.6895;
        double lon = 139.6917;
        String result = getWeatherForecast(lat, lon);
        System.out.println(result);
    }
}
