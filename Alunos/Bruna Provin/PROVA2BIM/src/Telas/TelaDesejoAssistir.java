package view;

import controller.SistemaController;
import model.Serie;

import java.util.List;

/**
 * Tela da lista de séries que o usuário deseja assistir no futuro.
 * Também reaproveita toda a lógica de TelaListaBase, apontando para a
 * lista "desejo assistir" do usuário.
 */
public class TelaDesejoAssistir extends TelaListaBase {

    public TelaDesejoAssistir(SistemaController controller, TelaPrincipal telaPrincipal) {
        super(controller, telaPrincipal);
    }

    /** Fonte dos dados desta tela: a lista "desejo assistir" do usuário */
    @Override
    protected List<Serie> getListaSeries() {
        return controller.getUsuario().getDesejoAssistir();
    }

    /** Palavra-base do título exibido no topo da tela (será traduzida por TelaListaBase) */
    @Override
    protected String getTituloBase() {
        return "Desejo Assistir";
    }

    /** Como remover uma série especificamente da lista de desejo assistir */
    @Override
    protected void removerSerie(Serie serie) {
        controller.removerDesejoAssistir(serie);
    }
}
