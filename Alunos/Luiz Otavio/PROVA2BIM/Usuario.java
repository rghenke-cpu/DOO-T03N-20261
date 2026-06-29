package tvmanager.model;

/**
 * Armazena as informações do usuário do sistema.
 * Persistida em JSON pelo PersistenciaJSON.
 */
public class Usuario {
    private String nome;
    private String apelido;

    public Usuario() {
        this.nome = "";
        this.apelido = "";
    }

    public Usuario(String nome, String apelido) {
        this.nome = nome != null ? nome : "";
        this.apelido = apelido != null ? apelido : "";
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome != null ? nome : ""; }

    public String getApelido() { return apelido; }
    public void setApelido(String apelido) { this.apelido = apelido != null ? apelido : ""; }

    /** Retorna apelido se preenchido, caso contrário o nome. */
    public String getNomeExibicao() {
        if (apelido != null && !apelido.isBlank()) return apelido;
        if (nome != null && !nome.isBlank()) return nome;
        return "Usuário";
    }
}
