package util;

/**
 * Classe utilitária bem pequena que apenas "empresta" as constantes de
 * idioma já definidas em Tradutor.java, para quem quiser importar/usar
 * um nome de classe mais curto (Idioma.PT em vez de Tradutor.PT).
 * Não tem nenhuma lógica própria — é só um atalho de nomenclatura.
 */
public class Idioma {
    public static final String PT = Tradutor.PT; // Português
    public static final String EN = Tradutor.EN; // Inglês
    public static final String ES = Tradutor.ES; // Espanhol
}
