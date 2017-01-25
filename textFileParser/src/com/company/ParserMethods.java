/**
 * Made by Anne Pieter Boonstra & Robert Bijl
 */

package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ParserMethods {

    // Persistent variables
    String currentActor = "";
    String currentMovie = "";
    static final HashMap<Integer, String> monthNameNumber;
    static {
        monthNameNumber = new HashMap<>();
        monthNameNumber.put(1, "january");
        monthNameNumber.put(2, "february");
        monthNameNumber.put(3, "march");
        monthNameNumber.put(4, "april");
        monthNameNumber.put(5, "may");
        monthNameNumber.put(6, "june");
        monthNameNumber.put(7, "july");
        monthNameNumber.put(8, "august");
        monthNameNumber.put(9, "september");
        monthNameNumber.put(10, "october");
        monthNameNumber.put(11, "november");
        monthNameNumber.put(12, "december");
    }

    // Persistent variables for business.list
    String previousCountry = "";
    String previousMovie = "";



    /** Method for converting movies.list */
    public String MoviesList(String line){
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
            if (end != -1){
                movieName = line.substring(0, end);
                movieName = movieName.trim();

                line = line.substring(end, line.length()).trim();

            }
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

        line = movieName.replace(",",";") + "," + releaseYear + "," + episodeInfo.replace(",","") + ",";

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

    /** Method for converting locations.list */
    public String LocationsList(String line){
        //Get movie (or serie) name
        String movieName = "";
        int end = line.indexOf(" (");
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

            int end2 = releaseYear.indexOf("/");
            if (end2 != -1)
                releaseYear = releaseYear.substring(0,end2);


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

        //Get locations and store them in an array
        List<String> locations = new ArrayList<>();
        while (line.contains(",")){
            end = line.indexOf(",");
            if (end != -1){
                locations.add(line.substring(0, end + 1).replace(",","").trim());

                line = line.substring(end + 1, line.length()).trim();
            }
        }
        //Get the last location (at the end of the string)
        end = line.indexOf("(");
        if (end != -1){
            locations.add(line.substring(0, end).replace(",","").trim());

            line = line.substring(end, line.length()).trim();
        }
        else{
            locations.add(line.trim());
        }

        line = movieName.replace(",","") + "," + releaseYear + "," + episodeName.replace(",","") + ",";

        int lol = 3;
        //Starting at the end of the array to get the last 3 locations
        for (int i = locations.size() - 1; i > 0; i--){
            //Only use the last 3 entries in locations (we don't need stage name etc.)
            if (i == locations.size() - 4)
                break;

            if (i == locations.size() - 3){
                end = locations.get(i).indexOf(")");
                if (end != -1) {
                    locations.set(i, locations.get(i).substring(end + 1, locations.get(i).length()).trim());
                }
            }
            //For the last item in locations (first in loop)
            else if (i == locations.size()){
                end = locations.get(i).indexOf("(");
                if (end != -1) {
                    locations.set(i, locations.get(i).substring(end, locations.get(i).length()).trim());
                }
            }

            line += locations.get(i) + ",";
            lol --;
        }
        for (int i = 0; i < lol; i++)
            line += ",";

        return line.substring(0, line.length() -1);
    }

    /** Method for converting Biographies.list */
    public String BiographiesList(String line){
        // This function only returns something when both 'NM:' and 'DB:' have been found
        // Only interested in name (NM:) and birth-info (DB:)
        if (!line.contains("NM:") && !line.contains("DB:"))
            return "";

        // For lines that contain the name
        if (line.contains("NM:")){
            if (line.contains("("))
                currentActor = line.substring(3, line.indexOf("(") - 1);
            else
                currentActor = line.substring(3, line.length() - 1);
        }

        // For lines that contain the birth info
        if (line.contains("DB:")){
            String birthInfo = line.substring(3, line.length());
            String country = "";
            String date = "";
            // DB: 26 August 1966, Santiago, Chile (example)
            // Counting commas to get total items in birthInfo string (0 commas is 1 item etc.)
            int commaCount = 0;
            for(int i = 0; i < birthInfo.length(); i++){
                if( birthInfo.charAt(i) == ',')
                    commaCount++;
            }

            // Only interested in data and country (first and last item)
            // With commaCount == 0 there is only 1 item, check if it's a date or a country
            if (commaCount == 0){
                // Check if it's a date
                for (int i = 0; i < birthInfo.length(); i++){
                    for (int number = 0; number < 10; number++){
                        if (birthInfo.charAt(i) == number){
                            date = birthInfo;
                            break;
                        }
                        else if(number == 9)
                            country = birthInfo;
                    }
                }
            }
            // With commaCount > 0 there is more than 1 item
            else{
                // Date is always first (if it is included)
                date = birthInfo.substring(0, birthInfo.indexOf(","));
                country = birthInfo.substring(birthInfo.lastIndexOf(",") + 1, birthInfo.length());
            }

            // Convert date to dd/MM/yyyy
            if(!date.isEmpty()){
                for (Map.Entry<Integer, String> month : monthNameNumber.entrySet()){
                    String monthName = month.getValue();
                    if (date.toLowerCase().contains(monthName)){
                        date = date.toLowerCase().replace(monthName, month.getKey().toString());
                        break;
                    }
                }
                date = date.trim().replace(" ", "-");
            }

            return currentActor.trim() + "," + country.trim() + "," + date.trim() + ",";
        }
        return "";
    }

    /** Method for converting actors.list */
    public String ActorsList(String line){
        String surname = "";
        String firstname = "";
        String movieName = "";
        int end = -1;
        int end2 = -1;
        //Check for actor
        if (!(line.startsWith("\t"))){
            end = line.indexOf("\t");
            currentActor = line.substring(0, end + 1);
            currentActor = currentActor.trim();

            line = line.substring(end, line.length()).trim();
        }

        end = currentActor.indexOf(",");
        if (end != -1){
            surname = currentActor.substring(0, end);
            firstname = currentActor.substring(end + 1);
        }
        else
            firstname = currentActor;

        //Exception for (((Resonancia))) (only 2 occurrences in actors)
        if (line.contains("(((Resonancia)))"))
            return (firstname + " " + surname).replace(",","").trim() + "," + "Resonancia" + "," + "," + ",";

        //Get movie (or serie) name
        end = line.indexOf("(");
        if (end != -1) {

            int maxLoops = 5;
            // Loop till we have a valid year value (max 5 loops)
            while (maxLoops > 0){
                end2 = line.indexOf(")",end + 1);

                // If it could be a year value
                if (end2 - end >= 5 && !line.substring(end, end2).contains(" ") && !line.substring(end, end2).contains("\'")){
                    if ((Character.isDigit(line.charAt(end + 1)) && Character.isDigit(line.charAt(end + 2)) && Character.isDigit(line.charAt(end + 3)) && Character.isDigit(line.charAt(end + 4))) || ('?' == line.charAt(end + 1))){
                        break;
                    }
                }

                if (end2 != -1)
                    end = end2;
                else
                    break;

                maxLoops--;
            }

            movieName = line.substring(0, end);
            movieName = movieName.trim();

            line = line.substring(end, line.length()).trim();
        }

        //Get release year
        String releaseYear = "";
        end = line.indexOf(")");
        if (end != -1) {
            releaseYear = line.substring(0, end + 1);
            releaseYear = releaseYear.replace("(","").replace(")","").replace("????","").trim();
            end2 = releaseYear.indexOf("/");
            if (end2 != -1){
                releaseYear = releaseYear.substring(0, end2);
                line = line.substring(end2 + 1, line.length()).trim();
            }
            else
                line = line.substring(end + 1, line.length()).trim();

            end2 = releaseYear.indexOf("-");
            if (end2 != -1)
                releaseYear = releaseYear.substring(end2 + 1);

            try{
                Integer.parseInt(releaseYear);
            } catch (Exception e){
                releaseYear = "";
            }
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

        line = (firstname + " " + surname).replace(",","").replace("\"","").replace("\'","").trim() + "," + movieName.replace(",","").replace("\"","").replace("\'","") + "," + releaseYear + "," + episodeName.replace(",","").replace("\"","") + "," + actorRole.replace("`","").replace("\"","").replace(",","");

        // Sanity checks to avoid query errors
        int count = 0;
        for (int i = 0; i < line.length(); i++){
            if (line.charAt(i) == ',')
                count++;
        }
        if (count == 4)
            return line;
        else
            return "";
    }

    /** Method for converting business.list */
    public String BusinessList(String line){
        // This function only returns something when MV(title) and (BT(budget) or GR(revenue)) have been been found
        if (!line.contains("MV:") && !line.contains("BT:") && !line.contains("GR:"))
            return "";

        if (line.contains("MV:")){
            if (line.contains("("))
                currentMovie = line.substring(3, line.indexOf("(") - 1);
            else
                currentMovie = line.substring(3, line.length() - 1);
        }

        if (line.contains("GR:")){
            String revenueData = line.substring(3, line.length() - 1).trim();

            String currency = "";
            String revenue = "";
            String country = "";

            // Get currency
            currency = revenueData.substring(0, 3);
            revenueData = revenueData.substring(3, revenueData.length());

            // Get revenue
            int end = revenueData.indexOf("(");
            if (end != -1){
                revenue = revenueData.substring(0, end);
                revenueData = revenueData.substring(end + 1, revenueData.length());

                // Get country
                end = revenueData.indexOf(")");
                if (end != -1){
                    country = revenueData.substring(0, end);
                }
            } else
                revenue = revenueData.substring(0, revenueData.length());

            // Ignore entries that are the same except for revenue and date (old entries from idmb)
            if (country.toLowerCase().equals(previousCountry.toLowerCase()) && currentMovie.toLowerCase().equals(previousMovie.toLowerCase())){ //als de country het zelfde is als de vorige skippen
                previousCountry = country;
                previousMovie = currentMovie;

                return "";
            } else {
                previousCountry = country;
                previousMovie = currentMovie;

                return currentMovie.replace(",",";").trim() + "," + currency.trim() + "," + revenue.trim().replace(",",".") + "," + country.trim() + ",";
            }
        }
        return "";
    }

    /** Method for converting ratings.list */
    public String RatingsList(String line){
        // Distribution  Votes  Rank  Title
        // Only interested in Rank and Title

        // If line does not start with an empty space there is no data we are interested in
        if (!line.startsWith(" "))
            return "";

        String rank = "";
        String title = "";

        // Entries are divided by whitespaces, skip the first 2 entries
        int skipEntries = 2;
        int currentEntry = 1;
        for (int i = 0; i < line.length(); i++){
            // Entry found
            if (line.charAt(i) != ' '){
                // Skip entry
                if (skipEntries > 0){
                    while (line.charAt(i) != ' '){
                        i++;
                    }
                    skipEntries--;
                }
                else{
                    if (currentEntry == 3){
                        rank = line.substring(i, line.indexOf(" ", i));
                        while (line.charAt(i) != ' '){
                            i++;
                        }
                    }
                    else if (currentEntry == 4)
                        title = line.substring(i, line.indexOf("("));
                }
                currentEntry++;
            }
        }

        return rank.trim() + "," + title.replace(",",";").trim() + ",";
    }

    /** Method for converting countries.list */
    public String CountriesList(String line){
        line = line.replace(","     ,";");
        line = line.replace(")}"    ,",");
        line = line.replace("(#"	,"#");
        line = line.replace("\""    ,"");
        line = line.replace("("     ,",");
        line = line.replace(")"     ,",");
        line = line.replace("{"     ,"");
        line = line.replace("}}"    ,"");
        line = line.replace("\t"    ,",");

        for(int i = 0; i < 10; i++)
        {
            String repstr = ",,,,";
            line = line.replace(repstr,",,,");
        }

        int count = 0;
        for(int x = 0; x < line.length(); x++)
        {
            if(line.charAt(x) == ',')
            {
                count++;
            }
        }

        while(count > 3) {
            line = line.replace(",,", ",");
            count--;
        }

        return line;
    }

} 