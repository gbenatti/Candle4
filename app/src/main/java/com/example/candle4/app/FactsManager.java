package com.example.candle4.app;

import android.content.Context;
import android.content.res.AssetManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;

/**
* Created by GB on 1/22/14.
*/
class FactsManager {

    private static final String TAG = "FactsManager";
    private final Random random = new Random();
    private final String[] factsArray;
    private final Context context;
    private int newFactIndex = -1;

    private final TextToSpeech speech;
    private String toc;

    public FactsManager(Context context) {
        this.context = context;

        factsArray = context.getResources().getStringArray(R.array.facts);

        speech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) { }
        });
    }

    public void selectNext() {
        newFactIndex = random.nextInt(factsArray.length);
    }

    public String getText() {
//        if (newFactIndex != -1) {
//            return factsArray[newFactIndex];
//        } else {
//            return "\"Chuck Norris facts !!\"";
//        }
        toc = loadBookTOC(context);
        return toc;
    }

    private String loadBookTOC(Context context) {
        try {
            Log.d(TAG, "loadBookTOC");
            InputStream epubRaw = context.getResources().openRawResource(R.raw.scalaonandroidepub);

            Log.d(TAG, "Stream ok");
            Book book = (new EpubReader()).readEpub(epubRaw);
            Log.d(TAG, "Book ok");
            TableOfContents toc = book.getTableOfContents();
            StringBuilder tocString = new StringBuilder();
            logTableOfContents(toc.getTocReferences(), 0, tocString);

            return tocString.toString();
        } catch (IOException ex) {
            for (StackTraceElement element: ex.getStackTrace()){
                Log.d(TAG, element.toString());
            }
            return ex.getMessage();
        }
    }

    private void logTableOfContents(List<TOCReference> tocReferences, int depth, StringBuilder tocString) {

        if (tocReferences == null) {
            return;
        }

        for (TOCReference tocReference : tocReferences) {

            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }

            tocString.append(tocReference.getTitle());
            logTableOfContents(tocReference.getChildren(), depth + 1, tocString);
        }

    }

    public void sayText() {
        String text = getText();
        speech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }
}
