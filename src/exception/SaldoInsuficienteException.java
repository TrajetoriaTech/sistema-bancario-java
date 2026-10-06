package exception;

public class SaldoInsuficienteException extends Exception {

    // Mensagem padrão: usada quando a exceção é lançada sem texto,
    // para a tela nunca mostrar "Erro: null"
    public SaldoInsuficienteException() {
        super("Saldo insuficiente.");
    }

    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}