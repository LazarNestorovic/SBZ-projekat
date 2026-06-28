package com.ludo.game;

import com.ludo.model.StatistikaIgraca;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameManager {

    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

    private StatistikaIgraca[] persistentStats = null;

    public String createGame() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        sessions.put(id, new GameSession(persistentStats));
        return id;
    }

    public void saveStats(StatistikaIgraca[] stats) {
        this.persistentStats = stats;
    }

    public GameSession getSession(String id) {
        return sessions.get(id);
    }

    public void removeSession(String id) {
        sessions.remove(id);
    }
}
