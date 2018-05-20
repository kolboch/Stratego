package com.kakaboc.stratego.activities.game

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kakaboc.stratego.R
import com.kakaboc.stratego.model.FieldState
import com.kakaboc.stratego.model.Game
import com.kakaboc.stratego.model.GamePlayer
import com.kakaboc.stratego.model.Player
import com.kakaboc.stratego.views.CustomGrid
import kotlinx.android.synthetic.main.activity_game.*

const val GAME_SHARED_PREFS = "com.kakaboc.stratego.GAME_SP"
const val GAME_ROWS = "com.kakaboc.stratego.GAME_SP.ROWS"
const val GAME_COLS = "com.kakaboc.stratego.GAME_SP.COLS"
const val EXTRA_PLAYER_ONE = "com.kakaboc.stratego.PLAYER_ONE"
const val EXTRA_PLAYER_TWO = "com.kakaboc.stratego.PLAYER_TWO"

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
        val rows = sharedPreferences.getInt(GAME_ROWS, 5)
        val cols = sharedPreferences.getInt(GAME_COLS, 5)
        game = Game(
                rows, cols,
                Player(GamePlayer.Human, FieldState.Circle),
                Player(GamePlayer.Human, FieldState.Cross)
        )
        game.playerToMove = game.player1
        game.updateGameViewCallback = { points1, points2 ->
            updateScores(points1, points2)
            updateBoardView()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateScores(points1: Int, points2: Int) {
        score1.text = "" + points1
        score2.text = "" + points2
    }

    private fun initCellListener() {
        adapter.onCellClickListener = { x, y ->
            game.makeMove(x, y)
        }
    }

    private fun updateBoardView() {
        adapter.gridItems = game.boardStates
        adapter.notifyDataSetChanged()
    }
}
