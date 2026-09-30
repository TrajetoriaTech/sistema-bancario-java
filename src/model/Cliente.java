package model;

import java.util.ArrayList;
import java.util.List;

import exception.LoginInvalidoException;
import exception.OperacaoNaoPermitidaException;
import exception.ValorInvalidoException;

public class Cliente {

    // Dados básicos do cliente
    private String nome;
    private String cpf;
    private String senha;

    // Contas vinculadas ao cliente
    private ContaCorrente contaCorrente;
    private ContaPoupanca contaPoupanca;

    // Lista de notificações
    private List<String> notificacoes;

    // Cria o cliente com seus dados e contas
    public Cliente(String nome, String cpf, String senha,
                   ContaCorrente contaCorrente,
                   ContaPoupanca contaPoupanca) {

        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.contaCorrente = contaCorrente;
        this.contaPoupanca = contaPoupanca;

        // Inicia a lista de notificações vazia
        this.notificacoes = new ArrayList<>();
    }

    // Verifica se a senha informada está correta
    public boolean validarSenha(String senhaInformada) {
        return senhaInformada != null
                && senhaInformada.equals(this.senha);
    }

    // Altera o nome do cliente
    public void alterarNome(String novoNome)
            throws ValorInvalidoException {

        if (novoNome == null || novoNome.trim().isEmpty()) {
            throw new ValorInvalidoException(
                    "Nome inválido."
            );
        }

        this.nome = novoNome;
    }

    // Altera a senha após validar a senha atual
    public void alterarSenha(String senhaAtual, String novaSenha)
            throws LoginInvalidoException {

        if (!validarSenha(senhaAtual)) {
            throw new LoginInvalidoException(
                    "Senha atual incorreta."
            );
        }

        this.senha = novaSenha;
    }

    // Vincula uma conta poupança ao cliente
    public void abrirPoupanca(ContaPoupanca poupanca)
            throws OperacaoNaoPermitidaException {

        if (this.contaPoupanca != null) {
            throw new OperacaoNaoPermitidaException(
                    "O cliente já possui uma conta poupança."
            );
        }

        if (poupanca == null) {
            throw new OperacaoNaoPermitidaException(
                    "A conta poupança não pode ser nula."
            );
        }

        this.contaPoupanca = poupanca;
    }

    // Adiciona uma nova notificação
    public void adicionarNotificacao(String mensagem) {
        notificacoes.add(mensagem);
    }

    // Retorna o nome
    public String getNome() {
        return nome;
    }

    // Retorna o CPF
    public String getCpf() {
        return cpf;
    }

    // Retorna a conta corrente
    public ContaCorrente getContaCorrente() {
        return contaCorrente;
    }

    // Retorna a conta poupança
    public ContaPoupanca getContaPoupanca() {
        return contaPoupanca;
    }

    // Retorna as notificações
    public List<String> getNotificacoes() {
        return notificacoes;
    }
}