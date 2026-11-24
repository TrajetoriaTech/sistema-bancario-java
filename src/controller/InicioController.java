package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Cliente;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.Emprestimo;

/**
 * Controller do Início (inicio.fxml): só mostra o resumo da conta.
 * Não altera nada, então não precisa de try/catch nem de salvar.
 */
public class InicioController {

    @FXML private Label rotuloResumo;
    @FXML private Label rotuloCorrente;
    @FXML private Label valorCorrente;
    @FXML private Label detalheCorrente;
    @FXML private Label rotuloPoupanca;
    @FXML private Label valorPoupanca;
    @FXML private Label detalhePoupanca;
    @FXML private Label valorInvestido;
    @FXML private Label detalheInvestido;
    @FXML private Label valorEmprestimo;
    @FXML private Label detalheEmprestimo;

    @FXML
    private void initialize() {
        Cliente cliente = Sessao.getCliente();
        int notificacoes = cliente.getNotificacoes().size();
        rotuloResumo.setText(notificacoes == 0
                ? "Você não tem notificações novas."
                : "Você tem " + notificacoes + " notificação(ões). Veja em Notificações.");

        ContaCorrente corrente = cliente.getContaCorrente();
        rotuloCorrente.setText("Conta corrente " + corrente.getNumero());
        valorCorrente.setText(Formato.dinheiro(corrente.getSaldo()));
        detalheCorrente.setText("Cheque especial: " + Formato.dinheiro(corrente.getLimiteChequeEspecial())
                + (corrente.isBloqueada() ? " · conta bloqueada" : ""));

        ContaPoupanca poupanca = cliente.getContaPoupanca();
        if (poupanca == null) {
            rotuloPoupanca.setText("Poupança");
            valorPoupanca.setText("—");
            detalhePoupanca.setText("Você ainda não tem poupança. Abra em Perfil.");
        } else {
            rotuloPoupanca.setText("Poupança " + poupanca.getNumero());
            valorPoupanca.setText(Formato.dinheiro(poupanca.getSaldo()));
            detalhePoupanca.setText("Rende 0,5% ao mês" + (poupanca.isBloqueada() ? " · bloqueada" : ""));
        }

        valorInvestido.setText(Formato.dinheiro(corrente.getSaldoInvestido()));
        detalheInvestido.setText("Rende cerca de "
                + Formato.dinheiro(corrente.simularRendimentoInvestimento()) + " por mês");

        Emprestimo emprestimo = corrente.getEmprestimo();
        if (emprestimo == null) {
            valorEmprestimo.setText("—");
            detalheEmprestimo.setText("Nenhum empréstimo ativo.");
        } else {
            valorEmprestimo.setText(Formato.dinheiro(emprestimo.getValor()));
            detalheEmprestimo.setText(emprestimo.getPrazoMeses() + "x de "
                    + Formato.dinheiro(emprestimo.getValorParcela()));
        }
    }
}
