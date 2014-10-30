package io.oei.speechtest;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import java.util.ArrayList;


/**
 * An activity representing a list of Hashes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link HashDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link HashListFragment} and the item details
 * (if present) is a {@link HashDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link HashListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class HashListActivity extends Activity
        implements HashListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static SpeechRecognizer speechRecognizer;
    public static TextToSpeech textToSpeech;
    public static long speechId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_list);

        if (findViewById(R.id.hash_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((HashListFragment) getFragmentManager()
                    .findFragmentById(R.id.hash_list))
                    .setActivateOnItemClick(true);
        }

        Log.d("speech_test", "Initiating TextToSpeech");
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.SUCCESS) {
                    Log.e("speech_test", "Text to speech init failure");
                    throw new RuntimeException();
                }
                Log.d("speech_test", "Text to speech inititialized");
            }
        });
        Log.d("speech_test", "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(getApplicationContext()));
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("speech_test", "Ready for Speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("speech_test", "Beginning of Speech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
//                Log.d("speech_test", "RMS: " + rmsdB);
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d("speech_test", "Buffer received");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("speech_test", "End of Speech");
            }

            @Override
            public void onError(int error) {
                Log.d("speech_test", "Error: " + error);
                switch(error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        Log.e("speech_test", "Audio recording error");
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        Log.e("speech_test", "Other client side errors");
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        Log.e("speech_test", "Insufficient permissions");
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        Log.e("speech_test", "Other network related errors");
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        Log.e("speech_test", "Network operation timed out");
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        Log.e("speech_test", "No recognition result matched");
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        Log.e("speech_test", "RecognitionService busy");
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        Log.e("speech_test", "Server sends error status");
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        Log.e("speech_test", "No speech input");
                        break;
                }
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> x = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.d("speech_test", "Result strings: " + x);
                Log.d("speech_test", "Result confidence: " +
                    results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)[0]);
//                CharSequence cs = results.getStringArray(SpeechRecognizer.RESULTS_RECOGNITION)[0];
//                textToSpeech.speak(cs, TextToSpeech.QUEUE_ADD, new Bundle(), "speech-test-" + speechId++);
                Log.d("speech_test", "Max string length "+ textToSpeech.getMaxSpeechInputLength());
                for (String s : x) {
                    Log.d("speech_test", "About to speak string");
                    int rc = textToSpeech.speak(s,
                            TextToSpeech.QUEUE_ADD,
                            null
                    );
                    Log.d("speech_test", "Text to speech return code: " + rc);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d("speech_test", "Partial results");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d("speech_test", "Event: " + eventType);
            }
        });
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"io.oei.SpeechTest");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        speechRecognizer.startListening(intent);
        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link HashListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(HashDetailFragment.ARG_ITEM_ID, id);
            HashDetailFragment fragment = new HashDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.hash_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, HashDetailActivity.class);
            detailIntent.putExtra(HashDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
