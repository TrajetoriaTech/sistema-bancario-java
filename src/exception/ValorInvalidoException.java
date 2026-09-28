package exception;

public class ValorInvalidoException extends Exception {

    public ValorInvalidoException() {
        super();
    }

    public ValorInvalidoException(String mensagem) {
        super(mensagem);
    }
}