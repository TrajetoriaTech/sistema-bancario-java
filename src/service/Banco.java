package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import exception.ContaBloqueadaException;
import exception.ContaNaoEncontradaException;
import exception.LimiteDiarioExcedidoException;
import exception.LoginInvalidoException;
import exception.OperacaoNaoPermitidaException;
import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;

public class Banco {

    // Lista de clientes cadastrados
    private List<Cliente> clientes;

    // Controla o próximo número de conta
    private int proximoNumeroConta = 1000;

    // Cria um banco inicialmente vazio
    public Banco() {
        clientes = new ArrayList<>();
    }

    // Adiciona um cliente ao banco
    public void adicionarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

    // Cadastra um cliente e cria suas contas
    public Cliente cadastrarCliente(String nome, String cpf, String senha,
                                    boolean criarPoupanca) {

        // Cria a conta corrente
        ContaCorrente contaCorrente =
                new ContaCorrente(proximoNumeroConta++);

        // Começa sem conta poupança
        ContaPoupanca contaPoupanca = null;

        // Cria a poupança quando solicitado
        if (criarPoupanca) {
            contaPoupanca =
                    new ContaPoupanca(proximoNumeroConta++);
        }

        // Cria o cliente
        Cliente cliente = new Cliente(
                nome,
                cpf,
                senha,
                contaCorrente,
                contaPoupanca
        );

        // Salva o cliente no banco
        clientes.add(cliente);

        return cliente;
    }

    // Procura um cliente pelo CPF
    public Cliente buscarClientePorCpf(String cpf)
            throws ContaNaoEncontradaException {

        for (Cliente cliente : clientes) {

            if (cpf != null && cpf.equals(cliente.getCpf())) {
                return cliente;
            }
        }

        throw new ContaNaoEncontradaException(
                "Cliente não encontrado."
        );
    }

    // Procura um cliente pelo número da conta
    public Cliente buscarClientePorNumero(int numero)
            throws ContaNaoEncontradaException {

        for (Cliente cliente : clientes) {

            // Procura na conta corrente
            if (cliente.getContaCorrente() != null
                    && cliente.getContaCorrente().getNumero() == numero) {

                return cliente;
            }

            // Procura na conta poupança
            if (cliente.getContaPoupanca() != null
                    && cliente.getContaPoupanca().getNumero() == numero) {

                return cliente;
            }
        }

        throw new ContaNaoEncontradaException(
                "Conta não encontrada."
        );
    }

    // Procura o cliente dono de uma conta
    public Cliente buscarClientePorConta(Conta conta)
            throws ContaNaoEncontradaException {

        // Evita procurar uma conta nula
        if (conta != null) {

            for (Cliente cliente : clientes) {

                if (cliente.getContaCorrente() == conta
                        || cliente.getContaPoupanca() == conta) {

                    return cliente;
                }
            }
        }

        throw new ContaNaoEncontradaException(
                "Cliente da conta não encontrado."
        );
    }

    // Faz login usando CPF e senha
    public Cliente login(String cpf, String senha)
            throws ContaNaoEncontradaException,
            LoginInvalidoException {

        // Localiza o cliente pelo CPF
        Cliente cliente = buscarClientePorCpf(cpf);

        // Verifica a senha
        if (!cliente.validarSenha(senha)) {
            throw new LoginInvalidoException(
                    "Senha inválida."
            );
        }

        return cliente;
    }

    // Encerra a conta quando os saldos estão zerados
    public void encerrarConta(Cliente cliente)
            throws OperacaoNaoPermitidaException {

        // Verifica o saldo da conta corrente
        if (cliente.getContaCorrente() != null
                && cliente.getContaCorrente().getSaldo() != 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar a conta com saldo."
            );
        }

        // Verifica o saldo da poupança
        if (cliente.getContaPoupanca() != null
                && cliente.getContaPoupanca().getSaldo() != 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar a conta com saldo."
            );
        }

        // Verifica valores investidos
        if (cliente.getContaCorrente() != null
                && cliente.getContaCorrente().getSaldoInvestido() != 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar com investimento ativo."
            );
        }

        // Remove o cliente do banco
        clientes.remove(cliente);
    }

    // =================================================================
    // Métodos do ALAN
    // =================================================================

    // Transferir (funcionalidade #7)
    // Assinatura atualizada conforme a proposta 1 aprovada pelo grupo
    public void transferir(Conta origem, Conta destino, double valor)
            throws ValorInvalidoException,
            ContaBloqueadaException,
            LimiteDiarioExcedidoException,
            SaldoInsuficienteException,
            OperacaoNaoPermitidaException,
            ContaNaoEncontradaException {

        // Mesma conta: não move dinheiro, só sujaria o extrato
        // e consumiria limite diário à toa
        if (origem == destino) {
            throw new OperacaoNaoPermitidaException(
                    "Origem e destino não podem ser a mesma conta.");
        }

        // Descobre os donos ANTES de mexer no dinheiro: se alguma conta
        // não pertencer a um cliente do banco, nada é transferido
        Cliente donoOrigem = buscarClientePorConta(origem);
        Cliente donoDestino = buscarClientePorConta(destino);

        // 1. Todas as validações (bloqueio, valor, limite diário, saldo e
        //    cheque especial) acontecem dentro de sacar()
        origem.sacar(valor);

        // 2. Só chega aqui se o saque funcionou: não precisa de rollback
        destino.depositar(valor);

        // 3. Notifica só se o dinheiro veio de OUTRA pessoa
        if (donoOrigem != donoDestino) {
            donoDestino.adicionarNotificacao(
                    "Você recebeu uma transferência de R$ "
                            + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor));
        }
    }

    // Pix (método usado pelo menu do Felipe)
    // Assinatura atualizada conforme as propostas 1 e 2 aprovadas pelo grupo
    public void pix(Conta origem, String cpfDestino,
                    String tipoConta, double valor)
            throws ContaNaoEncontradaException,
            ValorInvalidoException,
            ContaBloqueadaException,
            LimiteDiarioExcedidoException,
            SaldoInsuficienteException,
            OperacaoNaoPermitidaException {

        // tipoConta só aceita "CORRENTE" ou "POUPANCA"
        if (!"CORRENTE".equals(tipoConta) && !"POUPANCA".equals(tipoConta)) {
            throw new ValorInvalidoException(
                    "Tipo de conta inválido: use CORRENTE ou POUPANCA.");
        }

        // 1. Busca o cliente pelo CPF (lança exceção se não existir)
        Cliente cliente = buscarClientePorCpf(cpfDestino);

        // 2. Pega a conta do tipo pedido
        Conta destino;
        if (tipoConta.equals("CORRENTE")) {
            destino = cliente.getContaCorrente();
        } else {
            destino = cliente.getContaPoupanca(); // pode ser null
        }

        // Cliente não tem o tipo de conta pedido
        if (destino == null) {
            throw new ContaNaoEncontradaException(
                    "O destinatário não possui conta do tipo " + tipoConta + ".");
        }

        // 3. Daqui pra frente é uma transferência normal
        transferir(origem, destino, valor);
    }
}