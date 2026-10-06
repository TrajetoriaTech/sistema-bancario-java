package exception;

public class OperacaoNaoPermitidaException extends Exception {

    // Mensagem padrão: usada quando a exceção é lançada sem texto,
    // para a tela nunca mostrar "Erro: null"
    public OperacaoNaoPermitidaException() {
        super("Operação não permitida.");
    }

    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}