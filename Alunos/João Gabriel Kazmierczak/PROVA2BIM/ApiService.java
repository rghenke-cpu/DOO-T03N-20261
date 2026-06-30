package fag.service;

import fag.model.Serie;
import fag.util.JsonUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;

// Classe responsável por acessar a API do TVMaze e buscar séries pelo nome
public class ApiService {

    // Busca séries na API do TVMaze usando o nome digitado pelo usuário
    public ArrayList<Serie> buscarSeriesPorNome(String nomeBusca) throws Exception {
        // Validação para evitar busca vazia
        if (nomeBusca == null || nomeBusca.trim().isEmpty()) {
            throw new IllegalArgumentException("Digite o nome de uma série para buscar.");
        }

        String nomeCodificado = URLEncoder.encode(nomeBusca.trim(), "UTF-8");

        // Monta o endereço da API com o nome da série informado
        String endereco = "https://api.tvmaze.com/search/shows?q=" + nomeCodificado;

        URL url = new URL(endereco);

        // Abre a conexão HTTP com a API.
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

        conexao.setRequestMethod("GET");
        conexao.setConnectTimeout(15000);
        conexao.setReadTimeout(20000);

        int codigoResposta = conexao.getResponseCode();

        // Se a API retornar erro, lança uma exceção
        if (codigoResposta != 200) {
            throw new Exception("Erro ao buscar séries. Código HTTP: " + codigoResposta);
        }

        BufferedReader leitor = new BufferedReader(
                new InputStreamReader(conexao.getInputStream(), "UTF-8")
        );

        StringBuilder resposta = new StringBuilder();
        String linha;

        while ((linha = leitor.readLine()) != null) {
            resposta.append(linha);
        }

        leitor.close();
        conexao.disconnect();

        // Converte o JSON retornado pela API em uma lista de objetos Serie
        return JsonUtil.converterJsonTvMazeParaSeries(resposta.toString());
    }
}