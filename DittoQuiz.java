import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Scanner;

public class DittoQuiz {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        java.util.Random rand = new java.util.Random();
        while (true) {
            try {
                // 図鑑番号（1～1025）でランダム出題（2025年7月時点で1025まで存在）
                int pokeNumber = rand.nextInt(1025) + 1;
                String apiUrl = "https://pokeapi.co/api/v2/pokemon/" + pokeNumber;
                JSONObject json = getJson(apiUrl);
                String pokeName = json.getString("name");
                JSONArray types = json.getJSONArray("types");
                StringBuilder typeNames = new StringBuilder();
                for (int i = 0; i < types.length(); i++) {
                    JSONObject typeObj = types.getJSONObject(i).getJSONObject("type");
                    if (i > 0)
                        typeNames.append(", ");
                    typeNames.append(typeObj.getString("name"));
                }
                int height = json.getInt("height");
                int weight = json.getInt("weight");
                int baseExp = json.getInt("base_experience");

                // 日本語名・色・世代取得
                String pokeNameJp = null;
                String colorJp = null;
                String generation = null;
                try {
                    String speciesUrl = "https://pokeapi.co/api/v2/pokemon-species/" + pokeNumber;
                    JSONObject jsonJp = getJson(speciesUrl);
                    JSONArray names = jsonJp.getJSONArray("names");
                    for (int i = 0; i < names.length(); i++) {
                        JSONObject nameObj = names.getJSONObject(i);
                        if (nameObj.getJSONObject("language").getString("name").equals("ja-Hrkt")) {
                            pokeNameJp = nameObj.getString("name");
                            break;
                        }
                    }
                    colorJp = jsonJp.getJSONObject("color").getString("name");
                    generation = jsonJp.getJSONObject("generation").getString("name");
                } catch (Exception ex) {
                    // 無視
                }
                // タイプ日本語訳
                java.util.Map<String, String> typeMap = new java.util.HashMap<>();
                typeMap.put("normal", "ノーマル");
                typeMap.put("fire", "ほのお");
                typeMap.put("water", "みず");
                typeMap.put("electric", "でんき");
                typeMap.put("grass", "くさ");
                typeMap.put("ice", "こおり");
                typeMap.put("fighting", "かくとう");
                typeMap.put("poison", "どく");
                typeMap.put("ground", "じめん");
                typeMap.put("flying", "ひこう");
                typeMap.put("psychic", "エスパー");
                typeMap.put("bug", "むし");
                typeMap.put("rock", "いわ");
                typeMap.put("ghost", "ゴースト");
                typeMap.put("dragon", "ドラゴン");
                typeMap.put("dark", "あく");
                typeMap.put("steel", "はがね");
                typeMap.put("fairy", "フェアリー");
                StringBuilder typeNamesJp = new StringBuilder();
                for (int i = 0; i < types.length(); i++) {
                    JSONObject typeObj = types.getJSONObject(i).getJSONObject("type");
                    if (i > 0)
                        typeNamesJp.append(", ");
                    String typeEn = typeObj.getString("name");
                    typeNamesJp.append(typeMap.getOrDefault(typeEn, typeEn));
                }
                // 色・世代の日本語訳
                java.util.Map<String, String> colorMap = new java.util.HashMap<>();
                colorMap.put("black", "くろ");
                colorMap.put("blue", "あお");
                colorMap.put("brown", "ちゃいろ");
                colorMap.put("gray", "はいいろ");
                colorMap.put("green", "みどり");
                colorMap.put("pink", "ピンク");
                colorMap.put("purple", "むらさき");
                colorMap.put("red", "あか");
                colorMap.put("white", "しろ");
                colorMap.put("yellow", "きいろ");
                java.util.Map<String, String> genMap = new java.util.HashMap<>();
                genMap.put("generation-i", "第1世代");
                genMap.put("generation-ii", "第2世代");
                genMap.put("generation-iii", "第3世代");
                genMap.put("generation-iv", "第4世代");
                genMap.put("generation-v", "第5世代");
                genMap.put("generation-vi", "第6世代");
                genMap.put("generation-vii", "第7世代");
                genMap.put("generation-viii", "第8世代");
                genMap.put("generation-ix", "第9世代");
                // クイズ出題
                System.out.println("\n==============================");
                System.out.println("【ポケモンクイズ】");
                System.out.println("------------------------------");
                System.out.println("ヒント:");
                System.out.println("  ・タイプ: " + typeNamesJp);
                System.out.println("  ・高さ: " + height + "（デシメートル）");
                System.out.println("  ・重さ: " + weight + "（ヘクトグラム）");
                System.out.println("  ・基礎経験値: " + baseExp);
                if (colorJp != null)
                    System.out.println("  ・色: " + colorMap.getOrDefault(colorJp, colorJp));
                if (generation != null)
                    System.out.println("  ・世代: " + genMap.getOrDefault(generation, generation));
                System.out.println("------------------------------");
                System.out.print("このポケモンの名前を図鑑ナンバーで答えてください（終了は空欄Enter）: ");
                String answer = scanner.nextLine().trim().toLowerCase();
                if (answer.isEmpty())
                    break;
                // 正規化関数
                java.util.function.Function<String, String> normalizeKana = s -> {
                    if (s == null)
                        return null;
                    s = s.trim().toLowerCase();
                    s = s.replaceAll("[・’'‐‑‒–—―゠.\\s♂♀-]", "");
                    s = s.replace("\u3099", "").replace("\u309A", "");
                    StringBuilder sb = new StringBuilder();
                    for (char ch : s.toCharArray()) {
                        if (ch >= 'ぁ' && ch <= 'ん') {
                            sb.append((char) (ch - 'ぁ' + 'ァ'));
                        } else {
                            sb.append(ch);
                        }
                    }
                    return sb.toString();
                };
                java.util.function.Function<String, String> normalizeHira = s -> {
                    if (s == null)
                        return null;
                    s = s.trim().toLowerCase();
                    s = s.replaceAll("[・’'‐‑‒–—―゠.\\s♂♀-]", "");
                    s = s.replace("\u3099", "").replace("\u309A", "");
                    StringBuilder sb = new StringBuilder();
                    for (char ch : s.toCharArray()) {
                        if (ch >= 'ァ' && ch <= 'ン') {
                            sb.append((char) (ch - 'ァ' + 'ぁ'));
                        } else {
                            sb.append(ch);
                        }
                    }
                    return sb.toString();
                };
                java.util.function.Function<String, String> normalizeEng = s -> {
                    if (s == null)
                        return null;
                    return s.trim().toLowerCase().replaceAll("[・’'‐‑‒–—―゠.\\s♂♀-]", "");
                };
                boolean correct = false;
                String[] answerForms = new String[] {
                        answer,
                        normalizeKana.apply(answer),
                        normalizeHira.apply(answer),
                        normalizeEng.apply(answer)
                };
                String[] nameJpForms = pokeNameJp == null ? new String[] {}
                        : new String[] {
                                pokeNameJp,
                                normalizeKana.apply(pokeNameJp),
                                normalizeHira.apply(pokeNameJp),
                                normalizeEng.apply(pokeNameJp)
                        };
                String[] nameEnForms = new String[] {
                        pokeName,
                        normalizeKana.apply(pokeName),
                        normalizeHira.apply(pokeName),
                        normalizeEng.apply(pokeName)
                };
                outer: for (String a : answerForms) {
                    for (String b : nameJpForms) {
                        if (a != null && b != null && a.equals(b)) {
                            correct = true;
                            break outer;
                        }
                    }
                    for (String b : nameEnForms) {
                        if (a != null && b != null && a.equals(b)) {
                            correct = true;
                            break outer;
                        }
                    }
                }
                if (!correct && pokeNumber != -1) {
                    for (String a : answerForms) {
                        if (a != null && a.equals(String.valueOf(pokeNumber))) {
                            correct = true;
                            break;
                        }
                    }
                }
                if (correct) {
                    System.out.println("正解！\n----------------------");
                } else {
                    System.out.println("不正解。正解は '" + (pokeNameJp != null ? pokeNameJp : pokeName) + "'（図鑑No."
                            + pokeNumber + ") です。\n----------------------");
                }
            } catch (Exception e) {
                System.out.println("エラー: " + e.getMessage());
            }
        }
        scanner.close();
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
