package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/**
 * Tradutor automático dos textos da interface, usando a API gratuita
 * MyMemory (não precisa de chave/cadastro). Toda a interface é escrita
 * originalmente em português; quando o usuário troca para inglês (EN)
 * ou espanhol (ES), esta classe traduz os textos sob demanda e guarda
 * o resultado em um cache (Map) para não ficar chamando a API toda hora
 * repetindo a mesma tradução.
 *
 * IMPORTANTE: os métodos getCached()/traduzirSync() e traduzir() só
 * funcionam para textos que já passaram (ou estão passando agora) por
 * uma tradução. Por isso a tela principal (TelaPrincipal) pré-traduz em
 * lote uma lista com todos os textos fixos do sistema assim que o
 * idioma muda — veja o método traduzirLote() mais abaixo.
 */
public class Tradutor {

    // Códigos de idioma usados nas chamadas da API (padrão ISO 639-1)
    public static final String PT = "pt";
    public static final String EN = "en";
    public static final String ES = "es";

    // Todo o texto da interface é escrito originalmente em português,
    // então o idioma de origem das traduções é sempre PT
    private static final String IDIOMA_ORIGEM = PT;
    // Idioma atualmente selecionado pelo usuário (começa em português)
    private static String idiomaAtual = PT;
    // Cache de traduções já feitas. Chave: "texto original|idioma destino"
    // Valor: texto já traduzido. Evita repetir chamadas à API para o mesmo texto.
    private static final Map<String, String> cache = new HashMap<>();

    /** Define o idioma atual da aplicação */
    public static void setIdioma(String idioma) { idiomaAtual = idioma; }
    /** Retorna o idioma atualmente selecionado */
    public static String getIdioma() { return idiomaAtual; }
    /** Atalho para saber se o idioma atual é português (não precisa traduzir nada) */
    public static boolean isPortugues() { return PT.equals(idiomaAtual); }

