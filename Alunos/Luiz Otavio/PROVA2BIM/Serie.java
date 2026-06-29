package tvmanager.model;

/**
 * Representa uma série de TV com todos os dados exibidos no sistema.
 */
public class Serie {
    private int id;
    private String nome;
    private String sinopse;
    private String status;        // valor bruto da API: "Running", "Ended", "To Be Determined", etc.
    private String generos;
    private String imagemUrl;
    private String premiada;      // data de estreia (premiered)
    private String dataTermino;   // data de término (ended)
    private String idioma;        // language
    private String emissora;      // network name
    private double rating;
    private int totalTemporadas;

    public Serie() {}

    // --- Getters e Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGeneros() { return generos; }
    public void setGeneros(String generos) { this.generos = generos; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public String getPremiada() { return premiada; }
    public void setPremiada(String premiada) { this.premiada = premiada; }

    public String getDataTermino() { return dataTermino; }
    public void setDataTermino(String dataTermino) { this.dataTermino = dataTermino; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getEmissora() { return emissora; }
    public void setEmissora(String emissora) { this.emissora = emissora; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalTemporadas() { return totalTemporadas; }
    public void setTotalTemporadas(int totalTemporadas) { this.totalTemporadas = totalTemporadas; }

    /**
     * Retorna o status traduzido e normalizado para exibição e ordenação.
     */
    public String getStatusExibicao() {
        if (status == null) return "Desconhecido";
        return switch (status) {
            case "Running" -> "Transmitindo";
            case "Ended"   -> "Concluída";
            case "To Be Determined" -> "Indefinida";
            default -> status;
        };
    }

    @Override
    public String toString() { return nome != null ? nome : "Sem título"; }
}
