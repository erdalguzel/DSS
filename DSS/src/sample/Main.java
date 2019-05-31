package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import weka.core.Instances;

public class Main extends Application {
	Controller cont = null;
	ClassifierObject cl = null;
	Pane root = new Pane();

	Label label1 = new Label("Best algorithm");
	@FXML
	TextField textField = new TextField();

	Label label2 = new Label("Best perception score");
	@FXML
	TextField textField2 = new TextField();

	final ComboBox<String> combobox = new ComboBox<String>();
	TextField[] text;

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Choose a File");
		FileChooser fileChooser = new FileChooser();
		
		Button button = new Button("Choose training file");
		button.setOnAction(event -> {
			File file = fileChooser.showOpenDialog(primaryStage);
			String path = file.getPath();
			BufferedReader dataset = null;

			try {
				dataset = new BufferedReader(new FileReader(path));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			try {
				cont = new Controller(dataset);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				cl = cont.StartTrainAndTest();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				dataset.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		Button button2 = new Button("Choose prediction file");
		button2.setOnAction(event -> {
			File file2 = fileChooser.showOpenDialog(primaryStage);
			String predictionDatasetPath = file2.getPath();
			BufferedReader predictionDataset = null;
			try {
				predictionDataset = new BufferedReader(new FileReader(predictionDatasetPath));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			Instances ins = null;
			try {
				// This part works
				ins = new Instances(predictionDataset);
				for (int i = 0; i < ins.numAttributes(); i++) {
					if (ins.attribute(i).isNominal()) {
						String[] s = ins.attribute(i).toString().split(" ");
						String temp = s[s.length - 1].replaceAll("[{}]", "").toString();
						String[] tempArray = temp.split(",");
						for (String elem : tempArray) {
							System.out.println(elem);
							combobox.getItems().addAll(elem);
						}
					}
				}
				
				int numTextFields = 0;
				for(int a = 0;a < ins.numAttributes();a++) {
					if(ins.attribute(a).isNumeric()) {
						numTextFields++;
					}
				}
				
				int offset = 50;
		        TextField[] textFields = new TextField[numTextFields];
		        for (int i = 0; i < numTextFields; i++) {
		            TextField tf = new TextField();
		            tf.setLayoutX(50);
		    		tf.setLayoutY(300 + offset * i);
		            root.getChildren().add(tf);
		            textFields[i] = tf;
		        }

			} catch (IOException ex) {
				ex.printStackTrace();
			}
			ins.setClassIndex(ins.numAttributes() - 1);
			try {
				cont.StartPrediction(cl, ins);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				predictionDataset.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
		});
		
		Button discoverButton = new Button("Discover");
		discoverButton.setOnAction(event -> {
			textField.setText(cl.getName());
			textField2.setText(cl.toString());
		});

		textField.setLayoutX(250);
		textField.setLayoutY(50);
		HBox hb = new HBox();
		hb.setSpacing(10);
		hb.getChildren().add(textField);
		root.getChildren().addAll(textField);

		textField2.setLayoutX(250);
		textField2.setLayoutY(150);
		hb.setSpacing(10);
		hb.getChildren().add(textField2);
		root.getChildren().addAll(textField2);

		button.setLayoutX(50);
		button.setLayoutY(50);
		root.getChildren().add(button);
		button2.setLayoutX(50);
		button2.setLayoutY(150);
		root.getChildren().add(button2);

		combobox.setLayoutX(50);
		combobox.setLayoutY(250);
		root.getChildren().add(combobox);
		
		discoverButton.setLayoutX(50);
		discoverButton.setLayoutY(450);
		root.getChildren().add(discoverButton);

		primaryStage.setScene(new Scene(root, 600, 600));
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public static String tokenizeString(String s) {
		String[] splits = s.split(" ");
		return splits[1];
	}
}
