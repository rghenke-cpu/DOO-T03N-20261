import java.util.List;

public class Usuario {
    private String nome;
    private List<Serie> favoritos;
    private List<Serie> assistidas;
    private List<Serie> desejaAssistir;

    // Getters e Setters

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public List<Serie> getFavoritos() {
        return favoritos;
    }
    public void setFavoritos(List<Serie> favoritos) {
        this.favoritos = favoritos;
    }
    public List<Serie> getAssistidas() {
        return assistidas;
    }
    public void setAssistidas(List<Serie> assistidas) {
        this.assistidas = assistidas;
    }
    public List<Serie> getDesejaAssistir() {
        return desejaAssistir;
    }
    public void setDesejaAssistir(List<Serie> desejaAssistir) {
        this.desejaAssistir = desejaAssistir;
    }

    
}
