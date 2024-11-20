package com.n19.ltmproject.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.util.Objects;

public class TutorialController {
    @FXML
    private ImageView imageView;
    @FXML
    private Button nextButton;
    @FXML
    private Button backButton;
    @FXML
    private Button homeButton;
    @FXML
    private Label LabelTutorial;
    @FXML
    private Label LabelTutorialTitle;
    @FXML
    private Label endTutotial;
    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    private final String[] imagePaths = {
            "/com/n19/ltmproject/images/tutorial/tutorial1.png",
            "/com/n19/ltmproject/images/tutorial/tutorial2.png",
            "/com/n19/ltmproject/images/tutorial/tutorial3.png",
            "/com/n19/ltmproject/images/tutorial/tutorial4.png",
            "/com/n19/ltmproject/images/tutorial/tutorial5.png",
            "/com/n19/ltmproject/images/tutorial/tutorial6.png",
            "/com/n19/ltmproject/images/tutorial/tutorial8.png",
            "/com/n19/ltmproject/images/tutorial/tutorial9.png",
            "/com/n19/ltmproject/images/tutorial/tutorial7.png"
    };

    private int currentIndex = -1;

    @FXML
    private void initialize() {
        showIntroductionPage();
    }

    private void showIntroductionPage() {
        LabelTutorialTitle.setVisible(true);
        LabelTutorial.setVisible(true);
        imageView.setVisible(false);
        LabelTutorial.setText(""" 
                 1. Thời gian: Trò chơi diễn ra trong 2 phút.
                 2. Phân loại rác: Các loại rác ngẫu nhiên sẽ xuất hiện kèm theo các thùng rác tương ứng.
                    - Hữu cơ: Thùng màu xanh lá cây (thực phẩm, vỏ hoa quả...)
                    - Nhựa: Thùng màu vàng (chai nhựa, túi nylon...)
                    - Kim loại: Thùng màu xám (lon nhôm, hộp kim loại...)
                    - Giấy: Thùng màu xanh dương (báo cũ, bìa carton...)
                    - Thủy tinh: Thùng màu trắng (chai thủy tinh, ly thủy tinh...)
                 3. Điểm số: Mỗi lần phân loại đúng sẽ nhận 1 điểm, phân loại sai sẽ không được điểm.
                 4. Kết thúc trò chơi: Khi hết thời gian, người chơi có điểm cao hơn sẽ thắng.
                 5. Tổng kết điểm tích lũy: Trận thắng sẽ được 3 điểm, hòa được 1 điểm, thua được 0 điểm. Người chơi thoát trận sớm sẽ thua ngay lập tức.
               """);

        LabelTutorial.setWrapText(true);
        nextButton.setText("Next");
        backButton.setVisible(false); // Ẩn nút Quay lại trong trang đầu tiên
    }

    @FXML
    private void handleNextButtonAction(ActionEvent event) {
        System.out.println("Click Next : "+currentIndex);
        if (currentIndex < imagePaths.length) {
            LabelTutorial.setVisible(false);
            LabelTutorialTitle.setVisible(false);
            imageView.setVisible(true);
            currentIndex++;
            System.out.println(currentIndex == (imagePaths.length - 1) );
            if (currentIndex == (imagePaths.length - 1) ) {
                // Nếu đến trang cuối cùng
                System.out.println("FINAL PAGE");
                showFinalPage();
            }
            else{
                displayImage(currentIndex);
            }
        }
        System.out.println("END Next : "+currentIndex);
        updateButtonVisibility();
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        if (currentIndex >= 0) {
                if(currentIndex == (imagePaths.length - 1) ){
                    endTutotial.setVisible(false);
                }
                System.out.println("Click Back : " + currentIndex);
                currentIndex--;

//                currentIndex--;
                if (currentIndex == -1) {
                    // Nếu đến trang cuối cùng
                    System.out.println("START PAGE");
                    showIntroductionPage();
                }
                else{
                    displayImage(currentIndex);
                }
//            }
        }
        System.out.println("END Back : "+currentIndex);
        updateButtonVisibility();
    }



    private void displayImage(int index) {
        Image image = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(imagePaths[index])));
        imageView.setImage(image);
//        nextButton.setText(index == imagePaths.length - 1 ? "Đã hiểu" : "Next");
    }

    private void showFinalPage() {
        endTutotial.setVisible(true);
        displayImage(currentIndex);
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        backButton.setVisible(currentIndex >= 0); // Hiện nút Quay lại nếu không phải trang đầu
        nextButton.setVisible(currentIndex < imagePaths.length-1); // Hiện nút Next nếu không phải trang cuối
    }

    @FXML
    private void goToMainPage(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/n19/ltmproject/MainPage.fxml"));

        Parent MainPageViewParent = loader.load();
        Scene scene = new Scene(MainPageViewParent);

        MainPageController mainPageController = loader.getController();
        mainPageController.setPrimaryStage(primaryStage);

        primaryStage.setScene(scene);
        mainPageController.setupMainPage();
    }
}