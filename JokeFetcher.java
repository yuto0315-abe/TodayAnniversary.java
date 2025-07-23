import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;

public class JokeFetcher {
    // ジョークAPIからランダムなジョークを取得し、日本語訳して表示
    public static void main(String[] args) {
        try {
            String apiUrl = "https://official-joke-api.appspot.com/jokes/random";
            JSONObject json = getJson(apiUrl);
            String setup = json.optString("setup", "");
            String punchline = json.optString("punchline", "");
            String jokeEn = setup + "\n" + punchline;
            // setupとpunchlineを個別に翻訳
            String setupJp = translateToJapanese(setup);
            String punchlineJp = translateToJapanese(punchline);
            String jokeJp = setupJp + "\n" + punchlineJp;
            System.out.println("【英語ジョーク】\n" + jokeEn);
            System.out.println("\n【日本語訳】\n" + jokeJp);
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    // APIからJSON取得
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

    // Google翻訳APIで日本語訳
    private static String translateToJapanese(String text) {
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ja&dt=t&q="
                    + encodedText;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            String result = response.toString();
            int firstQuote = result.indexOf('"') + 1;
            int secondQuote = result.indexOf('"', firstQuote);
            if (firstQuote >= 0 && secondQuote > firstQuote) {
                return result.substring(firstQuote, secondQuote);
            } else {
                return text;
            }
        } catch (Exception e) {
            return "（翻訳失敗）" + text;
        }
    }
}
