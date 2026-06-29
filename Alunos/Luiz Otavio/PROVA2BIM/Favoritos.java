package tvmanager.util;

import tvmanager.model.Serie;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Favoritos {

    private static final String ARQUIVO = System.getProperty("user.home") + File.separator + "tvmanager_favoritos.dat";
    private final Map<Integer, Serie> mapa = new LinkedHashMap<>();

    public Favoritos() {
        carregar();
    }

    public void adicionar(Serie s) {
        mapa.put(s.getId(), s);
        salvar();
    }

    public void remover(int id) {
        mapa.remove(id);
        salvar();
    }

    public boolean contem(int id) {
        return mapa.containsKey(id);
    }

    public List<Serie> listar() {
        return new ArrayList<>(mapa.values());
    }

    private void salvar() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(ARQUIVO))) {
            // Salvar lista de dados simples: id|nome|imagemUrl|status|rating
            List<String[]> dados = new ArrayList<>();
            for (Serie s : mapa.values()) {
                dados.add(new String[]{
                    String.valueOf(s.getId()),
                    s.getNome(),
                    s.getImagemUrl() != null ? s.getImagemUrl() : "",
                    s.getStatus() != null ? s.getStatus() : "",
                    String.valueOf(s.getRating()),
                    s.getGeneros() != null ? s.getGeneros() : "",
                    s.getPremiada() != null ? s.getPremiada() : ""
                });
            }
            oos.writeObject(dados);
        } catch (IOException e) {
            System.err.println("Erro ao salvar favoritos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregar() {
        File f = new File(ARQUIVO);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            List<String[]> dados = (List<String[]>) ois.readObject();
            for (String[] d : dados) {
                Serie s = new Serie();
                s.setId(Integer.parseInt(d[0]));
                s.setNome(d[1]);
                s.setImagemUrl(d[2].isEmpty() ? null : d[2]);
                s.setStatus(d[3].isEmpty() ? null : d[3]);
                try { s.setRating(Double.parseDouble(d[4])); } catch (Exception ignored) {}
                if (d.length > 5) s.setGeneros(d[5].isEmpty() ? null : d[5]);
                if (d.length > 6) s.setPremiada(d[6].isEmpty() ? null : d[6]);
                mapa.put(s.getId(), s);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar favoritos: " + e.getMessage());
        }
    }
}
