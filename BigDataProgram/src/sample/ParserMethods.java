/**
 * Made by Anne Pieter Boonstra & Robert Bijl
 */

package sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserMethods {

    // Persistent variables
    private String currentActor = "";
    private String currentMovie = "";
    private final HashMap<Integer, String> monthNameNumber;
    {
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
    public final ArrayList<String> supportedFiles;
    {
        supportedFiles = new ArrayList<>();
        supportedFiles.add("Actors");
        supportedFiles.add("Actresses");
        supportedFiles.add("Biographies");
        supportedFiles.add("Business");
        supportedFiles.add("Countries");
        supportedFiles.add("Ratings");
        supportedFiles.add("Movies");
        supportedFiles.add("Locations");

    }

    // Persistent variables for business.list
    private String previousCountry = "";
    private String previousMovie = "";

    public ParserMethods(){

    }

    /** Method for converting movies.list */
    public String MoviesList(String line){
        //Get movie (or serie) name
        String movieName = "";
        int splitIndex = line.lastIndexOf("\"");
        if (splitIndex != -1){
            movieName = line.substring(0, splitIndex + 1);
            movieName = movieName.replace("\"","").trim();

            line = line.substring(splitIndex + 1, line.length()).trim();
        }
        else{
            splitIndex = line.indexOf("(");
            if (splitIndex != -1){
                movieName = line.substring(0, splitIndex);
                movieName = movieName.trim();

                line = line.substring(splitIndex, line.length()).trim();
            }
        }

        //Get release year
        String releaseYear = "";
        splitIndex = line.indexOf(")");
        if (splitIndex != -1){
            releaseYear = line.substring(0, splitIndex + 1);
            releaseYear = releaseYear.replace("(","").replace(")","").trim();

            line = line.substring(splitIndex + 1, line.length()).trim();
        }

        //Get episode name (if exists)
        String episodeInfo = "";
        splitIndex = line.lastIndexOf("}");
        if (splitIndex != -1){
            episodeInfo = line.substring(0, splitIndex + 1);
            episodeInfo = episodeInfo.replace("{","").replace("}","").trim();

            line = line.substring(splitIndex + 1, line.length()).trim();
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
        int splitIndex = line.indexOf(" (");
        if (splitIndex != -1) {
            movieName = line.substring(0, splitIndex);
            movieName = movieName.trim();

            line = line.substring(splitIndex, line.length()).trim();
        }

        //Get release year
        String releaseYear = "";
        splitIndex = line.indexOf(")");
        if (splitIndex != -1) {

            releaseYear = line.substring(0, splitIndex + 1);
            releaseYear = releaseYear.replace("(","").replace(")","").trim();

            int splitIndex2 = releaseYear.indexOf("/");
            if (splitIndex2 != -1)
                releaseYear = releaseYear.substring(0,splitIndex2);


            line = line.substring(splitIndex + 1, line.length()).trim();
        }

        //Get serie episode name (if found)
        String episodeName = "";
        splitIndex = line.indexOf("}");
        if (splitIndex != -1){
            episodeName = line.substring(0, splitIndex + 1);
            episodeName = episodeName.replace("{","").replace("}","").trim();

            line = line.substring(splitIndex + 1, line.length()).trim();
        }

        //Get locations and store them in an array
        List<String> locations = new ArrayList<>();
        while (line.contains(",")){
            splitIndex = line.indexOf(",");
            if (splitIndex != -1){
                locations.add(line.substring(0, splitIndex + 1).replace(",","").trim());

                line = line.substring(splitIndex + 1, line.length()).trim();
            }
        }
        //Get the last location (at the splitIndex of the string)
        splitIndex = line.indexOf("(");
        if (splitIndex != -1){
            locations.add(line.substring(0, splitIndex).replace(",","").trim());

            line = line.substring(splitIndex, line.length()).trim();
        }
        else{
            locations.add(line.trim());
        }

        line = movieName.replace(",","") + "," + releaseYear + "," + episodeName.replace(",","") + ",";

        int lol = 3;
        //Starting at the splitIndex of the array to get the last 3 locations
        for (int i = locations.size() - 1; i > 0; i--){
            //Only use the last 3 entries in locations (we don't need stage name etc.)
            if (i == locations.size() - 4)
                break;

            if (i == locations.size() - 3){
                splitIndex = locations.get(i).indexOf(")");
                if (splitIndex != -1) {
                    locations.set(i, locations.get(i).substring(splitIndex + 1, locations.get(i).length()).trim());
                }
            }
            //For the last item in locations (first in loop)
            else if (i == locations.size()){
                splitIndex = locations.get(i).indexOf("(");
                if (splitIndex != -1) {
                    locations.set(i, locations.get(i).substring(splitIndex, locations.get(i).length()).trim());
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

            // Only interested in date and country (first and last item)
            // With commaCount == 0 there is only 1 item, check if it's a date or a country
            if (commaCount == 0){
                // Check if it's a date
                if (birthInfo.length() > 4){
                    if (isNumeric(birthInfo.substring(birthInfo.length() - 4))){
                        date = birthInfo;
                    }
                }
                //if no date was found
                if (date.isEmpty())
                    country = birthInfo;
            }
            // With commaCount > 0 there is more than 1 item
            else{
                // Date is always first (if it is included)
                date = birthInfo.substring(0, birthInfo.indexOf(","));
                country = birthInfo.substring(birthInfo.lastIndexOf(",") + 1, birthInfo.length());

                // check if date is actually a valid date
                if (!isNumeric(date.substring(date.length() - 4))){
                    date = "";
                }
            }

            // Exception for DB: c. 1936
            if (line.contains("."))
                date = date.substring(date.indexOf(".") + 1);

            // Exception for 'DB: circa 1938'
            if (date.contains("circa"))
                date = date.substring(date.indexOf("circa") + 5);

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
                // Count (-), must be 2
                int dashIndex = date.indexOf("-");
                if (dashIndex != -1){
                    dashIndex = date.indexOf("-", dashIndex + 1);
                    if (dashIndex == -1)
                        date = "1-" + date.trim();
                }
                else
                    date = "1-1-" + date.trim();

            }

            String surname = "";
            String forename = "";
            int splitIndex = currentActor.indexOf(",");
            if (splitIndex != -1){
                surname = currentActor.substring(0, splitIndex).trim();
                forename = currentActor.substring(splitIndex + 1).trim();
            }
            else
                forename = currentActor.trim();

            return (forename + " " + surname).replace(",","").replace("\"","").replace("\'","").trim() + "," + country.replace("]","").replace("[","").trim() + "," + date.trim();
        }
        return "";
    }

    /** Method for converting actors.list */
    public String ActorsList(String line){
        String surname = "";
        String forename = "";
        String movieName = "";
        int splitIndex = -1;
        int splitIndex2 = -1;
        //Check for actor
        if (!(line.startsWith("\t"))){
            splitIndex = line.indexOf("\t");
            currentActor = line.substring(0, splitIndex + 1).trim();

            line = line.substring(splitIndex).trim();
        }

        splitIndex = currentActor.indexOf(",");
        if (splitIndex != -1){
            surname = currentActor.substring(0, splitIndex).trim();
            forename = currentActor.substring(splitIndex + 1).trim();
        }
        else
            forename = currentActor.trim();

        //Exception for (((Resonancia))) (only 2 occurrences in actors)
        if (line.contains("(((Resonancia)))"))
            return (forename + " " + surname).replace(",","").trim() + "," + "Resonancia" + "," + "," + ",";

        //Get movie (or serie) name
        //Exception for lines that start with "("
        line = line.trim();
        if (line.startsWith("(") || line.startsWith("\"("))
            splitIndex = line.indexOf("(", line.indexOf("(") + 1);
        else
            splitIndex = line.indexOf("(");

        if (splitIndex != -1) {
            int maxLoops = 5;
            // Loop till we have a valid year value (max 5 loops)
            while (maxLoops > 0){
                splitIndex2 = line.indexOf(")",splitIndex);
                // If no ")" is found
                if (splitIndex2 == -1){
                    break;
                }
                else{
                    // If it could be a year value
                    if (splitIndex2 - splitIndex >= 5 && !line.substring(splitIndex, splitIndex2).contains(" ") && !line.substring(splitIndex, splitIndex2).contains("\'")){
                        if (isNumeric(line.substring(splitIndex + 1, splitIndex + 5)) || ("????" == line.substring(splitIndex + 1, splitIndex + 5)))
                            break;
                    }
                    else{
                        splitIndex = line.indexOf("(",splitIndex2);
                        if (splitIndex == -1)
                            splitIndex = splitIndex2;
                    }
                }
                maxLoops--;
            }
            movieName = line.substring(0, splitIndex).trim();
            line = line.substring(splitIndex).trim();
        }

        //Get release year
        String releaseYear = "";
        splitIndex = line.indexOf(")");
        if (splitIndex != -1) {
            releaseYear = line.substring(0, splitIndex + 1);
            releaseYear = releaseYear.replace("(","").replace(")","").replace("????","").trim();
            splitIndex2 = releaseYear.indexOf("/");
            if (splitIndex2 != -1){
                releaseYear = releaseYear.substring(0, splitIndex2);
                line = line.substring(splitIndex + 1).trim();
            }
            else
                line = line.substring(splitIndex + 1).trim();

            splitIndex2 = releaseYear.indexOf("-");
            if (splitIndex2 != -1)
                releaseYear = releaseYear.substring(splitIndex2 + 1);

            try{
                Integer.parseInt(releaseYear);
            } catch (Exception e){
                releaseYear = "";
            }
        }

        //Get serie episode name (if found)
        String episodeName = "";
        splitIndex = line.indexOf("}");
        if (splitIndex != -1){
            episodeName = line.substring(0, splitIndex + 1);
            episodeName = episodeName.replace("{","").replace("}","").trim();

            line = line.substring(splitIndex + 1, line.length()).trim();
        }

        //Get actor role
        String actorRole = "";
        splitIndex = line.indexOf("]");
        if (splitIndex != -1){
            actorRole = line.substring(0, splitIndex + 1);
            actorRole = actorRole.replace("[","").replace("]","").trim();

            line = line.substring(splitIndex + 1, line.length()).trim();
        }

        line = (forename + " " + surname).replace(",","").replace("\"","").replace("\'","").trim() + "," + movieName.replace(",","").replace("\"","").replace("\'","").trim() + "," + releaseYear.trim() + "," + episodeName.replace(",","").replace("\"","").trim() + "," + actorRole.replace("`","").replace("\"","").replace(",","");

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
        if (!line.startsWith("MV:") && !line.startsWith("GR:"))
            return "";

        if (line.startsWith("MV:")){
            if (line.contains("(")){
                int splitIndex = line.lastIndexOf("(");
                //if (!Character.isDigit(line.charAt(splitIndex + 1)))
               //if (splitIndex - 1 >= 0)

                currentMovie = line.substring(3, splitIndex);
            }
            else
                currentMovie = line.substring(3);
        }

        if (line.startsWith("GR:")){
            String revenueData = line.substring(3, line.length() - 1).trim();

            String currency = "";
            String revenue = "";
            String country = "";

            // Get currency
            currency = revenueData.substring(0, 3);
            revenueData = revenueData.substring(3, revenueData.length());

            // Get revenue
            int splitIndex = revenueData.indexOf("(");
            if (splitIndex != -1){
                revenue = revenueData.substring(0, splitIndex);
                revenueData = revenueData.substring(splitIndex + 1, revenueData.length());

                // Get country
                splitIndex = revenueData.indexOf(")");
                if (splitIndex != -1){
                    country = revenueData.substring(0, splitIndex);
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

                return currentMovie.replace(",",";").trim() + "," + currency.trim() + "," + revenue.replace(",","").trim() + "," + country.trim();
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

        return rank.trim() + "," + title.replace(",",";").trim();
    }

    /** Method for converting countries.list */
    public String CountriesList(String line){

        String movieName = "";
        String releaseYear = "";
        String country = "";

        //Get movie (or serie) name
        int splitIndex = line.indexOf("(");

        //Exception for lines that start with "("
        line = line.trim();
        if (line.startsWith("(") || line.startsWith("\"("))
            splitIndex = line.indexOf("(", line.indexOf("(") + 1);

        if (splitIndex != -1){
            int splitIndex2 = -1;
            int maxLoops = 5;
            // Loop till we have a valid year value (max 5 loops)
            while (maxLoops > 0){
                splitIndex2 = line.indexOf(")",splitIndex);
                // If no ")" is found
                if (splitIndex2 == -1){
                    break;
                }
                else{
                    // If it could be a year value
                    if (splitIndex2 - splitIndex >= 5 && !line.substring(splitIndex, splitIndex2).contains(" ") && !line.substring(splitIndex, splitIndex2).contains("\'")){
                        if (isNumeric(line.substring(splitIndex + 1, splitIndex + 5)) || ('?' == line.charAt(splitIndex + 1)))
                            break;
                    }
                    else{
                        splitIndex = line.indexOf("(",splitIndex2);
                        if (splitIndex == -1)
                            splitIndex = splitIndex2;
                    }
                }
                maxLoops--;
            }

            movieName = line.substring(0, splitIndex);
            line = line.substring(splitIndex + 1);
        }
        // Get release year
        splitIndex = line.indexOf(")");
        if (splitIndex != -1){
            releaseYear = line.substring(0, splitIndex).replace("????","");
            line = line.substring(splitIndex + 1);

            int splitIndex2 = releaseYear.indexOf("/");
            if (splitIndex2 != -1){
                releaseYear = releaseYear.substring(0, splitIndex2);
            }

            splitIndex2 = releaseYear.indexOf("-");
            if (splitIndex2 != -1)
                releaseYear = releaseYear.substring(splitIndex2 + 1);

            try{
                Integer.parseInt(releaseYear);
            } catch (Exception e){
                releaseYear = "";
            }
        }

        // Get country
        country = line;
        if (!country.isEmpty()){
            splitIndex = country.indexOf("}");
            if (splitIndex != -1)
                country = country.substring(splitIndex + 1);
            splitIndex = country.indexOf(")");
            if (splitIndex != -1)
                country = country.substring(splitIndex + 1);
        }

        return movieName.replace(",","").replace("\"","").replace("\'","").trim() + "," + releaseYear.trim() + "," + country.trim();
    }

    /** Method for checking if a string consists only of integers */
    public static boolean isNumeric(String str)
    {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
} 