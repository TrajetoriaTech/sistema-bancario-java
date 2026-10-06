package app;

import java.util.List;
import java.util.Scanner;

import model.Cliente;
import model.Conta;
import model.ContaPoupanca;
import model.Transacao;

/**
 * Funções de tela repetidas nos menus das áreas extras (Felipe):
 * ler um valor, pedir confirmação e mostrar o comprovante.
 * Sem "public" na classe: só o pacote app enxerga (acesso default).
 */
final class MenuUtil {

    // Construtor privado: ninguém precisa criar um objeto MenuUtil
    private MenuUtil() {
    }

    // Lê um valor em reais. Aceita vírgula ou ponto (150,90 ou 150.90).
    // Se o texto não for número, o próprio Java lança NumberFormatException.
    static double lerValor(Scanner in) {
        System.out.print("Valor: R$ ");
        return Double.parseDouble(in.nextLine().trim().replace(",", "."));
    }

    // Faz uma pergunta de sim/não. Só "s" ou "S" confirma.
    static boolean confirmar(Scanner in, String pergunta) {
        System.out.print(pergunta + " (s/n): ");
        return in.nextLine().trim().equalsIgnoreCase("s");
    }

    // Pergunta de qual conta do cliente sai o dinheiro.
    // Quem não tem poupança usa direto a corrente, sem pergunta.
    // Repete a pergunta até a pessoa digitar 1 ou 2.
    static Conta escolherConta(Cliente cliente, Scanner in, String pergunta) {
        ContaPoupanca poupanca = cliente.getContaPoupanca();
        if (poupanca == null) {
            return cliente.getContaCorrente();
        }
        while (true) {
            System.out.print(pergunta + " 1) Corrente  2) Poupança: ");
            String opcao = in.nextLine().trim();
            if (opcao.equals("1")) {
                return cliente.getContaCorrente();
            }
            if (opcao.equals("2")) {
                return poupanca;
            }
            System.out.println("Opção inválida. Digite 1 ou 2.");
        }
    }

    // Mostra a última transação da conta, que é a da operação recém-feita.
    // Recebe Conta, então serve tanto para ContaCorrente quanto ContaPoupanca.
    static void mostrarComprovante(Conta conta) {
        List<Transacao> transacoes = conta.getTransacoes();
        System.out.println("Comprovante: "
                + transacoes.get(transacoes.size() - 1).gerarComprovante());
    }
}
