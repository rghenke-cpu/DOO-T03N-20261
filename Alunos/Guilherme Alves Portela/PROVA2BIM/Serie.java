import java.util.List;

public class Serie {
    private String nome;
    private String idioma;
    private List<String> generos;
    private Double notaGeral;
    private String estado;
    private String dataEstreia; 
    private String dataTermino;
    private String emissora;

    public Serie(String nome, String idioma, List<String> generos, Double notaGeral, String estado, String dataEstreia,
        String dataTermino, String emissora) {
            this.nome = nome;
            this.idioma = idioma;
            this.generos = generos;
            this.notaGeral = notaGeral;
            this.estado = estado;
            this.dataEstreia = dataEstreia;
            this.dataTermino = dataTermino;
            this.emissora = emissora;
        }
    
    // Getters e Setters

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getIdioma() {
        return idioma;
    }
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    public List<String> getGeneros() {
        return generos;
    }
    public void setGeneros(List<String> generos) {
        this.generos = generos;
    }
    public Double getNotaGeral() {
        return notaGeral;
    }
    public void setNotaGeral(Double notaGeral) {
        this.notaGeral = notaGeral;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getDataEstreia() {
        return dataEstreia;
    }
    public void setDataEstreia(String dataEstreia) {
        this.dataEstreia = dataEstreia;
    }
    public String getDataTermino() {
        return dataTermino;
    }
    public void setDataTermino(String dataTermino) {
        this.dataTermino = dataTermino;
    }
    public String getEmissora() {
        return emissora;
    }
    public void setEmissora(String emissora) {
        this.emissora = emissora;
    }

    
}
