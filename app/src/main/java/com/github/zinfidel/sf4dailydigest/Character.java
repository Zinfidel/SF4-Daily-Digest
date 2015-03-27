package com.github.zinfidel.sf4dailydigest;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class Character {

    private static final HashMap<String, Character> nameMap = new HashMap<>();
    public static final List<String> defaultCharList = new ArrayList<>();

    public final String id, name;
    public final int icon;
    // TODO: Search terms probably need to be in a giant, inclusive map that map the terms to the
    // char name. Regexes for common patterns can be used to identify the search terms in a string,
    // eventually (possibly) falling into language processing for hard-to-process strings. Maybe
    // something like Apache Lucene.
    public final List<String> search;


    /** Private constructor for internal use. Get characters with the getter. */
    private Character(String id, String name, int icon, List<String> search) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.search = search;
    }


    /**
     * Gets character objects.
     * @param id The id of the character. (ex: "abel" or "viper").
     * @return Immutable character object.
     */
    public static Character get(String id) {
        return nameMap.get(id);
    }

    /**
     * Loads character data from characters.xml into this class' static data structures.
     * @param res Application-level resources instance.
     * @throws IOException if characters.xml can't be opened.
     * @throws XmlPullParserException if the pull parser fails to read characters.xml.
     */
    public static void LoadCharacters(Resources res) throws IOException,
                                                            XmlPullParserException {
        XmlResourceParser parser = res.getXml(R.xml.characters);
        ParseCharacters(parser, res);
        parser.close();
    }

    /**
     * Parses a single character node in character.xml and adds it to the name map.
     * @param parser The parser currently parsing character.xml.
     * @param res Application-level resources instance.
     * @throws IOException if an error occurs while reading character.xml.
     * @throws XmlPullParserException if the parser fails to read characters.xml.
     */
    private static void ParseCharacters(XmlPullParser parser, Resources res)
            throws IOException, XmlPullParserException {

        String id = null;
        String name = null;
        int icon = 0;
        List<String> search = null;

        // The parser starts out before the first line of XML, so need to advance one "tag" to get
        // to the prelude tag, then once more to get to the resource tag. The loop starts by
        // advancing the tag again, so it will start on the first character tag.
        parser.next();
        parser.next();
        while (parser.nextTag() == XmlPullParser.START_TAG) {
            id = parser.getAttributeValue(null, "id");

            // Dynamically load character icon by its name.
            icon = res.getIdentifier(id, "drawable", "com.github.zinfidel.sf4dailydigest");

            // Loop through tags in character node. Recurse into search tags.
            while (parser.nextTag() == XmlPullParser.START_TAG) {
                switch (parser.getName()) {
                    case "name":
                        name = parser.nextText();
                        break;
                    case "search":
                        search = ParseSearch(parser);
                        break;
                    default:
                        throw new XmlPullParserException("Invalid tag encountered.");
                }
            }

            Character character = new Character(id, name, icon, search);
            nameMap.put(id, character);
            defaultCharList.add(character.id);
        }
    }

    /**
     * Parses a single search node in character.xml.
     * @param parser The parser currently parsing character.xml.
     * @throws IOException if an error occurs while reading character.xml.
     * @throws XmlPullParserException if the parser fails to read characters.xml.
     */
    private static List<String> ParseSearch(XmlPullParser parser) throws IOException,
                                                                         XmlPullParserException {
        ArrayList<String> search = new ArrayList<>(5);
        while (parser.nextTag() == XmlPullParser.START_TAG) {
            search.add(parser.nextText());
        }

        return search;
    }

}
