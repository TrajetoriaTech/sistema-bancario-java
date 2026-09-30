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

    // Próximo número disponível para uma conta
    private int proximoNumeroConta = 1000;

    // Cria um banco inicialmente vazio
    public Banco() {
        clientes = new ArrayList<>();
    }

    // Adiciona um cliente à lista
    public void adicionarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

    // Cadastra um novo cliente
    public Cliente cadastrarCliente(String nome, String cpf, String senha,
                                    boolean criarPoupanca) {

        // Cria a conta corrente
        ContaCorrente contaCorrente =
                new ContaCorrente(proximoNumeroConta++);

        // Por padrão, o cliente não possui poupança
        ContaPoupanca contaPoupanca = null;

        // Cria poupança quando solicitado
        if (criarPoupanca) {
            contaPoupanca =
                    new ContaPoupanca(proximoNumeroConta++);
        }

        // Cria o cliente com suas contas
        Cliente cliente = new Cliente(
                nome,
                cpf,
                senha,
                contaCorrente,
                contaPoupanca
        );

        // Adiciona o cliente ao banco
        clientes.add(cliente);

        return cliente;
    }

    // Procura um cliente pelo CPF
    public Cliente buscarClientePorCpf(String cpf)
            throws ContaNaoEncontradaException {

        for (Cliente cliente : clientes) {

            if (cliente.getCpf().equals(cpf)) {
                return cliente;
            }
        }

        throw new ContaNaoEncontradaException(
                "Cliente não encontrado."
        );
    }

    // Procura o cliente pelo número da conta
    public Cliente buscarClientePorNumero(int numero)
            throws ContaNaoEncontradaException {

        for (Cliente cliente : clientes) {

            // Verifica a conta corrente
            if (cliente.getContaCorrente() != null
                    && cliente.getContaCorrente().getNumero() == numero) {

                return cliente;
            }

            // Verifica a conta poupança
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

    // Faz o login usando CPF e senha
    public Cliente login(String cpf, String senha)
            throws ContaNaoEncontradaException, LoginInvalidoException {

        // Procura o cliente pelo CPF
        Cliente cliente = buscarClientePorCpf(cpf);

        // Verifica a senha informada
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

        // Verifica se a conta corrente possui saldo
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

        // Verifica se existe dinheiro investido
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