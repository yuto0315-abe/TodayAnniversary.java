import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class PokeApiFetcher {
    public static void main(String[] args) {
        String[] urls = {
                "https://pokeapi.co/api/v2/pokemon/pikachu",
                "https://pokeapi.co/api/v2/pokemon/151/"
        };
        // ここに他のポケモンのAPI URLを追加できます
        for (String apiUrl : urls) {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    System.out.println("データの取得に失敗しました！レスポンスコード：" + responseCode);
                    continue;
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
                String name = json.getString("name");
                int id = json.getInt("id");
                JSONArray types = json.getJSONArray("types");
                StringBuilder typeNames = new StringBuilder();
                for (int i = 0; i < types.length(); i++) {
                    JSONObject typeObj = types.getJSONObject(i).getJSONObject("type");
                    if (i > 0)
                        typeNames.append(", ");
                    typeNames.append(typeObj.getString("name"));
                }
                System.out.println("ポケモンID: " + id);
                System.out.println("名前: " + name);
                System.out.println("タイプ: " + typeNames);
                System.out.println("----------------------");
            } catch (Exception e) {
                System.out.println("エラー: " + e.getMessage());
            }
        }
    }
}
