# Sistema Bancário em Java — Simulador de Internet Banking

Projeto em grupo da disciplina **Projeto de Programação** (Prof. Fernando Henrique).

Simulador de internet banking em **Java 21**, com duas interfaces sobre as mesmas regras: uma de **console** e uma **gráfica, em JavaFX**. Aplica Programação Orientada a Objetos, tratamento de exceções, coleções, leitura/escrita de arquivos e interface gráfica.

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

O projeto usa **Maven** só para baixar o JavaFX: não precisa instalar nada além do JDK.

1. Instalar o **JDK 21**
2. Abrir a pasta do projeto no **IntelliJ IDEA**. Se aparecer *"Maven build script found"*, clicar em **Load Maven Project** (ou botão direito no `pom.xml` → *Add as Maven Project*)
3. Em *File → Project Structure*, conferir o SDK **21**
4. Rodar a versão desejada (▶ ou `Shift+F10`):
   - **Interface gráfica:** `src/application/Launcher.java`
   - **Console:** `src/app/Main.java`

Pelo terminal, a interface gráfica também roda com `mvn javafx:run`.

## Estrutura das pastas

A interface gráfica segue o padrão **MVC** (Model, View, Controller):

```
src/
 ├── application/ Inicia a interface gráfica: Launcher (main) e App (extends Application)
 ├── controller/  CONTROLLER: liga cada tela ao model (LoginController, BoletoController...)
 │                e as classes de apoio das telas (Navegador, Sessao, Mensagem, Formato, Tela)
 ├── model/       MODEL: Conta, ContaCorrente, ContaPoupanca, Cliente, Transacao, Emprestimo
 ├── service/     MODEL: Banco, regras que envolvem mais de uma conta (login, cadastro, transferência, Pix)
 ├── exception/   Exceções personalizadas (saldo insuficiente, conta bloqueada, etc.)
 ├── persistence/ ArquivoBanco: salva e carrega os dados em arquivos .txt
 ├── util/        Entrada: validação do que é digitado, usada pelo console e pela interface gráfica
 └── app/         Interface de console (1ª parte): menus, leitura do teclado e mensagens
resources/
 └── view/        VIEW: as telas (.fxml) e o estilo visual (estilo.css)
```

| Quero mudar... | Onde mexo |
|---|---|
| Um componente da tela (botão, campo, tabela) | `.fxml` em `resources/view/` (Scene Builder) |
| A aparência (cor, fonte, cursor, efeito ao passar o mouse) | `resources/view/estilo.css` |
| O que acontece ao clicar num botão | O controller da tela, em `src/controller/` |
| Uma regra de negócio (saldo, limite, juros) | `src/model/` ou `src/service/` |

A separação em camadas garante que **nenhuma regra de negócio fica na tela** (`app/`, `controller/` e `view/`). As regras ficam em `model/` e `service/`, e por isso a interface gráfica foi criada sem mudar nenhuma regra: o console e a interface gráfica chamam os mesmos métodos.

## Onde os dados ficam salvos

Os dados são salvos automaticamente em arquivos `.txt` na pasta `dados/`, na raiz do projeto, e carregados quando o programa abre. O console e a interface gráfica usam os mesmos arquivos:

| Arquivo | Conteúdo |
|---|---|
| `clientes.txt` | Clientes, números das contas, bloqueio e empréstimo |
| `transacoes.txt` | Todas as transações (o saldo é recalculado a partir delas) |
| `notificacoes.txt` | Notificações de valores recebidos |

Para começar do zero, basta apagar a pasta `dados/`. Ela não vai para o GitHub (está no `.gitignore`).

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
| Felipe | Áreas extras: menus de Pix, boleto, investimentos e empréstimo; salvamento em arquivo |
| Daniel | Menu principal e integração |
