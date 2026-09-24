package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Transacao {

    // Formato de data usado no extrato e no comprovante: 23/09/2026 14:32
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Locale brasileiro: faz o valor sair com vírgula decimal (500,00)
    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    // ENTRADA | SAIDA | INVESTIMENTO | RESGATE
    // só SAIDA conta para o limite diário
    private final String tipo;
    private final double valor;
    private final LocalDateTime data;

    public Transacao(String tipo, double valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.data = LocalDateTime.now(); // data = agora
    }

    // Uma linha no formato: 23/09/2026 14:32 | ENTRADA | R$ 500,00
    public String gerarComprovante() {
        String dataFormatada = data.format(FORMATO_DATA);
        String valorFormatado = String.format(BRASIL, "%.2f", valor);
        return dataFormatada + " | " + tipo + " | R$ " + valorFormatado;
    }

    public String getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getData() {
        return data;
    }
}