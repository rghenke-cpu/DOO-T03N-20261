package view;

import controller.SistemaController;
import model.Serie;

import java.util.List;

/**
 * Tela da lista de séries favoritas.
 * Praticamente todo o comportamento (tabela, busca, ordenação, botões)
 * vem pronto da classe TelaListaBase; aqui só dizemos QUAL lista do
 * usuário mostrar e COMO remover um item dela.
 */
public class TelaFavoritos extends TelaListaBase {

    public TelaFavoritos(SistemaController controller, TelaPrincipal telaPrincipal) {
        super(controller, telaPrincipal);
    }

    /** Fonte dos dados desta tela: a lista de favoritos do usuário logado */
    @Override
    protected List<Serie> getListaSeries() {
        return controller.getUsuario().getFavoritos();
    }

    /** Palavra-base do título exibido no topo da tela (será traduzida por TelaListaBase) */
    @Override
    protected String getTituloBase() {
        return "Favoritos";
    }

    /** Como remover uma série especificamente da lista de favoritos */
    @Override
    protected void removerSerie(Serie serie) {
        controller.removerFavorito(serie);
    }
}
