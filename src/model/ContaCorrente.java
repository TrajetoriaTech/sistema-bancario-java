package model;

import java.time.LocalDate;

import exception.ContaBloqueadaException;
import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;
import exception.LimiteDiarioExcedidoException;
import exception.OperacaoNaoPermitidaException;

public class ContaCorrente extends Conta {

    private double limiteChequeEspecial;
    private double saldoInvestido;
    private Emprestimo emprestimo;

    public ContaCorrente(int numero) {
        super(numero);

        this.limiteChequeEspecial = 500.0;
        this.saldoInvestido = 0.0;
        this.emprestimo = null;
    }

    public double getLimiteChequeEspecial(){
        return limiteChequeEspecial;
    }

    public double getSaldoInvestido() {
        return saldoInvestido;
    }

    public Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public double simularRendimentoInvestimento() {
        return saldoInvestido * 0.008;
    }
    
    public void investir(double valor) 
            throws ValorInvalidoException,
                   ContaBloqueadaException,
                   SaldoInsuficienteException {
       if (isBloqueada()) {
            throw new ContaBloqueadaException();
       }             
       
       if (valor <= 0) {
            throw new ValorInvalidoException();
       }
       // getSaldo mantém o encapsulamento, pq SALDO é privado na classe Conta                     
       if (valor > getSaldo()) {
            throw new SaldoInsuficienteException();
       }

       removerSaldo(valor);

       saldoInvestido += valor;

       getTransacoes().add(
            new Transacao("INVESTIMENTO", valor)
            );
    }

    public void resgatar(double valor)
            throws ValorInvalidoException,
                   ContaBloqueadaException,
                   SaldoInsuficienteException {

        if (isBloqueada()) {
            throw new ContaBloqueadaException ();
        }
        
        if (valor <= 0) {
            throw new ValorInvalidoException();
        }

        if (valor > saldoInvestido) {
            throw new SaldoInsuficienteException();
        }

        saldoInvestido -= valor;

        adicionarSaldo(valor);

        getTransacoes().add(
            new Transacao("RESGATE", valor)
        );
    }
    
    // Subescreve o método sacar() que já existe na classe Conta.
    // POLIMORFISMO - mesmo metodo() mas com comportamentos diferentes
    @Override 
    public void sacar(double valor)
            throws ValorInvalidoException,
                     ContaBloqueadaException,
                     SaldoInsuficienteException,
                     LimiteDiarioExcedidoException {

        if (isBloqueada()) {
            throw new ContaBloqueadaException();
        }

        if (valor <= 0) {
            throw new ValorInvalidoException();
        }

        double totalSaidasHoje = 0.0;
        LocalDate hoje = LocalDate.now();

        // Usa getTransacoes() para manter o encapsulamento, pois a lista de transações é privada na classe Conta
        for (Transacao transacao : getTransacoes()){

            if ("SAIDA".equals(transacao.getTipo())
                && transacao.getData().toLocalDate().equals(hoje)) {
                totalSaidasHoje += transacao.getValor();
            }
        }

        if (totalSaidasHoje + valor > getLimiteDiario()) {
            throw new LimiteDiarioExcedidoException();
        }

        if (valor <= getSaldo() + limiteChequeEspecial) {
            removerSaldo(valor);

            getTransacoes().add(
                new Transacao("SAIDA", valor)
            );

            return;
        }

        throw new SaldoInsuficienteException();


    }

    public void solicitarEmprestimo(double valor, int prazoMeses)
        throws ValorInvalidoException,
               ContaBloqueadaException,
               OperacaoNaoPermitidaException {

    if (isBloqueada()) {
        throw new ContaBloqueadaException();
    }

    if (valor <= 0) {
        throw new ValorInvalidoException();
    }

    if (prazoMeses != 6 &&
        prazoMeses != 12 &&
        prazoMeses != 24) {

        throw new ValorInvalidoException();
    }

    if (emprestimo != null) {
        throw new OperacaoNaoPermitidaException();
    }

    if (valor > (3 * getSaldo()) + 500) {
        throw new OperacaoNaoPermitidaException(
            "Empréstimo não aprovado no momento"
        );
    }

    emprestimo = new Emprestimo(valor, prazoMeses);

    depositar(valor);
}
}
