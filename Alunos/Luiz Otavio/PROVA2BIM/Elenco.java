package tvmanager.model;

public class Elenco {
    private String nomeAtor;
    private String nomePersonagem;
    private String imagemUrl;

    public Elenco() {}

    public String getNomeAtor() { return nomeAtor; }
    public void setNomeAtor(String nomeAtor) { this.nomeAtor = nomeAtor; }

    public String getNomePersonagem() { return nomePersonagem; }
    public void setNomePersonagem(String nomePersonagem) { this.nomePersonagem = nomePersonagem; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
}
