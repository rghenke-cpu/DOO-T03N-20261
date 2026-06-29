package tvmanager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tvmanager.model.Serie;
import tvmanager.model.Usuario;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza toda a persistência do sistema em um único arquivo JSON.
 * Salva e carrega: usuário, favoritos, assistidas e quero assistir.
 */
public class PersistenciaJSON {

    private static final String ARQUIVO = System.getProperty("user.home")
            + File.separator + "tvmanager_dados.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Estrutura interna do JSON
    private static class Dados {
        Usuario usuario = new Usuario();
        List<Serie> favoritos = new ArrayList<>();
        List<Serie> assistidas = new ArrayList<>();
        List<Serie> queroAssistir = new ArrayList<>();
    }

    private Dados dados = new Dados();

    public PersistenciaJSON() {
        carregar();
    }

    // --- Usuário ---

    public Usuario getUsuario() { return dados.usuario; }

    public void salvarUsuario(Usuario u) {
        dados.usuario = u;
        salvar();
    }

    // --- Favoritos ---

    public List<Serie> getFavoritos() { return dados.favoritos; }

    public void adicionarFavorito(Serie s) {
        removerDaLista(dados.favoritos, s.getId());
        dados.favoritos.add(s);
        salvar();
    }

    public void removerFavorito(int id) {
        removerDaLista(dados.favoritos, id);
        salvar();
    }

    public boolean isFavorito(int id) { return contemNaLista(dados.favoritos, id); }

    // --- Assistidas ---

    public List<Serie> getAssistidas() { return dados.assistidas; }

    public void adicionarAssistida(Serie s) {
        removerDaLista(dados.assistidas, s.getId());
        dados.assistidas.add(s);
        salvar();
    }

    public void removerAssistida(int id) {
        removerDaLista(dados.assistidas, id);
        salvar();
    }

    public boolean isAssistida(int id) { return contemNaLista(dados.assistidas, id); }

    // --- Quero Assistir ---

    public List<Serie> getQueroAssistir() { return dados.queroAssistir; }

    public void adicionarQueroAssistir(Serie s) {
        removerDaLista(dados.queroAssistir, s.getId());
        dados.queroAssistir.add(s);
        salvar();
    }

    public void removerQueroAssistir(int id) {
        removerDaLista(dados.queroAssistir, id);
        salvar();
    }

    public boolean isQueroAssistir(int id) { return contemNaLista(dados.queroAssistir, id); }

    // --- I/O ---

    private void salvar() {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(ARQUIVO), StandardCharsets.UTF_8)) {
            GSON.toJson(dados, w);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    private void carregar() {
        File f = new File(ARQUIVO);
        if (!f.exists()) return;
        try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
            Dados carregado = GSON.fromJson(r, Dados.class);
            if (carregado != null) dados = carregado;
            // Garantir listas não nulas após deserialização
            if (dados.favoritos == null)     dados.favoritos = new ArrayList<>();
            if (dados.assistidas == null)    dados.assistidas = new ArrayList<>();
            if (dados.queroAssistir == null) dados.queroAssistir = new ArrayList<>();
            if (dados.usuario == null)       dados.usuario = new Usuario();
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    // --- Utilitários internos ---

    private void removerDaLista(List<Serie> lista, int id) {
        lista.removeIf(s -> s.getId() == id);
    }

    private boolean contemNaLista(List<Serie> lista, int id) {
        return lista.stream().anyMatch(s -> s.getId() == id);
    }
}
