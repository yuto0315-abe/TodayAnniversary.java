import java.io.BufferedReader;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;

import java.net.URL;

import java.net.URLEncoder;

import java.util.Random;

import java.util.Scanner;

import org.json.JSONObject;

public class FortuneWithAdviceAndHoroscope {

    public static String getLifeAdvice() {

        String apiUrl = "https://api.adviceslip.com/advice";

        try {

            URL url = new URL(apiUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            if (status != 200) {

                return "API通信エラー: ステータスコード " + status;

            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuilder content = new StringBuilder();

            String inputLine;

            while ((inputLine = in.readLine()) != null) {

                content.append(inputLine);

            }

            in.close();

            con.disconnect();

            JSONObject json = new JSONObject(content.toString());

            String advice = json.getJSONObject("slip").getString("advice");

            return translateToJapanese(advice);

        } catch (Exception e) {

            return "例外発生: " + e.getMessage();

        }

    }

    public static String translateToJapanese(String text) {

        try {

            String encodedText = URLEncoder.encode(text, "UTF-8");

            String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ja&dt=t&q="

                    + encodedText;

            URL url = new URL(urlStr);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            StringBuilder response = new StringBuilder();

            String inputLine;

            while ((inputLine = in.readLine()) != null) {

                response.append(inputLine);

            }

            in.close();

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

    public static String getHoroscope(String sign) {

        try {

            String urlStr = "https://aztro.sameerkumar.website/?sign=" + URLEncoder.encode(sign, "UTF-8")

                    + "&day=today";

            URL url = new URL(urlStr);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // POSTボディに空文字を送信
            try (java.io.OutputStream os = con.getOutputStream()) {
                os.write(new byte[0]);
            }
            con.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            StringBuilder response = new StringBuilder();

            String inputLine;

            while ((inputLine = in.readLine()) != null) {

                response.append(inputLine);

            }

            in.close();

            JSONObject json = new JSONObject(response.toString());

            String description = json.getString("description");

            String luckyNumber = json.getString("lucky_number");

            String luckyTime = json.getString("lucky_time");

            return "星座占い（" + sign + "）:\n" + description + "\nラッキーナンバー: " + luckyNumber + "\nラッキータイム: " + luckyTime;

        } catch (Exception e) {

            return "星座占いAPI取得失敗: " + e.getMessage();

        }

    }

    public static String getZodiacSign(int month, int day) {

        if ((month == 3 && day >= 21) || (month == 4 && day <= 19))
            return "aries";

        if ((month == 4 && day >= 20) || (month == 5 && day <= 20))
            return "taurus";

        if ((month == 5 && day >= 21) || (month == 6 && day <= 21))
            return "gemini";

        if ((month == 6 && day >= 22) || (month == 7 && day <= 22))
            return "cancer";

        if ((month == 7 && day >= 23) || (month == 8 && day <= 22))
            return "leo";

        if ((month == 8 && day >= 23) || (month == 9 && day <= 22))
            return "virgo";

        if ((month == 9 && day >= 23) || (month == 10 && day <= 23))
            return "libra";

        if ((month == 10 && day >= 24) || (month == 11 && day <= 22))
            return "scorpio";

        if ((month == 11 && day >= 23) || (month == 12 && day <= 21))
            return "sagittarius";

        if ((month == 12 && day >= 22) || (month == 1 && day <= 19))
            return "capricorn";

        if ((month == 1 && day >= 20) || (month == 2 && day <= 18))
            return "aquarius";

        if ((month == 2 && day >= 19) || (month == 3 && day <= 20))
            return "pisces";

        return "aries";

    }

    private static final String[] fortunes = {

            "大吉！今日は最高の1日になるよ",

            "中吉。ちょっといいことあるかも！",

            "小吉。ぼちぼちがんばろう！",

            "吉。無理せずマイペースで。",

            "凶。今日は慎重に行動してね。",

            "大凶。あせらずゆっくり休もう。"

    };

    private static final String[] colors = {

            "赤", "青", "緑", "黄色", "オレンジ", "紫", "ピンク", "白", "黒", "水色"

    };

    private static final String[] personalityTraits = {

            "好奇心旺盛でチャレンジ精神があるね。",

            "落ち着いていて頼りになるタイプ。",

            "明るくてみんなのムードメーカー！",

            "繊細で感受性豊か。",

            "慎重派だけど優しい心の持ち主。",

            "楽観的でいつもポジティブ！"

    };

    public static String checkMood(int score) {

        if (score < 0 || score > 10)
            return "気分は0から10の間で教えてね！";

        if (score >= 8)
            return "絶好調！今日も楽しんで！";

        else if (score >= 5)
            return "まあまあいい感じだね。";

        else if (score >= 2)
            return "少し疲れてるかな？休憩しよう。";

        else
            return "辛い時は無理しないでね。";

    }

    public static String getFortune(int month, int day) {

        if (month < 1 || month > 12 || day < 1 || day > 31) {

            return "誕生日の入力が正しくないよ。運勢は秘密にしとこう。";

        }

        int seed = month * 31 + day;

        Random rnd = new Random(seed);

        return fortunes[rnd.nextInt(fortunes.length)];

    }

    public static String getLuckyColor(String name) {

        if (name == null || name.isEmpty())
            return "今日はラッキーカラーは秘密だよ。";

        int seed = Math.abs(name.hashCode());

        Random rnd = new Random(seed);

        return colors[rnd.nextInt(colors.length)];

    }

    public static String getPersonality(String name) {

        if (name == null || name.isEmpty())
            return "まだ名前がないから秘密だよ。";

        int seed = Math.abs(name.hashCode());

        Random rnd = new Random(seed);

        return personalityTraits[rnd.nextInt(personalityTraits.length)];

    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("こんにちは！占いを始めるよ。");

        System.out.print("名前を教えてね：");

        String name = sc.nextLine().trim();

        System.out.print("誕生日を教えてね（月日、例: 7 9）：");

        String birthInput = sc.nextLine().trim();

        String[] parts = birthInput.split("\\s+");

        int month = Integer.parseInt(parts[0]);

        int day = Integer.parseInt(parts[1]);

        System.out.print("今日の気分を0から10で教えてね：");

        int moodScore = Integer.parseInt(sc.nextLine().trim());

        System.out.println("\n--- 占い結果 ---");

        System.out.println("こんにちは！");

        System.out.println("★ 今日の運勢 ★");

        System.out.println(getFortune(month, day));

        System.out.println("★ ラッキーカラー ★");

        System.out.println(getLuckyColor(name));

        System.out.println("★ 性格診断 ★");

        System.out.println(getPersonality(name));

        System.out.println("★ 今日の気分チェック ★");

        System.out.println(checkMood(moodScore));

        System.out.println("★ 今日のアドバイス ★");

        System.out.println(getLifeAdvice());

        sc.close();

    }

}
