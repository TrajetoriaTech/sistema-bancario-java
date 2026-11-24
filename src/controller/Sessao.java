package controller;

import model.Cliente;
import persistence.ArquivoBanco;
import service.Banco;

/**
 * Guarda o que todas as telas precisam: o banco e o cliente logado.
 * Faz na interface gráfica o papel dos atributos static do Main do console.
 */
public final class Sessao {

    private static Banco banco;
    private static Cliente clienteLogado;

    // Classe utilitária: ninguém cria objeto Sessao
    private Sessao() {
    }

    // Chamado uma vez pelo App, quando o programa abre
    public static void iniciar(Banco bancoCarregado) {
        banco = bancoCarregado;
        clienteLogado = null;
    }

    public static Banco getBanco() {
        return banco;
    }

    public static Cliente getCliente() {
        return clienteLogado;
    }

    public static void entrar(Cliente cliente) {
        clienteLogado = cliente;
    }

    public static void sair() {
        clienteLogado = null;
    }

    // Grava tudo em dados/. Toda tela chama depois de uma operação que deu certo.
    public static void salvar() {
        if (banco != null) {
            ArquivoBanco.salvar(banco);
        }
    }
}
