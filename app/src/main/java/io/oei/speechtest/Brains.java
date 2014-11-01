package io.oei.speechtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by c on 10/31/14.
 */
public class Brains {
    protected static final String log_tag = "brains";

    public static SpeechRecognizer speechRecognizer;
    public static TextToSpeech textToSpeech;
    public static long speechId = 0;

    public static void init(Context ctx) {
        initTextToSpeech(ctx);
        initSpeechRecognizer(ctx);
    }

    protected static void initTextToSpeech(Context ctx) {
        textToSpeech = new TextToSpeech(ctx, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.SUCCESS) {
                    Log.e(log_tag, "Text to speech init failure");
                    throw new RuntimeException();
                }
                Log.d(log_tag, "Text to speech inititialized");
            }
        });
    }

    protected static void initSpeechRecognizer(Context ctx) {
        assert SpeechRecognizer.isRecognitionAvailable(ctx);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(ctx);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(log_tag, "Ready for Speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(log_tag, "Beginning of Speech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
//                Log.d(log_tag, "RMS: " + rmsdB);
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d(log_tag, "Buffer received");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d(log_tag, "End of Speech");
            }

            @Override
            public void onError(int error) {
                handleSpeechRecognizerError(error);
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d(log_tag, "Event: " + eventType);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d(log_tag, "Partial results");
            }

            @Override
            public void onResults(Bundle results) {
                handleRecognitionResults(results);
            }
        });
    }

    protected static void say(String s) {
        int rc = textToSpeech.speak(s, TextToSpeech.QUEUE_ADD, null);
        Log.d(log_tag, "Text to speech return code: " + rc);
    }

    protected static void handleRecognitionResults(Bundle results) {
        ArrayList<String> x = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.i(log_tag, "Result strings: " + x);
        Log.i(log_tag, "Result confidence: " +
                results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)[0]);

        for (String s : x) {
            String l = s.toLowerCase();

            if (l.contains("flash") || l.contains("light")) {
                Hardware.toggleFlashlight();
                return;
            }

        }
    }


    protected static void handleSpeechRecognizerError(int error) {
        Log.d(log_tag, "Error: " + error);
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                Log.e(log_tag, "Audio recording error");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                Log.e(log_tag, "Other client side errors");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                Log.e(log_tag, "Insufficient permissions");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                Log.e(log_tag, "Other network related errors");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                Log.e(log_tag, "Network operation timed out");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                Log.e(log_tag, "No recognition result matched");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                Log.e(log_tag, "RecognitionService busy");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                Log.e(log_tag, "Server sends error status");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                Log.e(log_tag, "No speech input");
                break;
        }
    }

    protected static Intent speechRecognizerIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "io.oei.SpeechTest");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        return intent;
    }

    public static void start() {
        speechRecognizer.startListening(speechRecognizerIntent());
    }

    public static void stop() {
        speechRecognizer.stopListening();
    }
}