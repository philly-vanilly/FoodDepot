package de.uni_hamburg.vsis.fooddepot.fooddepotclient.speech;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.box.BoxActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.configuration.ProfileActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.configuration.SettingsActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.SortingSelector;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.main.FDepotApplication;

/**
 * parts of the code are based on https://github.com/gast-lib/gast-lib by
 * Greg Milette &#60;<a href="mailto:gregorym@gmail.com">gregorym@gmail.com</a>&#62;
 * and licensed under Apache License V.2
 */
public class SpeechActivator implements RecognitionListener {
    private static final String TAG = "SpeechActivator";

    private Context context;
    private SpeechRecognizer recognizer;

    public SpeechActivator(Context context) {
        this.context = context;
    }

    public void detectActivation() {
        Log.d(TAG, "Activation detected");
        recognizeSpeechDirectly();
    }

    private void recognizeSpeechDirectly() {
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        // accept partial results if they come:
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        if (!recognizerIntent.hasExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE)) {
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.dummy");
        }
        getSpeechRecognizer();
        recognizer.setRecognitionListener(this);
        recognizer.startListening(recognizerIntent);
    }

    public void stop() {
        if (getSpeechRecognizer() != null) {
            getSpeechRecognizer().stopListening();
            getSpeechRecognizer().cancel();
            getSpeechRecognizer().destroy();
        }
    }

    @Override
    public void onResults(Bundle results) {
        //GOOGLE FIRST RETURNS ONE BEST RESULT
        Log.d(TAG, "full results");
        receiveResults(results, false);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        //GOOGLE THEN RETURNS A LIST OF PARTIAL RESULTS
        Log.d(TAG, "partial results");
        receiveResults(partialResults, true);
    }

    /**
     * common method to process any results bundle from {@link SpeechRecognizer}
     */
    private void receiveResults(Bundle results, boolean isPartial) {
        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            float[] scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
            receiveWhatWasHeard(heard, scores, isPartial);
        } else {
            Log.d(TAG, "no results");
        }
    }

    private void receiveWhatWasHeard(List<String> heard, float[] scores, boolean isPartial) {

        Activity activity = FDepotApplication.getApplication().getCurrentActivity();
        if (activity != null && activity instanceof  BoxesActivity) {
            BoxesActivity boxesActivity = (BoxesActivity) activity;

            final DrawerLayout drawer = (DrawerLayout) boxesActivity.findViewById(R.id.drawer_layout);
            final NavigationView navView = (NavigationView) boxesActivity.findViewById(R.id.nav_view);

            for (int i = 0; i < heard.size(); i++) {
                String heardTrimmedLowerCased = heard.get(i).trim().toLowerCase();

                for (HandledCommand command : HandledCommand.values()) {
                    if (HandledCommand.getPhrases(command).contains(heardTrimmedLowerCased)) {
                        switch (command) {
                            case MENU:
                                if (drawer.isDrawerOpen(navView)) {
                                    drawer.closeDrawers();
                                } else {
                                    drawer.openDrawer(navView);
                                }
                                break;
                            case VIEW:
                                MenuItem mapListItem = navView.getMenu().findItem(R.id.nav_switch_map_list);
                                boxesActivity.getDrawerMenuActions().mapListSwitch(mapListItem);
                                break;
                            case BEACONS:
                                MenuItem beaconsItem = navView.getMenu().findItem(R.id.nav_switch_beacon_active);
                                boxesActivity.getDrawerMenuActions().beaconActiveSwitch(beaconsItem);
                                break;
                            case PROFILE:
                                boxesActivity.getDrawerMenuActions().openProfile();
                                break;
                            case SETTINGS:
                                boxesActivity.getDrawerMenuActions().openSettings();
                                break;
                            case SPEECH:
                                MenuItem speechItem = navView.getMenu().findItem(R.id.nav_speech);
                                boxesActivity.getDrawerMenuActions().speechActiveSwitch(speechItem);
                                break;
                            case NAME:
                                BoxFactory.getFactory().getBoxDao().sortBySelection(SortingSelector.NAME);
                                break;
                            case PRICE:
                                BoxFactory.getFactory().getBoxDao().sortBySelection(SortingSelector.PRICE);
                                break;
                            case DISTANCE:
                                BoxFactory.getFactory().getBoxDao().sortBySelection(SortingSelector.DISTANCE);
                                break;
                            case RATING:
                                BoxFactory.getFactory().getBoxDao().sortBySelection(SortingSelector.RATING);
                                break;
                            case BACK:
                                boxesActivity.onBackPressed();
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } else if (activity != null && activity instanceof BoxActivity){
            BoxActivity boxActivity = (BoxActivity) activity;
            for (int i = 0; i < heard.size(); i++) {
                String heardTrimmedLowerCased = heard.get(i).trim().toLowerCase();
                if (HandledCommand.getPhrases(HandledCommand.BACK).contains(heardTrimmedLowerCased)) {
                    boxActivity.onBackPressed();
                    break;
                }
            }
        } else if (activity != null && activity instanceof ProfileActivity){
            ProfileActivity profileActivity = (ProfileActivity) activity;
            for (int i = 0; i < heard.size(); i++) {
                String heardTrimmedLowerCased = heard.get(i).trim().toLowerCase();
                if (HandledCommand.getPhrases(HandledCommand.BACK).contains(heardTrimmedLowerCased)) {
                    profileActivity.onBackPressed();
                    break;
                }
            }
        } else if (activity != null && activity instanceof SettingsActivity){
            SettingsActivity settingsActivity = (SettingsActivity) activity;
            for (int i = 0; i < heard.size(); i++) {
                String heardTrimmedLowerCased = heard.get(i).trim().toLowerCase();
                if (HandledCommand.getPhrases(HandledCommand.BACK).contains(heardTrimmedLowerCased)) {
                    settingsActivity.onBackPressed();
                    break;
                }
            }
        }

        //display what has been heard:
        String text = "";
        for (int i = 0; i<heard.size(); i++){
            text += "'" + heard.get(i) + "'";
            if (i<heard.size()-1){
                text += ", ";
            }
        }
        if (!Objects.equals("", text) && !Objects.equals("''", text)) {
            text = "Heard Results: " + text;
            Log.d(TAG, text);
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }

        //STOP LOOPING:
//      stop();
//      resultListener.activated(true);

        recognizeSpeechDirectly(); //continue looping
    }

    @Override
    public void onError(int errorCode)
    {
        if ((errorCode == SpeechRecognizer.ERROR_NO_MATCH) || (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {
            Log.d(TAG, "didn't recognize anything");
            // keep going
            recognizeSpeechDirectly();
        }
        else {
            Log.d(TAG, "FAILED " + diagnoseErrorCode(errorCode));
        }
    }

    private SpeechRecognizer getSpeechRecognizer() {
        if (recognizer == null) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context);
        }
        return recognizer;
    }

   @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "ready for speech " + params);
    }

    @Override
    public void onEndOfSpeech() {
    }
    
    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }


    public static String diagnoseErrorCode(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
