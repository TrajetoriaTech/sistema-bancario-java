package app;

import java.util.Scanner;

import exception.ContaBloqueadaException;
import exception.ContaNaoEncontradaException;
import exception.LimiteDiarioExcedidoException;
import exception.OperacaoNaoPermitidaException;
import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;
import model.Cliente;
import model.Conta;
import service.Banco;

/**
 * Menu do Pix (Felipe).
 * O menu só coleta os dados (conta de origem, CPF, tipo de conta do
 * destinatário e valor). Quem faz o Pix é o Banco.pix() (Alan), que
 * por baixo usa o transferir() com todas as regras.
 */
public class MenuPix {

    public static void exibir(Banco banco, Cliente cliente, Scanner in) {
        System.out.println("\n--- Pix ---");

        try {
            // 1) De qual conta sai o dinheiro
            Conta origem = MenuUtil.escolherConta(cliente, in, "Pagar com qual conta?");

            // 2) CPF do destinatário: tira ponto, traço e espaço
            System.out.print("CPF do destinatário: ");
            String cpf = in.nextLine().replace(".", "").replace("-", "").replace(" ", "");

            // Busca antes de pedir o valor, para mostrar o nome na confirmação
            // (como os apps de banco fazem). Se não existir, cai no catch.
            Cliente destinatario = banco.buscarClientePorCpf(cpf);

            // 3) Em qual conta do destinatário o dinheiro cai
            System.out.print("Cai em qual conta de " + destinatario.getNome()
                    + "? 1) Corrente  2) Poupança: ");
            String opcao = in.nextLine().trim();
            String tipoConta;
            if (opcao.equals("1")) {
                tipoConta = "CORRENTE";
            } else if (opcao.equals("2")) {
                tipoConta = "POUPANCA";
            } else {
                System.out.println("Opção inválida.");
                return;
            }

            // 4) Valor e confirmação
            double valor = MenuUtil.lerValor(in);
            String pergunta = String.format("Enviar R$ %.2f para %s?", valor, destinatario.getNome());
            if (!MenuUtil.confirmar(in, pergunta)) {
                System.out.println("Pix cancelado.");
                return;
            }

            // 5) Quem faz o Pix de verdade é o Banco
            banco.pix(origem, cpf, tipoConta, valor);
            System.out.println("Pix enviado com sucesso!");
            MenuUtil.mostrarComprovante(origem);

        } catch (NumberFormatException e) {
            System.out.println("Valor inválido: digite um número, ex: 150,90.");
        } catch (ContaNaoEncontradaException e) {
            // Duas causas possíveis: CPF não cadastrado, ou o destinatário
            // não tem poupança. A mensagem do próprio Banco diz qual foi.
            System.out.println(e.getMessage());
        } catch (ValorInvalidoException e) {
            System.out.println("O valor do Pix precisa ser maior que zero.");
        } catch (ContaBloqueadaException e) {
            System.out.println("Sua conta está bloqueada. Desbloqueie em Perfil para fazer Pix.");
        } catch (LimiteDiarioExcedidoException e) {
            System.out.println("Esse Pix passa do seu limite diário de saídas.");
        } catch (SaldoInsuficienteException e) {
            System.out.println("Saldo insuficiente para esse Pix.");
        } catch (OperacaoNaoPermitidaException e) {
            System.out.println("A conta de origem e a de destino são a mesma.");
        }
    }
}
