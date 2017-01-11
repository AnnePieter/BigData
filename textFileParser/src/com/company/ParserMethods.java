/**
 * Made by Anne Pieter Boonstra & Robert Bijl
 */

package com.company;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ParserMethods {

    /** Method for converting movies.list */
    public String MoviesList(String line){
        if(line.contains("SUSPENDED"))
            return "";

        //Get movie (or serie) name
        String movieName = "";
        int end = line.lastIndexOf("\"");
        if (end != -1){
            movieName = line.substring(0, end + 1);
            movieName = movieName.replace("\"","").trim();

            line = line.substring(end + 1, line.length()).trim();
        }
        else{
            end = line.indexOf("(");
            movieName = line.substring(0, end);
            movieName = movieName.trim();

            line = line.substring(end, line.length()).trim();
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

        line = movieName + ";" + releaseYear + ";" + episodeInfo + ";";

        return line;
    }

    /** Method for converting movies.list with a regex (This is a test) */
    public String MoviesListRegex(String line, String reg){
        final String regex = reg;

        Pattern r = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher m = r.matcher(line);

        if(m.find()){
            line = m.group(1) + ";" + m.group(2) + ";" + m.group(5) + ";";
            //line = line.replace("null","");
        }
        return line;
    }

    String currentActor = "";
    /** Method for converting actors.list */
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

    /** Method for converting countries.list */
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

} 