package exception;

public class OperacaoNaoPermitidaException extends Exception {

    public OperacaoNaoPermitidaException() {
        super();
    }

    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}