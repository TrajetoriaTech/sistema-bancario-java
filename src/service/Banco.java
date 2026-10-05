package service;

import java.util.ArrayList;
import java.util.List;

import exception.ContaNaoEncontradaException;
import exception.LoginInvalidoException;
import exception.OperacaoNaoPermitidaException;
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

    // Procura o cliente somente pelo número da conta corrente
    public Cliente buscarClientePorNumero(int numero)
            throws ContaNaoEncontradaException {

        for (Cliente cliente : clientes) {

            if (cliente.getContaCorrente() != null
                    && cliente.getContaCorrente().getNumero() == numero) {

                return cliente;
            }
        }

        throw new ContaNaoEncontradaException(
                "Conta corrente não encontrada."
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

    // Faz login usando CPF ou número da conta corrente
    public Cliente login(String identificador, String senha)
            throws ContaNaoEncontradaException,
            LoginInvalidoException {

        Cliente cliente;

        // Primeiro tenta localizar pelo CPF
        try {
            cliente = buscarClientePorCpf(identificador);

        } catch (ContaNaoEncontradaException cpfNaoEncontrado) {

            // Se não encontrou pelo CPF, tenta pelo número da conta corrente
            int numeroConta;

            try {
                numeroConta = Integer.parseInt(identificador);

            } catch (NumberFormatException e) {
                throw cpfNaoEncontrado;
            }

            cliente = buscarClientePorNumero(numeroConta);
        }

        // Confere a senha depois de encontrar o cliente
        if (!cliente.validarSenha(senha)) {
            throw new LoginInvalidoException(
                    "Senha inválida."
            );
        }

        return cliente;
    }

    // Encerra a conta somente quando não houver dívida,
    // saldo disponível ou investimento ativo
    public void encerrarConta(Cliente cliente)
            throws OperacaoNaoPermitidaException {

        // Dívida no cheque especial precisa ser quitada primeiro
        if (cliente.getContaCorrente() != null
                && cliente.getContaCorrente().getSaldo() < 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar a conta: "
                            + "existe uma dívida na conta corrente. "
                            + "Quite a dívida antes de encerrar."
            );
        }

        // Saldo positivo na conta corrente precisa ser retirado
        if (cliente.getContaCorrente() != null
                && cliente.getContaCorrente().getSaldo() > 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar a conta: "
                            + "há saldo positivo na conta corrente. "
                            + "Retire ou transfira o valor antes de encerrar."
            );
        }

        // Saldo positivo na poupança também precisa ser retirado
        if (cliente.getContaPoupanca() != null
                && cliente.getContaPoupanca().getSaldo() > 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar a conta: "
                            + "há saldo positivo na conta poupança. "
                            + "Retire ou transfira o valor antes de encerrar."
            );
        }

        // Investimento precisa ser resgatado antes do encerramento
        if (cliente.getContaCorrente() != null
                && cliente.getContaCorrente().getSaldoInvestido() > 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar a conta: "
                            + "existe investimento ativo. "
                            + "Resgate o investimento antes de encerrar."
            );
        }

        // Tudo zerado: remove o cliente do banco
        clientes.remove(cliente);
    }
}