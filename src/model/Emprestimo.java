package model;

import exception.ValorInvalidoException;

public class Emprestimo {

    private double valor;
    private int prazoMeses;
    private double valorParcela;

    public Emprestimo(double valor, int prazoMeses)
            throws ValorInvalidoException {

        if(valor <= 0) {
            throw new ValorInvalidoException();
        }
        

        if (prazoMeses != 6 &&
            prazoMeses != 12 &&
            prazoMeses != 24) {

                throw new ValorInvalidoException();
        }

        this.valor = valor;
        this.prazoMeses = prazoMeses;
        this.valorParcela = (valor * 1.10) / prazoMeses;
    }

    public double getValor() {
        return valor;
    }

    public int getPrazoMeses() {
        return prazoMeses;
    }

    public double getValorParcela() {
        return valorParcela;
    }
        
    
}