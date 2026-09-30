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

    // Adiciona um cliente já criado
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

        // Localiza o cliente
        Cliente cliente = buscarClientePorCpf(cpf);

        // Verifica a senha
        if (!cliente.validarSenha(senha)) {
            throw new LoginInvalidoException(
                    "Senha inválida."
            );
        }

        return cliente;
    }

    // Encerra a conta quando todos os saldos estão zerados
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

        // Remove o cliente da lista do banco
        clientes.remove(cliente);
    }
}