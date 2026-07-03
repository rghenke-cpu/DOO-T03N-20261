package util;

import javax.swing.*;
import java.awt.*;

/**
 * Utilitário para exibição de mensagens padronizadas ao usuário (pop-ups).
 * Centraliza o uso de JOptionPane para manter um visual/comportamento
 * consistente em todo o sistema, em vez de cada tela montar seu próprio
 * JOptionPane.showMessageDialog manualmente.
 *
 * Os títulos das janelas ("Erro", "Aviso", etc.) e as mensagens fixas
 * passam pelo Tradutor.getCached() para respeitar o idioma escolhido
 * pelo usuário, assim como o resto da interface.
 */
public class MensagensUtil {

    // Cores do tema escuro (atualmente não usadas diretamente pelo JOptionPane
    // padrão do Swing, mas ficam definidas aqui para uso futuro/customização)
    private static final Color COR_FUNDO = new Color(30, 30, 47);
    private static final Color COR_TEXTO = new Color(220, 220, 240);

    /** Atalho interno para buscar o texto já traduzido em cache (ver Tradutor.java) */
    private static String t(String texto) {
        return Tradutor.getCached(texto);
    }

    /** Exibe uma janela de mensagem de erro (ícone vermelho) ao usuário */
    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai,
            mensagem,
            t("Erro"),
            JOptionPane.ERROR_MESSAGE);
    }

    /** Exibe uma janela de aviso (ícone amarelo) ao usuário */
    public static void aviso(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai,
            mensagem,
            t("Aviso"),
            JOptionPane.WARNING_MESSAGE);
    }

    /** Exibe uma janela de informação (ícone "i") ao usuário */
    public static void info(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai,
            mensagem,
            t("Informação"),
            JOptionPane.INFORMATION_MESSAGE);
    }

    /** Exibe uma janela de sucesso (usa o mesmo ícone de informação) ao usuário */
    public static void sucesso(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai,
            mensagem,
            t("Sucesso"),
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Solicita uma confirmação Sim/Não do usuário.
     *
     * @return true se o usuário clicou em "Sim", false caso contrário (Não ou fechou a janela)
     */
    public static boolean confirmar(Component pai, String mensagem) {
        int result = JOptionPane.showConfirmDialog(pai,
            mensagem,
            t("Confirmação"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    // ==================== MENSAGENS DE ERRO PRÉ-PRONTAS ====================
    // Atalhos para os erros mais comuns do sistema, para não repetir o
    // mesmo texto em vários lugares do código.

    /** Mensagem exibida quando não há conexão com a internet */
    public static void erroSemInternet(Component pai) {
        erro(pai, t("Sem conexão com a internet.\nVerifique sua conexão e tente novamente."));
    }

    /** Mensagem exibida quando a API TVMaze não responde corretamente */
    public static void erroApi(Component pai) {
        erro(pai, t("A API TVMaze está indisponível no momento.\nTente novamente mais tarde."));
    }

    /** Mensagem exibida quando uma busca de série não retorna nenhum resultado */
    public static void erroBuscaVazia(Component pai) {
        aviso(pai, t("Nenhuma série encontrada para o termo buscado.\nTente outro nome."));
    }

    /**
     * Mensagem exibida quando um campo obrigatório foi deixado em branco.
     * O nome do campo é montado entre duas partes traduzidas separadamente,
     * já que o nome do campo em si (ex: "Pesquisar Série") é dinâmico e
     * traduzido antes de chegar aqui.
     */
    public static void erroCampoVazio(Component pai, String campo) {
        aviso(pai, t("O campo '") + campo + t("' não pode estar vazio."));
    }
}
