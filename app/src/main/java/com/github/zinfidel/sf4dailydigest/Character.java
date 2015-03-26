package com.github.zinfidel.sf4dailydigest;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class Character {

    private static final HashMap<String, Character> nameMap = new HashMap<>();

    public final String id, name, name_jp;
    public final int icon;
    // TODO: Search terms probably need to be in a giant, inclusive map that map the terms to the
    // char name. Regexes for common patterns can be used to identify the search terms in a string,
    // eventually (possibly) falling into language processing for hard-to-process strings. Maybe
    // something like Apache Lucene.
    public final List<String> search, search_jp;


    private Character(String id, String name, String name_jp, int icon,
                      List<String> search, List<String> search_jp) {
        this.id = id;
        this.name = name;
        this.name_jp = name_jp;
        this.icon = icon;
        this.search = search;
        this.search_jp = search_jp;
    }


    public static void LoadCharacters(Resources res) throws IOException,
                                                            XmlPullParserException {
        XmlResourceParser parser = res.getXml(R.xml.characters);
        ParseCharacters(parser);
        Object test = nameMap;
        parser.close();
    }

    private static void ParseCharacters(XmlPullParser parser) throws IOException,
                                                                     XmlPullParserException {
        String id = null;
        String name = null;
        String name_jp = null;
        int icon = 0;
        List<String> search = null;
        List<String> search_jp = null;

        // TODO: http://stackoverflow.com/questions/19955542/xmlpullparserexception-while-parsing-a-resource-file-in-android
        parser.next();
        parser.next();
//        parser.nextTag(); // Skip the <resources> tag.
        while (parser.nextTag() == XmlPullParser.START_TAG) {
            id = parser.getAttributeValue(null, "id");
            // TODO: Set icon by retrieving resource
            icon = 1;

            while (parser.nextTag() == XmlPullParser.START_TAG) {
                switch (parser.getName()) {
                    case "name":
                        name = parser.nextText();
                        break;
                    case "name_jp":
                        name_jp = parser.nextText();
                        break;
                    case "search":
                        search = ParseSearch(parser);
                        break;
                    case "search_jp":
                        search_jp = ParseSearch(parser);
                        break;
                    default:
                        // TODO: throw exception?
                }
            }

            Character character = new Character(id, name, name_jp, icon, search, search_jp);
            nameMap.put(id, character);
        }
    }

    private static List<String> ParseSearch(XmlPullParser parser) throws IOException,
                                                                         XmlPullParserException {
        ArrayList<String> search = new ArrayList<>(5);
        while (parser.nextTag() == XmlPullParser.START_TAG) {
            search.add(parser.nextText());
        }

        return search;
    }

}
