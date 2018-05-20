package com.kakaboc.stratego.activities.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kakaboc.stratego.R
import com.kakaboc.stratego.activities.game.GameActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setButtonListeners()
    }

    private fun setButtonListeners() {
        pvpButton.setOnClickListener {
            val intent = getGameIntent()
            startActivity(intent)
        }
        pvaiButton.setOnClickListener {
            val intent = getGameIntent()
            startActivity(intent)
        }
        aivaiButton.setOnClickListener {
            val intent = getGameIntent()
            startActivity(intent)
        }
    }

    private fun getGameIntent(): Intent {
        return Intent(this, GameActivity::class.java)
    }

}
