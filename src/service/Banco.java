package service;

import exception.ContaBloqueadaException;
import exception.ContaNaoEncontradaException;
import exception.LimiteDiarioExcedidoException;
import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;
import model.Cliente;
import model.Conta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Banco {

    // Atributo definido no documento: Banco gerencia a lista de Cliente
    private List<Cliente> clientes = new ArrayList<>();

    // =================================================================
    // Métodos do FRANCISCO (ele adiciona aqui):
    // login(), cadastrarCliente(), buscarClientePorCpf(),
    // buscarClientePorNumero(), buscarClientePorConta(), encerrarConta()
    // =================================================================


    // =================================================================
    // Métodos do ALAN
    // =================================================================

    // Transferir (funcionalidade #7)
    public void transferir(Conta origem, Conta destino, double valor)
            throws ValorInvalidoException,
            ContaBloqueadaException,
            LimiteDiarioExcedidoException,
            SaldoInsuficienteException {

        // TODO (grupo): o documento manda recusar origem == destino com
        // OperacaoNaoPermitidaException, mas ela NÃO está no throws da
        // assinatura. Só descomentar depois que o grupo aprovar incluir
        // a exceção na assinatura (senão não compila).
        // if (origem == destino) {
        //     throw new OperacaoNaoPermitidaException(
        //             "Origem e destino não podem ser a mesma conta");
        // }

        // 1. Todas as validações (bloqueio, valor, limite diário, saldo e
        //    cheque especial) acontecem dentro de sacar().
        origem.sacar(valor);

        // 2. Só chega aqui se o saque funcionou. Se sacar() lançou exceção,
        //    esta linha nunca roda: não precisa de rollback.
        destino.depositar(valor);

        // 3. Notificação: só se o dinheiro veio de OUTRA pessoa.
        try {
            Cliente donoOrigem = buscarClientePorConta(origem);
            Cliente donoDestino = buscarClientePorConta(destino);
            if (donoOrigem != donoDestino) {
                // TODO (grupo): confirmar o texto da notificação
                donoDestino.adicionarNotificacao(
                        "Você recebeu uma transferência de R$ "
                                + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor));
            }
        } catch (ContaNaoEncontradaException e) {
            // TODO (grupo): exceção fora do throws de transferir().
            // O dinheiro já foi transferido; só a notificação não é criada.
        }
    }

    // Pix (método usado pelo menu do Felipe)
    public void pix(Conta origem, String cpfDestino,
                    String tipoConta, double valor)
            throws ContaNaoEncontradaException,
            ValorInvalidoException,
            ContaBloqueadaException,
            LimiteDiarioExcedidoException,
            SaldoInsuficienteException {

        // 1. Busca o cliente pelo CPF (lança exceção se não existir)
        Cliente cliente = buscarClientePorCpf(cpfDestino);

        // 2. Pega a conta do tipo pedido
        // TODO (Felipe): confirmar os textos exatos de tipoConta
        Conta destino;
        if (tipoConta.equals("CORRENTE")) {
            destino = cliente.getContaCorrente();
        } else if (tipoConta.equals("POUPANCA")) {
            destino = cliente.getContaPoupanca(); // pode ser null
        } else {
            // TODO (grupo): tipo desconhecido tratado como "não encontrada"
            destino = null;
        }

        if (destino == null) {
            throw new ContaNaoEncontradaException(
                    "O destinatário não possui conta do tipo " + tipoConta);
        }

        // 3. Daqui pra frente é uma transferência normal
        transferir(origem, destino, valor);
    }
}