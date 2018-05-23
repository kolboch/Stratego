package com.kakaboc.stratego.model

import android.os.Handler
import android.util.Log

/**
 * Created by Karlo on 2018-05-20.
 */

const val MAX_COMPUTATION = 3500000

class Game(
        val rows: Int,
        val cols: Int,
        val player1: Player,
        val player2: Player
) {
    var moves = 0
    var score1 = 0
    var score2 = 0
    var boardStates = Array(rows, { Array(cols, { FieldState.Neutral }) })
    lateinit var playerToMove: Player
    lateinit var updateGameViewCallback: (Int, Int) -> Unit
    lateinit var showResultCallback: (GameResult) -> Unit

    fun makeMove(x: Int, y: Int) {
        if (boardStates[x][y] == FieldState.Neutral) {
            boardStates[x][y] = playerToMove.fieldState
            val points = getMovePoints(x, y, playerToMove.fieldState)
            if (playerToMove == player1) {
                score1 += points
            } else {
                score2 += points
            }
            moves++
            if (gameEnded()) {
                updateGameViewCallback.invoke(score1, score2)
                showResultCallback.invoke(getWinner())
                Handler().postDelayed({
                    resetGameState()
                    updateGameViewCallback(score1, score2)
                }, 2000)
            }
            playerToMove = if (playerToMove == player1) player2 else player1
        }
        updateGameViewCallback.invoke(score1, score2)
        if (playerToMove.type == GamePlayer.AI && !gameEnded()) {
            Thread(Runnable {
                Handler().postDelayed({
                    val move = if (playerToMove == player1) {
                        generateMoveAI(boardStates, playerToMove.fieldState, player2.fieldState)
                    } else {
                        generateMoveAI(boardStates, playerToMove.fieldState, player1.fieldState)
                    }
                    makeMove(move.first, move.second)
                }, 500)
            }).run()
        }
    }

    private fun generateMoveAI(board: Array<Array<FieldState>>, maximizerField: FieldState, minimizerField: FieldState): Pair<Int, Int> {
        var bestX = -1
        var bestY = -1
        var bestMove = Int.MIN_VALUE
        var scoreDelta = score1 - score2
        if (player1.fieldState != maximizerField) {
            scoreDelta = score2 - score1
        }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (board[i][j] == FieldState.Neutral) {
                    Log.v("Game", "Call for move: $i, $j")
                    board[i][j] = maximizerField
                    val moveScore = getMovePoints(i, j, maximizerField)
                    val score = minmax(1, false, maximizerField, minimizerField, scoreDelta + moveScore, board, Int.MIN_VALUE, Int.MAX_VALUE)
                    board[i][j] = FieldState.Neutral
                    Log.v("Game", "Score for move $i, $j, $score")
                    if (score > bestMove) {
                        bestMove = score
                        bestX = i
                        bestY = j
                    }
                }
            }
        }
        Log.v("Game", "move: $bestX, $bestY")
        return Pair(bestX, bestY)
    }

    private fun minmax(depth: Int, maximizer: Boolean, maximizerField: FieldState, minimizerField: FieldState, score: Int,
                       board: Array<Array<FieldState>>, alpha: Int, beta: Int): Int {
        var depthComputation = 1
        (cols * rows - depth until cols * rows).forEach { depthComputation *= it }
        if (depthComputation >= MAX_COMPUTATION || depth == (cols * rows - moves)) {
            return score
        }
        if (maximizer) {
            var best = Integer.MIN_VALUE
            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    if (board[i][j] == FieldState.Neutral) {
                        board[i][j] = maximizerField
                        val moveScore = getMovePoints(i, j, maximizerField)
                        val bestEval = minmax(depth + 1, false, maximizerField, minimizerField, score + moveScore, board, alpha, beta)
                        best = kotlin.math.max(best, bestEval)
                        board[i][j] = FieldState.Neutral
                        val alpha = kotlin.math.max(alpha, best)
                        //pruning
                        if (beta <= alpha) {
                            break
                        }
                    }
                }
            }
            return best
        } else {
            var best = Integer.MAX_VALUE
            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    if (board[i][j] == FieldState.Neutral) {
                        board[i][j] = minimizerField
                        val moveScore = getMovePoints(i, j, minimizerField)
                        val bestEval = minmax(depth + 1, true, maximizerField, minimizerField, score - moveScore, board, alpha, beta)
                        board[i][j] = FieldState.Neutral
                        best = kotlin.math.min(best, bestEval)
                        val beta = kotlin.math.min(beta, best)
                        //pruning
                        if (beta <= alpha) {
                            break
                        }
                    }
                }
            }
            return best
        }
    }


    private fun getMovePoints(x: Int, y: Int, playersField: FieldState): Int {
        //column
        var scoreColumn = 0
        var filler = (0 until boardStates.size).none { boardStates[it][y] == FieldState.Neutral }
        if (filler) {
            var tempX = x
            while (tempX - 1 >= 0 && boardStates[tempX - 1][y] == playersField) {
                scoreColumn++
                tempX--
            }
            tempX = x
            while (tempX + 1 < rows && boardStates[tempX + 1][y] == playersField) {
                scoreColumn++
                tempX++
            }
            if (scoreColumn > 0) {
                scoreColumn++
            }
        }
        //row
        var scoreRow = 0
        filler = (0 until boardStates[0].size).none { boardStates[x][it] == FieldState.Neutral }
        if (filler) {
            var tempY = y
            while (tempY + 1 < cols && boardStates[x][tempY + 1] == playersField) {
                scoreRow++
                tempY++
            }
            tempY = y
            while (tempY - 1 >= 0 && boardStates[x][tempY - 1] == playersField) {
                scoreRow++
                tempY--
            }
            if (scoreRow > 0) {
                scoreRow++
            }
        }

        //diagonal LR
        var scoreLR = 0
        filler = true
        var xTemp = 0
        var yTemp = 0
        var compared = x.compareTo(y)
        if (compared > 0) {
            xTemp = x - y
            yTemp = 0
            while (xTemp < rows && yTemp < cols) {
                if (boardStates[xTemp][yTemp] == FieldState.Neutral) {
                    filler = false
                    break
                }
                xTemp++
                yTemp++
            }
        } else if (compared < 0) {
            xTemp = 0
            yTemp = y - x
            while (xTemp < rows && yTemp < cols) {
                if (boardStates[xTemp][yTemp] == FieldState.Neutral) {
                    filler = false
                    break
                }
                xTemp++
                yTemp++
            }
        } else {
            while (xTemp < rows && yTemp < cols) {
                if (boardStates[xTemp][yTemp] == FieldState.Neutral) {
                    filler = false
                    break
                }
                xTemp++
                yTemp++
            }
        }
        if (filler) {
            xTemp = x
            yTemp = y
            while (xTemp - 1 >= 0 && yTemp - 1 >= 0 && boardStates[xTemp - 1][yTemp - 1] == playersField) {
                scoreLR++
                xTemp--
                yTemp--
            }
            xTemp = x
            yTemp = y
            while (xTemp + 1 < rows && yTemp + 1 < cols && boardStates[xTemp + 1][yTemp + 1] == playersField) {
                scoreLR++
                xTemp++
                yTemp++
            }
            if (scoreLR > 0) {
                scoreLR++
            }
        }

        //diagonal RL
        var scoreRL = 0
        filler = true
        compared = (cols - 1).compareTo(x + y)
        if (compared > 0) {
            xTemp = 0
            yTemp = x + y
            while (xTemp < rows && yTemp >= 0) {
                if (boardStates[xTemp][yTemp] == FieldState.Neutral) {
                    filler = false
                    break
                }
                xTemp++
                yTemp--
            }
        } else if (compared < 0) {
            xTemp = (x + y) - (cols - 1)
            yTemp = cols - 1
            while (xTemp < rows && yTemp >= 0) {
                if (boardStates[xTemp][yTemp] == FieldState.Neutral) {
                    filler = false
                    break
                }
                xTemp++
                yTemp--
            }
        } else {
            xTemp = 0
            yTemp = cols - 1
            while (xTemp < rows && yTemp >= 0) {
                if (boardStates[xTemp][yTemp] == FieldState.Neutral) {
                    filler = false
                    break
                }
                xTemp++
                yTemp--
            }
        }
        if (filler) {
            xTemp = x
            yTemp = y
            while (xTemp - 1 >= 0 && yTemp + 1 < cols && boardStates[xTemp - 1][yTemp + 1] == playersField) {
                scoreRL++
                xTemp--
                yTemp++
            }
            xTemp = x
            yTemp = y
            while (xTemp + 1 < rows && yTemp - 1 >= 0 && boardStates[xTemp + 1][yTemp - 1] == playersField) {
                scoreRL++
                xTemp++
                yTemp--
            }
            if (scoreRL > 0) {
                scoreRL++
            }
        }
        return scoreColumn + scoreRow + scoreLR + scoreRL
    }

    fun resetGameState() {
        boardStates = Array(rows, { Array(cols, { FieldState.Neutral }) })
        moves = 0
        score1 = 0
        score2 = 0
        playerToMove = player1
    }

    private fun gameEnded(): Boolean {
        return moves == (rows * cols)
    }

    private fun getWinner(): GameResult {
        return when {
            score1 > score2 -> GameResult.Player1
            score2 > score1 -> GameResult.Player2
            else -> GameResult.Draw
        }
    }

    fun initAIvsAI() {
        val move = generateMoveAI(boardStates, player1.fieldState, player2.fieldState)
        makeMove(move.first, move.second)
    }
}

enum class GamePlayer(private val code: Int) {
    AI(0),
    Human(1)
}


// u can replace cross/circle with what u want ex. colors :)
enum class FieldState(private val symbol: Int) {
    Cross(1),
    Circle(-1),
    Neutral(0)
}

enum class GameResult(private val code: Int) {
    Player1(11),
    Player2(22),
    Draw(33)
}

class Player(val type: GamePlayer, val fieldState: FieldState)