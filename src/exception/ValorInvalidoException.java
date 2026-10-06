package exception;

public class ValorInvalidoException extends Exception {

    // Mensagem padrão: usada quando a exceção é lançada sem texto,
    // para a tela nunca mostrar "Erro: null"
    public ValorInvalidoException() {
        super("Valor inválido.");
    }

    public ValorInvalidoException(String mensagem) {
        super(mensagem);
    }
}