package com.kakaboc.stratego.model

import android.util.Log

/**
 * Created by Karlo on 2018-05-20.
 */
class Game(
        val rows: Int,
        val cols: Int,
        val player1: Player,
        val player2: Player
) {

    var score1 = 0
    var score2 = 0
    var boardStates = Array(rows, { Array(cols, { FieldState.Neutral }) })
    lateinit var playerToMove: Player
    lateinit var updateGameViewCallback: (Int, Int) -> Unit

    fun makeMove(x: Int, y: Int) {
        if (boardStates[x][y] == FieldState.Neutral) {
            //check points etc. // check if ending game
            boardStates[x][y] = playerToMove.fieldState
            val points = getMovePoints(x, y, playerToMove.fieldState)
            if (playerToMove == player1) {
                score1 += points
            } else {
                score2 += points
            }
            playerToMove = if (playerToMove == player1) player2 else player1
        }
        updateGameViewCallback.invoke(score1, score2)
        if (playerToMove.type == GamePlayer.AI) {
            Thread.sleep(500)
            //make move with min max
        }
    }

    private fun getMovePoints(x: Int, y: Int, playersField: FieldState): Int {
        //column
        var scoreColumn = 0
        var filler = true
        for (i in 0 until boardStates.size) {
            if (boardStates[i][y] == FieldState.Neutral) {
                filler = false
                break
            }
        }
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
        filler = true
        for (j in 0 until boardStates[0].size) {
            if (boardStates[x][j] == FieldState.Neutral) {
                filler = false
                break
            }
        }
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
        val sum = scoreColumn + scoreRow + scoreLR + scoreRL
        if (sum > 0) {
            Log.v("GAME", "columnScore: $scoreColumn, row: $scoreRow, lR: $scoreLR, RL: $scoreRL")
        }
        return sum
    }

    fun clearBoard() {
        boardStates = Array(rows, { Array(cols, { FieldState.Neutral }) })
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

class Player(val type: GamePlayer, val fieldState: FieldState)