package sample;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {

    private final static Charset ENCODING = StandardCharsets.ISO_8859_1;

    private ParserMethods parserMethods;
    private Controller controller;

    public Parser(){
        this.parserMethods = new ParserMethods();
        this.controller = Controller.GetInstance();
    }

    public void ParseFile(String fileToConvert) throws IOException {
        controller.UpdateStatusLabel("Converting " + Paths.get(fileToConvert).getFileName() + "...");
        controller.btn_ParseFiles.setDisable(true);

        Path sourceFile = Paths.get(fileToConvert);
        Path destinationFile = Paths.get(fileToConvert + ".csv");

        String conversionMethod = GetMethodByFileName(sourceFile.getFileName() + "");
        if (conversionMethod.isEmpty())
            return;

        BufferedWriter writer = Files.newBufferedWriter(destinationFile,ENCODING);

        try (Scanner scanner = new Scanner(sourceFile, ENCODING.name())){
            //skip file documentation
            preScannerLoop: while (scanner.hasNextLine()){
                String line = scanner.nextLine();

                // Check if the first line of data is reached (differs from file to file)
                switch (conversionMethod){
                    case "Actors":
                        if (line.contains("Name			Titles") && scanner.nextLine().contains("----\t\t\t------"))  break preScannerLoop; break;
                    case "Actresses":
                        if (line.contains("Name			Titles") && scanner.nextLine().contains("----\t\t\t------")) break preScannerLoop; break;
                    case "Movies":
                        if (line.contains("LIST") && scanner.nextLine().contains("======="))  break preScannerLoop; break;
                    case "Countries":
                        if (line.contains("LIST") && scanner.nextLine().contains("======="))  break preScannerLoop;  break;
                    case "Locations":
                        if (line.contains("LIST") && scanner.nextLine().contains("======="))  break preScannerLoop; break;
                    case "Biographies":
                        if (line.contains("LIST") && scanner.nextLine().contains("======="))  break preScannerLoop; break;
                    case "Business":
                        if (line.contains("LIST") && scanner.nextLine().contains("======="))  break preScannerLoop; break;
                    case "Ratings":
                        if (line.contains("New  Distribution  Votes  Rank  Title"))  break preScannerLoop;  break;
                    default: break;
                }
            }

            scannerLoop: while (scanner.hasNextLine()){
                //process each line
                String line = scanner.nextLine();
                if (line.isEmpty() || line.contains("SUSPENDED"))
                    continue;

                // Check if the last line of data is reached (differs from file to file) (some files don't have end lines)
                switch (conversionMethod){
                    case "Actors":
                        if (line.contains("-----------------------------------------------------------------------------")) break scannerLoop;
                        else line = parserMethods.ActorsList(line); break;
                    case "Actresses":
                        if (line.contains("-----------------------------------------------------------------------------")) break scannerLoop;
                        else line = parserMethods.ActorsList(line); break;
                    case "Movies":
                        if (line.contains("-----------------------------------------------------------------------------")) break scannerLoop;
                        else line = parserMethods.MoviesList(line); break;
                    case "Countries":
                        if (line.contains("--------------------------------------------------------------------------------")) break scannerLoop;
                        else line = parserMethods.CountriesList(line); break;
                    case "Locations":
                        if (line.contains("-----------------------------------------------------------------------------")) break scannerLoop;
                        else line = parserMethods.LocationsList(line); break;
                    case "Biographies": line = parserMethods.BiographiesList(line); break;
                    case "Business":
                        if (line.contains("NOTES")) break scannerLoop;
                        else line = parserMethods.BusinessList(line); break;
                    case "Ratings":
                        if (line.contains("------------------------------------------------------------------------------")) break scannerLoop;
                        else line = parserMethods.RatingsList(line); break;
                    default: break;
                }

                if (line.isEmpty())
                    continue;

                writer.write(line);
                writer.newLine();

                // log(line);
            }
            scanner.close();
            writer.close();

            controller.UpdateStatusLabel("Conversion of " + Paths.get(fileToConvert).getFileName() + " completed");
            controller.btn_ParseFiles.setDisable(false);
        }
    }

    public String GetMethodByFileName(String sourceFileName){
        ArrayList<String> availableMethods = parserMethods.supportedFiles;
        for (int i = 0; i < availableMethods.size(); i++){
            if (sourceFileName.toLowerCase().contains(availableMethods.get(i).toLowerCase()))
                return availableMethods.get(i);
        }
        return "";
    }



}
