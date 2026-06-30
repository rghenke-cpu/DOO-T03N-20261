package fag.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fag.model.Serie;
import fag.model.Usuario;

import java.util.ArrayList;

// Classe utilitária responsável por converter objetos Java em JSON e JSON em objetos Java.
public class JsonUtil {

    // Cria o objeto Gson configurado para gerar JSON formatado e mais fácil de ler.
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Converte o objeto Usuario em texto JSON para salvar no arquivo.
    public static String converterUsuarioParaJson(Usuario usuario) {
        return gson.toJson(usuario);
    }

    // Converte o texto JSON salvo no arquivo novamente em um objeto Usuario.
    public static Usuario converterJsonParaUsuario(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        Usuario usuario = gson.fromJson(json, Usuario.class);

        if (usuario != null) {
            usuario.garantirListasCriadas();
        }

        return usuario;
    }

    // Interpreta o JSON retornado pela API do TVMaze e monta objetos Serie.
    public static ArrayList<Serie> converterJsonTvMazeParaSeries(String json) {
        ArrayList<Serie> series = new ArrayList<Serie>();

        if (json == null || json.trim().isEmpty()) {
            return series;
        }

        JsonElement elementoRaiz = JsonParser.parseString(json);

        if (!elementoRaiz.isJsonArray()) {
            return series;
        }

        JsonArray resultados = elementoRaiz.getAsJsonArray();

        // Percorre todos os resultados retornados pela API.
        for (JsonElement elemento : resultados) {
            if (!elemento.isJsonObject()) {
                continue;
            }

            JsonObject objetoResultado = elemento.getAsJsonObject();

            if (!objetoResultado.has("show") || objetoResultado.get("show").isJsonNull()) {
                continue;
            }

            JsonObject show = objetoResultado.getAsJsonObject("show");

            Serie serie = new Serie();

            serie.setNome(obterTexto(show, "name", "Sem nome"));
            serie.setIdioma(obterTexto(show, "language", "Não informado"));
            serie.setGeneros(obterGeneros(show));
            serie.setNotaGeral(obterNota(show));
            serie.setEstado(obterTexto(show, "status", "Não informado"));
            serie.setDataEstreia(obterTexto(show, "premiered", "Não informado"));
            serie.setDataTermino(obterTexto(show, "ended", "Não informado"));
            serie.setEmissora(obterEmissora(show));

            series.add(serie);
        }

        return series;
    }

    private static String obterTexto(JsonObject objeto, String campo, String valorPadrao) {
        if (objeto == null || !objeto.has(campo) || objeto.get(campo).isJsonNull()) {
            return valorPadrao;
        }

        return objeto.get(campo).getAsString();
    }

    private static ArrayList<String> obterGeneros(JsonObject show) {
        ArrayList<String> generos = new ArrayList<String>();

        if (show == null || !show.has("genres") || show.get("genres").isJsonNull()) {
            return generos;
        }

        JsonArray arrayGeneros = show.getAsJsonArray("genres");

        for (JsonElement genero : arrayGeneros) {
            if (!genero.isJsonNull()) {
                generos.add(genero.getAsString());
            }
        }

        return generos;
    }

    // Obtém a nota da série dentro do objeto "rating" retornado pela API.
    private static double obterNota(JsonObject show) {
        if (show == null || !show.has("rating") || show.get("rating").isJsonNull()) {
            return 0;
        }

        JsonObject rating = show.getAsJsonObject("rating");

        if (!rating.has("average") || rating.get("average").isJsonNull()) {
            return 0;
        }

        return rating.get("average").getAsDouble();
    }

    // Obtém a emissora da série
    private static String obterEmissora(JsonObject show) {
        JsonObject network = obterObjeto(show, "network");

        if (network != null) {
            String nomeNetwork = obterTexto(network, "name", "");

            if (!nomeNetwork.trim().isEmpty()) {
                return nomeNetwork;
            }
        }

        JsonObject webChannel = obterObjeto(show, "webChannel");

        if (webChannel != null) {
            String nomeWebChannel = obterTexto(webChannel, "name", "");

            if (!nomeWebChannel.trim().isEmpty()) {
                return nomeWebChannel;
            }
        }

        return "Não informado";
    }

    private static JsonObject obterObjeto(JsonObject objeto, String campo) {
        if (objeto == null || !objeto.has(campo) || objeto.get(campo).isJsonNull()) {
            return null;
        }

        if (!objeto.get(campo).isJsonObject()) {
            return null;
        }

        return objeto.getAsJsonObject(campo);
    }
}	