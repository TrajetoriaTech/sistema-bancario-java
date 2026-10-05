package app;

import java.util.Scanner;

import exception.ContaBloqueadaException;
import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;
import model.Cliente;
import model.ContaCorrente;
import model.ContaPoupanca;
import service.Banco;

/**
 * Menu de investimentos (Felipe).
 * Investir e resgatar movem dinheiro entre o saldo e o saldo investido
 * da conta corrente. Render juros credita 0,5% na poupança.
 * As regras ficam em ContaCorrente e ContaPoupanca (Breno).
 */
public class MenuInvestimentos {

    public static void exibir(Banco banco, Cliente cliente, Scanner in) {
        ContaCorrente conta = cliente.getContaCorrente();

        System.out.println("\n--- Investimentos ---");
        System.out.printf("Saldo em conta:  R$ %.2f%n", conta.getSaldo());
        System.out.printf("Saldo investido: R$ %.2f%n", conta.getSaldoInvestido());
        System.out.printf("Rendimento estimado em 1 mês: R$ %.2f%n",
                conta.simularRendimentoInvestimento());
        System.out.println("1) Investir");
        System.out.println("2) Resgatar");
        System.out.println("3) Render juros da poupança");
        System.out.println("0) Voltar");
        System.out.print("Opção: ");
        String opcao = in.nextLine().trim();

        try {
            switch (opcao) {
                case "1":
                    conta.investir(MenuUtil.lerValor(in));
                    System.out.println("Valor investido com sucesso!");
                    MenuUtil.mostrarComprovante(conta);
                    break;
                case "2":
                    conta.resgatar(MenuUtil.lerValor(in));
                    System.out.println("Resgate feito! O valor voltou para o saldo da conta.");
                    MenuUtil.mostrarComprovante(conta);
                    break;
                case "3":
                    renderJurosPoupanca(cliente);
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido: digite um número, ex: 150,90.");
        } catch (ContaBloqueadaException e) {
            System.out.println("Sua conta está bloqueada. Desbloqueie em Perfil para investir ou resgatar.");
        } catch (ValorInvalidoException e) {
            System.out.println("O valor precisa ser maior que zero.");
        } catch (SaldoInsuficienteException e) {
            // A mesma exceção tem significado diferente em cada opção
            if (opcao.equals("1")) {
                System.out.println("Saldo em conta insuficiente. O cheque especial não pode ser investido.");
            } else {
                System.out.println("Você não tem esse valor investido para resgatar.");
            }
        }
    }

    // Opção 3: só funciona para quem tem conta poupança
    private static void renderJurosPoupanca(Cliente cliente) {
        ContaPoupanca poupanca = cliente.getContaPoupanca();
        if (poupanca == null) {
            System.out.println("Você não tem conta poupança. Abra uma em Perfil.");
            return;
        }
        poupanca.renderJuros();
        System.out.println("Rendimento de 0,5% creditado na poupança!");
        MenuUtil.mostrarComprovante(poupanca);
    }
}
