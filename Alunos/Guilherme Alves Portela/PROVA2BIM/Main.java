import java.util.List;
import java.util.Scanner;

import javax.swing.SwingUtilities;

    public class Main {
        public static void main(String[] args) {
        // Mantém a mesma injeção de dependência que você já fez!
        IStorageService storageService = new JsonStorageServiceImpl();
        IUsuarioService usuarioService = new UsuarioServiceImpl(storageService);
        ITvApiClient tvApiClient = new TvMazeApiClientImpl();

        // Carrega dados se existirem
        try {
            Usuario salvo = storageService.carregarDados();
            if (salvo != null) {
                usuarioService.inicializarUsuario(salvo.getNome());
                usuarioService.getUsuarioAtual().setFavoritos(salvo.getFavoritos());
                usuarioService.getUsuarioAtual().setAssistidas(salvo.getAssistidas());
                usuarioService.getUsuarioAtual().setDesejaAssistir(salvo.getDesejaAssistir());
            }
        } catch (ExceptionManager e) { }

        // Inicia a Interface Gráfica
        SwingUtilities.invokeLater(() -> {
            new MainFrame(usuarioService, tvApiClient).setVisible(true);
        });
    }
    
    public static void modoTeste(){
        // 1. Inicialização e Injeção de Dependências (SOLID)
        IStorageService storageService = new JsonStorageServiceImpl();
        IUsuarioService usuarioService = new UsuarioServiceImpl(storageService);
        ITvApiClient tvApiClient = new TvMazeApiClientImpl();

        Scanner scanner = new Scanner(System.in);
        
        System.out.println("====================================================");
        System.out.println("   SISTEMA DE GESTÃO DE SÉRIES - MODO TESTE TOTAL   ");
        System.out.println("====================================================");

        // 2. Carga Inicial de Dados
        try {
            Usuario salvo = storageService.carregarDados();
            if (salvo != null) {
                System.out.println("-> Dados carregados com sucesso!");
                usuarioService.inicializarUsuario(salvo.getNome());
                // Restaura as listas do objeto salvo para o serviço atual
                usuarioService.getUsuarioAtual().setFavoritos(salvo.getFavoritos());
                usuarioService.getUsuarioAtual().setAssistidas(salvo.getAssistidas());
                usuarioService.getUsuarioAtual().setDesejaAssistir(salvo.getDesejaAssistir());
            } else {
                System.out.print("Primeiro acesso! Digite seu apelido: ");
                usuarioService.inicializarUsuario(scanner.nextLine());
            }
        } catch (ExceptionManager e) {
            System.out.println("[ALERTA] " + e.getMessage());
        }

        boolean executando = true;
        List<Serie> ultimaBusca = null;

        // 3. Loop Principal com Proteção de Exceção
        while (executando) {
            try {
                System.out.println("\n----------------------------------------------------");
                System.out.println("USUÁRIO: " + usuarioService.getUsuarioAtual().getNome());
                System.out.println("1. Pesquisar Séries (API)");
                System.out.println("2. Ver Detalhes e Adicionar à uma Lista");
                System.out.println("3. Ver Minhas Listas e Ordenar");
                System.out.println("4. Remover Série de uma Lista");
                System.out.println("5. Sair");
                System.out.print("Escolha: ");

                String opcao = scanner.nextLine();

                switch (opcao) {
                    case "1":
                        System.out.print("Digite o nome da série: ");
                        ultimaBusca = tvApiClient.buscarSeriesPorNome(scanner.nextLine());
                        exibirResultadosBusca(ultimaBusca);
                        break;

                    case "2":
                        if (ultimaBusca == null || ultimaBusca.isEmpty()) {
                            throw new ExceptionManager("Faça uma busca primeiro!");
                        }
                        System.out.print("Índice da série para detalhes/adicionar: ");
                        int idx = Integer.parseInt(scanner.nextLine());
                        Serie escolhida = ultimaBusca.get(idx);
                        
                        // Exibe todos os detalhes exigidos na prova
                        exibirDetalhesSerie(escolhida);
                        
                        System.out.println("\nAdicionar à qual lista?");
                        System.out.println("[1] Favoritos | [2] Já Assistidas | [3] Deseja Assistir | [0] Cancelar");
                        String listaOp = scanner.nextLine();
                        
                        if (listaOp.equals("1")) usuarioService.adicionarFavorito(escolhida);
                        else if (listaOp.equals("2")) usuarioService.adicionarAssistido(escolhida);
                        else if (listaOp.equals("3")) usuarioService.adicionarDesejaAssistir(escolhida);
                        
                        System.out.println("Operação realizada!");
                        break;

                    case "3":
                        exibirEManipularListas(usuarioService, scanner);
                        break;

                    case "4":
                        removerSerieFluxo(usuarioService, scanner);
                        break;

                    case "5":
                        System.out.println("Salvando dados e saindo...");
                        executando = false;
                        break;

                    default:
                        System.out.println("Opção inválida.");
                }

            } catch (ExceptionManager e) {
                // Aqui o sistema captura o erro e NÃO para o loop
                System.err.println("\n>>> AVISO DO SISTEMA: " + e.getMessage());
            } catch (Exception e) {
                // Captura erros genéricos (ex: digitar letra onde pede número)
                System.err.println("\n>>> ERRO INESPERADO: Verifique os dados digitados.");
            }
        }
        scanner.close();
    }

    // --- MÉTODOS AUXILIARES PARA ORGANIZAR O TESTE ---

    private static void exibirResultadosBusca(List<Serie> lista) {
        System.out.println("\n--- RESULTADOS ENCONTRADOS ---");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("[" + i + "] " + lista.get(i).getNome() + " (" + lista.get(i).getEmissora() + ")");
        }
    }

    private static String formatarData(String data) {
        if (data == null || data.isBlank() || data.equals("N/A")) {
            return "N/A";
        }

        try {
            return java.time.LocalDate.parse(data).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return data;
        }
    }

    private static void exibirDetalhesSerie(Serie s) {
        System.out.println("\n=== DETALHES DA SÉRIE ===");
        System.out.println("Nome: " + s.getNome());
        System.out.println("Idioma: " + s.getIdioma());
        System.out.println("Gêneros: " + s.getGeneros());
        System.out.println("Nota: " + s.getNotaGeral());
        System.out.println("Estado: " + s.getEstado());
        System.out.println("Estreia: " + formatarData(s.getDataEstreia()) + " | Término: " + formatarData(s.getDataTermino()));
        System.out.println("Emissora: " + s.getEmissora());
    }

    private static void exibirEManipularListas(IUsuarioService service, Scanner sc) {
        Usuario u = service.getUsuarioAtual();
        System.out.println("\n[1] Favoritos (" + u.getFavoritos().size() + ")");
        System.out.println("[2] Já Assistidas (" + u.getAssistidas().size() + ")");
        System.out.println("[3] Deseja Assistir (" + u.getDesejaAssistir().size() + ")");
        System.out.print("Escolha uma lista para ver e ordenar (ou 0): ");
        
        String op = sc.nextLine();
        List<Serie> listaAlvo = null;
        
        if (op.equals("1")) listaAlvo = u.getFavoritos();
        else if (op.equals("2")) listaAlvo = u.getAssistidas();
        else if (op.equals("3")) listaAlvo = u.getDesejaAssistir();
        
        if (listaAlvo != null) {
            System.out.println("\n--- CONTEÚDO DA LISTA ---");
            listaAlvo.forEach(s -> System.out.println("- " + s.getNome() + " | Nota: " + s.getNotaGeral() + " | Status: " + s.getEstado()));
            
            System.out.println("\nOrdenar por:");
            System.out.println("[A] Alfabética | [N] Nota | [E] Estado | [D] Data Estreia");
            String sort = sc.nextLine().toUpperCase();
            
            String crit = switch (sort) {
                case "A" -> SerieComparatorFactory.ALFABETICA;
                case "N" -> SerieComparatorFactory.NOTA;
                case "E" -> SerieComparatorFactory.ESTADO;
                case "D" -> SerieComparatorFactory.DATA_ESTREIA;
                default -> null;
            };
            
            if (crit != null) {
                if (op.equals("1")) service.ordenarListaFavoritos(crit);
                else if (op.equals("2")) service.ordenarListaAssistidos(crit);
                else if (op.equals("3")) service.ordenarListaDesejaAssistir(crit);
                System.out.println("Lista ordenada!");
            }
        }
    }

    private static void removerSerieFluxo(IUsuarioService service, Scanner sc) throws ExceptionManager {
        System.out.println("Remover de qual lista? [1] Favoritos | [2] Assistidas | [3] Desejos");
        String op = sc.nextLine();
        System.out.print("Digite o nome EXATO da série para remover: ");
        String nome = sc.nextLine();
        
        Serie temp = new Serie(); // Usando objeto temporário para busca
        temp.setNome(nome);
        
        if (op.equals("1")) service.removerFavorito(temp);
        else if (op.equals("2")) service.removerAssistido(temp);
        else if (op.equals("3")) service.removerDesejaAssistir(temp);
        
        System.out.println("Remoção concluída!");
    }
}