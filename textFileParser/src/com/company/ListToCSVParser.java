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

public class ListToCSVParser {

    final static Charset ENCODING = StandardCharsets.ISO_8859_1;

    void ParseFile(String fileToConvert, String fileToConvertTo) throws IOException {
        Path sourceFile = Paths.get(fileToConvert);
        Path destinationFile = Paths.get(fileToConvertTo);

        BufferedWriter writer = Files.newBufferedWriter(destinationFile,ENCODING);

        try (Scanner scanner = new Scanner(sourceFile, ENCODING.name())){
            while (scanner.hasNextLine()){
                //process each line
                String temp = scanner.nextLine();
                temp = temp.replace(")}",";");
                temp = temp.replace("(#","#");
                temp = temp.replace("\"","");
                temp = temp.replace("(",";");
                temp = temp.replace(")",";");
                temp = temp.replace("{","");
                temp = temp.replace("}}","");
                temp = temp.replace("\t",";");

                for(int i =0;i<10;i++)
                {
                    String repstr = ";;;;";
                    temp = temp.replace(repstr,";;;");
                }
                char[] chararr = temp.toCharArray();
                int count = 0;
                for(int x = 0; x < temp.length();x++)
                {
                    if(chararr[x] == ';')
                    {
                        count++;
                    }
                }
                while(count > 3) {
                    temp = temp.replace(";;", ";");
                    count--;
                }

                writer.write(temp);
                writer.newLine();

                log(temp);
            }
            scanner.close();
            writer.close();
        }
    }

    public static void log(Object message){
        System.out.println(String.valueOf(message));
    }
} 