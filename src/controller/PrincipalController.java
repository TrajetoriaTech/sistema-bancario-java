package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Controller da tela principal (principal.fxml): monta o menu lateral,
 * mostra o nome do cliente e abre cada tela na área central.
 */
public class PrincipalController {

    @FXML private BorderPane painel;
    @FXML private VBox menu;
    @FXML private Label rotuloCliente;

    private Button botaoAtivo;

    // O JavaFX chama o initialize() sozinho, depois de ligar os @FXML
    @FXML
    private void initialize() {
        Navegador.registrarPainel(painel);
        Navegador.registrarCabecalho(rotuloCliente);

        adicionarGrupo("Conta", Tela.INICIO, Tela.EXTRATO, Tela.DEPOSITO, Tela.SAQUE);
        adicionarGrupo("Pagamentos", Tela.TRANSFERENCIA, Tela.PIX, Tela.BOLETO);
        adicionarGrupo("Serviços", Tela.INVESTIMENTOS, Tela.EMPRESTIMO, Tela.CARTAO);
        adicionarGrupo("Minha conta", Tela.NOTIFICACOES, Tela.BLOQUEIO, Tela.PERFIL);

        // Abre o Início: é o primeiro botão do menu
        ((Button) menu.getChildren().get(1)).fire();
    }

    // "Tela... telas" (varargs): o método aceita quantas telas forem passadas
    private void adicionarGrupo(String nomeDoGrupo, Tela... telas) {
        Label rotulo = new Label(nomeDoGrupo.toUpperCase());
        rotulo.getStyleClass().add("grupo-menu");
        menu.getChildren().add(rotulo);

        for (Tela tela : telas) {
            Button botao = new Button(tela.getTitulo());
            botao.getStyleClass().add("item-menu");
            botao.setMaxWidth(Double.MAX_VALUE);
            botao.setOnAction(evento -> abrir(tela, botao));
            menu.getChildren().add(botao);
        }
    }

    private void abrir(Tela tela, Button botao) {
        if (botaoAtivo != null) {
            botaoAtivo.getStyleClass().remove("item-menu-ativo");
        }
        botao.getStyleClass().add("item-menu-ativo");
        botaoAtivo = botao;
        Navegador.abrir(tela);
    }

    @FXML
    private void sair() {
        Sessao.salvar();
        Sessao.sair();
        Navegador.trocarJanela(Tela.LOGIN);
    }
}
