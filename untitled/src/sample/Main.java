package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
import java.sql.DriverManager;
import java.util.*;

import javax.imageio.ImageIO;
import javax.xml.transform.Result;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main extends Application {

    Charset charset = StandardCharsets.ISO_8859_1;
    File sourcefile = new File("D:///OneDrive/Documenten/school/Jaar2/Periode 2/Big Data/BigData/untitled/countries.csv");
    Image img = new Image("file:///OneDrive/Documenten/school/Jaar2/Periode 2/Big Data/BigData/untitled/WorldMap.jpg");

    String[] europe = {"Albania","Armenia","Austria","Azerbaijan","Belarus","Belgium","Bosnia and Herzegovina","Bulgaria","Croatia","Cyprus","Czech Republic","Denmark","Estonia",
            "Finland","France","Georgia","Germany","Greece","Hungary","Ireland","Italy","Kazakhstan","Kosovo","Latvia","Lithuania","Luxembourg","Macedonia",
            "Moldova","Netherlands","Norway","Poland","Portugal","Romania","Russia","Serbia","Slovakia","Slovenia","Spain","Sweden","Switzerland","Turkey","Ukraine","UK"};

    GraphicsContext gc;
    Map<String,Point2D.Double> coordsList;

    Connection m_Connection;
    Statement m_Statement;
    ResultSet m_ResultSet;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Database connection
        GetDatabaseConnection();

        Group root1 = new Group();
        final Canvas canvas = new Canvas(2000,1500);
        root1.getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();
        ResetGraphicsContext();

        //Create scene
        Scene scene = new Scene(root1, 400,400);
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Visualisatie");
        primaryStage.setScene(scene);

        //Set coords
        SetCoordsToImage();

        Vraag_ImmigratieNederlanders();

        primaryStage.show();
    }

    public void ResetGraphicsContext(){
        gc.clearRect(0,0,1920,1080);
        gc.drawImage(img,0,0,1920,1080);

        gc.setFill(Color.BLUE);
        gc.fillRect(10,10,300,300);
    }

    public void Vraag_ImmigratieNederlanders(){
        ExecuteQuery("");

        // Draw lines
        for(int x=0;x<europe.length;x++) {

            String euCountry = europe[x];
            gc.strokeLine(coordsList.get(euCountry).getX(), coordsList.get(euCountry).getY(), coordsList.get("USA").getX(), coordsList.get("USA").getY());
        }
    }

    public void Vraag_2(){
        ExecuteQuery("");
    }

    public void GetDatabaseConnection(){
        try{
            Class.forName("org.postgresql.Driver");
            m_Connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/BigData", "postgres", "admin");
            m_Statement = m_Connection.createStatement();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void SetCoordsToImage() throws Exception{
        Scanner scanner = new Scanner(sourcefile);
        coordsList = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] lineparts = line.split(",");

            double xCoord = Double.parseDouble(lineparts[4]);
            double yCoord = Double.parseDouble(lineparts[3]);

            if(yCoord >920)
                yCoord -= 110;
            else if(yCoord <920 && yCoord >720)
                yCoord -= 100;
            else if(yCoord <720 && yCoord >540)
                yCoord -= 50;

            coordsList.put(lineparts[2], new Point2D.Double(xCoord, yCoord));
        }
    }

    public void ExecuteQuery(String query){
        try{
            m_ResultSet = m_Statement.executeQuery(query);

            while (m_ResultSet.next()) {
                System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", "
                        + m_ResultSet.getString(3));
            }
        } catch(Exception e){
            e.printStackTrace();
        }

    }

}

