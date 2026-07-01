public class UsuarioServiceImpl implements IUsuarioService {

    private Usuario usuarioAtual;
    private final IStorageService storageService; // Dependendo da abstração (SOLID)

    // O construtor recebe o mecanismo de persistência pronto (Injeção de Dependência)
    public UsuarioServiceImpl(IStorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void inicializarUsuario(String nome) {
        if (this.usuarioAtual == null) {
            this.usuarioAtual = new Usuario(nome);
        }
    }

    @Override
    public Usuario getUsuarioAtual() {
        return this.usuarioAtual;
    }

    // --- MANIPULAÇÃO DA LISTA: FAVORITOS ---

    @Override
    public void adicionarFavorito(Serie serie) throws ExceptionManager {
        // Validação de negócio: impede duplicados na mesma lista
        if (usuarioAtual.getFavoritos().contains(serie)) {
            throw new ExceptionManager("Esta série já está na sua lista de Favoritos.");
        }
        usuarioAtual.getFavoritos().add(serie);
        storageService.salvarDados(usuarioAtual); // Salva automaticamente no JSON
    }

    @Override
    public void removerFavorito(Serie serie) throws ExceptionManager {
        usuarioAtual.getFavoritos().remove(serie);
        storageService.salvarDados(usuarioAtual); // Salva as alterações
    }

    // --- MANIPULAÇÃO DA LISTA: ASSISTIDAS ---

    @Override
    public void adicionarAssistido(Serie serie) throws ExceptionManager {
        if (usuarioAtual.getAssistidas().contains(serie)) {
            throw new ExceptionManager("Esta série já está na sua lista de Assistidas.");
        }
        usuarioAtual.getAssistidas().add(serie);
        storageService.salvarDados(usuarioAtual);
    }

    @Override
    public void removerAssistido(Serie serie) throws ExceptionManager {
        usuarioAtual.getAssistidas().remove(serie);
        storageService.salvarDados(usuarioAtual);
    }

    // --- MANIPULAÇÃO DA LISTA: DESEJA ASSISTIR ---

    @Override
    public void adicionarDesejaAssistir(Serie serie) throws ExceptionManager {
        if (usuarioAtual.getDesejaAssistir().contains(serie)) {
            throw new ExceptionManager("Esta série já está na sua lista de Desejos.");
        }
        usuarioAtual.getDesejaAssistir().add(serie);
        storageService.salvarDados(usuarioAtual);
    }

    @Override
    public void removerDesejaAssistir(Serie serie) throws ExceptionManager {
        usuarioAtual.getDesejaAssistir().remove(serie);
        storageService.salvarDados(usuarioAtual);
    }

    // --- ORDENAÇÃO DAS LISTAS ---

    @Override
    public void ordenarListaFavoritos(String criterio) {
        usuarioAtual.getFavoritos().sort(SerieComparatorFactory.getComparator(criterio));
    }

    @Override
    public void ordenarListaAssistidos(String criterio) {
        usuarioAtual.getAssistidas().sort(SerieComparatorFactory.getComparator(criterio));
    }

    @Override
    public void ordenarListaDesejaAssistir(String criterio) {
        usuarioAtual.getDesejaAssistir().sort(SerieComparatorFactory.getComparator(criterio));
    }
}