package com.biggo.AndroidGMEPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

public class AlphaIndexerAdapter<T> extends ArrayAdapter<T> implements SectionIndexer {
	ArrayList<T> myElements;
    HashMap<String, Integer> alphaIndexer;

    String[] sections;

    public AlphaIndexerAdapter(Context context, int textViewResourceId,
                    List<T> objects) {
            super(context, textViewResourceId, objects);
            myElements = (ArrayList<T>) objects;
            // here is the tricky stuff
            alphaIndexer = new HashMap<String, Integer>();
            // in this hashmap we will store here the positions for
            // the sections

            int size = myElements.size();
            for (int i = size - 1; i >= 0; i--) {
                    String element = (String) myElements.get(i);
                    alphaIndexer.put(element.substring(0, 1).toUpperCase(), i);
            //We store the first letter of the word, and its index.
            //The Hashmap will replace the value for identical keys are putted in
            }

            // now we have an hashmap containing for each first-letter
            // sections(key), the index(value) in where this sections begins

            // we have now to build the sections(letters to be displayed)
            // array .it must contains the keys, and must (I do so...) be
            // ordered alphabetically

            Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
            // cannot be sorted...

            Iterator<String> it = keys.iterator();
            ArrayList<String> keyList = new ArrayList<String>(); // list can be
            // sorted

            while (it.hasNext()) {
                    String key = it.next();
                    keyList.add(key);
            }

            Collections.sort(keyList);

            sections = new String[keyList.size()]; // simple conversion to an
            // array of object
            keyList.toArray(sections);

            // ooOO00K !

    }

    public int getPositionForSection(int section) {
            // Log.v("getPositionForSection", ""+section);
            String letter = sections[section];

            return alphaIndexer.get(letter);
    }

    public int getSectionForPosition(int position) {

            // you will notice it will be never called (right?)
            Log.v("getSectionForPosition", "called");
            return 0;
    }

    public Object[] getSections() {

            return sections; // to string will be called each object, to display
            // the letter
    }
}
