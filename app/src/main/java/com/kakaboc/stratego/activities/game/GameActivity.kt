package com.kakaboc.stratego.activities.game

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kakaboc.stratego.R
import com.kakaboc.stratego.activities.main.GAME_MODE_EXTRA
import com.kakaboc.stratego.activities.main.MODE_AIAI
import com.kakaboc.stratego.activities.main.MODE_PVAI
import com.kakaboc.stratego.activities.main.MODE_PVP
import com.kakaboc.stratego.model.FieldState
import com.kakaboc.stratego.model.Game
import com.kakaboc.stratego.model.GamePlayer
import com.kakaboc.stratego.model.Player
import com.kakaboc.stratego.views.CustomGrid
import kotlinx.android.synthetic.main.activity_game.*

const val GAME_SHARED_PREFS = "com.kakaboc.stratego.GAME_SP"
const val GAME_ROWS = "com.kakaboc.stratego.GAME_SP.ROWS"
const val GAME_COLS = "com.kakaboc.stratego.GAME_SP.COLS"

class GameActivity : AppCompatActivity() {

    private lateinit var game: Game
    private val adapter = CustomGrid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        initGame()
        adapter.gridItems = game.boardStates
        adapter.rowMax = game.rows
        adapter.colMax = game.cols
        gridGame.numColumns = game.cols
        gridGame.adapter = adapter
        initCellListener()
    }

    private fun initGame() {
        val sharedPreferences = getSharedPreferences(GAME_SHARED_PREFS, Context.MODE_PRIVATE)
        val mode = intent.getStringExtra(GAME_MODE_EXTRA)
        val rows = sharedPreferences.getInt(GAME_ROWS, 4)
        val cols = sharedPreferences.getInt(GAME_COLS, 4)
        when (mode) {
            MODE_PVP -> {
                game = Game(
                        rows, cols,
                        Player(GamePlayer.Human, FieldState.Circle),
                        Player(GamePlayer.Human, FieldState.Cross)
                )
            }
            MODE_PVAI -> {
                game = Game(
                        rows, cols,
                        Player(GamePlayer.Human, FieldState.Circle),
                        Player(GamePlayer.AI, FieldState.Cross)
                )
            }
            MODE_AIAI -> {
                game = Game(
                        rows, cols,
                        Player(GamePlayer.AI, FieldState.Circle),
                        Player(GamePlayer.AI, FieldState.Cross)
                )
            }
        }
        game.playerToMove = game.player1
        game.updateGameViewCallback = { points1, points2 ->
            updateScores(points1, points2)
            updateBoardView()
        }
        game.showResultCallback = { result ->
            Toast.makeText(this, "Winner is ${result.name}", Toast.LENGTH_SHORT).show()
            if (mode == MODE_AIAI) {
                initGameAfterDelay()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val mode = intent.getStringExtra(GAME_MODE_EXTRA)
        if (mode == MODE_AIAI) {
            initGameAfterDelay()
        }

    }

    private fun initGameAfterDelay() {
        Thread(Runnable {
            Handler().postDelayed({
                game.resetGameState()
                game.initAIvsAI()
            }, 1000)
        }).run()
    }

    @SuppressLint("SetTextI18n")
    private fun updateScores(points1: Int, points2: Int) {
        score1.text = "" + points1
        score2.text = "" + points2
    }

    private fun initCellListener() {
        adapter.onCellClickListener = { x, y ->
            if (game.playerToMove.type != GamePlayer.AI) {
                game.makeMove(x, y)
            }
        }
    }

    private fun updateBoardView() {
        adapter.gridItems = game.boardStates
        adapter.notifyDataSetChanged()
    }
}
