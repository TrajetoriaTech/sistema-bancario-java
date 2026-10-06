package app;

import java.util.Scanner;

import exception.ContaBloqueadaException;
import exception.LimiteDiarioExcedidoException;
import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;
import model.Cliente;
import model.ContaCorrente;
import service.Banco;

/**
 * Menu de pagamento de boleto (Felipe).
 * Camada app/: só lê a entrada, valida o formato do que foi digitado
 * e chama o model. As regras de negócio (bloqueio, valor válido,
 * limite diário, saldo/cheque especial) ficam todas dentro de sacar().
 */
public class MenuBoleto {

    private static final int TAMANHO_CODIGO = 44;

    public static void exibir(Banco banco, Cliente cliente, Scanner in) {
        System.out.println("\n--- Pagar boleto ---");

        // 1) Código de barras: validação de ENTRADA, feita com if (sem exceção)
        System.out.print("Código de barras (" + TAMANHO_CODIGO + " números): ");
        String codigo = in.nextLine().replace(" ", "");
        if (!codigoValido(codigo)) {
            System.out.println("Código inválido: digite exatamente "
                    + TAMANHO_CODIGO + " números.");
            return;
        }

        // 2) Valor: só converte o texto em número.
        //    Se é positivo ou não, quem decide é o sacar() (regra de negócio).
        double valor;
        try {
            valor = MenuUtil.lerValor(in);
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido: digite um número, ex: 150,90.");
            return;
        }

        // 3) Confirmação, como num app de banco
        if (!MenuUtil.confirmar(in, String.format("Pagar R$ %.2f com a conta corrente?", valor))) {
            System.out.println("Pagamento cancelado.");
            return;
        }

        // 4) Boleto sempre debita da conta corrente (regra do documento)
        ContaCorrente conta = cliente.getContaCorrente();
        try {
            conta.sacar(valor);
            System.out.println("Boleto pago com sucesso!");
            MenuUtil.mostrarComprovante(conta);
        } catch (ContaBloqueadaException e) {
            System.out.println("Sua conta está bloqueada. Desbloqueie em Perfil para pagar.");
        } catch (ValorInvalidoException e) {
            System.out.println("O valor do boleto precisa ser maior que zero.");
        } catch (LimiteDiarioExcedidoException e) {
            System.out.printf("Esse pagamento passa do seu limite diário de R$ %.2f.%n",
                    conta.getLimiteDiario());
        } catch (SaldoInsuficienteException e) {
            System.out.println("Saldo insuficiente, mesmo com o cheque especial.");
        }
    }

    // Verifica se o texto tem exatamente 44 caracteres e todos são dígitos
    private static boolean codigoValido(String codigo) {
        if (codigo.length() != TAMANHO_CODIGO) {
            return false;
        }
        for (int i = 0; i < codigo.length(); i++) {
            if (!Character.isDigit(codigo.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
