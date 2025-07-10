public class Scvicesample {
    public static void main(String[] args) {
        String apikey = System.getenv("https://api.adviceslip.com/");
        if(apikey == null){
            System.out.println("APIキーが設定されていません。環境変数 'GMINI_API_KEY' にAPIキーを設定してください。");
            return;
        }
        try{
            AdviceApiClient client = new AdviceApiClient();
            String advice = client.getAdvice();
    
            String question = "これは英語のアドバイスです。日本語で、かつ日本人に伝わるような文章に翻訳し、翻訳結果のみ出力してください："+ advice;
            String answer = GeminClient.queryGemini(question, apikey);
            
            System.out.println("今日のアドバイス: " + answer);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}