package com.pickasso.analysis;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class City {

    static final double CHUNK_SCORE = 1.0;

    public static void main(String[] args) {

        JSONParser parser = new JSONParser();
        MapDictionary<String> dictionary = new MapDictionary<String>();

        try {
            Object obj = parser.parse(new FileReader("resources/les-communes-generalisees-dile-de-france.json"));
            JSONArray jsonArray = (JSONArray) obj;
            jsonArray.stream().forEach(object -> {
                JSONObject jsonObject = (JSONObject) object;
                JSONObject fields = (JSONObject) jsonObject.get("fields");
                String nomCom = (String) fields.get("nomcom");
                dictionary.addEntry(new DictionaryEntry<String>(nomCom, "CITY", CHUNK_SCORE));
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ExactDictionaryChunker dictionaryChunkerTT
                = new ExactDictionaryChunker(dictionary,
                IndoEuropeanTokenizerFactory.INSTANCE,
                true, true);

        ExactDictionaryChunker dictionaryChunkerTF
                = new ExactDictionaryChunker(dictionary,
                IndoEuropeanTokenizerFactory.INSTANCE,
                true, false);

        ExactDictionaryChunker dictionaryChunkerFT
                = new ExactDictionaryChunker(dictionary,
                IndoEuropeanTokenizerFactory.INSTANCE,
                false, true);

        ExactDictionaryChunker dictionaryChunkerFF
                = new ExactDictionaryChunker(dictionary,
                IndoEuropeanTokenizerFactory.INSTANCE,
                false, false);


        System.out.println("\nDICTIONARY\n" + dictionary);

        String text = "des etudiants à Torcy et à Précy-sur-Marne et encore à Torcy et torcy";
        System.out.println("\n\nTEXT=" + text);

        chunk(dictionaryChunkerTT, text);
        chunk(dictionaryChunkerTF, text);
        chunk(dictionaryChunkerFT, text);
        chunk(dictionaryChunkerFF, text);

    }

    static void chunk(ExactDictionaryChunker chunker, String text) {
        System.out.println("\nChunker."
                + " All matches=" + chunker.returnAllMatches()
                + " Case sensitive=" + chunker.caseSensitive());
        Chunking chunking = chunker.chunk(text);
        for (Chunk chunk : chunking.chunkSet()) {
            int start = chunk.start();
            int end = chunk.end();
            String type = chunk.type();
            double score = chunk.score();
            String phrase = text.substring(start, end);
            System.out.println("     phrase=|" + phrase + "|"
                    + " start=" + start
                    + " end=" + end
                    + " type=" + type
                    + " score=" + score);
        }
    }

}
