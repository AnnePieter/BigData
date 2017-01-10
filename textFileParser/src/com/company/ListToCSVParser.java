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
                //line = CountriesList(line);
                line = ActorsList(line);

                writer.write(line);
                writer.newLine();

                log(line);
            }
            scanner.close();
            writer.close();
        }
    }

    String currentActor = "";
    public String ActorsList(String line){
        //Check for actor
        if (!(line.startsWith("\t"))){
            int end = line.indexOf("\t");
            currentActor = line.substring(1, end);
            currentActor.trim();

            line = line.substring(end, line.length());
        }

        //Get movie name
        int end = line.indexOf("(");
        if (end != -1)
        {
            String currentMovie = line.substring(0, end);
        }

        line = currentActor + ";" + ";";

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