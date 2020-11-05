package com.exa.test.helper

import android.view.View
import android.widget.Button
import com.exa.test.model.Country

/**
 * Interface for callbacks to GameActivity
 *
 */
interface GameActivityInterface {

    fun getAttackFragment()
    fun getRelocationFragment()
    fun getReinforcementFragment()
    fun getBotFragment()
    fun toastIt(bread: String)
    fun setReinforcement(countrySelected: String, troops: Int)
    fun attack(source: String, target: String, troopsAttacking: Int, troopsLeft:Int, fastAttack: Boolean)
    fun relocate(source: String, target: String, troops: Int)
    fun updateButtons()
    fun changePhase(phase: String)
    fun getButtonById(buttonId: String) : Button?
    fun getCountryById(countryId: String) : Country?
    fun showAttackPopup() : View
    fun closeAttackPopup()
    fun setTroopsForReinforcement()
    fun nextPlayer()
    fun isPlayerWinner(winnerText: String)

}