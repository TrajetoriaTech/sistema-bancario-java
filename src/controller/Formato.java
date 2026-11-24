package controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.Transacao;

/**
 * Como os dados aparecem na tela: dinheiro em reais, nome das contas
 * e a caixa de escolha "de qual conta?" que várias telas usam.
 */
public final class Formato {

    private static final NumberFormat REAIS = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private Formato() {
    }

    // 1234.5 vira "R$ 1.234,50"
    public static String dinheiro(double valor) {
        return REAIS.format(valor);
    }

    // "Conta corrente 1000" ou "Poupança 1001"
    public static String nomeDaConta(Conta conta) {
        String tipo = (conta instanceof ContaCorrente) ? "Conta corrente" : "Poupança";
        return tipo + " " + conta.getNumero();
    }

    // Linha do comprovante da última operação da conta
    public static String ultimoComprovante(Conta conta) {
        List<Transacao> transacoes = conta.getTransacoes();
        return transacoes.get(transacoes.size() - 1).gerarComprovante();
    }

    // Preenche uma ComboBox com as contas do cliente (corrente e, se tiver, poupança)
    // e já deixa a corrente escolhida. A caixa mostra o nome da conta, não o objeto.
    public static void preencherContas(ComboBox<Conta> caixa, Cliente cliente) {
        caixa.getItems().setAll(cliente.getContaCorrente());
        if (cliente.getContaPoupanca() != null) {
            caixa.getItems().add(cliente.getContaPoupanca());
        }
        caixa.setConverter(new StringConverter<Conta>() {
            @Override
            public String toString(Conta conta) {
                return conta == null ? "" : nomeDaConta(conta) + " · " + dinheiro(conta.getSaldo());
            }

            @Override
            public Conta fromString(String texto) {
                return null; // a caixa não é editável: nunca converte texto em conta
            }
        });
        caixa.getSelectionModel().selectFirst();
    }
}
