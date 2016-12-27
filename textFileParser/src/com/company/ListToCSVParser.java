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

        try (Scanner scanner =  new Scanner(sourceFile, ENCODING.name())){
            while (scanner.hasNextLine()){
                //process each line in some way
                writer.write(scanner.nextLine());
                writer.newLine();

                log(scanner.nextLine());
            }
        }
    }

    private static void log(Object message){
        System.out.println(String.valueOf(message));
    }

} 