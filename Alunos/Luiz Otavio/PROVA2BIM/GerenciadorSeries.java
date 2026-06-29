package tvmanager.util;

import tvmanager.model.Serie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Centraliza as operações sobre as listas de séries:
 * favoritos, assistidas e quero assistir.
 * Delegam a persistência para PersistenciaJSON.
 */
public class GerenciadorSeries {

    public enum TipoLista { FAVORITOS, ASSISTIDAS, QUERO_ASSISTIR }

    public enum OrdemLista {
        NOME("Nome (A-Z)"),
        NOTA("Nota (maior primeiro)"),
        STATUS("Estado"),
        DATA_ESTREIA("Data de estreia");

        private final String label;
        OrdemLista(String label) { this.label = label; }

        @Override public String toString() { return label; }
    }

    private final PersistenciaJSON persistencia;

    public GerenciadorSeries(PersistenciaJSON persistencia) {
        this.persistencia = persistencia;
    }

    // --- Adicionar ---

    public void adicionar(TipoLista tipo, Serie s) {
        switch (tipo) {
            case FAVORITOS      -> persistencia.adicionarFavorito(s);
            case ASSISTIDAS     -> persistencia.adicionarAssistida(s);
            case QUERO_ASSISTIR -> persistencia.adicionarQueroAssistir(s);
        }
    }

    // --- Remover ---

    public void remover(TipoLista tipo, int id) {
        switch (tipo) {
            case FAVORITOS      -> persistencia.removerFavorito(id);
            case ASSISTIDAS     -> persistencia.removerAssistida(id);
            case QUERO_ASSISTIR -> persistencia.removerQueroAssistir(id);
        }
    }

    // --- Verificar presença ---

    public boolean contem(TipoLista tipo, int id) {
        return switch (tipo) {
            case FAVORITOS      -> persistencia.isFavorito(id);
            case ASSISTIDAS     -> persistencia.isAssistida(id);
            case QUERO_ASSISTIR -> persistencia.isQueroAssistir(id);
        };
    }

    // --- Listar com ordenação ---

    public List<Serie> listar(TipoLista tipo, OrdemLista ordem) {
        List<Serie> lista = new ArrayList<>(switch (tipo) {
            case FAVORITOS      -> persistencia.getFavoritos();
            case ASSISTIDAS     -> persistencia.getAssistidas();
            case QUERO_ASSISTIR -> persistencia.getQueroAssistir();
        });
        ordenar(lista, ordem);
        return lista;
    }

    // --- Ordenação ---

    private void ordenar(List<Serie> lista, OrdemLista ordem) {
        Comparator<Serie> comp = switch (ordem) {
            case NOME        -> Comparator.comparing(s -> s.getNome() != null ? s.getNome().toLowerCase() : "");
            case NOTA        -> Comparator.comparingDouble(Serie::getRating).reversed();
            case STATUS      -> Comparator.comparing(s -> s.getStatusExibicao() != null ? s.getStatusExibicao() : "");
            case DATA_ESTREIA -> Comparator.comparing(s -> s.getPremiada() != null ? s.getPremiada() : "");
        };
        lista.sort(comp);
    }
}
