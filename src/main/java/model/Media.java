/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Class that encapsulate the media data and methods, can be used either to
 * store the media, parse a media from string and also to print it's xml format
 *
 * @author lucas
 */
public class Media {

    public static float MAX_SCORE = 10;
    public static float BASE_SCORE = 5;

    public String title;
    public ArrayList<String> altNames = new ArrayList();
    public int chapters = 0;
    public float score = 0.0f;
    public String mediaParams = " ";

    public Media(String title) {
        this.title = title;
    }

    public Media(String title, int chapters, float score) {
        this.title = title;
        this.chapters = chapters;
        this.score = score;
    }

    public Media(String title, int chapters, float score, HashMap<String, String> fileParams) {
        this.title = title;
        this.chapters = chapters;
        this.score = score;
        for (Map.Entry<String, String> param : fileParams.entrySet()) {
            if (this.mediaParams.length() > 1) {
                this.mediaParams += " ";
            }
            this.mediaParams += String.format("%s=\"%s\"", StringEscapeUtils.escapeHtml(param.getKey()), StringEscapeUtils.escapeHtml(param.getValue()));
        }
    }

    /**
     * Parses a line and adds as a new alt title for this media (a valid alt
     * title is required to work as intended)
     *
     * @param line String the line that will be parsed as an alternative name
     * for this media
     */
    public void addAltName(String line) {
        this.altNames.add(line.substring(1, line.length() - 1));
    }

    /**
     * Method that checks if a line is a media, media alt title or invalid data
     *
     * @param line
     * @return 1 for valid media, 2 for valid alt title, 0 for invalid media
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
     *
     * @param data String the string with the media to be parsed
     * @param fileParams HashMap<String, String> The params of the file
     * @return Media a instance of the Media class
     */
    public static Media parse(String data, HashMap<String, String> fileParams) {
        HashMap<String, String> newFileParams = new HashMap(fileParams);
        // Generate the pattern
        Pattern pattern = Pattern.compile("[|]*(.+[ ]*)[ ]+-(.+)-[ ]*([ªº*]*)");
        Matcher matcher = pattern.matcher(data);
        // Check the pattern for complete medias
        if (matcher.find() && matcher.group(1).length() > 0 && matcher.group(2).length() > 0) {
            // Try parsing the chapter length 
            int chapters;
            try {
                chapters = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                chapters = 1;
                newFileParams.put("chapter-type", matcher.group(2));
            }
            // Parse the score
            float score = 0.0f;
            String scoreGroup = matcher.group(3);
            if (scoreGroup != null && scoreGroup.length() > 0) {
                switch (scoreGroup.charAt(0)) {
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
                    chapters,
                    score,
                    newFileParams
            );
        } else {
            // Check for medias without chapter number, but with chapter type
            pattern = Pattern.compile("[|]*(.+[ ]*) (.+) ([ªº*]+)");
            matcher = pattern.matcher(data);
            if (matcher.find()) {
                // Set the params
                newFileParams.put("chapter-type", matcher.group(2));
                // Parse the score
                float score = 0.0f;
                String scoreGroup = matcher.group(3);
                if (scoreGroup != null && scoreGroup.length() > 0) {
                    switch (scoreGroup.charAt(0)) {
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
                        1,
                        score,
                        newFileParams
                );
            }
        }
        return null;
    }

    /**
     * Override of the default toString method, used for converting this object
     * into a xml equivalent
     *
     * @return String xml formatted object representing this media
     */
    @Override
    public String toString() {
        String names = "";
        for (String name : this.altNames) {
            names += String.format("<name>%s</name>", StringEscapeUtils.escapeHtml(name));
        }

        return String.format("<media%s><title>%s</title><altnames>%s</altnames><chapters>%d</chapters><score>%.2f</score></media>",
                this.mediaParams,
                StringEscapeUtils.escapeHtml(this.title),
                names,
                this.chapters,
                this.score
        );
    }
}
