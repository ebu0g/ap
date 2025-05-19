import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import model.CustomerLogin;
import model.ManagerLogin;
import model.DBHelper;

public class App extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    private ImageView createBackgroundImage() {
        try {
            Image image = new Image(getClass().getResource("/resources/Movie.jpg").toExternalForm()); 
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(800);
            imageView.setFitHeight(500);
            imageView.setOpacity(1);
            return imageView;
        } catch (Exception e) {
            System.out.println("Image file not found!");
            e.printStackTrace();
            return new ImageView();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            DBHelper.connect(); // Initialize database connection

            StackPane root = new StackPane();
            root.getChildren().add(createBackgroundImage());

            try {
                Image icon = new Image(getClass().getResource("/resources/Logo.jpeg").toExternalForm());
                primaryStage.getIcons().add(icon);
            } catch (NullPointerException e) {
                System.out.println("Logo image not found!");
                e.printStackTrace();
            }

            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(0, 0, 0, 0));
            primaryStage.setTitle("CINEBOOK");
            root.getChildren().add(grid);

            Label label = new Label("WELCOME TO CINEBOOK!");
            label.setFont(new Font("Arial", 50));
            label.setTextFill(Color.LIME);
            grid.add(label, 0, 0, 2, 1);

            Label chooseRole = new Label("Choose your role:");
            chooseRole.setFont(new Font("Times New Roman", 25));
            chooseRole.setTextFill(Color.RED);
            chooseRole.setTextAlignment(TextAlignment.JUSTIFY);
            grid.add(chooseRole, 0, 1);

            RadioButton customerRadio = new RadioButton("Customer");
            RadioButton managerRadio = new RadioButton("Manager");
            ToggleGroup roleGroup = new ToggleGroup();
            customerRadio.setToggleGroup(roleGroup);
            customerRadio.setTextFill(Color.AQUA);
            customerRadio.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 25));
            customerRadio.setPrefSize(300, 100);
            managerRadio.setTextFill(Color.AQUA);
            managerRadio.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 25));
            managerRadio.setPrefSize(300, 100);
            managerRadio.setToggleGroup(roleGroup);
            grid.add(customerRadio, 0, 2);
            grid.add(managerRadio, 1, 2);

            Button nextButton = new Button("Next");
            nextButton.setPrefSize(100, 25);
            grid.add(nextButton, 0, 3, 2, 1);

            nextButton.setOnAction(event -> {
                if (customerRadio.isSelected()) {
                    CustomerLogin customerLogin = new CustomerLogin();
                    StackPane customerLoginPane = new StackPane(customerLogin);
                    customerLoginPane.setPadding(new Insets(10));
                    Scene customerScene = new Scene(customerLoginPane, 500, 500);
                    Stage customerStage = new Stage();
                    customerStage.setTitle("Customer Login/Signup");
                    customerStage.setScene(customerScene);
                    customerStage.show();
                } else if (managerRadio.isSelected()) {
                    ManagerLogin managerLogin = new ManagerLogin();
                    StackPane manageLoginPane = new StackPane(managerLogin);
                    manageLoginPane.setPadding(new Insets(10));
                    Scene managerScene = new Scene(manageLoginPane, 500, 500);
                    Stage managerStage = new Stage();
                    managerStage.setTitle("Manager Screen");
                    managerStage.setScene(managerScene);
                    managerStage.show();
                }
            });

            Scene scene = new Scene(root, 800, 500);
            primaryStage.setScene(scene);
            primaryStage.show();

            System.out.println("Loading background image...");

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}
