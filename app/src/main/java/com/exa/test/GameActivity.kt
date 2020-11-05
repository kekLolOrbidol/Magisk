package com.exa.test


import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.exa.test.fragments.*
import com.exa.test.helper.BattleGround
import com.exa.test.helper.GameActivityInterface
import com.exa.test.helper.GameInitializer
import com.exa.test.helper.JsonHandler
import com.exa.test.ai_machine.BiDirectionalLinkList
import com.exa.test.ai_machine.MinimalRisk
import com.exa.test.model.ContinentList
import com.exa.test.model.Country
import com.exa.test.model.Player
import kotlinx.coroutines.*

/**
 * Controller of the Game. This is handling most of the user input and handles API calls
 * to com.example.test.gameplay.ai_machine and updates the view.
 *
 */
class GameActivity : AppCompatActivity(), GameActivityInterface {

    private val fragmentManager: FragmentManager = supportFragmentManager
    private lateinit var playerList: MutableList<Player>

    private var phase = "initialize game"
    //pointer to player in players<> that is now on the relocate
    private var moveOfPlayer = 0
    private var continentList = ContinentList()
    private var biDirectionalLinkList = BiDirectionalLinkList()

    private lateinit var reinforcementFragment: ReinforcementFragment
    private lateinit var attackFragment: AttackFragment
    private lateinit var relocationFragment: RelocationFragment
    private lateinit var botFragment: BotFragment

    private lateinit var battleGround: BattleGround

    private lateinit var attackPopupWindow: PopupWindow
    var ongoingBattle = false
    private var buttonsLocked = false

    private var attackFragID = 0
    private var relocationFragID = 0
    private var botFragmentID = 0

    private val initialGameState: String = "start_state.json"
    private var continentBonus = 3
    private var troopsPerCountryDivisor = 2

    lateinit var a11: Button
    lateinit var a12: Button
    lateinit var a13: Button
    lateinit var a21: Button
    lateinit var a22: Button
    lateinit var a23: Button
    lateinit var a31: Button
    lateinit var a32: Button
    lateinit var a33: Button

    lateinit var b11: Button
    lateinit var b12: Button
    lateinit var b13: Button
    lateinit var b21: Button
    lateinit var b22: Button
    lateinit var b23: Button
    lateinit var b31: Button
    lateinit var b32: Button
    lateinit var b33: Button

    lateinit var c11: Button
    lateinit var c12: Button
    lateinit var c13: Button
    lateinit var c21: Button
    lateinit var c22: Button
    lateinit var c23: Button
    lateinit var c31: Button
    lateinit var c32: Button
    lateinit var c33: Button

    lateinit var d11: Button
    lateinit var d12: Button
    lateinit var d13: Button
    lateinit var d21: Button
    lateinit var d22: Button
    lateinit var d31: Button
    lateinit var d23: Button
    lateinit var d32: Button
    lateinit var d33: Button

    lateinit var continentA: List<Button>
    lateinit var continentB: List<Button>
    lateinit var continentC: List<Button>
    lateinit var continentD: List<Button>

    lateinit var continentButtonList: List<List<Button>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        playerList = getPlayers()

        a11 = findViewById(R.id.a11)
        a12 = findViewById(R.id.a12)
        a13 = findViewById(R.id.a13)
        a21 = findViewById(R.id.a21)
        a22 = findViewById(R.id.a22)
        a23 = findViewById(R.id.a23)
        a31 = findViewById(R.id.a31)
        a32 = findViewById(R.id.a32)
        a33 = findViewById(R.id.a33)

        b11 = findViewById(R.id.b11)
        b12 = findViewById(R.id.b12)
        b13 = findViewById(R.id.b13)
        b21 = findViewById(R.id.b21)
        b22 = findViewById(R.id.b22)
        b23 = findViewById(R.id.b23)
        b31 = findViewById(R.id.b31)
        b32 = findViewById(R.id.b32)
        b33 = findViewById(R.id.b33)

