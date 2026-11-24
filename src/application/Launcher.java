package application;

import javafx.application.Application;

/**
 * Ponto de entrada da interface gráfica. Rode esta classe no IntelliJ.
 *
 * Por que existe uma classe só para isso: quando a classe com o main
 * herda de Application, o Java exige o JavaFX configurado como módulo
 * e dá o erro "JavaFX runtime components are missing". Chamando o
 * Application.launch a partir de uma classe comum, o erro não acontece.
 */
public class Launcher {

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
