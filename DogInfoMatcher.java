import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class DogInfoMatcher {
    public static void main(String[] args) {
        try {
            // 1. 犬の画像を取得
            String imgApi = "https://dog.ceo/api/breeds/image/random";
            JSONObject imgJson = getJson(imgApi);
            String imgUrl = imgJson.optString("message", "");

            // 2. 画像URLから犬種を抽出
            String breed = extractBreedFromUrl(imgUrl);

            // 3. 犬の豆知識APIから情報取得
            String factApi = "https://dogapi.dog/api/v2/facts";
            JSONObject factJson = getJson(factApi);
            JSONArray factArr = factJson.optJSONArray("data");
            String fact = "";
            if (factArr != null && factArr.length() > 0) {
                fact = factArr.getJSONObject(0).optString("attributes", "");
                if (fact.startsWith("{")) {
                    JSONObject attrObj = new JSONObject(fact);
                    fact = attrObj.optString("body", "");
                }
            }
            String factJp = translateToJapanese(fact);

            // ===== 出力部（日本語中心・整形） =====
            System.out.println("\n==============================");
            System.out.println("🐶 今日の犬豆知識");
            System.out.println("==============================");
            System.out.println("■ 犬の豆知識（日本語訳）");
            System.out.println("  " + factJp);
            System.out.println();
            System.out.println("■ 犬の豆知識（英語原文）");
            System.out.println("  " + fact);
            System.out.println();
            System.out.println("==============================");
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

    // APIからJSONArray取得
    private static JSONArray getJsonArray(String urlStr) throws Exception {
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
        return new JSONArray(sb.toString());
    }

    // 画像URLから犬種を抽出
    private static String extractBreedFromUrl(String url) {
        if (url == null)
            return null;
        // 例: https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg
        String[] parts = url.split("/breeds/");
        if (parts.length < 2)
            return null;
        String breedPart = parts[1].split("/")[0];
        // サブブリード対応: hound-afghan → hound
        if (breedPart.contains("-")) {
            return breedPart.split("-")[0];
        }
        return breedPart;
    }

    // Google翻訳APIで日本語訳
    private static String translateToJapanese(String text) {
        try {
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
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
