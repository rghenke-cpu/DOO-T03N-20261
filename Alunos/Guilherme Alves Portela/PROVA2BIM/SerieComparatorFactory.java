import java.util.Comparator;

public class SerieComparatorFactory {

    public static final String ALFABETICA = "ALFABETICA";
    public static final String NOTA = "NOTA";
    public static final String ESTADO = "ESTADO";
    public static final String DATA_ESTREIA = "DATA_ESTREIA";

    public static Comparator<Serie> getComparator(String criterio) {
        if (criterio == null) {
            return Comparator.comparing(Serie::getNome, Comparator.nullsLast(String::compareToIgnoreCase));
        }

        switch (criterio.toUpperCase()) {
            case ALFABETICA:
                // Ordena por nome ignorando maiúsculas/minúsculas
                return Comparator.comparing(Serie::getNome, 
                        Comparator.nullsLast(String::compareToIgnoreCase));

            case NOTA:
                // Ordena da maior nota para a menor (reverso), tratando nulos
                return Comparator.comparing(Serie::getNotaGeral, 
                        Comparator.nullsLast(Comparator.reverseOrder()));

            case ESTADO:
                // Ordena pelo status da série (ex: Cancelada, Concluída, Transmitindo)
                return Comparator.comparing(Serie::getEstado, 
                        Comparator.nullsLast(String::compareToIgnoreCase));

            case DATA_ESTREIA:
                // Ordena pela string da data (formato YYYY-MM-DD permite ordenação direta por String)
                return Comparator.comparing(Serie::getDataEstreia, 
                        Comparator.nullsLast(String::compareTo));

            default:
                // Padrão de segurança: ordem alfabética
                return Comparator.comparing(Serie::getNome, 
                        Comparator.nullsLast(String::compareToIgnoreCase));
        }
    }
}