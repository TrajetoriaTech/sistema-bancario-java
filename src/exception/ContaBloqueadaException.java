package exception;

public class ContaBloqueadaException extends Exception {

    // Mensagem padrão: usada quando a exceção é lançada sem texto,
    // para a tela nunca mostrar "Erro: null"
    public ContaBloqueadaException() {
        super("Conta bloqueada. Desbloqueie a conta para fazer esta operação.");
    }

    public ContaBloqueadaException(String mensagem) {
        super(mensagem);
    }
}