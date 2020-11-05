package com.exa.test.helper

import android.content.Context
import android.util.Log
import com.exa.test.ai_machine.BiDirectionalLinkList
import com.exa.test.model.ContinentList
import com.exa.test.model.Player
import kotlin.random.Random.Default.nextInt

/**
 * Initializer for a new game
 *
 * @property players List of player that participate in the game
 * @property gameState String of the location of the JSON file with the initial game state
 * @property newGame Boolean represents if is a new game or if it needs to be restored (restore not
 * impelented yet)
 * @property context Context for callbacks
 */
class GameInitializer(
    val players: MutableList<Player>,
    private val gameState: String,
    val newGame: Boolean,
    val context: Context
) {

    lateinit var listOfContinents: ContinentList
    lateinit var biDirectionalLinkList: BiDirectionalLinkList

    init {
        //Get initial set of countries/continentButtonList
        val jsonFile = JsonHandler.getJsonFromFile(gameState, context)
        if (jsonFile != null) {
            listOfContinents = JsonHandler.getContinentListFromJson(jsonFile)
            biDirectionalLinkList = JsonHandler.getBidirectionalLinkfromJson(jsonFile)
        } else {
            Log.e("Initalizer", "JSON file not found")
        }

        if (newGame) {
            distributeCountries()
            //randomize turn order with the players List --> For testing disabled!!
            //players.shuffle()
        }
    }

    /**
     * Assign a country to a player
     *
     * @param continent Continent where country is
     * @param country Country to be assigned
     * @param player Assigned Player
     */
    private fun assignCountry(continent: Int, country: Int, player: Int) {
        listOfContinents.continents[continent].countries[country].player = players[player].name
    }


    /**
     *  Distribute all countries evenly to all available to players
     */
    private fun distributeCountries() {
        var totalNrOfCountries = 0
        for (continent in listOfContinents.continents)
            totalNrOfCountries += continent.countries.size

        val minCountryPerPlayer = totalNrOfCountries / players.size

        //When countries cannot be distributed evenly the leftovers will be distributed randomly to players
        var moduloCountry = totalNrOfCountries % players.size


        var distributionList = mutableListOf<Int>()
        for (i in 0 until players.size)
            distributionList.add(i, 0)

        //Distribution loop
        for (continent in listOfContinents.continents.indices) {
            for (country in listOfContinents.continents[continent].countries.indices) {
                var assigned = false
                while (!assigned) {
                    //Pick random player from List
                    var player = nextInt(0, players.size)
                    //Check if player has not reached max amount of assigned countries
                    if (distributionList[player] < minCountryPerPlayer + moduloCountry) {
                        assignCountry(continent, country, player)
                        distributionList[player]++
                        //If a country from the modulo was assigned, reduce moduloCounty by one
                        if (distributionList[player] > minCountryPerPlayer) moduloCountry--
                        assigned = true
                    }
                }
            }
        }
    }
}
