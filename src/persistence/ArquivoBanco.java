package persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.ValorInvalidoException;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.Emprestimo;
import model.Transacao;
import service.Banco;

/**
 * Salva e carrega os dados do banco em arquivos .txt (Felipe).
 *
 * Três arquivos na pasta dados/, um registro por linha, campos separados por ";":
 *   clientes.txt     nome;cpf;senha;numCorrente;correnteBloqueada;numPoupanca;poupancaBloqueada;valorEmprestimo;prazoEmprestimo
 *   transacoes.txt   numeroConta;tipo;valor;dataHora
 *   notificacoes.txt cpf;mensagem
 *
 * O saldo NÃO é salvo: ao carregar, ele é recalculado a partir das transações
 * (ENTRADA soma, SAIDA subtrai...). Assim o saldo e o extrato nunca ficam diferentes.
 */
public final class ArquivoBanco {

    private static final Path PASTA = Paths.get("dados");
    private static final Path CLIENTES = PASTA.resolve("clientes.txt");
    private static final Path TRANSACOES = PASTA.resolve("transacoes.txt");
    private static final Path NOTIFICACOES = PASTA.resolve("notificacoes.txt");

    private static final String SEPARADOR = ";";
    private static final int SEM_POUPANCA = -1;

    // Classe utilitária: só métodos static, ninguém precisa criar objeto
    private ArquivoBanco() {
    }

    // ------------------------------------------------------------------ salvar

    public static void salvar(Banco banco) {
        try {
            Files.createDirectories(PASTA); // cria a pasta dados/ se não existir

            // try-with-resources: os três arquivos são fechados sozinhos no final,
            // mesmo se der erro no meio (Aula 05)
            try (BufferedWriter arqClientes = Files.newBufferedWriter(CLIENTES, StandardCharsets.UTF_8);
                 BufferedWriter arqTransacoes = Files.newBufferedWriter(TRANSACOES, StandardCharsets.UTF_8);
                 BufferedWriter arqNotificacoes = Files.newBufferedWriter(NOTIFICACOES, StandardCharsets.UTF_8)) {

                for (Cliente cliente : banco.getClientes()) {
                    arqClientes.write(linhaCliente(cliente));
                    arqClientes.newLine();

                    salvarTransacoes(cliente.getContaCorrente(), arqTransacoes);
                    if (cliente.getContaPoupanca() != null) {
                        salvarTransacoes(cliente.getContaPoupanca(), arqTransacoes);
                    }

                    for (String mensagem : cliente.getNotificacoes()) {
                        arqNotificacoes.write(cliente.getCpf() + SEPARADOR + mensagem);
                        arqNotificacoes.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Atenção: não foi possível salvar os dados (" + e.getMessage() + ").");
        }
    }

    private static String linhaCliente(Cliente cliente) {
        ContaCorrente corrente = cliente.getContaCorrente();
        ContaPoupanca poupanca = cliente.getContaPoupanca();
        Emprestimo emprestimo = corrente.getEmprestimo();

        int numPoupanca = (poupanca == null) ? SEM_POUPANCA : poupanca.getNumero();
        boolean poupancaBloqueada = (poupanca != null) && poupanca.isBloqueada();
        double valorEmprestimo = (emprestimo == null) ? 0 : emprestimo.getValor();
        int prazoEmprestimo = (emprestimo == null) ? 0 : emprestimo.getPrazoMeses();

        return String.join(SEPARADOR,
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getSenha(),
                String.valueOf(corrente.getNumero()),
                String.valueOf(corrente.isBloqueada()),
                String.valueOf(numPoupanca),
                String.valueOf(poupancaBloqueada),
                String.valueOf(valorEmprestimo),
                String.valueOf(prazoEmprestimo));
    }

    private static void salvarTransacoes(Conta conta, BufferedWriter arquivo) throws IOException {
        for (Transacao t : conta.getTransacoes()) {
            // String.valueOf(double) sempre usa ponto (150.9), independente do idioma do Windows
            arquivo.write(conta.getNumero() + SEPARADOR + t.getTipo() + SEPARADOR
                    + t.getValor() + SEPARADOR + t.getData());
            arquivo.newLine();
        }
    }

    // ------------------------------------------------------------------ carregar

    public static Banco carregar() {
        Banco banco = new Banco();

        // Primeira vez que o programa roda: ainda não existe arquivo
        if (!Files.exists(CLIENTES)) {
            return banco;
        }

        try {
            // Mapas para achar rápido a conta pelo número e o cliente pelo CPF (Collections)
            Map<Integer, Conta> contasPorNumero = new HashMap<>();
            Map<String, Cliente> clientesPorCpf = new HashMap<>();

            // 1) Clientes e contas
            for (String linha : lerLinhas(CLIENTES)) {
                String[] campo = linha.split(SEPARADOR, -1);

                ContaCorrente corrente = new ContaCorrente(Integer.parseInt(campo[3]));
                ContaPoupanca poupanca = null;
                int numPoupanca = Integer.parseInt(campo[5]);
                if (numPoupanca != SEM_POUPANCA) {
                    poupanca = new ContaPoupanca(numPoupanca);
                }

                Cliente cliente = new Cliente(campo[0], campo[1], campo[2], corrente, poupanca);

                double valorEmprestimo = Double.parseDouble(campo[7]);
                if (valorEmprestimo > 0) {
                    int prazo = Integer.parseInt(campo[8]);
                    corrente.restaurarEmprestimo(new Emprestimo(valorEmprestimo, prazo));
                }

                // O bloqueio é aplicado no fim, depois das transações
                if (Boolean.parseBoolean(campo[4])) {
                    corrente.bloquear();
                }
                if (poupanca != null && Boolean.parseBoolean(campo[6])) {
                    poupanca.bloquear();
                }

                banco.adicionarCliente(cliente);
                clientesPorCpf.put(cliente.getCpf(), cliente);
                contasPorNumero.put(corrente.getNumero(), corrente);
                if (poupanca != null) {
                    contasPorNumero.put(poupanca.getNumero(), poupanca);
                }
            }

            // 2) Transações: recalculam o saldo e refazem o extrato
            for (String linha : lerLinhas(TRANSACOES)) {
                String[] campo = linha.split(SEPARADOR, -1);
                Conta conta = contasPorNumero.get(Integer.parseInt(campo[0]));
                if (conta != null) {
                    Transacao t = new Transacao(campo[1],
                            Double.parseDouble(campo[2]),
                            LocalDateTime.parse(campo[3]));
                    conta.restaurarTransacao(t);
                }
            }

            // 3) Notificações (limite 2 no split: a mensagem pode ter ";" dentro)
            for (String linha : lerLinhas(NOTIFICACOES)) {
                String[] campo = linha.split(SEPARADOR, 2);
                Cliente cliente = clientesPorCpf.get(campo[0]);
                if (cliente != null && campo.length == 2) {
                    cliente.adicionarNotificacao(campo[1]);
                }
            }

            return banco;

        } catch (IOException | ValorInvalidoException | RuntimeException e) {
            // Arquivo corrompido ou editado à mão: começa vazio em vez de quebrar o programa
            System.out.println("Atenção: os dados salvos estão com problema e não foram carregados ("
                    + e.getMessage() + "). O sistema vai começar vazio.");
            return new Banco();
        }
    }

    // Lê todas as linhas não vazias de um arquivo (arquivo inexistente = lista vazia)
    private static List<String> lerLinhas(Path arquivo) throws IOException {
        if (!Files.exists(arquivo)) {
            return List.of();
        }
        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        linhas.removeIf(linha -> linha.isBlank());
        return linhas;
    }
}
