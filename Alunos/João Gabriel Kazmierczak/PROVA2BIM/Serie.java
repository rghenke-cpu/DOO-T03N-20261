package fag.model;

import java.util.ArrayList;
import java.util.Objects;

public class Serie {

    private String nome;
    private String idioma;
    private ArrayList<String> generos;
    private double notaGeral;
    private String estado;
    private String dataEstreia;
    private String dataTermino;
    private String emissora;

    public Serie() {
        this.generos = new ArrayList<String>();
    }

    public Serie(String nome, String idioma, ArrayList<String> generos, double notaGeral,
                 String estado, String dataEstreia, String dataTermino, String emissora) {
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos;
        this.notaGeral = notaGeral;
        this.estado = estado;
        this.dataEstreia = dataEstreia;
        this.dataTermino = dataTermino;
        this.emissora = emissora;
    }

    // Monta o texto com todos os detalhes da série pra mostrar na tela
    public String getDetalhesFormatados() {
        StringBuilder detalhes = new StringBuilder();

        detalhes.append("Nome: ").append(valorOuPadrao(nome)).append("\n");
        detalhes.append("Idioma: ").append(valorOuPadrao(idioma)).append("\n");

        if (generos == null || generos.isEmpty()) {
            detalhes.append("Gêneros: Não informado\n");
        } else {
            detalhes.append("Gêneros: ").append(String.join(", ", generos)).append("\n");
        }

        if (notaGeral > 0) {
            detalhes.append("Nota geral: ").append(notaGeral).append("\n");
        } else {
            detalhes.append("Nota geral: Não informada\n");
        }

        detalhes.append("Estado: ").append(valorOuPadrao(estado)).append("\n");
        detalhes.append("Data de estreia: ").append(valorOuPadrao(dataEstreia)).append("\n");
        detalhes.append("Data de término: ").append(valorOuPadrao(dataTermino)).append("\n");
        detalhes.append("Emissora: ").append(valorOuPadrao(emissora)).append("\n");

        return detalhes.toString();
    }

    // Evita exibir valores nulos ou vazios na interface.
    private String valorOuPadrao(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "Não informado";
        }
        return valor;
    }

    // Define como a série aparece dentro dos componentes JList.
    @Override
    public String toString() {
        if (nome == null || nome.trim().isEmpty()) {
            return "Série sem nome";
        }
        return nome;
    }

    // Evita série duplicada na mesma lista usando o nome como comparação.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Serie)) {
            return false;
        }

        Serie outraSerie = (Serie) obj;

        if (this.nome == null || outraSerie.nome == null) {
            return false;
        }

        return this.nome.equalsIgnoreCase(outraSerie.nome);
    }

    @Override
    public int hashCode() {
        if (nome == null) {
            return Objects.hash("");
        }

        return Objects.hash(nome.toLowerCase());
    }

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


    public ArrayList<String> getGeneros() {
        return generos;
    }

    public void setGeneros(ArrayList<String> generos) {
        this.generos = generos;
    }


    public double getNotaGeral() {
        return notaGeral;
    }

    public void setNotaGeral(double notaGeral) {
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