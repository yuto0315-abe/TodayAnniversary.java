import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class AdviceFetcher {

    public static String getLifeAdvice() {
        String apiUrl = "https://api.adviceslip.com/advice";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            if (status != 200) {
                return "API通信でエラーが発生しました。ステータスコード: " + status;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            con.disconnect();

            JSONObject json = new JSONObject(content.toString());
            String advice = json.getJSONObject("slip").getString("advice");

            // 英語のアドバイスを日本語に翻訳
            String adviceJP = translateToJapanese(advice);
            return adviceJP;

        } catch (Exception e) {
            return "例外が発生しました: " + e.getMessage();
        }
    }

    // Google翻訳API（非公式）を使って英語→日本語に翻訳
    public static String translateToJapanese(String text) {
        try {
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
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
            String result = response.toString();
            int firstQuote = result.indexOf('"') + 1;
            int secondQuote = result.indexOf('"', firstQuote);
            return result.substring(firstQuote, secondQuote);
        } catch (Exception e) {
            return "（翻訳失敗）" + text;
        }
    }

    public static void main(String[] args) {
        System.out.println("今日の人生アドバイス:");
        System.out.println(getLifeAdvice());
    }
}
