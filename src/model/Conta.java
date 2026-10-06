package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

import exception.ValorInvalidoException;
import exception.ContaBloqueadaException;
import exception.LimiteDiarioExcedidoException;
import exception.SaldoInsuficienteException;

public abstract class Conta {

    private int numero;
    private double saldo;
    private boolean bloqueada;
    private double limiteDiario;
    private List<Transacao> transacoes;

    protected Conta(int numero) {
        this.numero = numero;
        this.saldo = 0.0;
        this.bloqueada = false;
        this.limiteDiario = 2000.0;
        this.transacoes = new ArrayList<>();
    }


    public double getSaldo() {
        return saldo;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public double getLimiteDiario() {
        return limiteDiario;
    }

    // Devolve a lista SÓ PARA LEITURA: quem chama consegue ver o extrato,
    // mas não consegue adicionar, apagar ou alterar transações (encapsulamento)
    public List<Transacao> getTransacoes() {
        return Collections.unmodifiableList(transacoes);
    }

    // Único jeito de registrar uma transação. É protected: só a própria Conta
    // e as subclasses (ContaCorrente, ContaPoupanca) podem usar.
    protected void registrarTransacao(Transacao transacao) {
        transacoes.add(transacao);
    }

    // Usado SÓ ao carregar os dados do arquivo .txt: devolve a transação para
    // o extrato e recalcula o saldo. Não valida regras (bloqueio, limite...),
    // porque a operação já foi validada quando aconteceu de verdade.
    public void restaurarTransacao(Transacao transacao) {
        registrarTransacao(transacao);
        String tipo = transacao.getTipo();
        if (tipo.equals("ENTRADA") || tipo.equals("RESGATE")) {
            adicionarSaldo(transacao.getValor());
        } else {
            // SAIDA e INVESTIMENTO tiram dinheiro do saldo
            removerSaldo(transacao.getValor());
        }
    }

    protected void adicionarSaldo( double valor) {
        saldo += valor;
    }

    protected void removerSaldo( double valor) {
        saldo -= valor;
    }
    public void bloquear() {
        bloqueada = true;
    }

    public void desbloquear() {
        bloqueada = false;
    }

    public void depositar(double valor) throws ValorInvalidoException {

    if (valor <= 0) {
        throw new ValorInvalidoException();
    }

    adicionarSaldo(valor);

    transacoes.add(new Transacao("ENTRADA", valor));
}

   public void sacar(double valor)
        throws ValorInvalidoException,
               ContaBloqueadaException,
               LimiteDiarioExcedidoException,
               SaldoInsuficienteException {

    if (bloqueada) {
        throw new ContaBloqueadaException();
    }

    if (valor <= 0) {
        throw new ValorInvalidoException();
    }

    double totalSaidasHoje = 0.0;
    LocalDate hoje = LocalDate.now();

    for (Transacao transacao : transacoes) {

        if ("SAIDA".equals(transacao.getTipo())
                && transacao.getData().toLocalDate().equals(hoje)) {

            totalSaidasHoje += transacao.getValor();
        }
    }

    if (totalSaidasHoje + valor > limiteDiario) {
        throw new LimiteDiarioExcedidoException();
    }

    if (valor <= getSaldo()) {
        removerSaldo(valor);

        transacoes.add(new Transacao("SAIDA", valor));
        return;
    }

    throw new SaldoInsuficienteException();
}

    public String extrato() {
        StringBuilder extrato = new StringBuilder();

        for(Transacao transacao : transacoes) {
            extrato.append(transacao.gerarComprovante());
            extrato.append("\n");
        }

        extrato.append(String.format("Saldo atual: R$ %.2f", saldo));
        
        return extrato.toString();
    }

}