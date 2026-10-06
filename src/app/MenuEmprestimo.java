package app;

import java.util.Scanner;

import exception.ContaBloqueadaException;
import exception.OperacaoNaoPermitidaException;
import exception.ValorInvalidoException;
import model.Cliente;
import model.ContaCorrente;
import model.Emprestimo;
import service.Banco;

/**
 * Menu de empréstimo (Felipe).
 * Toda a regra de aprovação, juros e parcela fica em
 * ContaCorrente.solicitarEmprestimo() e na classe Emprestimo (Breno).
 */
public class MenuEmprestimo {

    public static void exibir(Banco banco, Cliente cliente, Scanner in) {
        ContaCorrente conta = cliente.getContaCorrente();

        System.out.println("\n--- Empréstimo ---");
        System.out.println("1) Solicitar empréstimo");
        System.out.println("2) Ver minhas parcelas");
        System.out.println("0) Voltar");
        System.out.print("Opção: ");
        String opcao = in.nextLine().trim();

        switch (opcao) {
            case "1":
                solicitar(conta, in);
                break;
            case "2":
                verParcelas(conta);
                break;
            case "0":
                break;
            default:
                System.out.println("Opção inválida.");
        }
    }

    // Opção 1: lê valor e prazo e pede o empréstimo à conta corrente
    private static void solicitar(ContaCorrente conta, Scanner in) {
        try {
            double valor = MenuUtil.lerValor(in);
            System.out.print("Prazo em meses (6, 12 ou 24): ");
            int prazo = Integer.parseInt(in.nextLine().trim());

            String pergunta = String.format("Solicitar R$ %.2f em %d meses?", valor, prazo);
            if (!MenuUtil.confirmar(in, pergunta)) {
                System.out.println("Solicitação cancelada.");
                return;
            }

            conta.solicitarEmprestimo(valor, prazo);
            System.out.println("Empréstimo aprovado! O valor já está na sua conta corrente.");
            verParcelas(conta);

        } catch (NumberFormatException e) {
            System.out.println("Digite apenas números no valor e no prazo.");
        } catch (ValorInvalidoException e) {
            System.out.println("O valor precisa ser maior que zero e o prazo deve ser 6, 12 ou 24 meses.");
        } catch (ContaBloqueadaException e) {
            System.out.println("Sua conta está bloqueada. Desbloqueie em Perfil para pedir empréstimo.");
        } catch (OperacaoNaoPermitidaException e) {
            // Duas causas: já existe empréstimo ativo, ou o valor não foi aprovado
            if (conta.getEmprestimo() != null) {
                System.out.println("Você já tem um empréstimo ativo. Só é permitido um por vez.");
            } else {
                System.out.println(e.getMessage());
            }
        }
    }

    // Opção 2: só mostra os dados do empréstimo, não altera nada
    private static void verParcelas(ContaCorrente conta) {
        Emprestimo emprestimo = conta.getEmprestimo();
        if (emprestimo == null) {
            System.out.println("Você não tem empréstimo ativo.");
            return;
        }
        System.out.printf("Valor emprestado: R$ %.2f%n", emprestimo.getValor());
        System.out.printf("Prazo: %d meses%n", emprestimo.getPrazoMeses());
        System.out.printf("Parcela mensal: R$ %.2f%n", emprestimo.getValorParcela());
    }
}
