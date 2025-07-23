import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class PublicHolidayApiFetcher {
    // 日本の2020年の祝日一覧を取得する関数
    public static void getJapanPublicHolidays2020() {
        String apiUrl = "https://date.nager.at/api/v3/PublicHolidays/2020/JP";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                StringBuilder errorBody = new StringBuilder();
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), "UTF-8"))) {
                    String line;
                    while (errorReader != null && (line = errorReader.readLine()) != null) {
                        errorBody.append(line);
                    }
                } catch (Exception ex) {
                    // ignore
                }
                System.out.println("データの取得に失敗しました！レスポンスコード：" + responseCode);
                System.out.println("レスポンス内容: " + errorBody.toString());
                return;
            }
            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
            }
            JSONArray arr = new JSONArray(responseBody.toString());
            System.out.println("2020年 日本の祝日一覧:");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject holiday = arr.getJSONObject(i);
                String date = holiday.getString("date");
                String localName = holiday.getString("localName");
                System.out.println(date + " : " + localName );
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        getJapanPublicHolidays2020();
    }
}
