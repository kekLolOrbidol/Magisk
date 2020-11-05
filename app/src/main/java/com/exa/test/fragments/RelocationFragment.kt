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

/**
 * Relocation fragment is loaded in reinforcement phase. It handles all the user input during that phase
 * and provides information about the game state and possible moves.
 * It is loaded into the fragment container provided ba the GameActivity
 */
class RelocationFragment : Fragment() {

    private lateinit var listener: GameActivityInterface

    var troopsSelected = 0
    var troopsLeft = 0
    var sourceCountry = NO_SELECTION
    var targetCountry = NO_SELECTION

    private lateinit var sourceCountryText: TextView
    private lateinit var targetCountryText: TextView
    private lateinit var troopsSelectedText: TextView
    private lateinit var troopsLeftText: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GameActivityInterface) listener = context
    }

    fun newInstance(): RelocationFragment {
        return RelocationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentView = inflater.inflate(R.layout.relocation_fragment, container, false)

        sourceCountryText = fragmentView.findViewById(R.id.sourceMoveCountry)
        targetCountryText = fragmentView.findViewById(R.id.targetMoveCountry)
        troopsSelectedText = fragmentView.findViewById(R.id.nrOfTroopsSelectedMove)
        troopsLeftText = fragmentView.findViewById(R.id.troopsAvailableMove)

        fragmentView.findViewById<Button>(R.id.moveButton).setOnClickListener {
            move()
        }
        fragmentView.findViewById<Button>(R.id.plusMoveButton).setOnClickListener {
            addTroops()
        }
        fragmentView.findViewById<Button>(R.id.minusMoveButton).setOnClickListener {
            minTroops()
        }
        fragmentView.findViewById<Button>(R.id.cancelMoveButton).setOnClickListener {
            cancel()
        }
        fragmentView.findViewById<Button>(R.id.skipMoveButton).setOnClickListener {
            listener.updateButtons()
            listener.nextPlayer()
            //TODO: PopUp "do you really want to skip / goto next phase
        }
        updateTextViews()
        return fragmentView
    }

    fun move() {
        when {
            sourceCountry == NO_SELECTION -> listener.toastIt("select source country")
            targetCountry == NO_SELECTION -> listener.toastIt("select target country")
            troopsSelected < 1 -> listener.toastIt("select troops")
            else -> listener.relocate(sourceCountry, targetCountry, troopsSelected)
        }
    }

    private fun addTroops() {
        when {
            troopsLeft < 2 -> listener.toastIt("no troops left for moving troops")
            else -> {
                troopsLeft--
                troopsSelected++
                updateTextViews()
            }
        }
    }

    private fun minTroops() {
        when {
            troopsSelected < 1 -> listener.toastIt("no troops selected")
            else -> {
                troopsLeft++
                troopsSelected--
                updateTextViews()
            }
        }
    }

    fun updateTextViews() {
        troopsSelectedText.text = troopsSelected.toString()
        troopsLeftText.text = troopsLeft.toString()
        sourceCountryText.text = sourceCountry
        targetCountryText.text = targetCountry
    }

    private fun cancel() {
        troopsSelected = 0
        troopsLeft = 0
        sourceCountry = NO_SELECTION
        targetCountry = NO_SELECTION
        updateTextViews()
        listener.updateButtons()
    }

    fun updateCountry(countryId: String) : String {
        return if (sourceCountry == NO_SELECTION) {
            updateSourceCountry(countryId)
            "source"
        } else {
            "target"
        }
    }

    fun updateSourceCountry(countryId: String) {

        sourceCountry = countryId
        var troopsAvailable = listener.getCountryById(countryId)?.count ?: 1
        troopsSelected = troopsAvailable - 1
        troopsLeft = troopsAvailable - troopsSelected
        updateTextViews()
    }

    fun updateTargetCountry(country: String) {
        targetCountry = country
        updateTextViews()
    }


}