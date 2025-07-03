import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.util.Scanner;

public class GenderizeApiFetcher {
    // Genderize APIで名前から性別を判定する関数
    public static String getGender(String name) {
        String apiUrl = "https://api.genderize.io/?name=" + name;
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
            String gender = json.optString("gender", "不明");
            String probabilityStr;
            if (json.isNull("probability")) {
                probabilityStr = "-";
            } else {
                double probability = json.getDouble("probability");
                probabilityStr = String.format("%.1f%%", probability * 100);
            }
            int count = json.optInt("count", 0);
            String genderJp;
            switch (gender) {
                case "male":
                    genderJp = "男性";
                    break;
                case "female":
                    genderJp = "女性";
                    break;
                case "不明":
                    genderJp = "不明";
                    break;
                default:
                    genderJp = gender;
            }
            return String.format("名前: %s\n性別: %s (%s)\n確率: %s\n件数: %d", name, genderJp, gender, probabilityStr, count);
        } catch (Exception e) {
            return "エラー: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("名前を入力してください（終了するには空欄でEnter）: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty())
                break;
            String result = getGender(name);
            System.out.println(result);
            System.out.println("----------------------");
        }
        scanner.close();
    }
}
