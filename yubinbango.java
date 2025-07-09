import java.util.Scanner;

class getYubinbango {

}

public class yubinbango {
    private static final String API_URL = "http://zip.cgis.biz/xml/zip.php?zn=";

    public static void main(String[] args) {
        System.out.println("郵便番号を入力");
        Scanner scanner = new Scanner(System.in);
        String Code = scanner.nextLine();
        scanner.close();

        try {
            if (Code.length() != 7 || !Code.matches("\\d{7}")) {
                System.out.println("郵便番号は7桁の数字で入力してください。");
                return; // ここで処理を終了
            }
            String apiUrl = API_URL + Code;
            java.net.URL url = new java.net.URL(apiUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // レスポンスから住所部分を抜き出す（例:
            // <state>東京都</state><city>千代田区</city><address>千代田</address>）
            String xml = response.toString();
            // 正規表現で漢字の都道府県・市区町村・町名を抽出
            String state = "";
            String city = "";
            String address = "";
            java.util.regex.Matcher m1 = java.util.regex.Pattern.compile("<value state=\"(.*?)\"").matcher(xml);
            if (m1.find()) {
                state = m1.group(1);
            }
            java.util.regex.Matcher m2 = java.util.regex.Pattern.compile("<value city=\"(.*?)\"").matcher(xml);
            if (m2.find()) {
                city = m2.group(1);
            }
            java.util.regex.Matcher m3 = java.util.regex.Pattern.compile("<value address=\"(.*?)\"").matcher(xml);
            if (m3.find()) {
                address = m3.group(1);
            }
            if (state == city || city == address || address == state) {
                System.out.println("住所の取得に失敗しました。");
                return; // ここで処理を終了
            }
            System.out.println(state);
            System.out.println(city);
            System.out.println(address);

            System.out.println("住所: " + state + city + address);

        } catch (Exception e) {
            System.out.println("住所の取得に失敗しました: " + e.getMessage());
        }
    }
}
