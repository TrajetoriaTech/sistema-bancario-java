package service;

import java.util.ArrayList;
import java.util.Collections;
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

    private List<Cliente> clientes;
    private int proximoNumeroConta = 1000;

    public Banco() {
        clientes = new ArrayList<>();
    }

    // Lista de clientes somente para leitura externa.
    public List<Cliente> getClientes() {
        return Collections.unmodifiableList(clientes);
    }

    // Adiciona cliente e mantém a próxima conta disponível.
    public void adicionarCliente(Cliente cliente) {
        clientes.add(cliente);

        if (cliente.getContaCorrente() != null) {
            proximoNumeroConta = Math.max(
                    proximoNumeroConta,
                    cliente.getContaCorrente().getNumero() + 1
            );
        }

        if (cliente.getContaPoupanca() != null) {
            proximoNumeroConta = Math.max(
                    proximoNumeroConta,
                    cliente.getContaPoupanca().getNumero() + 1
            );
        }
    }

    // Abre uma conta poupança para o cliente.
    public void abrirPoupanca(Cliente cliente)
            throws OperacaoNaoPermitidaException {

        if (cliente == null) {
            throw new OperacaoNaoPermitidaException(
                    "Cliente inválido."
            );
        }

        ContaPoupanca poupanca =
                new ContaPoupanca(proximoNumeroConta);

        cliente.abrirPoupanca(poupanca);

        proximoNumeroConta++;
    }

    // Cadastra cliente com conta corrente e, opcionalmente, poupança.
    public Cliente cadastrarCliente(
            String nome,
            String cpf,
            String senha,
            boolean abrirPoupanca)
            throws ValorInvalidoException,
            OperacaoNaoPermitidaException {

        if (nome == null || nome.isBlank()) {
            throw new ValorInvalidoException(
                    "Nome não pode ser vazio."
            );
        }

        if (cpf == null || cpf.isBlank()) {
            throw new ValorInvalidoException(
                    "CPF não pode ser vazio."
            );
        }

        if (senha == null || senha.isBlank()) {
            throw new ValorInvalidoException(
                    "Senha não pode ser vazia."
            );
        }

        try {
            buscarClientePorCpf(cpf);

            throw new OperacaoNaoPermitidaException(
                    "Já existe um cliente cadastrado com este CPF."
            );

        } catch (ContaNaoEncontradaException e) {
            // CPF ainda não cadastrado. Continua o cadastro.
        }

        ContaCorrente contaCorrente =
                new ContaCorrente(proximoNumeroConta++);

        ContaPoupanca contaPoupanca = null;

        if (abrirPoupanca) {
            contaPoupanca =
                    new ContaPoupanca(proximoNumeroConta++);
        }

        Cliente cliente = new Cliente(
                nome,
                cpf,
                senha,
                contaCorrente,
                contaPoupanca
        );

        adicionarCliente(cliente);

        return cliente;
    }

    // Busca cliente pelo CPF.
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

    // Busca cliente somente pelo número da conta corrente.
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

    // Busca o cliente proprietário de uma conta.
    public Cliente buscarClientePorConta(Conta conta)
            throws ContaNaoEncontradaException {

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

    // Login por CPF ou número da conta corrente.
    public Cliente login(String identificador, String senha)
            throws ContaNaoEncontradaException,
            LoginInvalidoException {

        Cliente cliente;

        try {
            cliente = buscarClientePorCpf(identificador);

        } catch (ContaNaoEncontradaException cpfNaoEncontrado) {

            int numeroConta;

            try {
                numeroConta = Integer.parseInt(identificador);

            } catch (NumberFormatException e) {
                throw cpfNaoEncontrado;
            }

            cliente = buscarClientePorNumero(numeroConta);
        }

        if (!cliente.validarSenha(senha)) {
            throw new LoginInvalidoException(
                    "Senha inválida."
            );
        }

        return cliente;
    }

    // Encerra a conta somente quando não existem pendências.
    public void encerrarConta(Cliente cliente)
            throws OperacaoNaoPermitidaException {

        if (cliente.getContaCorrente() != null
                && cliente.getContaCorrente().getSaldo() < 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar a conta: "
                            + "existe uma dívida na conta corrente. "
                            + "Quite a dívida antes de encerrar."
            );
        }

        if (cliente.getContaCorrente() != null
                && cliente.getContaCorrente().getSaldo() > 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar a conta: "
                            + "há saldo positivo na conta corrente. "
                            + "Retire ou transfira o valor antes de encerrar."
            );
        }

        if (cliente.getContaPoupanca() != null
                && cliente.getContaPoupanca().getSaldo() > 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar a conta: "
                            + "há saldo positivo na conta poupança. "
                            + "Retire ou transfira o valor antes de encerrar."
            );
        }

        if (cliente.getContaCorrente() != null
                && cliente.getContaCorrente().getSaldoInvestido() > 0) {

            throw new OperacaoNaoPermitidaException(
                    "Não é possível encerrar a conta: "
                            + "existe investimento ativo. "
                            + "Resgate o investimento antes de encerrar."
            );
        }

        clientes.remove(cliente);
    }

    // Realiza transferência entre contas.
    public void transferir(
            Conta origem,
            Conta destino,
            double valor)
            throws ValorInvalidoException,
            ContaBloqueadaException,
            LimiteDiarioExcedidoException,
            SaldoInsuficienteException,
            OperacaoNaoPermitidaException,
            ContaNaoEncontradaException {

        if (origem == destino) {
            throw new OperacaoNaoPermitidaException(
                    "Origem e destino não podem ser a mesma conta."
            );
        }

        Cliente donoOrigem = buscarClientePorConta(origem);
        Cliente donoDestino = buscarClientePorConta(destino);

        origem.sacar(valor);
        destino.depositar(valor);

        if (donoOrigem != donoDestino) {
            donoDestino.adicionarNotificacao(
                    "Você recebeu uma transferência de R$ "
                            + String.format(
                            Locale.forLanguageTag("pt-BR"),
                            "%.2f",
                            valor)
            );
        }
    }

    // Realiza PIX usando CPF e tipo da conta.
    public void pix(
            Conta origem,
            String cpfDestino,
            String tipoConta,
            double valor)
            throws ContaNaoEncontradaException,
            ValorInvalidoException,
            ContaBloqueadaException,
            LimiteDiarioExcedidoException,
            SaldoInsuficienteException,
            OperacaoNaoPermitidaException {

        if (!"CORRENTE".equals(tipoConta)
                && !"POUPANCA".equals(tipoConta)) {

            throw new ValorInvalidoException(
                    "Tipo de conta inválido: use CORRENTE ou POUPANCA."
            );
        }

        Cliente cliente = buscarClientePorCpf(cpfDestino);

        Conta destino;

        if ("CORRENTE".equals(tipoConta)) {
            destino = cliente.getContaCorrente();
        } else {
            destino = cliente.getContaPoupanca();
        }

        if (destino == null) {
            throw new ContaNaoEncontradaException(
                    "O destinatário não possui conta do tipo "
                            + tipoConta + "."
            );
        }

        transferir(origem, destino, valor);
    }
}