package exception;

public class LimiteDiarioExcedidoException extends Exception {

    public LimiteDiarioExcedidoException() {
        super();
    }

    public LimiteDiarioExcedidoException(String mensagem) {
        super(mensagem);
    }
}