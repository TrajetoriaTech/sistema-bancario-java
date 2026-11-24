package controller;

/**
 * Todas as telas do sistema: o arquivo .fxml de cada uma e o título do menu.
 * É um enum porque a lista de telas é fixa: o compilador só aceita
 * telas que existem aqui (Tela.PIX), nunca um nome digitado errado.
 */
public enum Tela {

    // Janelas inteiras (antes do login ou a moldura do sistema)
    LOGIN("login.fxml", "Entrar"),
    CADASTRO("cadastro.fxml", "Criar conta"),
    PRINCIPAL("principal.fxml", "Internet Banking"),

    // Telas que abrem dentro da área central da tela principal
    INICIO("inicio.fxml", "Início"),
    EXTRATO("extrato.fxml", "Saldo e extrato"),
    DEPOSITO("deposito.fxml", "Depositar"),
    SAQUE("saque.fxml", "Sacar"),
    TRANSFERENCIA("transferencia.fxml", "Transferir"),
    PIX("pix.fxml", "Pix"),
    BOLETO("boleto.fxml", "Pagar boleto"),
    INVESTIMENTOS("investimentos.fxml", "Investimentos"),
    EMPRESTIMO("emprestimo.fxml", "Empréstimo"),
    NOTIFICACOES("notificacoes.fxml", "Notificações"),
    CARTAO("cartao.fxml", "Cartão de crédito"),
    BLOQUEIO("bloqueio.fxml", "Bloquear conta"),
    PERFIL("perfil.fxml", "Perfil");

    private final String arquivo;
    private final String titulo;

    // Construtor de enum é sempre privado: só as constantes acima existem
    Tela(String arquivo, String titulo) {
        this.arquivo = arquivo;
        this.titulo = titulo;
    }

    public String getArquivo() {
        return arquivo;
    }

    public String getTitulo() {
        return titulo;
    }
}
