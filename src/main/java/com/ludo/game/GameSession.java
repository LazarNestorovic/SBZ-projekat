package com.ludo.game;

import com.ludo.model.Figura;
import com.ludo.model.StatistikaIgraca;
import com.ludo.model.StatusFigure;
import com.ludo.util.BoardUtils;

import java.util.Arrays;
import java.util.List;

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
        this(null);
    }

    public GameSession(StatistikaIgraca[] existingStats) {
        figure = new Figura[NUM_PLAYERS * PIECES_PER_PLAYER];
        stat = new StatistikaIgraca[NUM_PLAYERS];
        uzastopneSestice = new int[NUM_PLAYERS];

        for (int p = 0; p < NUM_PLAYERS; p++) {
            if (existingStats != null && existingStats[p] != null) {
                stat[p] = existingStats[p];
                stat[p].resetZaNoviGame();
            } else {
                stat[p] = new StatistikaIgraca(p);
            }
            for (int i = 0; i < PIECES_PER_PLAYER; i++) {
                figure[p * PIECES_PER_PLAYER + i] = new Figura(p * PIECES_PER_PLAYER + i, p);
            }
        }

        currentPlayer = 0;
        gameOver = false;
        winnerId = -1;
    }

    public void applyMove(int figuraId, int novaRelPos) {
        if (figuraId < 0 || figuraId >= figure.length) return;
        Figura moved = figure[figuraId];
        moved.updatePozicija(novaRelPos);

        if (novaRelPos >= 1 && novaRelPos <= BoardUtils.ZADNJA_GLAVNA_REL_POZICIJA) {
            int newAbs = moved.getApsolutnaPozicija();
            for (Figura f : figure) {
                if (f.getId() != figuraId
                        && f.getVlasnikId() != moved.getVlasnikId()
                        && f.getStatus() == StatusFigure.AKTIVNA
                        && f.getApsolutnaPozicija() == newAbs
                        && !f.isSigurna()) {
                    System.out.println("[ELIMINACIJA] Figura " + f.getId() +
                            " igraca " + f.getVlasnikId() + " vracena u bazu.");
                    f.updatePozicija(0);
                }
            }
        }

        checkWin(moved.getVlasnikId());
    }

    public void finalizeGame() {
        for (int p = 0; p < NUM_PLAYERS; p++) {
            if (p != winnerId) {
                stat[p].zavrsiPartiju(false);
            }
        }
    }

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
