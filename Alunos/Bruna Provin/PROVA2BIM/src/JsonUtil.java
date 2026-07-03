package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Serie;
import model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária central para tudo que envolve JSON no sistema:
 * 1) Serializar/desserializar objetos Usuario (usado pela persistência local)
 * 2) Fazer o parse "manual" da resposta da API TVMaze, que tem uma
 *    estrutura própria (não é exatamente igual à classe Serie), então
 *    precisamos extrair campo por campo em vez de mapear automaticamente.
 */
public class JsonUtil {

    // ObjectMapper é a classe principal do Jackson para converter Java <-> JSON.
    // É thread-safe, então uma única instância estática pode ser reaproveitada
    // por toda a aplicação (evita recriar/reconfigurar a cada uso).
    private static final ObjectMapper mapper = new ObjectMapper();

    // ==================== SERIALIZAÇÃO (Objeto → JSON) ====================

    /**
     * Converte um objeto Usuario para uma String JSON formatada (com identação).
     *
     * @param usuario objeto a serializar
     * @return String contendo o JSON
     * @throws Exception em caso de erro na serialização
     */
    public static String usuarioParaJson(Usuario usuario) throws Exception {
        // writerWithDefaultPrettyPrinter formata o JSON com indentação legível
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(usuario);
    }

    // ==================== DESSERIALIZAÇÃO (JSON → Objeto) ====================

    /**
     * Converte uma String JSON de volta para um objeto Usuario.
     *
     * @param json String JSON a desserializar
     * @return objeto Usuario reconstruído
     * @throws Exception em caso de JSON inválido
     */
    public static Usuario jsonParaUsuario(String json) throws Exception {
        return mapper.readValue(json, Usuario.class);
    }

    // ==================== PARSER DA API TVMAZE ====================
    // A API TVMaze não devolve o JSON no mesmo "formato" da nossa classe
    // Serie (nomes de campos diferentes, estruturas aninhadas como
    // rating.average, network.name, etc.), então aqui fazemos a conversão
    // manualmente, nó por nó, em vez de usar mapper.readValue diretamente.

    /**
     * Converte a resposta JSON da busca da API TVMaze em uma lista de Series.
     * A API de busca retorna um array de objetos no formato:
     * [ { "score": 12.3, "show": { ...dados da série... } }, ... ]
     *
     * @param json resposta bruta (texto) da API
     * @return lista de Series encontradas (vazia se der algum erro)
     */
    public static List<Serie> jsonApiParaListaSeries(String json) {
        List<Serie> lista = new ArrayList<>();
        try {
            // readTree() interpreta o JSON como uma árvore de nós genéricos,
            // permitindo navegar por ele sem precisar de uma classe Java exata
            JsonNode raiz = mapper.readTree(json);

            // Percorre cada elemento do array de resultados da busca
            for (JsonNode item : raiz) {
                // Cada item tem um campo "show" com os dados reais da série
                JsonNode showNode = item.get("show");
                if (showNode != null) {
                    Serie serie = parsearShowNode(showNode);
                    // Só adiciona se conseguiu montar a série e ela tem nome
                    if (serie != null && serie.getNome() != null) {
                        lista.add(serie);
                    }
                }
            }
        } catch (Exception e) {
            // Se o JSON vier malformado por algum motivo, não quebra o app:
            // apenas loga o erro e devolve a lista (possivelmente vazia)
            System.err.println("Erro ao parsear lista da API: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Converte um JSON de um único "show" (série) da API TVMaze em objeto Serie.
     * Usado quando buscamos os detalhes completos de uma série específica
     * (endpoint /shows/{id}, que não tem o wrapper "show" nem "score").
     *
     * @param json String JSON de um show individual
     * @return objeto Serie, ou null em caso de erro
     */
    public static Serie parsearShowApi(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            return parsearShowNode(node);
        } catch (Exception e) {
            System.err.println("Erro ao parsear show da API: " + e.getMessage());
            return null;
        }
    }

    /**
     * Método central que efetivamente lê campo por campo de um JsonNode
     * "show" e monta um objeto Serie, tratando os casos em que o campo
     * pode não existir ou vir nulo (comum em APIs públicas).
     */
    private static Serie parsearShowNode(JsonNode node) {
        try {
            Serie s = new Serie();

            // ID — usa 0 como valor padrão se o campo não existir
            s.setId(node.has("id") ? node.get("id").asInt() : 0);

            // Nome da série
            s.setNome(textoOuNulo(node, "name"));

            // Idioma original da série
            s.setIdioma(textoOuNulo(node, "language"));

            // Status atual (Running, Ended, etc.)
            s.setStatus(textoOuNulo(node, "status"));

            // Nota — vem aninhada dentro de um objeto: "rating": { "average": X }
            // Por isso é preciso checar se "rating" existe e não é nulo antes
            // de tentar acessar "average" dentro dele.
            if (node.has("rating") && !node.get("rating").isNull()) {
                JsonNode rating = node.get("rating");
                if (rating.has("average") && !rating.get("average").isNull()) {
                    s.setNota(rating.get("average").asDouble());
                }
            }

            // Gêneros — vem como um array de strings, ex: ["Drama", "Crime"].
            // Aqui percorremos o array e juntamos tudo numa única String
            // separada por vírgula, já que o model.Serie guarda como texto simples.
            if (node.has("genres") && node.get("genres").isArray()) {
                List<String> generos = new ArrayList<>();
                for (JsonNode g : node.get("genres")) {
                    generos.add(g.asText());
                }
                s.setGeneros(generos.isEmpty() ? "N/A" : String.join(", ", generos));
            } else {
                s.setGeneros("N/A");
            }

            // Data de estreia (campo "premiered" na API)
            s.setDataEstreia(textoOuNulo(node, "premiered"));

            // Data de término (campo "ended" na API, pode ser nulo se ainda estiver rodando)
            s.setDataTermino(textoOuNulo(node, "ended"));

            // Emissora — normalmente vem em "network": { "name": "..." },
            // mas séries de streaming (ex: Netflix) usam "webChannel" em vez de "network".
            String emissora = null;
            if (node.has("network") && !node.get("network").isNull()) {
                emissora = textoOuNulo(node.get("network"), "name");
            }
            // Fallback: se não achou em "network", tenta em "webChannel"
            if (emissora == null && node.has("webChannel") && !node.get("webChannel").isNull()) {
                emissora = textoOuNulo(node.get("webChannel"), "name");
            }
            s.setEmissora(emissora != null ? emissora : "N/A");

            // Resumo — o campo "summary" da API vem com tags HTML (<p>, <b> etc.),
            // então usamos uma expressão regular para remover qualquer tag antes de salvar.
            String resumo = textoOuNulo(node, "summary");
            s.setResumo(resumo != null ? resumo.replaceAll("<[^>]*>", "").trim() : "Sem resumo disponível.");

            return s;
        } catch (Exception e) {
            System.err.println("Erro ao montar Serie a partir do JsonNode: " + e.getMessage());
            return null;
        }
    }

    /**
     * Método auxiliar que extrai um campo de texto de um JsonNode de forma
     * segura, retornando null se o campo não existir, for JSON null,
     * ou for uma string vazia (evita valores "lixo" no objeto Serie).
     */
    private static String textoOuNulo(JsonNode node, String campo) {
        if (node.has(campo) && !node.get(campo).isNull()) {
            String valor = node.get(campo).asText().trim();
            return valor.isEmpty() ? null : valor;
        }
        return null;
    }

    /** Retorna o ObjectMapper compartilhado, caso outra classe precise usá-lo diretamente */
    public static ObjectMapper getMapper() {
        return mapper;
    }
}
