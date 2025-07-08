import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CatFactFetcher {

    public static void main(String[] args) throws Exception {
        String fact = fetchCatFact();
        String translated = translateToJapanese(fact);
        System.out.println("猫の豆知識: " + translated);
    }

    public static String fetchCatFact() throws Exception {
        String url = "https://catfact.ninja/fact";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("失敗 : HTTPエラーコード : " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String json = response.toString();
        int start = json.indexOf("\"fact\":\"") + 8;
        int end = json.indexOf("\"", start);
        String fact = json.substring(start, end);
        return fact;
    }

    // Google翻訳のWebページを使って英語→日本語に翻訳（簡易版）
    public static String translateToJapanese(String text) throws Exception {
        String encodedText = URLEncoder.encode(text, "UTF-8");
        String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ja&dt=t&q="
                + encodedText;
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // 結果は [[["日本語訳", ...], ...], ...] の形式
        String result = response.toString();
        int firstQuote = result.indexOf("\"") + 1;
        int secondQuote = result.indexOf("\"", firstQuote);
        return result.substring(firstQuote, secondQuote);
    }
}
