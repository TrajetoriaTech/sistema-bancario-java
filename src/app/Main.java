package app;

import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.Transacao;
import service.Banco;

import java.util.List;
import java.util.Scanner;

/**
 * Menu principal e integração (Daniel).
 * Camada app/: só lê entrada, chama service/ e model/ e trata exceções.
 * Nenhuma regra de negócio aqui.
 */
public class Main {

    private static final Scanner in = new Scanner(System.in);
    private static final Banco banco = new Banco();
    private static Cliente clienteLogado = null;

    public static void main(String[] args) {
        boolean rodando = true;
        while (rodando) {
            if (clienteLogado == null) {
                rodando = telaInicial();
            } else {
                menuPrincipal();
            }
        }
        System.out.println("Até logo!");
    }

    // ---------------------------------------------------------------- tela inicial

    private static boolean telaInicial() {
        System.out.println("\n=== INTERNET BANKING ===");
        System.out.println("1) Entrar");
        System.out.println("2) Criar conta");
        System.out.println("0) Sair");
        String op = ler("Opção: ");
        switch (op) {
            case "1": login(); return true;
            case "2": cadastrar(); return true;
            case "0": return false;
            default: System.out.println("Opção inválida."); return true;
        }
    }

    private static void login() {
        try {
            String id = ler("CPF ou número da conta corrente: ");
            String senha = ler("Senha: ");
            clienteLogado = banco.login(id, senha);
            System.out.println("Bem-vindo(a), " + clienteLogado.getNome() + "!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void cadastrar() {
        try {
            String nome = ler("Nome: ");
            String cpf = ler("CPF: ");
            String senha = ler("Senha: ");
            boolean poupanca = ler("Deseja abrir poupança também? (s/n): ").equalsIgnoreCase("s");
            Cliente novo = banco.cadastrarCliente(nome, cpf, senha, poupanca);
            System.out.println("Conta criada! Número da sua conta corrente: "
                    + novo.getContaCorrente().getNumero());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- menu principal

    private static void menuPrincipal() {
        int qtdNotif = clienteLogado.getNotificacoes().size();
        System.out.println("\n=== MENU — " + clienteLogado.getNome() + " ===");
        System.out.println(" 1) Consultar saldo");
        System.out.println(" 2) Ver extrato");
        System.out.println(" 3) Depositar");
        System.out.println(" 4) Sacar");
        System.out.println(" 5) Transferir");
        System.out.println(" 6) Pix");
        System.out.println(" 7) Pagar boleto");
        System.out.println(" 8) Investimentos");
        System.out.println(" 9) Empréstimo");
        System.out.println("10) Notificações (" + qtdNotif + ")");
        System.out.println("11) Solicitar cartão de crédito");
        System.out.println("12) Bloquear/desbloquear conta");
        System.out.println("13) Perfil / configurações");
        System.out.println("14) Encerrar conta");
        System.out.println(" 0) Logout");
        String op = ler("Opção: ");
        switch (op) {
            case "1": consultarSaldo(); break;
            case "2": verExtrato(); break;
            case "3": depositar(); break;
            case "4": sacar(); break;
            case "5": transferir(); break;
            case "6": /* TODO Felipe: chamar menu do Pix */ emIntegracao(); break;
            case "7": /* TODO Felipe: chamar menu do boleto */ emIntegracao(); break;
            case "8": /* TODO Felipe: chamar menu de investimentos */ emIntegracao(); break;
            case "9": /* TODO Felipe: chamar menu de empréstimo */ emIntegracao(); break;
            case "10": notificacoes(); break;
            case "11": cartaoDeCredito(); break;
            case "12": bloquearDesbloquear(); break;
            case "13": menuPerfil(); break;
            case "14": encerrarConta(); break;
            case "0": logout(); break;
            default: System.out.println("Opção inválida.");
        }
    }

    private static void emIntegracao() {
        System.out.println("Esta área ainda está sendo integrada.");
    }

    // ---------------------------------------------------------------- operações de conta

    private static void consultarSaldo() {
        Conta c = escolherConta("Consultar saldo de qual conta?");
        System.out.println(String.format("Saldo atual: R$ %.2f", c.getSaldo()));
    }

    private static void verExtrato() {
        Conta c = escolherConta("Extrato de qual conta?");
        System.out.println(c.extrato());
    }

    private static void depositar() {
        try {
            Conta c = escolherConta("Depositar em qual conta?");
            c.depositar(lerValor());
            System.out.println("Depósito realizado.");
            mostrarComprovante(c);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void sacar() {
        try {
            Conta c = escolherConta("Sacar de qual conta?");
            c.sacar(lerValor());
            System.out.println("Saque realizado.");
            mostrarComprovante(c);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void transferir() {
        try {
            Conta origem = escolherConta("Conta de origem:");
            System.out.println("Destino: 1) Minha outra conta  2) Conta corrente de outra pessoa");
            String op = ler("Opção: ");
            Conta destino;
            if (op.equals("1")) {
                ContaCorrente cc = clienteLogado.getContaCorrente();
                ContaPoupanca cp = clienteLogado.getContaPoupanca();
                destino = (origem == cc) ? cp : cc;
                if (destino == null) {
                    System.out.println("Você não tem outra conta.");
                    return;
                }
            } else if (op.equals("2")) {
                int numero = Integer.parseInt(ler("Número da conta corrente de destino: ").trim());
                destino = banco.buscarClientePorNumero(numero).getContaCorrente();
            } else {
                System.out.println("Opção inválida.");
                return;
            }
            banco.transferir(origem, destino, lerValor());
            System.out.println("Transferência realizada.");
            mostrarComprovante(origem);
        } catch (NumberFormatException e) {
            System.out.println("Erro: digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void bloquearDesbloquear() {
        Conta c = escolherConta("Qual conta?");
        if (c.isBloqueada()) {
            c.desbloquear();
            System.out.println("Conta desbloqueada.");
        } else {
            c.bloquear();
            System.out.println("Conta bloqueada.");
        }
    }

    // ---------------------------------------------------------------- notificações e cartão

    private static void notificacoes() {
        List<String> lista = clienteLogado.getNotificacoes();
        if (lista.isEmpty()) {
            System.out.println("Você não tem notificações.");
            return;
        }
        System.out.println("--- Notificações ---");
        for (String n : lista) {
            System.out.println("- " + n);
        }
    }

    private static void cartaoDeCredito() {
        System.out.println("Sua solicitação de cartão de crédito foi enviada e será analisada.");
    }

    // ---------------------------------------------------------------- perfil

    private static void menuPerfil() {
        System.out.println("\n--- Perfil ---");
        System.out.println("1) Alterar nome");
        System.out.println("2) Alterar senha");
        System.out.println("3) Abrir poupança");
        System.out.println("0) Voltar");
        String op = ler("Opção: ");
        try {
            switch (op) {
                case "1":
                    clienteLogado.alterarNome(ler("Novo nome: "));
                    System.out.println("Nome alterado.");
                    break;
                case "2":
                    String atual = ler("Senha atual: ");
                    String nova = ler("Nova senha: ");
                    clienteLogado.alterarSenha(atual, nova);
                    System.out.println("Senha alterada.");
                    break;
                case "3":
                    // O número da poupança é gerado pelo Banco (regra do Francisco);
                    // aqui só se chama o que o documento define.
                    System.out.println("TODO: integrar com Francisco (como obter o número da nova poupança).");
                    break;
                case "0": break;
                default: System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void encerrarConta() {
        try {
            String r = ler("Tem certeza que deseja encerrar a conta? (s/n): ");
            if (!r.equalsIgnoreCase("s")) return;
            banco.encerrarConta(clienteLogado);
            System.out.println("Conta encerrada.");
            clienteLogado = null; // logout automático
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void logout() {
        clienteLogado = null;
        System.out.println("Você saiu da conta.");
    }

    // ---------------------------------------------------------------- auxiliares de entrada

    /** Só escolhe entre as contas do cliente (entrada do usuário, sem regra de negócio). */
    private static Conta escolherConta(String pergunta) {
        ContaPoupanca cp = clienteLogado.getContaPoupanca();
        if (cp == null) {
            return clienteLogado.getContaCorrente();
        }
        while (true) {
            System.out.println(pergunta + " 1) Corrente  2) Poupança");
            String op = ler("Opção: ").trim();
            if (op.equals("1")) return clienteLogado.getContaCorrente();
            if (op.equals("2")) return cp;
            System.out.println("Opção inválida. Digite 1 ou 2.");
        }
    }

    private static void mostrarComprovante(Conta c) {
        List<Transacao> ts = c.getTransacoes();
        if (!ts.isEmpty()) {
            System.out.println(ts.get(ts.size() - 1).gerarComprovante());
        }
    }

    private static double lerValor() {
        String s = ler("Valor: R$ ").trim().replace(",", ".");
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Digite um número válido.");
        }
    }

    private static String ler(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }
}
