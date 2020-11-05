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
 * Bot fragment is loaded when the bot plays his turn. It provides information about the actions
 * and moves of the bot. phase.
 */
class BotFragment : Fragment() {

    private lateinit var listener: GameActivityInterface

    private lateinit var nameView: TextView
    private lateinit var actionView: TextView
    private lateinit var okButton: Button

    var botActonDone = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GameActivityInterface) listener = context
    }

    fun newInstance(): BotFragment {
        return BotFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentView = inflater.inflate(R.layout.bot_fragment, container, false)

        nameView = fragmentView.findViewById(R.id.playerNameText)
        actionView = fragmentView.findViewById(R.id.actionTextView)
        okButton = fragmentView.findViewById(R.id.okButton)

        fragmentView.findViewById<Button>(R.id.okButton).setOnClickListener {
            if(botActonDone) {
                listener.updateButtons()
                listener.nextPlayer()
            }
        }

        return fragmentView
    }

    fun addBotAction(action: String) {
        actionView.append(action)
        actionView.append('\n'.toString())

    }
}

