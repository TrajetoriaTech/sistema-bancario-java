package util;

/**
 * Validações e conversões do que o usuário digita.
 * Usada pelo console (pacote app) e pela interface gráfica (pacote controller),
 * para as duas telas aceitarem exatamente os mesmos formatos.
 * Só valida FORMATO de entrada: regra de negócio fica no model/service.
 */
public final class Entrada {

    public static final int TAMANHO_CPF = 11;
    public static final int TAMANHO_BOLETO = 44;

    // Classe utilitária: só métodos static, ninguém precisa criar objeto
    private Entrada() {
    }

    // Converte o texto digitado em valor. Aceita vírgula ou ponto (150,90 ou 150.90).
    // Texto que não é número: o próprio Java lança NumberFormatException.
    public static double converterValor(String texto) {
        return Double.parseDouble(texto.trim().replace(",", "."));
    }

    // Tira ponto, traço e espaço do CPF: "111.222.333-44" vira "11122233344"
    public static String limparCpf(String cpf) {
        return cpf.replace(".", "").replace("-", "").replace(" ", "");
    }

    // CPF já limpo com exatamente 11 números
    public static boolean cpfValido(String cpf) {
        return somenteDigitos(cpf, TAMANHO_CPF);
    }

    // Código de barras do boleto (sem espaços) com exatamente 44 números
    public static boolean codigoBoletoValido(String codigo) {
        return somenteDigitos(codigo, TAMANHO_BOLETO);
    }

    // Confere se o texto tem o tamanho certo e só tem dígitos
    private static boolean somenteDigitos(String texto, int tamanho) {
        if (texto.length() != tamanho) {
            return false;
        }
        for (int i = 0; i < texto.length(); i++) {
            if (!Character.isDigit(texto.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
