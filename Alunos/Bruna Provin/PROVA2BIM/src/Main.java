// Classe sem "package": fica no pacote padrão (raiz do projeto).
import controller.SistemaController;

import javax.swing.*;

/**
 * Ponto de entrada do sistema TV Tracker.
 * É esta classe que a JVM executa primeiro (método main).
 * Aqui apenas configuramos a aparência visual (Look and Feel) da interface
 * e delegamos toda a inicialização real para o SistemaController.
 */
public class Main {

    public static void main(String[] args) {
        // Configura a aparência do sistema (tema Nimbus, se disponível)
        configurarLookAndFeel();

        // Cria o controller principal, responsável por orquestrar todo o app,
        // e manda ele iniciar (isso abre a tela de login)
        SistemaController controller = new SistemaController();
        controller.iniciar();
    }

    /**
     * Tenta usar o Look and Feel "Nimbus" (visual mais moderno que o padrão do Swing).
     * Se não conseguir por algum motivo, cai para o Look and Feel padrão multiplataforma.
     * Também liga o antialiasing das fontes para o texto ficar mais suave na tela.
     */
    private static void configurarLookAndFeel() {
        try {
            // Percorre todos os Look and Feels instalados no sistema
            // procurando pelo "Nimbus" especificamente
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break; // achou, pode parar de procurar
                }
            }
        } catch (Exception e) {
            // Se o Nimbus não existir ou der erro ao aplicar, usa o LookAndFeel padrão
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                // Se nem isso funcionar, apenas avisa no console e segue com o visual padrão do SO
                System.err.println("Não foi possível configurar o Look and Feel: " + ex.getMessage());
            }
        }

        // Propriedades do sistema que pedem para o Java suavizar (antialiasing)
        // o desenho das fontes de texto em toda a aplicação Swing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }
}
