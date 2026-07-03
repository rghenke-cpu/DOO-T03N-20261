package br.escola.model;

import java.util.ArrayList;
import java.util.List;

// Representa o usuário do sistema com suas 3 listas de séries
public class Usuario {

    private String nome;
    private List<Serie> favoritos;
    private List<Serie> assistidas;
    private List<Serie> queroAssistir;

    // Construtor vazio obrigatório para o Jackson reconstruir o objeto a partir do JSON
    public Usuario() {
        this.favoritos = new ArrayList<>();
        this.assistidas = new ArrayList<>();
        this.queroAssistir = new ArrayList<>();
    }

    // Construtor usado quando o usuário é criado pela primeira vez com um nome
    public Usuario(String nome) {
        this.nome = nome;
        this.favoritos = new ArrayList<>();
        this.assistidas = new ArrayList<>();
        this.queroAssistir = new ArrayList<>();
    }

    // Getters e Setters necessários para o Jackson
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Serie> getFavoritos() { return favoritos; }
    public void setFavoritos(List<Serie> favoritos) { this.favoritos = favoritos; }

    public List<Serie> getAssistidas() { return assistidas; }
    public void setAssistidas(List<Serie> assistidas) { this.assistidas = assistidas; }

    public List<Serie> getQueroAssistir() { return queroAssistir; }
    public void setQueroAssistir(List<Serie> queroAssistir) { this.queroAssistir = queroAssistir; }

    // Métodos de manipulação das listas
    // O contains() usa o equals() da Serie para evitar duplicatas
    public void adicionarFavorito(Serie serie) {
        if (!favoritos.contains(serie)) favoritos.add(serie);
    }
    public void removerFavorito(Serie serie) { favoritos.remove(serie); }

    public void adicionarAssistida(Serie serie) {
        if (!assistidas.contains(serie)) assistidas.add(serie);
    }
    public void removerAssistida(Serie serie) { assistidas.remove(serie); }

    public void adicionarQueroAssistir(Serie serie) {
        if (!queroAssistir.contains(serie)) queroAssistir.add(serie);
    }
    public void removerQueroAssistir(Serie serie) { queroAssistir.remove(serie); }
}