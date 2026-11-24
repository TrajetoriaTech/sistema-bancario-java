package controller;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Troca as telas. As telas (.fxml) e o estilo (.css) ficam em resources/view/,
 * por isso o caminho começa com "/view/".
 *
 * Existem dois tipos de troca:
 *  - trocarJanela: troca a janela inteira (login, cadastro, tela principal)
 *  - abrir: troca só a área central da tela principal (Pix, extrato...)
 */
public final class Navegador {

    private static final String ESTILO = "/view/estilo.css";

    private static Stage janela;
    private static BorderPane painelPrincipal;
    private static Label rotuloCliente;

    private Navegador() {
    }

    // Chamado uma vez pelo App: guarda a janela principal (primary stage)
    public static void iniciar(Stage janelaDoApp) {
        janela = janelaDoApp;
    }

    // A tela principal se registra aqui para as outras telas abrirem no meio dela
    static void registrarPainel(BorderPane painel) {
        painelPrincipal = painel;
    }

    // O cabeçalho ("Olá, nome") também se registra, para poder ser atualizado
    static void registrarCabecalho(Label rotulo) {
        rotuloCliente = rotulo;
        atualizarCabecalho();
    }

    // Mostra o nome atual do cliente no topo. Chame depois de alterar o nome.
    public static void atualizarCabecalho() {
        if (rotuloCliente != null && Sessao.getCliente() != null) {
            rotuloCliente.setText("Olá, " + Sessao.getCliente().getNome());
        }
    }

    public static void trocarJanela(Tela tela) {
        Parent raiz = carregar(tela, true);
        if (janela.getScene() == null) {
            Scene cena = new Scene(raiz, 1100, 700);
            cena.getStylesheets().add(Navegador.class.getResource(ESTILO).toExternalForm());
            janela.setScene(cena);
        } else {
            janela.getScene().setRoot(raiz);
        }
    }

    public static void abrir(Tela tela) {
        painelPrincipal.setCenter(carregar(tela, false));
    }

    // Lê o .fxml da tela. Se o arquivo ainda não existe (tela de outro integrante
    // em construção) ou tem erro, mostra um aviso em vez de travar o programa.
    private static Parent carregar(Tela tela, boolean janelaInteira) {
        URL arquivo = Navegador.class.getResource("/view/" + tela.getArquivo());
        if (arquivo == null) {
            return aviso(tela.getTitulo(), "Esta tela ainda não foi criada (" + tela.getArquivo() + ").", janelaInteira);
        }
        try {
            return FXMLLoader.load(arquivo);
        } catch (IOException e) {
            e.printStackTrace(); // detalhe do erro no console, para quem está programando
            return aviso(tela.getTitulo(), "Erro ao abrir " + tela.getArquivo() + ": veja o console do IntelliJ.", janelaInteira);
        }
    }

    private static Parent aviso(String titulo, String mensagem, boolean janelaInteira) {
        Label rotuloTitulo = new Label(titulo);
        rotuloTitulo.getStyleClass().add("titulo-tela");
        Label rotuloMensagem = new Label(mensagem);
        rotuloMensagem.getStyleClass().add("texto-secundario");
        VBox caixa = new VBox(8, rotuloTitulo, rotuloMensagem);
        // Numa janela inteira não existe menu lateral: oferece o caminho de volta
        if (janelaInteira) {
            Hyperlink voltar = new Hyperlink("Voltar ao login");
            voltar.setOnAction(evento -> trocarJanela(Tela.LOGIN));
            caixa.getChildren().add(voltar);
        }
        caixa.setAlignment(Pos.CENTER);
        caixa.getStyleClass().add("conteudo");
        return caixa;
    }
}
