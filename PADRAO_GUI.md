# Padrão da interface gráfica (JavaFX)

Guia do grupo para a 2ª parte do projeto. A base já está pronta:
login, menu lateral, Início e a **tela modelo (Pagar boleto)**.
Cada um cria as telas da sua parte seguindo este padrão.

## 1. Como rodar

1. `git pull` na `main` (depois que a base for aceita).
2. IntelliJ: clique com o botão direito no `pom.xml` → **Add as Maven Project**
   (ou "Load Maven Project" no aviso que aparece). Ele baixa o JavaFX sozinho.
3. Rode `src/application/Launcher.java` (interface gráfica)
   ou `src/app/Main.java` (console, continua funcionando).
4. Login de teste: use um CPF/conta que já esteja em `dados/`.

> Rode sempre pelo **Launcher**, não pelo `App`. Rodar o `App` direto dá o erro
> "JavaFX runtime components are missing".

Console e GUI usam os mesmos arquivos de `dados/`, então o que você faz
em um aparece no outro.

## 2. Estrutura do projeto (padrão MVC)

```
src/
 ├── application/   Launcher (main) e App (extends Application): só inicia o JavaFX
 ├── controller/    CONTROLLER: um XxxController por tela + classes de apoio
 │                  (Navegador, Sessao, Mensagem, Formato, Tela)
 ├── model/         MODEL: Conta, ContaCorrente, Cliente, Transacao...
 ├── service/       MODEL: Banco (login, cadastro, transferência, Pix)
 ├── exception/     Exceções personalizadas
 ├── persistence/   ArquivoBanco (salva em dados/)
 ├── util/          Entrada (validação do que é digitado)
 └── app/           Console da 1ª parte (não mexer)
resources/
 └── view/          VIEW: telas .fxml e estilo.css
```

**Como a aplicação funciona:**

1. O `Launcher.main` chama `Application.launch(App.class)`.
2. O JavaFX chama o `App.start(janela)` e entrega a janela principal (*primary stage*).
3. O `Navegador` carrega o `.fxml` com o `FXMLLoader`. O arquivo vira o componente
   **root** (o layout que agrupa todos os outros).
4. Na primeira tela, o `Navegador` cria a `Scene` com esse root e adiciona o `estilo.css`
   na scene (`getStylesheets().add(...)`). A `Scene` vai para a janela e o `App` chama `show()`.
5. Ao carregar o `.fxml`, o JavaFX cria o **controller** indicado em `fx:controller`,
   injeta os componentes nos atributos `@FXML` e chama o `initialize()`.

**Onde mexer em cada coisa:**

| Quero mudar... | Onde mexo |
|---|---|
| Criar ou mudar um componente (botão, campo, tabela) | `.fxml` em `resources/view/` (Scene Builder) |
| Aparência: cor, fonte, cursor de mão, cor ao passar o mouse | `resources/view/estilo.css` |
| O que acontece ao clicar num botão | O controller da tela, em `src/controller/` |
| Regra de negócio (saldo, limite, juros, bloqueio) | `src/model/` ou `src/service/` |

## 3. Divisão das telas

| Pessoa | Telas (`resources/view/xxx.fxml` + `src/controller/XxxController.java`) | Equivalente no console | Métodos do model/service |
|---|---|---|---|
| **Felipe** | `boleto` ✅ (modelo), `pix`, `investimentos`, `emprestimo` | `MenuBoleto`, `MenuPix`, `MenuInvestimentos`, `MenuEmprestimo` | `sacar`, `banco.pix`, `investir`, `resgatar`, `simularRendimentoInvestimento`, `solicitarEmprestimo` |
| **Alan** | `extrato` (com `TableView`), `transferencia` | `verExtrato`, `consultarSaldo`, `transferir` | `getTransacoes`, `getSaldo`, `banco.buscarClientePorNumero`, `banco.transferir` |
| **Breno** | `deposito`, `saque`, `bloqueio` | `depositar`, `sacar`, `bloquearDesbloquear` | `depositar`, `sacar`, `bloquear`, `desbloquear` |
| **Daniel** | `notificacoes` (com `ListView`), `cartao`, ajustes no `principal`/`inicio` e no `estilo.css` | `notificacoes`, `cartaoDeCredito` | `getNotificacoes`, `adicionarNotificacao` |
| **Francisco** | `cadastro`, `perfil` (alterar nome/senha, abrir poupança, encerrar conta), ajustes no `login` | `cadastrar`, `menuPerfil`, `encerrarConta` | `banco.cadastrarCliente`, `alterarNome`, `alterarSenha`, `banco.abrirPoupanca`, `banco.encerrarConta` |

