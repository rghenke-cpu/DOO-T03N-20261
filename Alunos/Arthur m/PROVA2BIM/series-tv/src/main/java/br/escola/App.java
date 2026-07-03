package br.escola;

import br.escola.service.UsuarioService;
import br.escola.ui.TelaPrincipal;

import javax.swing.*;

// Ponto de entrada do sistema
public class App {

    public static void main(String[] args) {

        // SwingUtilities.invokeLater: garante que a interface gráfica seja criada
        // na thread correta do Swing (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {

            // UsuarioService já carrega os dados do JSON automaticamente no construtor
            UsuarioService usuarioService = new UsuarioService();

            // Se for a primeira vez (sem nome salvo), pede o nome do usuário
            if (usuarioService.getUsuario().getNome() == null ||
                    usuarioService.getUsuario().getNome().isEmpty()) {

                String nome = null;

                while (nome == null || nome.trim().isEmpty()) {
                    nome = JOptionPane.showInputDialog(
                            null,
                            "Bem-vindo!\nDigite seu nome:",
                            "Primeiro acesso",
                            JOptionPane.QUESTION_MESSAGE
                    );
                }

                usuarioService.setNomeUsuario(nome.trim());
            }

            // Abre a tela principal passando o serviço do usuário
            TelaPrincipal tela = new TelaPrincipal(usuarioService);
            tela.setVisible(true);
        });
    }
}