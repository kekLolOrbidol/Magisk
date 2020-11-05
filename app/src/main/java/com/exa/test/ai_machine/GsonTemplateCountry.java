package com.exa.test.ai_machine;

/**
 * Template class for converting data from Java objects to JSON and back, using the <a href="https://github.com/google/gson">Gson</a> library.
 */
public class GsonTemplateCountry {

    public String name;
    public String player;
    public int count;
    public boolean modified;

    public GsonTemplateCountry(String name, String player, int count, boolean modified) {
        this.name = name;
        this.player = player;
        this.count = count;
        this.modified = modified;
    }

}