Os nomes dos arquivos **já estão definidos** no enum `Tela`
(ex.: `Tela.PIX` → `pix.fxml`). Enquanto o arquivo não existe, o menu mostra
"Esta tela ainda não foi criada". Assim que você cria o `.fxml` com o nome
certo em `resources/view/`, a tela aparece sozinha. Não precisa mexer no menu.

## 4. O que já existe (use, não reescreva)

| Classe (pacote `controller`) | Para que serve | Como usar |
|---|---|---|
| `Sessao` | Guarda o banco e o cliente logado | `Sessao.getCliente()`, `Sessao.getBanco()`, `Sessao.salvar()` |
| `Navegador` | Troca de tela | `Navegador.abrir(Tela.EXTRATO)` (dentro do menu), `Navegador.trocarJanela(Tela.LOGIN)` (janela inteira) |
| `Mensagem` | Mensagem de erro/sucesso e confirmação | `Mensagem.erro(rotulo, "...")`, `Mensagem.sucesso(rotulo, "...")`, `Mensagem.limpar(rotulo)`, `Mensagem.confirmar("Título", "Pergunta?")` |
| `Formato` | Formatação para mostrar na tela | `Formato.dinheiro(150.9)` → "R$ 150,90", `Formato.nomeDaConta(conta)`, `Formato.ultimoComprovante(conta)`, `Formato.preencherContas(combo, cliente)` |
| `util.Entrada` | Validação de formato (a mesma do console) | `Entrada.converterValor("150,90")`, `Entrada.limparCpf(...)`, `Entrada.cpfValido(...)` |
| `estilo.css` | Visual de todas as telas | Use as `styleClass` da seção 7 |

**Arquivos da base:** `pom.xml`, `application/` (`App`, `Launcher`), `Sessao`, `Navegador`,
`Tela`, `Mensagem`, `Formato`, `principal.fxml`, `estilo.css`. Se precisar mudar algum,
**avise no grupo antes**: todo mundo depende deles e é onde dá conflito no Git.

## 5. Passo a passo para criar uma tela

1. Copie os dois arquivos do modelo e renomeie, **cada um na sua pasta**:
   - `resources/view/boleto.fxml` → `resources/view/deposito.fxml`
   - `src/controller/BoletoController.java` → `src/controller/DepositoController.java`
2. No `.fxml`, troque `fx:controller="controller.BoletoController"` pelo seu controller
   (ex.: `controller.DepositoController`). É esse caminho que liga a tela ao controller.
3. Abra o `.fxml` no **Scene Builder** (botão direito → Open in SceneBuilder) e monte
   os componentes:
   - Campo que o Java precisa **ler ou mudar** → preencha o **fx:id** (ex.: `campoValor`).
   - Botão → preencha o **On Action** com o nome do método (ex.: `depositar`).
     O botão só precisa de `fx:id` se o controller for mexer nele (ex.: desabilitar).
4. No controller, declare os componentes com `@FXML` e **o mesmo nome do fx:id**:
   ```java
   @FXML private TextField campoValor;
   @FXML private Label rotuloMensagem;
   ```
5. Escreva o método do botão (`@FXML private void depositar()`) seguindo o padrão abaixo.
6. Rode pelo `Launcher` e teste os casos de erro também.

**Nunca:** criar `src/main/java` ou `src/main/resources`, criar `module-info.java`,
montar a tela em Java puro (sem FXML) ou mudar o `pom.xml`. Isso quebra a estrutura.

## 6. O padrão de toda tela de operação (5 passos)

É o mesmo raciocínio dos menus do console, só que lendo dos campos e
mostrando a resposta num `Label` em vez do `System.out`.

```java
@FXML
private void depositar() {
    // 1) Validar o FORMATO do que foi digitado (if / NumberFormatException)
    double valor;
    try {
        valor = Entrada.converterValor(campoValor.getText());
    } catch (NumberFormatException e) {
        Mensagem.erro(rotuloMensagem, "Valor inválido: digite um número, ex.: 150,90.");
        return;
    }

    // 2) Confirmar antes de mexer em dinheiro
    if (!Mensagem.confirmar("Depositar", "Depositar " + Formato.dinheiro(valor) + "?")) {
        return;
    }

    // 3) Chamar o model/service: a REGRA fica lá, não aqui
    Conta conta = comboConta.getValue();
    try {
        conta.depositar(valor);
        // 5) Deu certo: salvar e mostrar o comprovante
        Sessao.salvar();
        Mensagem.sucesso(rotuloMensagem, "Depósito feito! " + Formato.ultimoComprovante(conta));
        campoValor.clear();
    // 4) Um catch para cada exceção, cada um com sua mensagem
    } catch (ValorInvalidoException e) {
        Mensagem.erro(rotuloMensagem, "O valor precisa ser maior que zero.");
    }
}
```

