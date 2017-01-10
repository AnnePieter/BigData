/**
 Made by Anne Pieter Boonstra & Robert Bijl
 */

package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ListToCSVParser {

    final static Charset ENCODING = StandardCharsets.ISO_8859_1;

    void ParseFile(String fileToConvert, String fileToConvertTo) throws IOException {
        Path sourceFile = Paths.get(fileToConvert);
        Path destinationFile = Paths.get(fileToConvertTo);

        BufferedWriter writer = Files.newBufferedWriter(destinationFile,ENCODING);

        try (Scanner scanner = new Scanner(sourceFile, ENCODING.name())){
            //skip file documentation
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if (line.contains("LIST")){
                    scanner.nextLine(); //skip an additional line
                    break;
                }
            }

            while (scanner.hasNextLine()){
                //process each line
                String line = scanner.nextLine();

                if (line.isEmpty())
                    continue;

                line = MoviesList(line);
                //line = CountriesList(line);
                //line = ActorsList(line);

                writer.write(line);
                writer.newLine();

                log(line);
            }
            scanner.close();
            writer.close();
        }
    }

    public String MoviesList(String line){
        //Get movie (or serie) name
        String movieName = "";
        int end = line.lastIndexOf("\"");
        if (end != -1){
            movieName = line.substring(0, end + 1);
            movieName = movieName.replace("\"","").trim();

            line = line.substring(end + 1, line.length()).trim();
        }

        //Get release year
        String releaseYear = "";
        end = line.indexOf(")");
        if (end != -1){
            releaseYear = line.substring(0, end + 1);
            releaseYear = releaseYear.replace("(","").replace(")","").trim();

            line = line.substring(end + 1, line.length()).trim();
        }

        //Get episode name (if exists)
        String episodeInfo = "";
        end = line.lastIndexOf("}");
        if (end != -1){
            episodeInfo = line.substring(0, end + 1);
            episodeInfo = episodeInfo.replace("{","").replace("}","").trim();

            line = line.substring(end + 1, line.length()).trim();
        }

        line = movieName + ";" + releaseYear + ";" + episodeInfo;

        return line;
    }

    String currentActor = "";
    public String ActorsList(String line){
        //Check for actor
        if (!(line.startsWith("\t"))){
            int end = line.indexOf("\t");
            currentActor = line.substring(0, end + 1);
            currentActor = currentActor.trim();

            line = line.substring(end, line.length()).trim();
        }

        //Get movie (or serie) name
        String movieName = "";
        int end = line.indexOf("(");
        if (end != -1) {
            movieName = line.substring(0, end);
            movieName = movieName.trim();

            line = line.substring(end, line.length()).trim();
        }

        //Get release year
        String releaseYear = "";
        end = line.indexOf(")");
        if (end != -1) {
            releaseYear = line.substring(0, end + 1);
            releaseYear = releaseYear.replace("(","").replace(")","").trim();

            line = line.substring(end + 1, line.length()).trim();
        }

        //Get serie episode name (if found)
        String episodeName = "";
        end = line.indexOf("}");
        if (end != -1){
            episodeName = line.substring(0, end + 1);
            episodeName = episodeName.replace("{","").replace("}","").trim();

            line = line.substring(end + 1, line.length()).trim();
        }

        //Get actor role
        String actorRole = "";
        end = line.indexOf("]");
        if (end != -1){
            actorRole = line.substring(0, end + 1);
            actorRole = actorRole.replace("[","").replace("]","").trim();

            line = line.substring(end + 1, line.length()).trim();
        }

        line = currentActor + ";" + movieName + ";" + releaseYear + ";" + episodeName + ";" + actorRole + ";";

        return line.trim();
    }

    public String CountriesList(String line){
        line = line.replace(")}"    ,";");
        line = line.replace("(#"	,"#");
        line = line.replace("\""    ,"");
        line = line.replace("("     ,";");
        line = line.replace(")"     ,";");
        line = line.replace("{"     ,"");
        line = line.replace("}}"    ,"");
        line = line.replace("\t"    ,";");

        for(int i = 0; i < 10; i++)
        {
            String repstr = ";;;;";
            line = line.replace(repstr,";;;");
        }

        int count = 0;
        for(int x = 0; x < line.length(); x++)
        {
            if(line.charAt(x) == ';')
            {
                count++;
            }
        }

        while(count > 3) {
            line = line.replace(";;", ";");
            count--;
        }

        return line;
    }

    public static void log(Object message){
        System.out.println(String.valueOf(message));
    }
} 