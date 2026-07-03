package br.escola.service;

import br.escola.model.Serie;
import br.escola.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

// Classe de serviço que centraliza todas as operações do usuário
// Faz a ponte entre as telas e os dados do usuário
public class UsuarioService {

    private Usuario usuario;
    private final PersistenciaService persistencia;

    // Construtor: cria o serviço de persistência e tenta carregar os dados salvos
    public UsuarioService() {
        this.persistencia = new PersistenciaService(new ObjectMapper());
        carregarOuCriar();
    }

    // Tenta carregar o usuário do arquivo JSON
    // Se não existir ou der erro, cria um usuário vazio
    private void carregarOuCriar() {
        try {
            Usuario carregado = persistencia.carregar();
            if (carregado != null) {
                this.usuario = carregado;
            } else {
                this.usuario = new Usuario();
            }
        } catch (IOException e) {
            this.usuario = new Usuario();
        }
    }

    // Salva os dados do usuário no arquivo JSON
    public void salvar() throws IOException {
        persistencia.salvar(usuario);
    }

    public Usuario getUsuario() { return usuario; }

    public void setNomeUsuario(String nome) { usuario.setNome(nome); }

    // Métodos de gerenciamento das listas — delegam para o Usuario
    public void adicionarFavorito(Serie serie) { usuario.adicionarFavorito(serie); }
    public void removerFavorito(Serie serie) { usuario.removerFavorito(serie); }

    public void adicionarAssistida(Serie serie) { usuario.adicionarAssistida(serie); }
    public void removerAssistida(Serie serie) { usuario.removerAssistida(serie); }

    public void adicionarQueroAssistir(Serie serie) { usuario.adicionarQueroAssistir(serie); }
    public void removerQueroAssistir(Serie serie) { usuario.removerQueroAssistir(serie); }

    // Métodos de ordenação — recebem a lista e ordenam pelo critério escolhido
    public void ordenarPorNome(List<Serie> lista) {
        lista.sort(Comparator.comparing(Serie::getNome, String.CASE_INSENSITIVE_ORDER));
    }

    public void ordenarPorNota(List<Serie> lista) {
        lista.sort(Comparator.comparingDouble(Serie::getNota).reversed());
    }

    public void ordenarPorStatus(List<Serie> lista) {
        lista.sort(Comparator.comparing(Serie::getStatus, String.CASE_INSENSITIVE_ORDER));
    }

    public void ordenarPorDataEstreia(List<Serie> lista) {
        lista.sort(Comparator.comparing(serie -> serie.getDataEstreia() == null ? "" : serie.getDataEstreia()));
    }
}