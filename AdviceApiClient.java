import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.json.JSONObject;

public class AdviceApiClient {
    private static final String URL_STRING = "https://api.adviceslip.com/advice";

    public String getAdvice() throws IOException, java.net.URISyntaxException, org.json.JSONException {
        URL url = new URI(URL_STRING).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("データの取得に失敗しました！レスポンスコード: " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            JSONObject data = new JSONObject(response.toString());
            JSONObject slip = data.getJSONObject("slip");
            String advice = slip.getString("advice");

            return advice;
        }
    }
}