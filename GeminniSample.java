/**
 * Geminiを呼び出すサンプルアプリ
 * 
 * 注意事項：
 * あらかじめ環境変数 'GWMINNI_API_KEY' にAPIキーを設定してから実行ください。
 * 
 * @author n.katayama
 * @version 1.0
 */
public class GeminniSample {
    /**
     * メイン処理：Geminiに質問して、返答を標準に出力します
     * 
     * @param args コマンドライン引数（使用しません）
     */
    public static void main(String[] args) throws Exception {
        String apikey = System.getenv("GEMINI_API_KEY");
        if (apikey == null) {
            System.out.println("APIキーが設定されていません。環境変数 'GEMINI_API_KEY' にAPIキーを設定してください。");
            return;
        }

        String question = "授業によく遅刻します。どうしたらいいでしょうか？";

        try {
            String answer = GeminClient.queryGemini(question, apikey);
            System.out.println(answer);
        } catch (Exception e) {
            System.out.println("エラーが発せしました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
