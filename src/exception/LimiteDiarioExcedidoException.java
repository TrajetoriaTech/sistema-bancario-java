package exception;

public class LimiteDiarioExcedidoException extends Exception {

    // Mensagem padrão: usada quando a exceção é lançada sem texto,
    // para a tela nunca mostrar "Erro: null"
    public LimiteDiarioExcedidoException() {
        super("Limite diário de saídas excedido.");
    }

    public LimiteDiarioExcedidoException(String mensagem) {
        super(mensagem);
    }
}