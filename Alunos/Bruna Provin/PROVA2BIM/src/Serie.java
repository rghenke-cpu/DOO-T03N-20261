package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Classe de modelo (MVC) que representa uma série de TV.
 * Guarda tanto os dados vindos da API TVMaze quanto os dados
 * que o usuário salva localmente (favoritos, assistidas, etc.).
 *
 * @JsonIgnoreProperties(ignoreUnknown = true): diz para o Jackson que,
 * se o JSON tiver campos que não existem aqui como atributo, ele deve
 * simplesmente ignorá-los em vez de lançar erro. Isso protege o programa
 * de quebrar caso a API TVMaze mude ou adicione novos campos no futuro.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Serie {

    // Identificador único da série na API TVMaze (usado para não duplicar séries nas listas)
    private int id;
    // Nome/título da série
    private String nome;
    // Idioma original da série (ex: "English", "German")
    private String idioma;
    // Gêneros da série, já concatenados em uma única String (ex: "Drama, Crime")
    private String generos;
    // Nota média da série (0 a 10)
    private double nota;
    // Status atual: "Running" (em exibição), "Ended" (encerrada), "Canceled" etc.
    private String status;
    // Data de estreia no formato "AAAA-MM-DD"
    private String dataEstreia;
    // Data de término no formato "AAAA-MM-DD" (pode ser nula se ainda estiver rodando)
    private String dataTermino;
    // Nome da emissora/canal ou plataforma de streaming
    private String emissora;
    // Sinopse/resumo da série (sem tags HTML)
    private String resumo;

    // Construtor vazio: obrigatório para o Jackson conseguir criar o objeto
    // ao desserializar um JSON (ele instancia vazio e depois usa os setters)
    public Serie() {}

    // Construtor completo, usado quando montamos uma Serie manualmente no código
    public Serie(int id, String nome, String idioma, String generos, double nota,
                 String status, String dataEstreia, String dataTermino,
                 String emissora, String resumo) {
        this.id = id;
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos;
        this.nota = nota;
        this.status = status;
        this.dataEstreia = dataEstreia;
        this.dataTermino = dataTermino;
        this.emissora = emissora;
        this.resumo = resumo;
    }

    // ===================== GETTERS E SETTERS =====================
    // O Jackson usa esses métodos (padrão JavaBeans) para ler e escrever
    // os campos automaticamente durante a serialização/desserialização JSON.

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getGeneros() { return generos; }
    public void setGeneros(String generos) { this.generos = generos; }

    public double getNota() { return nota; }
    public void setNota(double nota) { this.nota = nota; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataEstreia() { return dataEstreia; }
    public void setDataEstreia(String dataEstreia) { this.dataEstreia = dataEstreia; }

    public String getDataTermino() { return dataTermino; }
    public void setDataTermino(String dataTermino) { this.dataTermino = dataTermino; }

    public String getEmissora() { return emissora; }
    public void setEmissora(String emissora) { this.emissora = emissora; }

    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }

    // toString: usado sempre que o objeto precisa virar texto (ex: em componentes
    // Swing como JComboBox). Aqui simplesmente mostramos o nome da série.
    @Override
    public String toString() { return nome; }

    // equals: duas séries são consideradas "iguais" se tiverem o mesmo id.
    // Isso é essencial para métodos como list.contains(serie) e removeIf(...)
    // funcionarem corretamente nas listas de favoritos/assistidas/desejo assistir.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // mesma referência de objeto
        if (!(obj instanceof Serie)) return false; // não é nem uma Serie
        return this.id == ((Serie) obj).id; // compara pelo id
    }

    // hashCode: precisa ser consistente com equals(). Como a igualdade é
    // baseada no id, o hash também é baseado nele.
    @Override
    public int hashCode() { return Integer.hashCode(id); }
}
