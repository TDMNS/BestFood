package com.company;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends Application {

        private Product product = null;  // При таком состоянии кнопки ингридиентов вообще по идее не должны
    // быть активны ибо их не к чему применить.
        Scene scene1;

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
                if(product.hasIngridients()) {
                    CondimentDecorator dec = (CondimentDecorator)product;
                    System.out.println(dec.getDescription() + "  Your bill: " +  dec.cost());
                    helloLabelForSecondScreen.setText("Your ord: " + dec.getDescription() + ". Your bill: " +  dec.cost());
                } else {
                    System.out.println(product.getDescription() + "  total: " +  product.cost());
                    helloLabelForSecondScreen.setText("Your ord: " + product.getDescription() + ". Your bill: " +  product.cost());
                }

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
                buttonBill);
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
    public static void main(String args[]){
        launch(args);
    }
}



abstract class Product {
    String description = "Unknown Product";
    boolean ingredients = false;

    public String getDescription() {
        return description;
    }

    public boolean hasIngridients() {
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
    public boolean hasIngridients() {
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
    public boolean hasIngridients() {
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
    public boolean hasIngridients() {
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
    public boolean hasIngridients() {
        return true;
    }

    public double cost() {
        return 62 + product.cost();
    }
}
