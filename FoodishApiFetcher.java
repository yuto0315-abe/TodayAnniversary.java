import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class FoodishApiFetcher {
    public static void main(String[] args) {
        try {
            String apiUrl = "https://foodish-api.com/api";
            JSONObject json = getJson(apiUrl);
            String imgUrl = json.optString("image", "");
            System.out.println("==============================");
            System.out.println("🍽️ 今日のランダム料理画像");
            System.out.println("==============================");
            System.out.println("■ 画像URL");
            System.out.println(imgUrl);
            System.out.println("==============================");
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    private static JSONObject getJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int res = conn.getResponseCode();
        if (res != HttpURLConnection.HTTP_OK)
            throw new Exception("API取得失敗: " + urlStr);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return new JSONObject(sb.toString());
    }
}
