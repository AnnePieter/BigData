package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main extends Application {

    Charset charset = StandardCharsets.ISO_8859_1;
    File sourcefile = new File("C:///Users/annepieter/Documents/BigData/untitled/countries.csv");
    Image img = new Image("file:///Users/annepieter/Documents/BigData/untitled/WorldMap.jpg");
    String[] europe = {"Albania","Andorra","Armenia","Austria","Azerbaijan","Belarus","Belgium","Bosnia and Herzegovina","Bulgaria","Croatia","Cyprus","Czech Republic","Denmark","Estonia",
            "Finland","France","Georgia","Germany","Greece","Hungary","Iceland","Ireland","Italy","Kazakhstan","Kosovo","Latvia","Liechtenstein","Lithuania","Luxembourg","Macedonia","Malta",
            "Moldova","Monaco","Montenegro","Netherlands","Norway","Poland","Portugal","Romania","Russia","San Marino","Serbia","Slovakia","Slovenia","Spain","Sweden","Switzerland","Turkey","Ukraine","UK","Vatican City"};

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Group root1 = new Group();


        final Canvas canvas = new Canvas(2000,1500);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage(img,0,0,1920,1080);

        root1.getChildren().add(canvas);
        //root1.getChildren().add(root);


        Scene scene = new Scene(root1, 400,400);
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Visualisatie");
        primaryStage.setScene(scene);
        Scanner scanner = new Scanner(sourcefile);
        Map<String,Point2D.Double> coordsList = new HashMap<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] lineparts = line.split(",");
                //
                double xcoord = Double.parseDouble(lineparts[4]);
                double ycoord = Double.parseDouble(lineparts[3]);
                if(ycoord >920)
                {
                    ycoord -= 110;
                }
                else if(ycoord <920 && ycoord >720)
                {
                    ycoord -= 100;
                }
                else if(ycoord <720 && ycoord >540)
                {
                    ycoord -= 50;
                }
                coordsList.put(lineparts[2], new Point2D.Double(xcoord, ycoord));
                gc.strokeOval(xcoord, ycoord,1,1);
            }

            gc.strokeOval(620,870,5,5);
        gc.strokeOval(1108,738,5,5);
        for(int x=0;x<europe.length;x++) {

            String euCountry = europe[x];
            gc.strokeLine(coordsList.get(euCountry).getX(), coordsList.get(euCountry).getY(), coordsList.get("USA").getX(), coordsList.get("USA").getY());
        }
       /* gc.strokeOval(988,427,10,10);
        gc.strokeOval(988,427,10,10);
        gc.strokeOval(988,427,10,10);*/








        primaryStage.show();

        

    }

    public static void main(String[] args) {
        launch(args);



    }

    public static void log(Object message){
        System.out.println(String.valueOf(message));
    }


}

