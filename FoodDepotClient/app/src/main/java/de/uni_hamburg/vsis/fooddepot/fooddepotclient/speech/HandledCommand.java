package de.uni_hamburg.vsis.fooddepot.fooddepotclient.speech;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Phil on 11.08.2016.
 */
public enum HandledCommand {
    VIEW("map", "list", "view", "ansicht", "karte", "liste"),
    BEACONS("beacon", "beacons", "food beacons", "food beacon"),
    PROFILE("profile", "account", "user", "profil"),
    SETTINGS("preferences", "einstellungen", "settings"),
    SPEECH("speech", "voice control", "sprach steuerung", "sprache"),
    MENU("menu", "menü", "drawer"),
    NAME("name"),
    PRICE("price", "preis"),
    DISTANCE("distance", "distanz"),
    RATING("rating", "wertung"),
    BACK("back", "zurück", "schliessen", "beenden");

    private List<String> mPhrases;

    HandledCommand(String... phrases) {
        mPhrases = Arrays.asList(phrases);
    }

    public static List<String> getPhrases(HandledCommand command){
        return command.mPhrases;
    }
}
