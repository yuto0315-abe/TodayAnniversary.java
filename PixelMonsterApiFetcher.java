import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class PixelMonsterApiFetcher {
    // Pixel Encounter APIからランダムモンスター画像のURLを取得する関数
    public static String getRandomMonster() {
        String apiUrl = "https://app.pixelencounter.com/api/basic/monsters/random";
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
            String responseText = responseBody.toString();
            try {
                JSONObject json = new JSONObject(responseText);
                String svgUrl = json.getString("svgUrl");
                return String.format("モンスター画像URL: %s", svgUrl);
            } catch (Exception ex) {
                return "JSONパースエラー: " + ex.getMessage() + "\nレスポンス内容: " + responseText;
            }
        } catch (Exception e) {
            return "エラー: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        String result = getRandomMonster();
        System.out.println(result);
    }
}
