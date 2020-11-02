package com.company;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.sql.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.util.Callback;
import java.sql.Connection;
import java.sql.ResultSet;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;


public class Main extends Application {

        private Product product = null;  // При таком состоянии кнопки ингридиентов вообще по идее не должны
    // быть активны ибо их не к чему применить.
        Scene scene1;

        private ObservableList<ObservableList> data;
        private TableView table = new TableView();

    @Override
        public void start(Stage stage) {

            int width = 1000;
            int height = 500;

            //Creating a text label
            Label helloLabel = new Label("Hello! Choose what will you eat?");
            Label helloLabelForSecondScreen = new Label("Add ingredients or click on the Bill button");
            Label labelForSecondScene = new Label();

            //Settings for label
            helloLabel.setFont(new Font("Helvetic", 30));
            helloLabel.setWrapText(true);
            helloLabel.setAlignment(Pos.CENTER);

            helloLabelForSecondScreen.setFont(new Font("Helvetic", 30));
            helloLabelForSecondScreen.setWrapText(true);
            helloLabelForSecondScreen.setAlignment(Pos.CENTER);

            labelForSecondScene.setFont(new Font("Helvetic", 30));
            labelForSecondScene.setWrapText(true);
            labelForSecondScene.setAlignment(Pos.CENTER);


            //Creating a new Button
            Button buttonPizza = new Button("Pizza");
            Button buttonPancake = new Button("Pancake");
            Button buttonMeet = new Button("Meet");
            Button buttonCheese = new Button("Cheese");
            Button buttonTomato = new Button("Tomato");
            Button buttonHam = new Button("Ham");
            Button buttonBill = new Button("Bill");
            Button buttonOrders = new Button("All orders");

            //Set actions for buttons
            buttonPizza.setOnAction(value ->  {
                // Кнопка меняет продукт, следовательно всё сбрасываем и создаём заного.
                product = new Pizza();
                System.out.println(product.getDescription());
                stage.setScene(scene1);
            });

            buttonPancake.setOnAction(value ->  {
                product = new Pancake();
                System.out.println(product.getDescription());
                stage.setScene(scene1);
            });

            buttonMeet.setOnAction(value ->  {
                // Так как CondimentDecorator наследник Product это сработает
                // и потом этот объект можно будет опять передать в конструктор.
                product = new Meet(product);
                System.out.println(product.getDescription());
                labelForSecondScene.setText(labelForSecondScene.getText() + " Meet ");
            });

            buttonCheese.setOnAction(value ->  {
                product = new Cheese(product);
                System.out.println(product.getDescription());
                labelForSecondScene.setText(labelForSecondScene.getText() + " Cheese ");
            });

            buttonTomato.setOnAction(value ->  {
                product = new Tomato(product);
                System.out.println(product.getDescription());
                labelForSecondScene.setText(labelForSecondScene.getText() + " Tomato ");
            });

            buttonHam.setOnAction(value ->  {
                product = new Ham(product);
                System.out.println(product.getDescription());
                labelForSecondScene.setText(labelForSecondScene.getText() + " Ham ");
            });

            buttonBill.setOnAction(value ->  {
                if(product.hasIngredients()) {
                    CondimentDecorator dec = (CondimentDecorator)product;
                    System.out.println(dec.getDescription() + "  Your bill: " +  dec.cost());
                    helloLabelForSecondScreen.setText("Your ord: " + dec.getDescription() + ". Your bill: " +  dec.cost());
                } else {
                    System.out.println(product.getDescription() + "  total: " +  product.cost());
                    helloLabelForSecondScreen.setText("Your ord: " + product.getDescription() + ". Your bill: " +  product.cost());
                }

                InsertApp app = new InsertApp();
                // insert three new rows
                app.insert(helloLabelForSecondScreen.getText());
            });

        buttonOrders.setOnAction(value -> {

            String url = "jdbc:sqlite:C:/Users/773i/Desktop/SQLiteStudio-3.2.1 (1)/SQLiteStudio/Orders";

            String SQL = "SELECT * from Current_orders";

            data = FXCollections.observableArrayList();

            final Label label = new Label("All orders");
            label.setFont(new Font("Arial", 20));

            table.setEditable(true);

            try(Connection connect = DriverManager.getConnection(url);
                PreparedStatement prepState = connect.prepareStatement(SQL)){

                ResultSet rs = prepState.executeQuery();

                for(int i = 0 ; i < rs.getMetaData().getColumnCount(); i++){
                    //We are using non property style for making dynamic table
                    final int j = i;
                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                    col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                        public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });

                    table.getColumns().addAll(col);
                }

                while(rs.next()){
                    //Iterate Row
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for(int i = 1 ; i <= rs.getMetaData().getColumnCount(); i++){
                        //Iterate Column
                        row.add(rs.getString(i));
                    }

                    data.add(row);
                }

            } catch (SQLException e){
                System.out.println("Error! " + e);
            }

            table.setItems(data);

            final VBox vbox = new VBox();
            vbox.setSpacing(5);
            vbox.setPadding(new Insets(10, 0, 0, 10));
            vbox.getChildren().addAll(label, table);

//            Label secondLabel = new Label("I'm a Label on new Window");

            StackPane secondaryLayout = new StackPane();
            secondaryLayout.getChildren().add(vbox);

            Scene secondScene = new Scene(secondaryLayout, 900, 400);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("All orders");
            newWindow.setScene(secondScene);

            // Set position of second window, related to primary window.
            newWindow.setX(stage.getX() + 75);
            newWindow.setY(stage.getY() + 25);

            newWindow.show();
        });

        // layouts
            // buttons
        HBox.setHgrow(buttonPizza, Priority.ALWAYS);
        HBox.setHgrow(buttonPancake, Priority.ALWAYS);

        buttonPizza.setMaxWidth(Double.MAX_VALUE);
        buttonPancake.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(buttonBill, Priority.ALWAYS);
        buttonBill.setMaxWidth(Double.MAX_VALUE);

        // labels
        HBox.setHgrow(helloLabel, Priority.ALWAYS);
        helloLabel.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(helloLabelForSecondScreen, Priority.ALWAYS);
        helloLabelForSecondScreen.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(labelForSecondScene, Priority.ALWAYS);
        labelForSecondScene.setMaxWidth(Double.MAX_VALUE);

        BorderPane root = new BorderPane();

        HBox hBox = new HBox(25, buttonPizza, buttonPancake);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        spacer.setMinHeight(20);
        VBox vBox = new VBox(helloLabel, spacer, hBox);

        root.setCenter(vBox);

        //Creating a scene objects
        Scene scene = new Scene(root, width, height);

        Region spacer1 = new Region();
        HBox hBox1 = new HBox(25,  buttonMeet, buttonCheese, buttonTomato, buttonHam,
                buttonBill, buttonOrders);
        VBox.setVgrow(spacer1, Priority.ALWAYS);
        spacer1.setMinHeight(15);

        VBox layoutf1 = new VBox(helloLabelForSecondScreen, spacer1, labelForSecondScene, hBox1);
