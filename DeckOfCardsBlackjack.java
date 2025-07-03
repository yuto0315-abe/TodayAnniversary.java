import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Scanner;

public class DeckOfCardsBlackjack {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 新しいデッキを作成
            String newDeckUrl = "https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1";
            JSONObject deckInfo = getJson(newDeckUrl);
            String deckId = deckInfo.getString("deck_id");

            // プレイヤーとディーラーに2枚ずつ配る
            JSONArray playerHand = drawCards(deckId, 2);
            JSONArray dealerHand = drawCards(deckId, 2);

            System.out.println("\n==============================");
            System.out.println("あなたの手札:");
            printHand(playerHand);
            System.out.println("------------------------------");
            System.out.println("ディーラーの手札: [1枚は伏せられています]");
            printHand(new JSONArray().put(dealerHand.getJSONObject(0))
                    .put(new JSONObject().put("code", "??").put("value", "?").put("suit", "?")));
            System.out.println("==============================\n");

            // プレイヤーターン
            while (true) {
                int playerScore = calcScore(playerHand);
                System.out.println("あなたの現在の得点: " + playerScore);
                if (playerScore > 21) {
                    System.out.println("バースト！あなたの負けです。\n==============================");
                    return;
                }
                System.out.print("ヒットしますか？(h: ヒット, s: スタンド): ");
                String cmd = scanner.nextLine().trim().toLowerCase();
                if (cmd.equals("h")) {
                    JSONArray newCard = drawCards(deckId, 1);
                    playerHand.put(newCard.getJSONObject(0));
                    System.out.println("\nあなたの手札:");
                    printHand(playerHand);
                    System.out.println();
                } else if (cmd.equals("s")) {
                    break;
                }
            }

            // ディーラーターン
            System.out.println("\n==============================");
            System.out.println("ディーラーの手札:");
            printHand(dealerHand);
            while (calcScore(dealerHand) < 17) {
                JSONArray newCard = drawCards(deckId, 1);
                dealerHand.put(newCard.getJSONObject(0));
                System.out.println("ディーラーがカードを引きました。");
                printHand(dealerHand);
            }

            int playerScore = calcScore(playerHand);
            int dealerScore = calcScore(dealerHand);
            System.out.println("\n==============================");
            System.out.println("あなたの得点: " + playerScore);
            System.out.println("ディーラーの得点: " + dealerScore);
            System.out.println("==============================");
            if (playerScore > 21) {
                System.out.println("バースト！あなたの負けです。\n==============================");
            } else if (dealerScore > 21 || playerScore > dealerScore) {
                System.out.println("あなたの勝ちです！\n==============================");
            } else if (playerScore == dealerScore) {
                System.out.println("引き分けです。\n==============================");
            } else {
                System.out.println("あなたの負けです。\n==============================");
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
        scanner.close();
    }

    // カードをn枚引く
    private static JSONArray drawCards(String deckId, int count) throws Exception {
        String url = "https://deckofcardsapi.com/api/deck/" + deckId + "/draw/?count=" + count;
        JSONObject json = getJson(url);
        return json.getJSONArray("cards");
    }

    // 手札の表示
    private static void printHand(JSONArray hand) {
        for (int i = 0; i < hand.length(); i++) {
            JSONObject card = hand.getJSONObject(i);
            String value = card.optString("value", "?");
            String suit = card.optString("suit", "?");
            String suitMark = suitToMark(suit);
            System.out.print("[" + value + suitMark + "] ");
        }
        System.out.println();
    }

    // スートを記号に変換
    private static String suitToMark(String suit) {
        switch (suit.toUpperCase()) {
            case "SPADES":
                return "♠";
            case "HEARTS":
                return "♥";
            case "DIAMONDS":
                return "♦";
            case "CLUBS":
                return "♣";
            default:
                return suit;
        }
    }

    // 手札の合計点を計算
    private static int calcScore(JSONArray hand) {
        int total = 0;
        int aceCount = 0;
        for (int i = 0; i < hand.length(); i++) {
            String value = hand.getJSONObject(i).optString("value", "0");
            if (value.equals("ACE")) {
                aceCount++;
                total += 11;
            } else if (value.equals("KING") || value.equals("QUEEN") || value.equals("JACK")) {
                total += 10;
            } else {
                try {
                    total += Integer.parseInt(value);
                } catch (Exception e) {
                    // 無視
                }
            }
        }
        // ACEの調整
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }
        return total;
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
}
