import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Theme.apply();

        SwingUtilities.invokeLater(() -> {
            PersistenceService persistence = new PersistenceService();
            UserData userData;

            try {
                userData = persistence.load();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erro ao carregar dados: " + e.getMessage() + "\nIniciando com dados vazios.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                userData = new UserData();
            }

            if (!userData.getProfile().isConfigured()) {
                SetupDialog setup = new SetupDialog(null);
                setup.setVisible(true);

                String name = setup.getUsername();
                if (name == null || name.isEmpty()) {
                    System.exit(0);
                }
                userData.setProfile(new UserProfile(name));
                new PreloadedDataService().populate(userData);

                try {
                    persistence.save(userData);
                } catch (Exception e) {
                    // Segue sem salvar, tentará novamente ao fechar
                }
            }

            MainWindow window = new MainWindow(userData, persistence);
            window.setVisible(true);
        });
    }
}
