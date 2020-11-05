package com.exa.test.ai_machine;

import java.util.ArrayList;

/**
 * Template class for converting data from Java objects to JSON and back, using the <a href="https://github.com/google/gson">Gson</a> library.
 */
public class GsonTemplateCountryGraph {

    public ArrayList<GsonTemplateContinent> continents;
    public ArrayList<GsonTemplateBidirectionalLink> bidirectionalLinks;

    public GsonTemplateCountryGraph() {
        this.continents = new ArrayList<>(); 
        this.bidirectionalLinks = new ArrayList<>(); 
    }

}

