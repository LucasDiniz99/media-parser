/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Class that encapsulate the media data and methods, can be used either to
 * store the media, parse a media from string and also to print it's xml format
 * @author lucas
 */
public class Media {

    public static float MAX_SCORE = 10;
    public static float BASE_SCORE = 5;

    public String title;
    public ArrayList<String> altNames = new ArrayList();
    public int chapters = 0;
    public float score = 0.0f;

    public Media(String name) {
        this.title = name;
    }

    public Media(String name, int chapters, float score) {
        this.title = name;
        this.chapters = chapters;
        this.score = score;
    }

    /**
     * Parses a line and adds as a new alt title for this media (a valid alt 
     * title is required to work as intended)
     * @param line String the line that will be parsed as an alternative name
     * for this media
     */
    public void addAltName(String line) {
        this.altNames.add(line.substring(1, line.length() - 1));
    }

    /**
     * Method that checks if a line is a media, media alt title or invalid data
     * @param line
     * @return 1 for valid media, 2 for valid alt title, 0 for invalid  media
     */
    public static int checkLine(String line) {
        // Checks for a valid media
        Pattern pattern = Pattern.compile("[|]*(.+[ ]*) -([0-9]+)-");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return 1;
        }
        // Checks for a valid alt title
        pattern = Pattern.compile("\\[(.)+\\]");
        matcher = pattern.matcher(line);

        if (matcher.find()) {
            return 2;
        }
        return 0;
    }

    /**
     * Method that parses a string and converts it into a media
     * @param data String the string with the media to be parsed
     * @return Media a instance of the Media class
     */
    public static Media parse(String data) {
        // Generate the pattern
        Pattern pattern = Pattern.compile("[|]*(.+[ ]*) -([0-9]+)-[ ]*([ªº*]*)");
        Matcher matcher = pattern.matcher(data);
        // Check the pattern
        if (matcher.find()) {
            // Parse the score
            float score = 0.0f;
            String scoreGroup = matcher.group(3);
            if(scoreGroup != null && scoreGroup.length() > 0) {
                switch(scoreGroup.charAt(0)) {
                    case 'ª':
                    case 'º':
                        score = MAX_SCORE;
                        break;
                    default:
                        score = (scoreGroup.length() / BASE_SCORE) * MAX_SCORE;
                }
            }
            // Build and return the media
            return new Media(
                matcher.group(1),
                Integer.parseInt(matcher.group(2)),
                score
            );
        }
        return null;
    }

    /**
     * Override of the default toString method, used for converting this
     * object into a xml equivalent
     * @return String xml formatted object representing this media
     */
    @Override
    public String toString() {
        String names = "";
        for(String name : this.altNames) {
            names += String.format("<name>%s</name>", StringEscapeUtils.escapeHtml(name));
        }
        
        return String.format("<media><title>%s</title><altnames>%s</altnames><chapters>%d</chapters><score>%.2f</score></media>",
                StringEscapeUtils.escapeHtml(this.title),
                names,
                this.chapters,
                this.score
        );
    }
}
