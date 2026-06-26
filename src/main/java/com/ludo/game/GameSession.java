package com.ludo.game;

import com.ludo.model.Figura;
import com.ludo.model.StatistikaIgraca;
import com.ludo.model.StatusFigure;

import java.util.Arrays;
import java.util.List;

/**
 * Holds the mutable state of a single ongoing game:
 *  - 16 Figura objects (4 players × 4 pieces, IDs 0–15)
 *  - 4 StatistikaIgraca objects shared across turns
 *  - Turn tracking (current player, consecutive sixes)
 *
 * Piece ID layout: player P owns pieces [P*4 .. P*4+3].
 * Player IDs: 0=Red, 1=Blue, 2=Yellow, 3=Green.
 */
public class GameSession {

    private static final int NUM_PLAYERS = 4;
    private static final int PIECES_PER_PLAYER = 4;

    private final Figura[] figure;
    private final StatistikaIgraca[] stat;
    private final int[] uzastopneSestice;

    private int currentPlayer;
    private boolean gameOver;
    private int winnerId;

    public GameSession() {
        figure = new Figura[NUM_PLAYERS * PIECES_PER_PLAYER];
        stat = new StatistikaIgraca[NUM_PLAYERS];
        uzastopneSestice = new int[NUM_PLAYERS];

        for (int p = 0; p < NUM_PLAYERS; p++) {
            stat[p] = new StatistikaIgraca(p);
            for (int i = 0; i < PIECES_PER_PLAYER; i++) {
                // relPos=0 (BAZA) by default from Figura constructor
                figure[p * PIECES_PER_PLAYER + i] = new Figura(p * PIECES_PER_PLAYER + i, p);
            }
        }

        currentPlayer = 0;
        gameOver = false;
        winnerId = -1;
    }

    /** Apply the agent's chosen move and check for game-over. */
    public void applyMove(int figuraId, int novaRelPos) {
        if (figuraId < 0 || figuraId >= figure.length) return;
        figure[figuraId].updatePozicija(novaRelPos);
        checkWin(figure[figuraId].getVlasnikId());
    }

    /** Called after each turn to move to the next player (or keep for bonus roll). */
    public void advancePlayer() {
        currentPlayer = (currentPlayer + 1) % NUM_PLAYERS;
    }

    public void incrementSestice() {
        uzastopneSestice[currentPlayer]++;
    }

    public void resetSestice() {
        uzastopneSestice[currentPlayer] = 0;
    }

    public int getUzastopneSestice() {
        return uzastopneSestice[currentPlayer];
    }

    private void checkWin(int playerId) {
        long inGoal = Arrays.stream(figure)
                .filter(f -> f.getVlasnikId() == playerId && f.getStatus() == StatusFigure.ZAVRSENA)
                .count();
        if (inGoal == PIECES_PER_PLAYER) {
            gameOver = true;
            winnerId = playerId;
            stat[playerId].zavrsiPartiju(true);
        }
    }

    // ---- Accessors ----

    public List<Figura> getFigureList() {
        return Arrays.asList(figure);
    }

    public StatistikaIgraca[] getAllStat() {
        return stat;
    }

    public StatistikaIgraca getStat(int playerId) {
        return stat[playerId];
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getWinnerId() {
        return winnerId;
    }
}
