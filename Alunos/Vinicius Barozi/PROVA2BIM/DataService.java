

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataService {
    private static final String DATA_FILE = "dados_series.json";
    private final JsonService jsonService = new JsonService();

    public void salvarDados(AppData dados) throws IOException {
        Path arquivo = Paths.get(DATA_FILE);
        Files.write(arquivo, jsonService.toJson(dados).getBytes(StandardCharsets.UTF_8));
    }

    public AppData carregarDados() {
        Path arquivo = Paths.get(DATA_FILE);
        if (!Files.exists(arquivo)) return new AppData();
        try {
            byte[] bytes = Files.readAllBytes(arquivo);
            String conteudo = new String(bytes, StandardCharsets.UTF_8);
            return jsonService.parseAppData(conteudo);
        } catch (IOException e) {
            return new AppData();
        }
    }
}
