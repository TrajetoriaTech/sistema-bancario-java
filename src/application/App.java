package application;

import controller.Navegador;
import controller.Sessao;
import controller.Tela;
import javafx.application.Application;
import javafx.stage.Stage;
import persistence.ArquivoBanco;

/**
 * A aplicação JavaFX. O JavaFX chama o start() e entrega a janela principal
 * (o "primary stage"). Aqui: abre os dados salvos, cria a primeira tela
 * (login) e salva tudo quando a janela fecha.
 *
 * Quem carrega o .fxml, cria a Scene e aplica o CSS é o Navegador
 * (pacote controller), para todas as telas passarem pelo mesmo lugar.
 */
public class App extends Application {

    @Override
    public void start(Stage janela) {
        // Mesmos dados do console: os dois usam a pasta dados/
        Sessao.iniciar(ArquivoBanco.carregar());

        janela.setTitle("Internet Banking");
        janela.setMinWidth(900);
        janela.setMinHeight(600);

        // Garante que nada se perde ao fechar no X
        janela.setOnCloseRequest(evento -> Sessao.salvar());

        Navegador.iniciar(janela);
        Navegador.trocarJanela(Tela.LOGIN);
        janela.show();
    }
}
