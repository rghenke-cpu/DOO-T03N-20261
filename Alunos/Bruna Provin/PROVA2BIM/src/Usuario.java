package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe de modelo (MVC) que representa o usuário logado no sistema.
 * Guarda o nome do usuário e as três listas de séries que ele administra:
 * favoritos, já assistidas e desejo assistir.
 *
 * Esta classe é serializada/desserializada inteira pelo Jackson para o
 * arquivo dados_usuario.json — ou seja, é o "banco de dados" do sistema.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario {

    // Nome ou apelido informado na tela de login
    private String nome;
    // Lista de séries marcadas como favoritas
    private List<Serie> favoritos;
    // Lista de séries marcadas como já assistidas
    private List<Serie> assistidas;
    // Lista de séries que o usuário deseja assistir no futuro
    private List<Serie> desejoAssistir;

    // Construtor padrão obrigatório para o Jackson desserializar o JSON.
    // Já inicializa as três listas vazias para evitar NullPointerException
    // caso o JSON salvo não tenha algum desses campos.
    public Usuario() {
        this.favoritos = new ArrayList<>();
        this.assistidas = new ArrayList<>();
        this.desejoAssistir = new ArrayList<>();
    }

    // Construtor usado ao criar um usuário novo (primeiro login), recebendo apenas o nome
    public Usuario(String nome) {
        this.nome = nome;
        this.favoritos = new ArrayList<>();
        this.assistidas = new ArrayList<>();
        this.desejoAssistir = new ArrayList<>();
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    // Getter "defensivo": se por algum motivo a lista vier nula (ex: JSON antigo
    // sem esse campo), recria uma lista vazia em vez de devolver null.
    public List<Serie> getFavoritos() {
        if (favoritos == null) favoritos = new ArrayList<>();
        return favoritos;
    }
    public void setFavoritos(List<Serie> favoritos) { this.favoritos = favoritos; }

    public List<Serie> getAssistidas() {
        if (assistidas == null) assistidas = new ArrayList<>();
        return assistidas;
    }
    public void setAssistidas(List<Serie> assistidas) { this.assistidas = assistidas; }

    public List<Serie> getDesejoAssistir() {
        if (desejoAssistir == null) desejoAssistir = new ArrayList<>();
        return desejoAssistir;
    }
    public void setDesejoAssistir(List<Serie> desejoAssistir) { this.desejoAssistir = desejoAssistir; }

    // ===================== MÉTODOS DE MANIPULAÇÃO DAS LISTAS =====================
    // Cada lista tem um par adicionar/remover. O adicionar verifica se a série
    // já não está na lista (usando o equals() por id definido em Serie) para
    // não duplicar. O remover usa removeIf comparando por id.

    public void adicionarFavorito(Serie serie) {
        if (!favoritos.contains(serie)) favoritos.add(serie);
    }
    public void removerFavorito(Serie serie) {
        favoritos.removeIf(s -> s.getId() == serie.getId());
    }

    public void adicionarAssistida(Serie serie) {
        if (!assistidas.contains(serie)) assistidas.add(serie);
    }
    public void removerAssistida(Serie serie) {
        assistidas.removeIf(s -> s.getId() == serie.getId());
    }

    public void adicionarDesejoAssistir(Serie serie) {
        if (!desejoAssistir.contains(serie)) desejoAssistir.add(serie);
    }
    public void removerDesejoAssistir(Serie serie) {
        desejoAssistir.removeIf(s -> s.getId() == serie.getId());
    }
}
