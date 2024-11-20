package com.n19.ltmproject.client.controller;
// SIGN UP

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.dto.Response;
import com.n19.ltmproject.client.service.MessageService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SignUpController {

	@FXML
	private TextField userText;
	@FXML
	private PasswordField passText, confirmPassText;

	private final ServerHandler serverHandler = ServerHandler.getInstance();
	private final MessageService messageService = new MessageService(serverHandler);

	@FXML
	public void initialize() {
		userText.setOnKeyPressed(this::handleKeyPress);
		passText.setOnKeyPressed(this::handleKeyPress);
		confirmPassText.setOnKeyPressed(this::handleKeyPress);
	}

	private void handleKeyPress(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			ClickSignUp(null);
		}
	}

	@FXML
	public void ClickLogin(MouseEvent e) throws IOException {
		loadScene("/com/n19/ltmproject/Login.fxml");
	}

	@FXML
	public void ClickSignUp(ActionEvent e) {
		String username = userText.getText();
		String password = passText.getText();
		String confirmPassword = confirmPassText.getText();

		if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
			AlertController.showErrorAlert("Signup", "Vui lòng nhập đầy đủ thông tin.");
			return;
		}

		if (!password.equals(confirmPassword)) {
			AlertController.showErrorAlert("Signup", "Mật khẩu không khớp.");
			return;
		}

		Map<String, Object> params = new HashMap<>();
		params.put("username", username);
		params.put("password", password);
		params.put("confirmPassword", confirmPassword);

		Response response = messageService.sendRequestAndReceiveResponse("signUp", params);

		if (response != null && response.getMessage().contains("Đăng ký thành công!")) {
			AlertController.showInformationAlert("Signup", "Đăng ký thành công!");
			try {
				loadScene("/com/n19/ltmproject/Login.fxml");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			AlertController.showErrorAlert("Signup", response != null ? response.getMessage() : "Đăng ký thất bại.");
		}
	}

	private void loadScene(String resourcePath) throws IOException {
		Stage stage = (Stage) userText.getScene().getWindow();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
		Parent view = loader.load();
		stage.setScene(new Scene(view));
	}
}
