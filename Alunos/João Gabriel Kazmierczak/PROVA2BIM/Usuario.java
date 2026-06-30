package fag.model;

import java.util.ArrayList;

public class Usuario {

    private String nome;
    private ArrayList<Serie> favoritos;
    private ArrayList<Serie> jaAssistidas;
    private ArrayList<Serie> desejoAssistir;

    public Usuario() {
        this.favoritos = new ArrayList<Serie>();
        this.jaAssistidas = new ArrayList<Serie>();
        this.desejoAssistir = new ArrayList<Serie>();
    }

    public Usuario(String nome) {
        this();
        this.nome = nome;
    }

    // Garante que as listas nunca fiquem nulas ao carregar dados do JSON.
    public void garantirListasCriadas() {
        if (favoritos == null) {
            favoritos = new ArrayList<Serie>();
        }

        if (jaAssistidas == null) {
            jaAssistidas = new ArrayList<Serie>();
        }

        if (desejoAssistir == null) {
            desejoAssistir = new ArrayList<Serie>();
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public ArrayList<Serie> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(ArrayList<Serie> favoritos) {
        this.favoritos = favoritos;
    }


    public ArrayList<Serie> getJaAssistidas() {
        return jaAssistidas;
    }

    public void setJaAssistidas(ArrayList<Serie> jaAssistidas) {
        this.jaAssistidas = jaAssistidas;
    }


    public ArrayList<Serie> getDesejoAssistir() {
        return desejoAssistir;
    }

    public void setDesejoAssistir(ArrayList<Serie> desejoAssistir) {
        this.desejoAssistir = desejoAssistir;
    }
}