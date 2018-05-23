package com.kakaboc.stratego.activities.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kakaboc.stratego.R
import com.kakaboc.stratego.activities.game.GameActivity
import kotlinx.android.synthetic.main.activity_main.*

const val GAME_MODE_EXTRA = "com.kakaboc.stratego.EXTRA_MODE"
const val MODE_PVP = "PVP"
const val MODE_PVAI = "PVAI"
const val MODE_AIAI = "AIAI"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setButtonListeners()
    }

    private fun setButtonListeners() {
        pvpButton.setOnClickListener {
            val intent = getGameIntent()
            intent.putExtra(GAME_MODE_EXTRA, MODE_PVP)
            startActivity(intent)
        }
        pvaiButton.setOnClickListener {
            val intent = getGameIntent()
            intent.putExtra(GAME_MODE_EXTRA, MODE_PVAI)
            startActivity(intent)
        }
        aivaiButton.setOnClickListener {
            val intent = getGameIntent()
            intent.putExtra(GAME_MODE_EXTRA, MODE_AIAI)
            startActivity(intent)
        }
    }

    private fun getGameIntent(): Intent {
        return Intent(this, GameActivity::class.java)
    }

}