    /**
     * Traduz um texto de forma assíncrona (não trava a interface enquanto espera
     * a resposta da API) e entrega o resultado através de um callback.
     * Usa SwingWorker para rodar a chamada de rede em uma thread separada
     * da thread de interface do Swing (EDT), evitando congelar a tela.
     *
     * @param texto    texto original em português
     * @param callback função chamada com o texto traduzido quando terminar
     */
    public static void traduzir(String texto, Consumer<String> callback) {
        // Nada a traduzir: texto vazio/nulo, devolve como está
        if (texto == null || texto.isBlank()) { callback.accept(texto); return; }
        // Se o idioma atual já é português, não precisa traduzir nada
        if (PT.equals(idiomaAtual)) { callback.accept(texto); return; }

        String chave = texto + "|" + idiomaAtual;
        // Já temos essa tradução em cache? Devolve na hora, sem chamar a API de novo
        if (cache.containsKey(chave)) { callback.accept(cache.get(chave)); return; }

        // SwingWorker roda doInBackground() em outra thread (fora da EDT),
        // e quando termina, executa done() de volta na thread do Swing
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() {
                return chamarApi(texto, IDIOMA_ORIGEM, idiomaAtual);
            }
            @Override protected void done() {
                try {
                    String r = get(); // resultado retornado por doInBackground()
                    cache.put(chave, r); // guarda no cache para próximas vezes
                    callback.accept(r);
                } catch (Exception e) {
                    // Se der erro, devolve o texto original (sem tradução) em vez de travar
                    callback.accept(texto);
                }
            }
        }.execute();
    }

    /**
     * Versão síncrona (bloqueante) da tradução: espera a resposta da API
     * antes de continuar. Deve ser usada com cuidado, pois trava a thread
     * que a chamar até a API responder (por isso é usada dentro de
     * SwingWorkers, nunca diretamente na thread de interface).
     *
     * @param texto texto original em português
     * @return texto traduzido (ou o próprio texto original se já for PT ou der erro)
     */
    public static String traduzirSync(String texto) {
        if (texto == null || texto.isBlank() || PT.equals(idiomaAtual)) return texto;
        String chave = texto + "|" + idiomaAtual;
        if (cache.containsKey(chave)) return cache.get(chave);
        String r = chamarApi(texto, IDIOMA_ORIGEM, idiomaAtual);
        cache.put(chave, r);
        return r;
    }

    /**
     * Traduz vários textos de uma vez (em lote), de forma assíncrona,
     * chamando um Runnable quando TODOS já estiverem traduzidos e no cache.
     * É este método que a TelaPrincipal usa para pré-carregar todos os
     * textos fixos da interface (botões, títulos, colunas, etc.) antes de
     * montar as telas — assim, quando cada tela individual chamar
     * Tradutor.getCached(texto), a tradução já vai estar pronta no cache.
     *
     * @param textos      array com todos os textos originais (em português) a traduzir
     * @param aoFinalizar código a executar quando o lote inteiro terminar
     */
    public static void traduzirLote(String[] textos, Runnable aoFinalizar) {
        // Se o idioma já é português, não há nada para traduzir: executa direto
        if (PT.equals(idiomaAtual)) { aoFinalizar.run(); return; }
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                // Percorre cada texto do lote, um por um, e traduz só os que
                // ainda não estão no cache (evita chamadas repetidas)
                for (String texto : textos) {
                    if (texto == null || texto.isBlank()) continue;
                    String chave = texto + "|" + idiomaAtual;
                    if (!cache.containsKey(chave)) {
                        cache.put(chave, chamarApi(texto, IDIOMA_ORIGEM, idiomaAtual));
                    }
                }
                return null;
            }
            @Override protected void done() { aoFinalizar.run(); }
        }.execute();
    }

    /**
     * Busca a tradução de um texto diretamente no cache, SEM chamar a API.
     * Se o idioma atual for português, devolve o próprio texto (nada a traduzir).
     * Se não achar no cache (texto nunca foi traduzido/pré-carregado), devolve
     * o texto original em português como "fallback" — é exatamente esse
     * comportamento que causava telas não traduzidas quando um texto era
     * esquecido na lista de pré-tradução do TelaPrincipal.
     *
     * @param texto texto original em português
     * @return texto traduzido se estiver em cache, ou o próprio texto original
     */
    public static String getCached(String texto) {
        if (PT.equals(idiomaAtual)) return texto;
        return cache.getOrDefault(texto + "|" + idiomaAtual, texto);
    }

    /** Limpa todo o cache de traduções (chamado ao trocar de idioma) */
    public static void limparCache() { cache.clear(); }

    /**
     * Faz a chamada HTTP real para a API MyMemory Translation.
     * Endpoint: GET https://api.mymemory.translated.net/get?q={texto}&langpair={origem}|{destino}
     *
     * @param texto  texto a traduzir
     * @param origem código do idioma de origem (sempre "pt" neste projeto)
     * @param destino código do idioma de destino ("en" ou "es")
     * @return texto traduzido, ou o texto original se a chamada falhar
     */
    private static String chamarApi(String texto, String origem, String destino) {
        try {
            // Codifica o texto e o par de idiomas para serem usados com segurança
            // dentro de uma URL (espaços, acentos, etc. viram códigos %XX)
            String query = URLEncoder.encode(texto, StandardCharsets.UTF_8);
            String langpair = URLEncoder.encode(origem + "|" + destino, StandardCharsets.UTF_8);
            String urlStr = "https://api.mymemory.translated.net/get?q=" + query + "&langpair=" + langpair;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5 segundos para conectar
            conn.setReadTimeout(5000);    // 5 segundos para ler a resposta
            conn.setRequestProperty("User-Agent", "TVTracker-Java/1.0");

            // Se a API não responder OK (200), desiste e devolve o texto original
            if (conn.getResponseCode() != 200) return texto;

            // Lê a resposta JSON inteira em uma única String
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            conn.disconnect();

            String json = sb.toString();

            // Em vez de usar uma biblioteca JSON completa aqui, fazemos uma busca
            // simples de texto pelo campo "translatedText":"..." dentro da resposta,
            // já que só precisamos desse único valor.
            String campo = "\"translatedText\":\"";
            int inicio = json.indexOf(campo);
            if (inicio == -1) return texto; // campo não encontrado, devolve original
            inicio += campo.length();
            int fim = json.indexOf("\"", inicio);
            if (fim == -1) return texto;

            // Extrai o texto traduzido e converte alguns códigos de escape
            // Unicode/HTML comuns de volta para os caracteres normais
            return json.substring(inicio, fim)
                .replace("\\u003c", "<").replace("\\u003e", ">")
                .replace("\\u0026", "&").replace("\\'", "'");
        } catch (Exception e) {
            // Falha de rede, timeout, etc.: não trava o app, só loga e devolve o original
            System.err.println("Erro ao traduzir: " + e.getMessage());
            return texto;
        }
    }
}
