package com.exa.test.helper

import android.content.Context
import android.util.Log
import com.exa.test.ai_machine.*
import com.exa.test.model.ContinentList
import com.exa.test.model.CountryList
import com.google.gson.Gson
import java.io.InputStream
import java.util.*

/**
 * Handler for JSON operations and the communication between GameActivity and MinimalRisk.
 *  */
object JsonHandler {

    fun getJsonFromFile(fileName: String, context: Context): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = context.applicationContext.assets.open(fileName)
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return json
        }
        Log.d("JSON:", json)
        return json
    }

    /**
     * Provides a new list of continents which can be used to update the game state
     *
     * @param jsonString JSON representing state of the game
     * @return List of Continents
     */
    fun getContinentListFromJson(jsonString: String): ContinentList {
        Log.d("JSON:", "getContinentListFromJson")
        Log.d("JSON:", jsonString)

        val gson = Gson()
        val continentList = gson.fromJson(jsonString, ContinentList::class.java) ?: ContinentList()
        continentList.continents.sortBy { it.name }

        //DEBUG
        for (continent in continentList.continents) {
            continent.countries.sortBy { it.name }
            Log.d("JSON:", "continent: ${continent.name}")
            for (country in continent.countries) {
                Log.d(
                    "JSON:",
                    "name: ${country.name},\t player:${country.player},\t count:${country.count},\t modified:${country.modified}"
                )
            }
        }

        return continentList
    }

    /**
     * Get a list of countries. Mostly used to get possible target countries for an attack or
     * relocation move.
     *
     * @param jsonString
     * @return List of countries
     */
    fun getCountryListFromJson(jsonString: String): CountryList {
        val gson = Gson()
        return gson.fromJson(jsonString, CountryList::class.java) ?: CountryList()
    }

    /**
     * Bidirectional-Link List from JSON
     *
     * @param jsonString
     * @return List of Bidirectional links
     */
    fun getBidirectionalLinkfromJson(jsonString: String): BiDirectionalLinkList {
        val gson = Gson()
        Log.d("JSON:", "BiDirectionalLinkList from JSON")

        val biDirectionalLinkList = gson.fromJson(jsonString, BiDirectionalLinkList::class.java)
        for (link in biDirectionalLinkList.bidirectionalLinks)
            Log.d("Links:", "from: ${link.fromCountry},\t to:${link.toCountry},")
        return biDirectionalLinkList
    }

    /**
     * Converter of a list of continents and list of bidirectional links to produce a CountryGraph
     * for MinimalRisk
     *
     * @param continentList
     * @param bidirectionalLinks
     * @return CountryGraph
     */
    fun gsonTemplateConverter(
        continentList: ContinentList,
        bidirectionalLinks: ArrayList<GsonTemplateBidirectionalLink>
    ): GsonTemplateCountryGraph {
        val gsonContinentList = ArrayList<GsonTemplateContinent>()
        for (continent in continentList.continents) {
            val countryList = ArrayList<GsonTemplateCountry>()
            Log.d("gsonTemplateConverter:", "continent:${continent.name}")

            for (country in continent.countries) {
                countryList.add(
                    GsonTemplateCountry(
                        country.name,
                        country.player,
                        country.count,
                        false
                    )
                )
                Log.d(
                    "gsonTemplateConverter:",
                    "name: ${country.name},\t player:${country.player},\t count:${country.count},\t modified:${country.modified}"
                )
            }
            val gsonContinent = GsonTemplateContinent(continent.name, countryList)
            gsonContinentList.add(gsonContinent)
        }

        val gsonTemplateCountryGraph = GsonTemplateCountryGraph()
        gsonTemplateCountryGraph.continents = gsonContinentList
        gsonTemplateCountryGraph.bidirectionalLinks = bidirectionalLinks
        return gsonTemplateCountryGraph
    }

    /**
     * Used for communication with MinimalRisk. Produces a JSON string representing a CountryGraph
     *
     * @param continentList
     * @param bidirectionalLinks
     * @return JSON representing a CountryGraph
     */
    fun getCountryGraphJson(
        continentList: ContinentList,
        bidirectionalLinks: ArrayList<GsonTemplateBidirectionalLink>
    ): String {
        val jsonString =
            getJsonFromCountryGraph(gsonTemplateConverter(continentList, bidirectionalLinks))
        Log.d("getCountryGraphJson:", jsonString)
        return jsonString
    }

    /**
     *
     * Used for communication with MinimalRisk. Produces a JSON string representing a CountryGraph

     * @param countryGraphObject
     * @return JSON string representing a CountryGraph
     */

    fun getJsonFromCountryGraph(countryGraphObject: GsonTemplateCountryGraph): String {
        val gson = Gson()
        return gson.toJson(countryGraphObject)
    }
}