package com.exa.test.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.exa.test.R
import com.exa.test.helper.GameActivityInterface

const val NO_SELECTION: String = "<please select>"

/**
 * Reinforcement fragment is loaded in reinforcement phase. It handles all the user input during that phase
 * and provides information about the game state and possible moves.
 * It is loaded into the fragment container provided ba the GameActivity
 */
class ReinforcementFragment : Fragment() {

    private lateinit var listener: GameActivityInterface

    //TODO: SET TO 0, TESTING IS 5
    var troopsAvailable = 0
    var troopsLeft = 0
    var troopsSelected = 0
    var countrySelected = NO_SELECTION
    private lateinit var troopsAvailableText: TextView
    private lateinit var targetCountryText: TextView
    private lateinit var troopsSelectedText: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GameActivityInterface) listener = context
    }


    fun newInstance(): ReinforcementFragment {
        return ReinforcementFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentView = inflater.inflate(R.layout.reinforcement_fragment, container, false)

        troopsAvailableText = fragmentView.findViewById(R.id.troopsAvailableReIn)
        targetCountryText = fragmentView.findViewById(R.id.targetCountryReIn)
        troopsSelectedText = fragmentView.findViewById(R.id.nrOfTroopsSelectedReIn)

        fragmentView.findViewById<Button>(R.id.dispatchButton).setOnClickListener {
            dispatch()
        }

        fragmentView.findViewById<Button>(R.id.plusReInButton).setOnClickListener {
            addTroops()
        }

        fragmentView.findViewById<Button>(R.id.minusReInButton).setOnClickListener {
            removeTroops()
        }

        fragmentView.findViewById<Button>(R.id.abortButton).setOnClickListener {
            cancel()
        }

        listener.setTroopsForReinforcement()
        updateTextViews()
        return fragmentView
    }

    private fun updateTextViews() {
        troopsAvailableText.text = troopsLeft.toString()
        targetCountryText.text = countrySelected
        troopsSelectedText.text = troopsSelected.toString()
    }

    fun updateCountrySelected(country: String) {
        countrySelected = country
        troopsSelected = troopsAvailable
        troopsLeft = 0
        updateTextViews()
    }
    
    fun updateTroopsAvailable(troops: Int) {
        troopsAvailable = troops
        troopsLeft = troopsAvailable
        updateTextViews()
    }

    private fun addTroops() {
        if (troopsLeft > 0) {
            troopsSelected++
            troopsLeft--
            updateTextViews()
        }
    }

    private fun removeTroops() {
        if (troopsSelected > 0) {
            troopsSelected--
            troopsLeft++
            updateTextViews()
        }
    }

    private fun cancel() {
        troopsLeft = troopsSelected
        troopsSelected = 0
        countrySelected = NO_SELECTION
        updateTextViews()
        listener.updateButtons()
    }

    /**
     *
     *
     **/

    private fun dispatch() {
        if (countrySelected == NO_SELECTION) {
            listener.toastIt("no country selected")
        } else if (troopsSelected == 0) {
            listener.toastIt("no troops selected")
        } else {
            listener.setReinforcement(countrySelected, troopsSelected)
            troopsAvailable -= troopsSelected
            troopsSelected = 0
            updateTextViews()

            if (troopsAvailable == 0) {

                listener.changePhase("attack")
                listener.getAttackFragment()
            }
        }
    }
}