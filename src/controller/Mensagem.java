package controller;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

/**
 * Jeito padrão de dar retorno ao usuário em todas as telas:
 *  - erro / sucesso: texto colorido num Label da própria tela
 *  - confirmar: janelinha de "Confirmar / Cancelar" antes de mexer em dinheiro
 */
public final class Mensagem {

    private Mensagem() {
    }

    public static void erro(Label rotulo, String texto) {
        rotulo.setText(texto);
        rotulo.getStyleClass().removeAll("mensagem-sucesso");
        if (!rotulo.getStyleClass().contains("mensagem-erro")) {
            rotulo.getStyleClass().add("mensagem-erro");
        }
    }

    public static void sucesso(Label rotulo, String texto) {
        rotulo.setText(texto);
        rotulo.getStyleClass().removeAll("mensagem-erro");
        if (!rotulo.getStyleClass().contains("mensagem-sucesso")) {
            rotulo.getStyleClass().add("mensagem-sucesso");
        }
    }

    public static void limpar(Label rotulo) {
        rotulo.setText("");
        rotulo.getStyleClass().removeAll("mensagem-erro", "mensagem-sucesso");
    }

    // Mostra a pergunta e espera a resposta. true = o usuário clicou em OK.
    public static boolean confirmar(String titulo, String pergunta) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, pergunta, ButtonType.OK, ButtonType.CANCEL);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        Optional<ButtonType> resposta = alerta.showAndWait();
        return resposta.isPresent() && resposta.get() == ButtonType.OK;
    }
}
