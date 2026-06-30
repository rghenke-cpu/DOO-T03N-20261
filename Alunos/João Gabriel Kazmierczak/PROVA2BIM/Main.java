package fag;

import fag.model.Usuario;
import fag.service.PersistenciaService;
import fag.util.EstiloUtil;
import fag.view.TelaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // tema escuro
                    EstiloUtil.configurarTemaGlobal();

                    // servico carrega e salva os dados
                    PersistenciaService persistenciaService = new PersistenciaService();

                    Usuario usuario = persistenciaService.carregarUsuario();

                    // verifica se tem usuario salvo no json, se nao cria um novo
                    if (usuario == null) {
                        String nome = JOptionPane.showInputDialog(
                                null,
                                "Digite seu nome ou apelido:",
                                "Cadastro do usuário",
                                JOptionPane.QUESTION_MESSAGE
                        );

                        if (nome == null || nome.trim().isEmpty()) {
                            nome = "Usuário";
                        }

                        usuario = persistenciaService.criarUsuarioComDadosPreCarregados(nome.trim());
                        persistenciaService.salvarUsuario(usuario);
                    }

                    TelaPrincipal telaPrincipal = new TelaPrincipal(usuario, persistenciaService);
                    telaPrincipal.setVisible(true);

                } catch (Exception e) {
                    // tratamento de exceção pra nao fechar do nada quando inicia
                    JOptionPane.showMessageDialog(
                            null,
                            "Erro ao iniciar o sistema:\n" + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }
}