//        layoutf1.getChildren().addAll(vBox1);
        scene1= new Scene(layoutf1, width, height);

        //Setting title to the Stage
        stage.setTitle("BestFood");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }
    public static void main(String[] args){
        launch(args);
    }

    public static class InsertApp {

        private Connection connect() {
            // SQLite connection string
            String url = "jdbc:sqlite:C:/Users/773i/Desktop/SQLiteStudio-3.2.1 (1)/SQLiteStudio/Orders";
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url);
                System.out.println("Successful connect");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return conn;
        }

        public void insert(String k_ord) {
            String sql = "INSERT INTO Current_orders(k_ord, k_date) VALUES(?, datetime('now', 'localtime'))";

            try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, k_ord);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

    }

}



abstract class Product {

    String description = "Unknown Product";

    public String getDescription() {
        return description;
    }

    public boolean hasIngredients() {
        return false;
    }

    public abstract double cost();
}

abstract class CondimentDecorator extends Product {
    public abstract String getDescription();
}

class Pancake extends Product {
    public Pancake(){
        description = "Pancake";
    }
    public double cost(){
        return 50;
    }
}

class Pizza extends Product {
    public Pizza(){
        description = "Pizza";
    }
    public double cost(){
        return 149;
    }
}

class Meet extends CondimentDecorator {
    Product product;

    public Meet(Product product){
        this.product = product;
    }

    public String getDescription() {
        return product.getDescription() + " + Meet";
    }

    @Override
    public boolean hasIngredients() {
        return true;
    }

    public double cost() {
        return 72 + product.cost();
    }
}

class Cheese extends CondimentDecorator {
    Product product;

    public Cheese(Product product){
        this.product = product;
    }

    public String getDescription() {
        return product.getDescription() + " + Cheese";
    }

    @Override
    public boolean hasIngredients() {
        return true;
    }

    public double cost() {
        return 48 + product.cost();
    }
}

class Tomato extends CondimentDecorator {
    Product product;

    public Tomato(Product product){
        this.product = product;
    }

    public String getDescription() {
        return product.getDescription() + " + Tomato";
    }

    @Override
    public boolean hasIngredients() {
        return true;
    }

    public double cost() {
        return 27 + product.cost();
    }
}

class Ham extends CondimentDecorator {
    Product product;

    public Ham(Product product){
        this.product = product;
    }

    public String getDescription() {
        return product.getDescription() + " + Ham";
    }

    @Override
    public boolean hasIngredients() {
        return true;
    }

    public double cost() {
        return 62 + product.cost();
    }
}