        c11 = findViewById(R.id.c11)
        c12 = findViewById(R.id.c12)
        c13 = findViewById(R.id.c13)
        c21 = findViewById(R.id.c21)
        c22 = findViewById(R.id.c22)
        c23 = findViewById(R.id.c23)
        c31 = findViewById(R.id.c31)
        c32 = findViewById(R.id.c32)
        c33 = findViewById(R.id.c33)

        d11 = findViewById(R.id.d11)
        d12 = findViewById(R.id.d12)
        d13 = findViewById(R.id.d13)
        d21 = findViewById(R.id.d21)
        d22 = findViewById(R.id.d22)
        d23 = findViewById(R.id.d23)
        d31 = findViewById(R.id.d31)
        d32 = findViewById(R.id.d32)
        d33 = findViewById(R.id.d33)

        continentA = listOf(a11, a12, a13, a21, a22, a23, a31, a32, a33)
        continentB = listOf(b11, b12, b13, b21, b22, b23, b31, b32, b33)
        continentC = listOf(c11, c12, c13, c21, c22, c23, c31, c32, c33)
        continentD = listOf(d11, d12, d13, d21, d22, d23, d31, d32, d33)

        continentButtonList = listOf(continentA, continentB, continentC, continentD)

        for (i in continentButtonList.indices) {
            for (j in continentButtonList[i].indices) {
                continentButtonList[i][j].setOnClickListener {
                    if(!buttonsLocked) {
                        buttonClicked(continentButtonList[i][j])
                        Log.d("UI", "touched: ${getButtonId(continentButtonList[i][j])}")
                    }
                }
            }
        }

        val gameInitializer = GameInitializer(playerList, initialGameState, true, this)
        continentList = gameInitializer.listOfContinents
        playerList = gameInitializer.players
        biDirectionalLinkList = gameInitializer.biDirectionalLinkList

