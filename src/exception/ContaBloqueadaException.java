package exception;

public class ContaBloqueadaException extends Exception {

    public ContaBloqueadaException() {
        super();
    }

    public ContaBloqueadaException(String mensagem) {
        super(mensagem);
    }
}