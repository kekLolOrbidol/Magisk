package com.exa.test.ai_machine;

import java.util.ArrayList;

/**
 * Template class for converting data from Java objects to JSON and back, using the <a href="https://github.com/google/gson">Gson</a> library.
 */
public class GsonTemplateContinent {

    public String name;
    public ArrayList<GsonTemplateCountry> countries;

    public GsonTemplateContinent(String name, ArrayList<GsonTemplateCountry> countries) {
        this.name = name;
        this.countries = countries;
    }

}