**Regras:**

- **Nada de regra de negócio no controller.** Saldo, limite diário, bloqueio,
  juros etc. já estão no `model`/`service`. O controller só faz a ligação: lê a tela,
  chama o método e mostra o resultado.
- **`Sessao.salvar()` depois de toda operação que deu certo**, senão a mudança
  se perde se o programa fechar.
- **Um `catch` por exceção** com mensagem clara para o usuário.
  Nunca `catch (Exception e)` genérico.
- **Escolher conta:** use um `ComboBox<Conta>` e `Formato.preencherContas(combo, Sessao.getCliente())`
  no `initialize()`. Ele já mostra "Conta corrente 1001 · R$ 500,00".
- **`initialize()`** roda sozinho quando a tela abre: é onde se preenche combo,
  tabela, saldo etc.
- Telas que só **mostram** dados (extrato, notificações) não precisam de
  confirmação nem de `salvar()`.

## 7. Visual (classes do `estilo.css`)

Use `styleClass` em vez de cor/fonte direto no FXML, para todas as telas ficarem iguais.

| Elemento | styleClass |
|---|---|
| Raiz da tela (VBox) | `conteudo` |
| Título da tela | `titulo-tela` |
| Texto cinza de apoio | `texto-secundario` |
| Caixa branca do formulário | `formulario` (com `maxWidth="520"`) |
| Nome em cima de um campo | `rotulo-campo` |
| Botão principal (Confirmar, Pagar...) | `botao-principal` (+ `defaultButton="true"` para o Enter funcionar) |
| Botão secundário (Limpar, Cancelar) | `botao-secundario` |
| Cartões de resumo (como no Início) | `cartao`, `rotulo-cartao`, `valor-cartao` |
| Label de mensagem | não precisa: o `Mensagem` coloca sozinho |

O efeito ao passar o mouse (`:hover`) e o cursor de mão (`-fx-cursor: hand`) já estão
definidos em `.botao-principal`, `.botao-secundario` e `.item-menu`. Vale só para os
botões com essa classe. Para valer em todos os botões, seria a regra `.button`.

## 8. Exemplo de lista (para Extrato e Notificações)

```java
@FXML private TableView<Transacao> tabela;
@FXML private TableColumn<Transacao, String> colunaTipo;
@FXML private TableColumn<Transacao, String> colunaValor;

@FXML
private void initialize() {
    Conta conta = Sessao.getCliente().getContaCorrente();
    colunaTipo.setCellValueFactory(t -> new SimpleStringProperty(t.getValue().getTipo()));
    colunaValor.setCellValueFactory(t -> new SimpleStringProperty(Formato.dinheiro(t.getValue().getValor())));
    tabela.getItems().setAll(conta.getTransacoes());
}
```

Para Notificações, um `ListView<String>` com
`lista.getItems().setAll(Sessao.getCliente().getNotificacoes())` já resolve.

## 9. Git (para não dar conflito)

```bash
git checkout main
git pull
git checkout -b feature/gui-<seu-nome>      # ex.: feature/gui-breno
# ... cria suas telas ...
git add resources/view/deposito.fxml src/controller/DepositoController.java   # só os SEUS arquivos
git commit -m "Tela de depósito"
git push -u origin feature/gui-<seu-nome>
```

Depois abra o Pull Request para a `main`.

- **Nunca use `git add .`**: entra lixo (pasta `target/`, arquivos do IntelliJ etc.).
- Mexeu em arquivo da base (seção 4)? Avise no grupo antes.

## 10. Checklist antes do Pull Request

- [ ] O `.fxml` está em `resources/view/` e o nome é exatamente o do enum `Tela`.
- [ ] O controller está em `src/controller/` com `package controller;`.
- [ ] O `fx:controller` do `.fxml` aponta para `controller.MeuController`.
- [ ] Todo `fx:id` tem um `@FXML` com o mesmo nome no controller.
- [ ] Testei o caso certo **e** os casos de erro (valor vazio, letra, valor negativo, conta bloqueada...).
- [ ] Chamo `Sessao.salvar()` depois de cada operação que deu certo.
- [ ] Nenhuma regra de negócio no controller.
- [ ] Usei as `styleClass` do `estilo.css`.
- [ ] Sei explicar cada linha da minha tela (o professor vai perguntar).
