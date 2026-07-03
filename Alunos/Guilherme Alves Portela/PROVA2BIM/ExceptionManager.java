public class ExceptionManager extends Exception {
    
    // Construtor para mensagens customizadas simples
    public ExceptionManager(String mensagem) {
        super(mensagem);
    }

    // Construtor que recebe a mensagem e a causa original (ex: IOException, HttpException)
    public ExceptionManager(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}