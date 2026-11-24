package controller;

import exception.ContaNaoEncontradaException;
import exception.LoginInvalidoException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Cliente;
import util.Entrada;

/**
 * Controller da tela de login (login.fxml).
 * Faz o mesmo que o login() do Main do console: chama Banco.login()
 * e trata as exceções mostrando a mensagem na própria tela.
 */
public class LoginController {

    // @FXML liga o atributo ao componente do .fxml que tem o mesmo fx:id
    @FXML private TextField campoIdentificador;
    @FXML private PasswordField campoSenha;
    @FXML private Label rotuloMensagem;

    @FXML
    private void entrar() {
        String identificador = Entrada.limparCpf(campoIdentificador.getText());
        String senha = campoSenha.getText();

        // Validação de entrada: com if, sem exceção
        if (identificador.isEmpty() || senha.isEmpty()) {
            Mensagem.erro(rotuloMensagem, "Preencha o CPF (ou número da conta) e a senha.");
            return;
        }

        try {
            Cliente cliente = Sessao.getBanco().login(identificador, senha);
            Sessao.entrar(cliente);
            Navegador.trocarJanela(Tela.PRINCIPAL);
        } catch (ContaNaoEncontradaException e) {
            Mensagem.erro(rotuloMensagem, "Não encontramos esse CPF ou número de conta.");
        } catch (LoginInvalidoException e) {
            Mensagem.erro(rotuloMensagem, "Senha incorreta.");
            campoSenha.clear();
        }
    }

    @FXML
    private void abrirCadastro() {
        Navegador.trocarJanela(Tela.CADASTRO);
    }
}
