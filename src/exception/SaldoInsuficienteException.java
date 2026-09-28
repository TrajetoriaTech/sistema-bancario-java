package exception;

public class SaldoInsuficienteException extends Exception {

    public SaldoInsuficienteException() {
        super();
    }

    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}