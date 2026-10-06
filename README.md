# Sistema Bancário em Java — Simulador de Internet Banking

Projeto em grupo da disciplina **Projeto de Programação** (Prof. Fernando Henrique).

Simulador de internet banking que roda no **console**, feito em **Java puro** (sem bibliotecas externas), aplicando Programação Orientada a Objetos, tratamento de exceções, coleções e leitura/escrita de arquivos.

## Funcionalidades

| Área | O que dá para fazer |
|---|---|
| Acesso | Criar conta, entrar com CPF ou número da conta corrente, sair (logout) |
| Conta | Consultar saldo, ver extrato, depositar, sacar, transferir |
| Pix | Enviar Pix por CPF, escolhendo se cai na conta corrente ou na poupança do destinatário |
| Boleto | Pagar boleto pelo código de barras de 44 números |
| Investimentos | Investir, resgatar, simular rendimento e render juros da poupança |
| Empréstimo | Solicitar empréstimo (6, 12 ou 24 meses) e ver as parcelas |
| Segurança | Limite diário de saídas, bloquear e desbloquear a conta |
| Outros | Comprovante de cada operação, notificações de valores recebidos, solicitar cartão de crédito |
| Perfil | Alterar nome, alterar senha, abrir poupança, encerrar a conta |

## Como rodar

1. Instalar o **JDK 21**
2. Abrir a pasta do projeto no **IntelliJ IDEA**
3. Em *File → Project Structure*, escolher o SDK **21**
4. Clicar com o botão direito na pasta `src` → *Mark Directory as* → **Sources Root**
5. Abrir `src/app/Main.java` e rodar (▶ ou `Shift+F10`)

## Estrutura das pastas

```
src/
 ├── app/         Telas do console: menus, leitura do teclado e mensagens
 ├── model/       Classes do domínio: Conta, ContaCorrente, ContaPoupanca, Cliente, Transacao, Emprestimo
 ├── service/     Banco: regras que envolvem mais de uma conta (login, cadastro, transferência, Pix)
 └── exception/   Exceções personalizadas (saldo insuficiente, conta bloqueada, etc.)
```

A separação em camadas garante que **nenhuma regra de negócio fica na tela** (`app/`). As regras ficam em `model/` e `service/`, então a interface pode mudar sem mexer nelas.

## Regras principais

- Conta corrente tem **cheque especial de R$ 500**
- **Limite diário de R$ 2.000** em saídas (saque, transferência, Pix e boleto)
- Conta bloqueada não deixa sair dinheiro, mas continua recebendo
- Empréstimo com **10% de juros** no total, um por vez
- Poupança rende **0,5%** e o investimento **0,8%** ao mês (acionados manualmente, porque o sistema não simula a passagem do tempo)

## Integrantes

| Integrante | Responsabilidade |
|---|---|
| Breno | Contas: `Conta`, `ContaCorrente`, `ContaPoupanca`, `Emprestimo` |
| Francisco | Cliente, cadastro e login |
| Alan | Transações: `Transacao`, transferência e Pix |
| Felipe | Áreas extras: menus de Pix, boleto, investimentos e empréstimo |
| Daniel | Menu principal e integração |
