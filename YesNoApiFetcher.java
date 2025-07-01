import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class YesNoApiFetcher {
    // YesNo APIからyes/no/maybeの答えを取得する関数
    public static String getYesNoAnswer() {
        String apiUrl = "https://yesno.wtf/api";
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
            String answer = json.getString("answer");
            String image = json.getString("image");
            return String.format("答え: %s\n画像URL: %s", answer, image);
        } catch (Exception e) {
            return "エラー: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        String result = getYesNoAnswer();
        System.out.println(result);
    }
}
