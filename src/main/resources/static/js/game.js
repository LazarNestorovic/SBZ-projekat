// game.js — API calls, dice rolling, game loop
// Person B: wire animateMove() and highlightCandidates() from board.js

let gameId = null;
let rolling = false;

const PLAYER_NAMES = ['Crveni', 'Plavi', 'Zuti', 'Zeleni'];

async function newGame() {
    const res = await fetch('/api/game/new', { method: 'POST' });
    const data = await res.json();
    gameId = data.gameId;
    document.getElementById('game-id-label').textContent = '#' + gameId;
    document.getElementById('winner-panel').style.display = 'none';
    document.getElementById('btn-roll').disabled = false;
    updateUI({
        activePlayer: data.currentPlayer,
        nextPlayer: data.currentPlayer,
        figure: data.figure,
        statistike: data.statistike,
        gameOver: false,
        winnerId: -1,
        dice: null,
        cepEventi: [],
        bonusRoll: false,
        preskaciPotez: false,
        modus: 'NEUTRALNI',
        razlog: '',
        prioritet: 0
    });
    // Person B: call drawBoard() and drawPieces(data.figure) here
    if (typeof drawBoard === 'function') drawBoard();
    if (typeof drawPieces === 'function') drawPieces(data.figure);
}

async function roll() {
    if (!gameId || rolling) return;
    rolling = true;
    document.getElementById('btn-roll').disabled = true;

    const dice = Math.floor(Math.random() * 6) + 1;
    document.getElementById('dice-display').textContent = '🎲 ' + dice;

    // Small delay so the player sees the roll before the agent responds
    await sleep(400);

    const res = await fetch(`/api/game/${gameId}/potez`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ dice })
    });

    if (!res.ok) {
        console.error('Potez failed:', await res.text());
        rolling = false;
        document.getElementById('btn-roll').disabled = false;
        return;
    }

    const data = await res.json();

    // Person B: highlight all candidate squares for 500ms then animate chosen move
    if (typeof highlightCandidates === 'function') {
        highlightCandidates(data.kandidatPotezi);
        await sleep(500);
    }

    if (typeof animateMove === 'function' && data.odabranaFiguraId !== -1) {
        await animateMove(data.odabranaFiguraId, data.novaRelativnaPozicija, data.figure);
    } else if (typeof drawPieces === 'function') {
        drawPieces(data.figure);
    }

    updateUI(data);

    if (data.gameOver) {
        showWinner(data.winnerId);
        document.getElementById('btn-roll').disabled = true;
    } else {
        // Enable next roll (same player if bonusRoll, otherwise next player)
        document.getElementById('btn-roll').disabled = false;
    }

    rolling = false;
}

function updateUI(data) {
    // Modus badge
    const badge = document.getElementById('modus-badge');
    badge.textContent = data.modus || 'NEUTRALNI';
    badge.className = 'badge ' + (data.modus || 'NEUTRALNI').toLowerCase();

    // Bonus / preskaci badges
    toggle('bonus-badge',   data.bonusRoll);
    toggle('preskaci-badge', data.preskaciPotez);

    // Turn info
    setText('active-player', PLAYER_NAMES[data.activePlayer] || '–');
    setText('next-player',   PLAYER_NAMES[data.nextPlayer]   || '–');
    setText('razlog',        data.razlog  || '–');
    setText('prioritet',     data.prioritet ? '★'.repeat(Math.max(0, 8 - data.prioritet)) + ' (' + data.prioritet + ')' : '–');

    // CEP events
    if (data.cepEventi && data.cepEventi.length > 0) {
        const log = document.getElementById('events-log');
        data.cepEventi.forEach(e => {
            const li = document.createElement('li');
            li.className = e.tip;
            li.textContent = e.tip.replace(/_/g, ' ')
                + (e.figuraId >= 0 ? ' (f' + e.figuraId + ')' : '')
                + (e.pozicija >= 0 ? ' @' + e.pozicija : '');
            log.prepend(li);
        });
        // Keep log to last 20 entries
        while (log.children.length > 20) log.removeChild(log.lastChild);
    }

    // Stats table
    if (data.statistike) {
        const tbody = document.getElementById('stats-body');
        tbody.innerHTML = '';
        data.statistike.forEach(s => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
              <td class="player-${s.igracId}">${PLAYER_NAMES[s.igracId]}</td>
              <td>${s.ukupnoPoteza}</td>
              <td>${s.eliminacijeIzvedene}</td>
              <td>${s.eliminacijePrimljene}</td>
              <td>${s.figureUCilju}</td>
              <td>${s.omiljeniStil}</td>
              <td>${s.winRate.toFixed(0)}%</td>`;
            tbody.appendChild(tr);
        });
    }
}

function showWinner(winnerId) {
    const panel = document.getElementById('winner-panel');
    const colors = ['#e74c3c','#3498db','#f1c40f','#2ecc71'];
    panel.style.display = 'block';
    panel.style.background = colors[winnerId] + '33';
    document.getElementById('winner-text').textContent =
        '🏆 ' + PLAYER_NAMES[winnerId] + ' POBIJEDIO!';
}

// ---- Utilities ----
function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
function setText(id, val) { const el = document.getElementById(id); if (el) el.textContent = val; }
function toggle(id, show) {
    const el = document.getElementById(id);
    if (el) el.classList.toggle('hidden', !show);
}

// Auto-start on page load
window.addEventListener('DOMContentLoaded', newGame);
