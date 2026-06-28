package tvmanager.model;

public class Episodio {
    private int id;
    private String nome;
    private int temporada;
    private int numero;
    private String sinopse;
    private String dataAr;
    private int duracao;
    private double rating;
    private String imagemUrl;

    public Episodio() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getTemporada() { return temporada; }
    public void setTemporada(int temporada) { this.temporada = temporada; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }

    public String getDataAr() { return dataAr; }
    public void setDataAr(String dataAr) { this.dataAr = dataAr; }

    public int getDuracao() { return duracao; }
    public void setDuracao(int duracao) { this.duracao = duracao; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    @Override
    public String toString() {
        return String.format("S%02dE%02d - %s", temporada, numero, nome);
    }
}
