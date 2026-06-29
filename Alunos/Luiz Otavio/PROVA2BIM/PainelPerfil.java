package tvmanager.ui;

import tvmanager.model.Usuario;
import tvmanager.util.PersistenciaJSON;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tela de perfil do usuário: permite editar nome e apelido.
 */
public class PainelPerfil extends JPanel {

    private final PersistenciaJSON persistencia;
    private final JTextField campoNome;
    private final JTextField campoApelido;
    private Runnable onUsuarioAlterado;

    public PainelPerfil(PersistenciaJSON persistencia) {
        this.persistencia = persistencia;
        setBackground(Tema.BG_PRIMARIO);
        setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("Meu Perfil");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.TEXTO_PRIMARIO);
        titulo.setBorder(new EmptyBorder(28, 32, 16, 32));
        add(titulo, BorderLayout.NORTH);

        // Formulário centralizado
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Tema.BG_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDA),
            BorderFactory.createEmptyBorder(28, 32, 28, 32)
        ));
        form.setMaximumSize(new Dimension(440, 300));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        Usuario u = persistencia.getUsuario();

        campoNome = criarCampo("Nome completo", u.getNome());
        campoApelido = criarCampo("Apelido (exibido no sistema)", u.getApelido());

        JButton btnSalvar = Tema.botaoPrimario("Salvar perfil");
        btnSalvar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalvar.addActionListener(e -> salvar());

        JLabel lblMsg = new JLabel(" ");
        lblMsg.setFont(Tema.FONTE_PEQUENA);
        lblMsg.setForeground(Tema.VERDE);
        lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(rotulo("Nome completo"));
        form.add(Box.createVerticalStrut(4));
        form.add(campoNome);
        form.add(Box.createVerticalStrut(16));
        form.add(rotulo("Apelido (exibido no sistema)"));
        form.add(Box.createVerticalStrut(4));
        form.add(campoApelido);
        form.add(Box.createVerticalStrut(20));
        form.add(btnSalvar);
        form.add(Box.createVerticalStrut(8));
        form.add(lblMsg);

        btnSalvar.addActionListener(e2 -> lblMsg.setText("✔ Perfil salvo!"));

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 32, 0));
        wrapper.setBackground(Tema.BG_PRIMARIO);
        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);
    }

    public void setOnUsuarioAlterado(Runnable r) { this.onUsuarioAlterado = r; }

    private void salvar() {
        String nome = campoNome.getText().trim();
        String apelido = campoApelido.getText().trim();
        persistencia.salvarUsuario(new Usuario(nome, apelido));
        if (onUsuarioAlterado != null) onUsuarioAlterado.run();
    }

    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.FONTE_PEQUENA);
        l.setForeground(Tema.TEXTO_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField criarCampo(String placeholder, String valor) {
        JTextField f = new JTextField(valor != null ? valor : "");
        f.setFont(Tema.FONTE_CORPO);
        f.setBackground(Tema.BG_PRIMARIO);
        f.setForeground(Tema.TEXTO_PRIMARIO);
        f.setCaretColor(Tema.TEXTO_PRIMARIO);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDA),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }
}
