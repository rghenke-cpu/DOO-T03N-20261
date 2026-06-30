package fag.service;

import fag.model.Serie;
import fag.model.Usuario;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;

// adicionar, remover, exibir e ordenar séries.
public class SerieService {

    public static final String LISTA_FAVORITOS = "Favoritos";
    public static final String LISTA_JA_ASSISTIDAS = "Já assistidas";
    public static final String LISTA_DESEJO_ASSISTIR = "Desejo assistir";

    public static final String ORDENAR_ALFABETICA = "Ordem alfabética";
    public static final String ORDENAR_NOTA = "Nota geral";
    public static final String ORDENAR_ESTADO = "Estado";
    public static final String ORDENAR_ESTREIA = "Data de estreia";

    // Retorna a lista de acordo com a opção escolhida pelo usuário
    public ArrayList<Serie> obterListaPorTipo(Usuario usuario, String tipoLista) {
        usuario.garantirListasCriadas();

        if (LISTA_FAVORITOS.equals(tipoLista)) {
            return usuario.getFavoritos();
        }

        if (LISTA_JA_ASSISTIDAS.equals(tipoLista)) {
            return usuario.getJaAssistidas();
        }

        if (LISTA_DESEJO_ASSISTIR.equals(tipoLista)) {
            return usuario.getDesejoAssistir();
        }

        throw new IllegalArgumentException("Tipo de lista inválido: " + tipoLista);
    }

    // Adiciona uma série na lista escolhida
    public void adicionarSerie(Usuario usuario, Serie serie, String tipoLista) {
        if (serie == null) {
            throw new IllegalArgumentException("Nenhuma série foi selecionada.");
        }

        ArrayList<Serie> lista = obterListaPorTipo(usuario, tipoLista);

        // Impede que a mesma série seja adicionada duas vezes na mesma lista.
        if (lista.contains(serie)) {
            throw new IllegalArgumentException("Essa série já está na lista selecionada.");
        }

        lista.add(serie);
    }

    // Remove uma série da lista
    public void removerSerie(Usuario usuario, Serie serie, String tipoLista) {
        if (serie == null) {
            throw new IllegalArgumentException("Nenhuma série foi selecionada.");
        }

        ArrayList<Serie> lista = obterListaPorTipo(usuario, tipoLista);
        lista.remove(serie);
    }

    // Ordena a lista de séries
    public void ordenarLista(ArrayList<Serie> lista, String criterio) {
        if (lista == null) {
            return;
        }

        if (ORDENAR_ALFABETICA.equals(criterio)) {
            lista.sort(Comparator.comparing(
                    serie -> textoSeguro(serie.getNome()),
                    String.CASE_INSENSITIVE_ORDER
            ));
            return;
        }

        if (ORDENAR_NOTA.equals(criterio)) {
            // Ordena da maior nota para a menor nota.
            lista.sort(Comparator.comparingDouble(Serie::getNotaGeral).reversed());
            return;
        }

        if (ORDENAR_ESTADO.equals(criterio)) {
            lista.sort(Comparator.comparing(
                    serie -> textoSeguro(serie.getEstado()),
                    String.CASE_INSENSITIVE_ORDER
            ));
            return;
        }

        if (ORDENAR_ESTREIA.equals(criterio)) {
            lista.sort(Comparator.comparing(
                    serie -> converterData(serie.getDataEstreia())
            ));
        }
    }

    private String textoSeguro(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "zzzzzzzz";
        }

        return texto;
    }

    // Converte a data em LocalDate para permitir ordenar por data de estreia.
    private LocalDate converterData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return LocalDate.MAX;
        }

        try {
            return LocalDate.parse(data);
        } catch (DateTimeParseException e) {
            // Se a data vier vazia ou inválida da API, evita erro e coloca a série no final da ordenação.
            return LocalDate.MAX;
        }
    }
}