        phase = "reinforcement"
        getReinforcementFragment()
        updateButtons()
    }

    /**
     * Get players, can be extended if more players allowed
     *
     * @return list of players
     */
    private fun getPlayers(): MutableList<Player> {
        val playerA = Player("A", "blue", false, null)
        val playerB = Player("B", "red", true, 1)
        return mutableListOf(playerA, playerB)
    }

    /**
     * Initiate action if a button on the map is clicked depending on game phase
     *
     * @param button Button clicked
     */
    private fun buttonClicked(button: Button) {
        val buttonID = getButtonId(button)
        //Check if selected country is under control of player
        var ownButton = false
        for (continent in continentList.continents)
            for (country in continent.countries)
                if (country.name == getButtonId(button) && country.player == playerList[moveOfPlayer].name)
                    ownButton = true

        //only do something if it is the turn of the human player
        if (!playerList[moveOfPlayer].bot && !buttonsLocked) {

            when (phase) {
                "reinforcement" -> buttonClickedReinforcementPhase(button, ownButton, buttonID)
                "attack" -> buttonClickedAttackPhase(button, ownButton, buttonID)
                "relocation" -> buttonClickedRelocationPhase(button, ownButton, buttonID)
            }
        } else {
            toastIt("wait for your turn")
        }
    }

    /**
     * Action if game phase is 'reinforcement'
     *
     * @param button Button clicked
     * @param ownButton True if the country associated with button under own control
     * @param buttonID ID of Button (shortened to 3 characters to match witch country ID)
     */
    private fun buttonClickedReinforcementPhase(
        button: Button,
        ownButton: Boolean,
        buttonID: String
    ) {
        if (ownButton) {
            updateButtons()
            changeButtonToWhite(button)
            reinforcementFragment.updateCountrySelected(buttonID)

        } else {
            toastIt("not your country!")
        }
    }

    /**
     * Action if game phase is 'attack'
     *
     * @param button Button clicked
     * @param ownButton True if the country associated with button under own control
     * @param buttonID ID of Button (shortened to 3 characters to match witch country ID)
     */
    private fun buttonClickedAttackPhase(button: Button, ownButton: Boolean, buttonID: String) {
        when (ownButton) {
            true -> {
                attackFragment.updateSourceCountry(buttonID)
                attackFragment.updateTargetCountry(NO_SELECTION)
                updateButtons()
                changeButtonToWhite(button)
            }
            false -> {
                if (attackFragment.sourceCountry == NO_SELECTION) {
                    toastIt("choose first attacking country")
                } else {
                    var valid = false
                    runBlocking {
                        valid = attackCheck(attackFragment.sourceCountry, buttonID)
                    }
                    if (valid) {
                        updateButtons()
                        val attackButton = getButtonById(attackFragment.sourceCountry)
                        attackButton?.let { changeButtonToWhite(attackButton) }
                        changeButtonToBlack(button)
                        attackFragment.updateTargetCountry(buttonID)
                    } else {
                        toastIt("selection not valid, country must be a neighbour")
                    }
                }
            }
        }
    }

    /**
     * Action if game phase is 'relocation'
     *
     * @param button Button clicked
     * @param ownButton True if the country associated with button under own control
     * @param buttonID ID of Button (shortened to 3 characters to match witch country ID)
     */
    private fun buttonClickedRelocationPhase(button: Button, ownButton: Boolean, buttonID: String) {
        when (ownButton) {
            true -> {
                val sourceCountryId = relocationFragment.sourceCountry
                updateButtons()
                when (relocationFragment.updateCountry(buttonID)) {
                    "source" -> {
                        changeButtonToWhite(button)
                        relocationFragment.updateSourceCountry(buttonID)

                    }
                    "target" -> {
                        var valid = false
                        runBlocking {
                            valid = relocationCheck(sourceCountryId, buttonID)
                        }
                        if (valid) {
                            relocationFragment.updateTargetCountry(buttonID)
                            changeButtonToWhite(getButtonById(sourceCountryId))
                            changeButtonToBlack(button)
                        } else toastIt(
                            "relocate from $sourceCountryId to $buttonID is not "
                                    + "allowed. Countries must be connected!"
                        )
                    }
                }
            }
            false -> toastIt("you can only relocate troops to your own countries")
        }
    }

    /**
     * Set selected troops in reinforcement phase on selected country
     *
     * @param countrySelected selected country
     * @param troops selected troops
     */
    override fun setReinforcement(countrySelected: String, troops: Int) {

        //get button
        for (i in continentButtonList.indices)
            for (j in continentButtonList[i].indices)
                if (getButtonId(continentButtonList[i][j]) == countrySelected)

                //update button in seperate coroutine
                    GlobalScope.launch {
                        for (k in 0 until troops) {
                            continentList.continents[i].countries[j].count++
                            this@GameActivity.runOnUiThread { updateButtons() }
                            delay(70)
                        }
                    }
    }

    /**
     * Check if a selection of source and target country in relocation phase is valid. A relocate is
     * valid if the selected countries have a connection. A connection exists if the all the
     * countries which connect source and target country are under own control or if the selected
     * countries are neighbours.
     * It calls a static method of the MinimalRisk class to get a list of possible target countries
     * for a provided source country. This list is then checked if it contains the name of the
     * target country
     *
     * @param source source country
     * @param target target country
     * @return Boolean if relocation selection for relocation relocate is valid.
     */
    private suspend fun relocationCheck(source: String, target: String): Boolean {

        Log.d("relocationCheck:", "source:${source}\t target:${target}")

        //Coroutine Optimized for CPU intensive work off the main thread
        return withContext(Dispatchers.Default) {
            var relocationValidity = false

            //get list of possible targets and check if target country is in that list
            val countryGraph =
                JsonHandler.getCountryGraphJson(
                    continentList,
                    biDirectionalLinkList.bidirectionalLinks
                )
            val possibleTargetsJson =
                MinimalRisk.possibleDestinations(
                    countryGraph,
                    playerList[moveOfPlayer].name,
                    source
                )

            Log.d("relocationCheck:", "MinmalRisk:$possibleTargetsJson")

            val countryList = JsonHandler.getCountryListFromJson(possibleTargetsJson)

            for (country in countryList.countries) {

                Log.d("attackCheck:", "country in list checked: $country")

                if (country.name == target) {
                    relocationValidity = true
                    Log.d("attackCheck:", "country found:$country")
                }
            }
            return@withContext relocationValidity
        }
    }

    /**
     * Check if a selection of attacking / source and defending / target country in relocation
     * phase is valid. A relocate is valid if the selected countries are neighbours.
     * It calls a static method of the MinimalRisk class to get a list of possible
     * defending / target countries for a provided attacking / source country. This list is then
     * checked if it contains the name of the defending / target country
     *
     * @param source attacking / source country
     * @param target defending / target country
     * @return Boolean selection for attack relocate is valid.
     */
    private suspend fun attackCheck(source: String, target: String): Boolean {

        Log.d("attackCheck:", "source:${source}\t target:${target}")

        //Coroutine Optimized for CPU intensive work off the main thread


        return withContext(Dispatchers.Default) {
            var attackValidity = false
            //get list of possible targets and check if target country is in that list
            val countryGraph = JsonHandler.getCountryGraphJson(
                continentList,
                biDirectionalLinkList.bidirectionalLinks
            )
            val possibleTargetsJson = MinimalRisk.possibleTargetCountries(
                countryGraph,
                playerList[moveOfPlayer].name,
                source
            )

            Log.d("attackCheck:", "MinmalRisk:${possibleTargetsJson}")

            val countryList = JsonHandler.getCountryListFromJson(possibleTargetsJson)

            for (country in countryList.countries) {

                Log.d("attackCheck:", "country in list checked: $country")

                if (country.name == target) {
                    attackValidity = true
                    Log.d("attackCheck:", "country found:$country")
                }
            }

            return@withContext attackValidity
        }
    }

    /**
     * Attack relocate creates an instance of class BattleGround to evaluate outcome
     *
     * @param source attacking country
     * @param target defending country
     * @param troopsAttacking amount of troops which attack
     * @param troopsLeft amount of troops which are left behind in attacking country
     */
    override fun attack(source: String, target: String, troopsAttacking: Int, troopsLeft: Int, fastAttack: Boolean) {
        val counterparties = listOf(getCountryById(source)!!, getCountryById(target)!!)
        battleGround = BattleGround(counterparties, troopsAttacking, troopsLeft, fastAttack, this)
    }

    /**
     * Move troops from source to target country
     *
     * @param source source country
     * @param target target country
     * @param troops amount of troops moved
     */
    override fun relocate(source: String, target: String, troops: Int) {
        val sourceCountry = getCountryById(source)
        val targetCountry = getCountryById(target)

        sourceCountry!!.count -= troops
        targetCountry!!.count += troops
        updateCountries(listOf(sourceCountry, targetCountry))
        updateButtons()
        nextPlayer()
    }

    /**
     * Controls the turn of the bot. It has a 'reinforcement', 'attack' and 'relocation' phase.
     * The bot action is shown in the botlog in the botfragment
     */
    private fun botPhase() {
        val player = playerList[moveOfPlayer].name
        var newBoard: String
        var newContinentList: ContinentList
        botFragment.botActonDone = false

        //reinforcement phase of bot
        GlobalScope.launch {

            this@GameActivity.runOnUiThread {
                botFragment.view!!.findViewById<ImageView>(R.id.phaseImageView)
                    .setImageResource(R.drawable.phase_enemy_1)
                botFragment.view!!.findViewById<TextView>(R.id.playerNameText).text =
                    playerList[moveOfPlayer].name
            }

            val reinforcementTroops = getTroopsForReinforcement(player)
            newBoard =
                MinimalRisk.allocationOfExtraTroops(getJsonForBot(), player, reinforcementTroops)
            //Log.d("botPhase, reinforcement", "${newBoard}")
            Log.d("botPhase, reinforcement", "Troops: $reinforcementTroops")
            newContinentList = JsonHandler.getContinentListFromJson(newBoard)

            //Show Bot-Log
            var botReinforcementTroopsTotal = 0
            for ((i, continent) in continentList.continents.withIndex())
                for ((j, country) in continent.countries.withIndex())
                    if (country.count != newContinentList.continents[i].countries[j].count) {
                        val newTroops =
                            newContinentList.continents[i].countries[j].count.minus(country.count)
                        botReinforcementTroopsTotal += newTroops
                        delay(1000)
                        //Only the original thread that created a view hierarchy can touch its views.
                        this@GameActivity.runOnUiThread {
                            botFragment.addBotAction("$player placed $newTroops on ${country.name}")
                        }
                        changeButtonToWhite(getButtonById(country.name))
                        delay(500)
                        continentList.continents[i].countries[j] =
                            newContinentList.continents[i].countries[j]
                        updateButtons()

                    }
            Log.d("botPhase:", "Total troops: $botReinforcementTroopsTotal")

            //Attack phase of bot
            this@GameActivity.runOnUiThread {
                botFragment.view!!.findViewById<ImageView>(R.id.phaseImageView)
                    .setImageResource(R.drawable.phase_enemy_2)
            }
            newBoard = MinimalRisk.attack(getJsonForBot(), player)
            Log.d("botPhase, attack", newBoard)

            newContinentList = JsonHandler.getContinentListFromJson(newBoard)

            for ((i, continent) in newContinentList.continents.withIndex())
                for ((j, country) in continent.countries.withIndex())
                    when {
                        (country.modified && continentList.continents[i].countries[j].player == playerList[moveOfPlayer].name) -> {
                            this@GameActivity.runOnUiThread {
                                botFragment.addBotAction("$player lost troops in ${country.name}")
                            }
                            changeButtonToBlack(getButtonById(country.name))
                            delay(1000)
                            continentList.continents[i].countries[j] = country
                            updateButtons()
                        }
                        (country.modified && country.player == playerList[moveOfPlayer].name) -> {
                            this@GameActivity.runOnUiThread {
                                botFragment.addBotAction("$player attacked ${country.name}, you lost the battle")
                            }
                            changeButtonToBlack(getButtonById(country.name))
                            delay(400)
                            changeButtonToBlue(getButtonById(country.name))
                            delay(400)
                            changeButtonToBlack(getButtonById(country.name))
                            delay(400)
                            continentList.continents[i].countries[j] = country
                            updateButtons()
                        }

                        (country.modified && country.player != playerList[moveOfPlayer].name) -> {
                            this@GameActivity.runOnUiThread {
                                botFragment.addBotAction("$player attacked ${country.name}, you won the battle!")
                            }
                            changeButtonToBlack(getButtonById(country.name))
                            delay(400)
                            changeButtonToBlue(getButtonById(country.name))
                            delay(400)
                            changeButtonToBlack(getButtonById(country.name))
                            delay(400)
                            continentList.continents[i].countries[j] = country
                            updateButtons()
                        }
                        (country.modified && continentList.continents[i].countries[j].player == playerList[moveOfPlayer].name) -> {
                            this@GameActivity.runOnUiThread {
                                botFragment.addBotAction("$player lost troops in ${country.name}")
                            }
                            changeButtonToBlack(getButtonById(country.name))
                            delay(1000)
                            continentList.continents[i].countries[j] = country
                            updateButtons()
                        }

                    }

            isPlayerWinner("You lost!")

            //Relocation phase of bot
            this@GameActivity.runOnUiThread {
                botFragment.view!!.findViewById<ImageView>(R.id.phaseImageView)
                    .setImageResource(R.drawable.phase_enemy_3)
            }

            newBoard = MinimalRisk.moveTroops(getJsonForBot(), player)
            Log.d("botPhase, relocation", newBoard)
            newContinentList = JsonHandler.getContinentListFromJson(newBoard)
            var fromCountry = ""
            var toCountry = ""
            var troopsMoved = 0

            for ((i, continent) in continentList.continents.withIndex())
                for ((j, country) in continent.countries.withIndex())
                    if (newContinentList.continents[i].countries[j].modified) {
                        val troops =
                            newContinentList.continents[i].countries[j].count - country.count
                        if (troops < 0) {
                            fromCountry = country.name
                        } else {
                            toCountry = country.name
                            troopsMoved = troops
                        }
                    }

            if (troopsMoved > 0) {
                this@GameActivity.runOnUiThread {
                    botFragment.addBotAction(" $troopsMoved troops  moved from $fromCountry to $toCountry")
                }
                continentList = newContinentList
                delay(500)
                changeButtonToWhite(getButtonById(fromCountry))
                delay(500)
                changeButtonToBlack(getButtonById(toCountry))
                delay(500)
                updateButtons()
            }
            botFragment.botActonDone = true
        }

    }

    /**
     * Moves the turn to the next player in playerList (index of player in playerList is
     * moveOfPlayer)
     *
     */
    override fun nextPlayer() {

        if (moveOfPlayer == playerList.size - 1) moveOfPlayer = 0 else moveOfPlayer++

        if (playerList[moveOfPlayer].bot) {
            changePhase("bot")
            getBotFragment()

            GlobalScope.launch {
                while (botFragment.view == null) {
                    Log.d("nextPlayer()", "waiting for botFragmentView")
                    delay(10)
                }
                this@GameActivity.runOnUiThread { botPhase() }
                Log.d("nextPlayer", "botFragmentView was added")
            }
        } else {
            changePhase("reinforcement")
            getReinforcementFragment()
        }
    }

    override fun isPlayerWinner(winnerText: String) {
        var playerCount = 0
        var countryCount = 0
        for (continent in continentList.continents)
            for (country in continent.countries) {
                countryCount++
                if (playerList[moveOfPlayer].name == country.player)
                    playerCount++
            }
        Log.d("Winner:","countryCount:$countryCount, playerCount:$playerCount")

        if (playerCount == countryCount) {
            showEndOfGamePopUp(findViewById(android.R.id.content), winnerText)
            Log.d("Winner", "${playerList[moveOfPlayer].name}")
        }
    }

    private fun showEndOfGamePopUp(view: View, winnerText: String) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val endPopUpView = inflater.inflate(R.layout.end_of_game_layout, null)

        val width = 800
        val height = 200
        val focusable = false
        val popupWindow = PopupWindow(endPopUpView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, focusable)

        endPopUpView.findViewById<TextView>(R.id.winner).text = winnerText
        endPopUpView.findViewById<Button>(R.id.okButtonEnd).setOnClickListener {
            finish()
        }
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
    }


    /**
     * Helper method for method botPhase(). Calls static method of class JsonHandler
     *
     * @return JSON with Country Graph (see class MinimalRisk)
     */
    fun getJsonForBot(): String {
        val countryGraphObject = JsonHandler.gsonTemplateConverter(
            continentList,
            biDirectionalLinkList.bidirectionalLinks
        )
        return JsonHandler.getJsonFromCountryGraph(countryGraphObject)
    }

    /**
     * Calculates the troops for the player in the reinforcement phase based on variables
     * continentBonus and troopsPerCountryDivisor
     *
     * @param player player which is now in the reinforcement phase
     * @return amount of troops for reinforcement
     */
    private fun getTroopsForReinforcement(player: String): Int {
        var troops = 0
        for (continent in continentList.continents) {
            var countCountriesInContinent = 0
            var ownCountries = 0

            for (country in continent.countries) {
                countCountriesInContinent++
                if (country.player == player) ownCountries++
            }
            //Continent-Bonus: check if all countries on continent are under of control of player
            if (ownCountries == countCountriesInContinent) ownCountries += continentBonus
            troops += ownCountries / troopsPerCountryDivisor
        }
        return troops
    }

    /**
     * Set troops available for reinforcement in the reinforcement fragment
     *
     */
    override fun setTroopsForReinforcement() {
        val troops = getTroopsForReinforcement(playerList[moveOfPlayer].name)
        reinforcementFragment.updateTroopsAvailable(troops)
    }

    /**
     * Show message to user in Toaster
     *
     * @param bread Message
     */
    override fun toastIt(bread: String) {
        Toast.makeText(this@GameActivity, bread, Toast.LENGTH_LONG).show()
    }

    /**
     * Load reinforcement fragment in fragment container
     *
     */
    override fun getReinforcementFragment() {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        reinforcementFragment = ReinforcementFragment().newInstance()

        if (fragmentManager.fragments.size == 0) {
            transaction.add(R.id.fragment_container, reinforcementFragment)
        } else {
            transaction.replace(R.id.fragment_container, reinforcementFragment)
        }
        transaction.commit()
    }

    /**
     * Load attack fragment in fragment container
     *
     */
    override fun getAttackFragment() {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        attackFragment = AttackFragment().newInstance()
        transaction.replace(R.id.fragment_container, attackFragment)
        transaction.commit()
        attackFragID = attackFragment.id
    }

    /**
     * Load relocation fragment in fragment container
     *
     */
    override fun getRelocationFragment() {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        relocationFragment = RelocationFragment().newInstance()
        transaction.replace(R.id.fragment_container, relocationFragment)
        transaction.commit()
        relocationFragID = relocationFragment.id
    }

    /**
     * Load bot fragment in fragment container
     *
     */
    override fun getBotFragment() {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        botFragment = BotFragment().newInstance()
        transaction.replace(R.id.fragment_container, botFragment)
        transaction.commit()
        botFragmentID = botFragment.id
    }

    /**
     * Change game-phase
     *
     * @param phase game-phase
     */
    override fun changePhase(phase: String) {
        this.phase = phase
    }

    /**
     * Show attack pop-up, show result of battle and block buttons during battle-phases
     *
     * @return a view representing the pop-up
     */
    override fun showAttackPopup(): View {

        ongoingBattle = false

        val view: View = findViewById(android.R.id.content)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpView = inflater.inflate(R.layout.attack_popup, null)

        popUpView.findViewById<Button>(R.id.attackButtonPop).setOnClickListener {
            if (!ongoingBattle) {
                //Lock buttons when a battle is ongoing
                ongoingBattle = true

                val counterparties = battleGround.fight()
                //if a player is defeated counterparties are returned, otherwise null
                if (counterparties != null) {
                    updateCountries(counterparties)
                    isPlayerWinner("You won!!!!")
                    GlobalScope.launch {
                        delay(2000)
                        this@GameActivity.runOnUiThread {
                            closeAttackPopup()
                            updateButtons()
                            //unlock buttons
                            ongoingBattle = false
                        }
                    }
                } else {
                    //unlock buttons
                    ongoingBattle = false
                }
            }
        }

        popUpView.findViewById<Button>(R.id.fastAttackButtonPop).setOnClickListener {
            if (!ongoingBattle) {
                val counterparties = battleGround.fastFight()
                if (counterparties != null) {
                    updateCountries(counterparties)
                    isPlayerWinner("You won!!!!")

                    GlobalScope.launch {
                        delay(1000)
                        this@GameActivity.runOnUiThread {
                            closeAttackPopup()
                            updateButtons()
                            ongoingBattle = false
                        }

                    }
                } else {
                    ongoingBattle = false
                }
            }
        }

        popUpView.findViewById<Button>(R.id.withdrawalButtonPop).setOnClickListener {
            if (!ongoingBattle) {
                battleGround.withdrawal()
                updateButtons()
            }
        }

        //return the view
        val width = 1400
        val height = 1000
        val focusable = false
        attackPopupWindow = PopupWindow(popUpView, width, height, focusable)

//        attackPopupWindow = PopupWindow(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, focusable)
        attackPopupWindow.showAtLocation(view, Gravity.CENTER, 0, -20)
        return popUpView
    }

    /**
     * Close attack pop-up after battle
     *
     */
    override fun closeAttackPopup() {
        attackPopupWindow.dismiss()
    }

    /**
     * update a pair of countries after attack or relocate
     *
     * @param pairOfCountries List of Countries, max. size 2
     */
    private fun updateCountries(pairOfCountries: List<Country?>) {
        for (i in 0..1) {
            val country = getCountryById(pairOfCountries[i]!!.name)
            country?.player = pairOfCountries[i]!!.player
            country?.count = pairOfCountries[i]!!.count
            country?.modified = true
        }
    }

    /**
     * Get ID of button representing a country
     * Method is somehow ugly but no better solution was found how to get the ID ;)
     *
     * @param button
     * @return ID of button representing the country name
     */
    private fun getButtonId(button: Button): String {
        //This is somehow ugly but no better solution was found how to get the ID
        var buttonID = button.toString()
        buttonID = buttonID.substring(buttonID.length - 4, buttonID.length - 1).toUpperCase()
        return buttonID
    }

    /**
     * Get a button based on button ID (name of country and the representing button ID are the same)
     *
     * @param buttonId Name of button
     * @return get a button if found, otherwise return null
     */
    override fun getButtonById(buttonId: String): Button? {
        for (i in continentButtonList.indices)
            for (j in continentButtonList[i].indices)
                if (getButtonId(continentButtonList[i][j]) == buttonId) return continentButtonList[i][j]
        return null
    }

    /**
     * Get a country based on country name (name of country and the representing button ID are the same)
     *
     * @param countryId Name of the county
     * @return get a country if found, otherwise return null
     */
    override fun getCountryById(countryId: String): Country? {
        for (continent in continentList.continents)
            for (country in continent.countries)
                if (country.name == countryId) return country
        return null
    }


    /**
     * Update buttons on map relating on the state of the countries they represent stored in
     * continentList. Updates owner and amount of troops
     *
     * TODO: Improve so only modified countries get updated
     *
     */
    override fun updateButtons() {
        buttonsLocked = true

            for ((i, continent) in continentList.continents.withIndex())
                for ((j, country) in continent.countries.withIndex()) {
                    when (country.player) {
                        playerList[0].name -> changeButtonToBlue(continentButtonList[i][j])
                        playerList[1].name -> changeButtonToRed(continentButtonList[i][j])

                    }
                    val button = continentButtonList[i][j]
                    var countChange =
                        country.count.minus(button.text.toString().toInt())

                    when {

                        countChange > 0 -> GlobalScope.launch {
                            for (k in 1..countChange) {
                                this@GameActivity.runOnUiThread { increaseTroops(button) }
                                delay(100)
                            }
                        }

                        countChange < 0 -> {
                            countChange *= -1
                            GlobalScope.launch {
                                for (l in 1..countChange) {
                                    this@GameActivity.runOnUiThread { decreaseTroops(button) }
                                    delay(100)
                                }
                            }
                        }
                    }
                }
            buttonsLocked = false
    }

    /**
     * Increase the amoutn of troops showed on button by one
     *
     * @param button Button
     */
    private fun increaseTroops(button: Button?) {
        if (button != null) {
            var troops: Int = button.text.toString().toInt()
            troops++
            button.text = troops.toString()
        }
    }

    /**
     * Increase the amoutn of troops showed on button by one
     *
     * @param button Button
     */
    private fun decreaseTroops(button: Button?) {
        if (button != null) {
            var troops: Int = button.text.toString().toInt()
            troops--
            button.text = troops.toString()
        }
    }

    /**
     * Change the color of button to blue
     *
     * @param button Button
     */
    private fun changeButtonToBlue(button: Button?) {
        if (button != null) {
            button.setBackgroundResource(R.drawable.country_blue)
            button.setTextColor(Color.BLUE)
        }
    }

    /**
     * Change the color of button to red
     *
     * @param button Button
     */
    private fun changeButtonToRed(button: Button?) {
        if (button != null) {
            button.setBackgroundResource(R.drawable.country_red)
            button.setTextColor(Color.RED)
        }
    }

    /**
     * Change the color of button to white
     *
     * @param button Button
     */
    private fun changeButtonToWhite(button: Button?) {
        if (button != null) {
            button.setBackgroundResource(R.drawable.country_white)
            button.setTextColor(Color.WHITE)
        }
    }

    /**
     * Change the color of button to black
     *
     * @param button Button
     */
    private fun changeButtonToBlack(button: Button?) {
        if (button != null) {
            button.setBackgroundResource(R.drawable.country_black)
            button.setTextColor(Color.BLACK)
        }
    }
}