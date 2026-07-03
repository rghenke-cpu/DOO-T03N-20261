package view;

import controller.SistemaController;
import model.Serie;

import java.util.List;

/**
 * Tela da lista de séries já assistidas.
 * Assim como TelaFavoritos, reaproveita toda a lógica de TelaListaBase,
 * apenas apontando para a lista de "assistidas" do usuário.
 */
public class TelaAssistidas extends TelaListaBase {

    public TelaAssistidas(SistemaController controller, TelaPrincipal telaPrincipal) {
        super(controller, telaPrincipal);
    }

    /** Fonte dos dados desta tela: a lista de séries já assistidas do usuário */
    @Override
    protected List<Serie> getListaSeries() {
        return controller.getUsuario().getAssistidas();
    }

    /** Palavra-base do título exibido no topo da tela (será traduzida por TelaListaBase) */
    @Override
    protected String getTituloBase() {
        return "Já Assistidas";
    }

    /** Como remover uma série especificamente da lista de assistidas */
    @Override
    protected void removerSerie(Serie serie) {
        controller.removerAssistida(serie);
    }
}
