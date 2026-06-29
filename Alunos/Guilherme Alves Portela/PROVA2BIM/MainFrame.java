public class MainFrame {
    IUsuarioService usuarioService;
    ITvApiClient tvApiClient;

    
    public MainFrame(IUsuarioService usuarioService, ITvApiClient tvApiClient) {
        this.usuarioService = usuarioService;
        this.tvApiClient = tvApiClient;
    }
}
