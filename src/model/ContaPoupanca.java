package model;

//Herança: ContaPoupanca é uma subclasse de Conta
public class ContaPoupanca extends Conta {

    private double taxaRendimento;

    public ContaPoupanca(int numero) {
        super(numero);
        this.taxaRendimento = 0.005;
    }

    public double getTaxaRendimento() {
        return taxaRendimento;
    }

    public void renderJuros() {

        double rendimento = getSaldo() * taxaRendimento;

        adicionarSaldo(rendimento);

        getTransacoes().add(
            new Transacao("ENTRADA", rendimento)
        );
    }
}