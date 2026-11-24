package controller;

import exception.ContaBloqueadaException;
import exception.LimiteDiarioExcedidoException;
import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.ContaCorrente;
import util.Entrada;

/**
 * TELA MODELO (Felipe): pagamento de boleto.
 * Mesmo passo a passo do MenuBoleto do console, só que lendo dos campos
 * da tela e mostrando o resultado num Label em vez do System.out.
 *
 * O padrão que toda tela de operação segue:
 *  1. valida o FORMATO do que foi digitado, com if (sem exceção)
 *  2. pede confirmação antes de mexer em dinheiro
 *  3. chama o model/service, que aplica as regras de negócio
 *  4. um catch para cada exceção, com mensagem própria
 *  5. deu certo: Sessao.salvar() e mostra o comprovante
 */
public class BoletoController {

    @FXML private TextField campoCodigo;
    @FXML private TextField campoValor;
    @FXML private Label rotuloConta;
    @FXML private Label rotuloMensagem;

    @FXML
    private void initialize() {
        atualizarConta();
    }

    @FXML
    private void pagar() {
        // 1) Formato do código: só validação de entrada
        String codigo = campoCodigo.getText().replace(" ", "");
        if (!Entrada.codigoBoletoValido(codigo)) {
            Mensagem.erro(rotuloMensagem, "Código inválido: digite exatamente "
                    + Entrada.TAMANHO_BOLETO + " números.");
            return;
        }

        // 1) Formato do valor: se é positivo, quem decide é o sacar()
        double valor;
        try {
            valor = Entrada.converterValor(campoValor.getText());
        } catch (NumberFormatException e) {
            Mensagem.erro(rotuloMensagem, "Valor inválido: digite um número, ex.: 150,90.");
            return;
        }

        // 2) Confirmação
        if (!Mensagem.confirmar("Pagar boleto",
                "Pagar " + Formato.dinheiro(valor) + " com a conta corrente?")) {
            return;
        }

        // 3) Boleto sempre debita da conta corrente (regra do documento)
        ContaCorrente conta = Sessao.getCliente().getContaCorrente();
        try {
            conta.sacar(valor);
            Sessao.salvar();
            // 5) Comprovante e campos limpos para o próximo pagamento
            Mensagem.sucesso(rotuloMensagem, "Boleto pago! " + Formato.ultimoComprovante(conta));
            campoCodigo.clear();
            campoValor.clear();
            atualizarConta();
        // 4) Um catch para cada exceção
        } catch (ContaBloqueadaException e) {
            Mensagem.erro(rotuloMensagem, "Sua conta está bloqueada. Desbloqueie em \"Bloquear conta\" para pagar.");
        } catch (ValorInvalidoException e) {
            Mensagem.erro(rotuloMensagem, "O valor do boleto precisa ser maior que zero.");
        } catch (LimiteDiarioExcedidoException e) {
            Mensagem.erro(rotuloMensagem, "Esse pagamento passa do seu limite diário de "
                    + Formato.dinheiro(conta.getLimiteDiario()) + ".");
        } catch (SaldoInsuficienteException e) {
            Mensagem.erro(rotuloMensagem, "Saldo insuficiente, mesmo com o cheque especial.");
        }
    }

    @FXML
    private void limpar() {
        campoCodigo.clear();
        campoValor.clear();
        Mensagem.limpar(rotuloMensagem);
    }

    private void atualizarConta() {
        ContaCorrente conta = Sessao.getCliente().getContaCorrente();
        rotuloConta.setText("Debitado da " + Formato.nomeDaConta(conta).toLowerCase()
                + " · saldo " + Formato.dinheiro(conta.getSaldo())
                + " + cheque especial " + Formato.dinheiro(conta.getLimiteChequeEspecial()));
    }
}
