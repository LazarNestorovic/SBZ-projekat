// game.js — one AI turn per button click, with optional auto-play

let gameId   = null;
let gameOver = false;
let rolling  = false;  // true while a turn is in progress

let autoPlay  = false;
let autoTimer = null;
const AUTO_DELAY_MS = 1200;  // ms between auto turns

const DICE_SHOW_MS  = 500;
const CANDIDATES_MS = 400;

// ── Game lifecycle ─────────────────────────────────────────────────────────

async function newGame() {
    // Stop any running auto-play
    _stopAuto();

    gameOver = false;
    rolling  = false;
    gameId   = null;
    setBtnRoll(false, 'Učitavanje...');

    let data;
    try {
        const res = await fetch('/api/game/new', { method: 'POST' });
        data = await res.json();
    } catch (e) {
        console.error('newGame failed:', e);
        setBtnRoll(true, '🔄 Pokušaj ponovo');
        return;
    }

    gameId = data.gameId;
    document.getElementById('game-id-label').textContent = '#' + gameId;
    document.getElementById('winner-panel').style.display = 'none';
    document.getElementById('events-log').innerHTML = '';

    if (typeof drawPieces === 'function') drawPieces(data.figure);

    updateModusBadge('NEUTRALNI');
    updateStatsTable(data.statistike || []);
    setText('active-player', PLAYER_NAMES[data.currentPlayer] || '–');
    setText('next-player',   PLAYER_NAMES[(data.currentPlayer + 1) % 4] || '–');
    setText('figura-id', '–');
    setText('razlog',    '–');
    setText('prioritet', '–');
    toggle('bonus-badge',    false);
    toggle('preskaci-badge', false);

    setBtnRoll(true, '🎲 Baci kocku');
}

// ── Auto-play ──────────────────────────────────────────────────────────────

function toggleAuto() {
    if (autoPlay) {
        _stopAuto();
    } else {
        _startAuto();
    }
}

function _startAuto() {
    if (gameOver) return;
    autoPlay = true;
    const btn = document.getElementById('btn-auto');
    if (btn) { btn.textContent = '⏹ Stop'; btn.classList.add('running'); }
    setBtnRoll(false, '🎲 Baci kocku');
    _scheduleAutoTurn();
}

function _stopAuto() {
    autoPlay = false;
    clearTimeout(autoTimer);
    autoTimer = null;
    const btn = document.getElementById('btn-auto');
    if (btn) { btn.textContent = '▶ Auto'; btn.classList.remove('running'); }
    if (!gameOver) setBtnRoll(true, '🎲 Baci kocku');
}

function _scheduleAutoTurn() {
    if (!autoPlay || gameOver) return;
    autoTimer = setTimeout(async () => {
        if (!autoPlay || gameOver) return;
        await roll();
        if (autoPlay && !gameOver) _scheduleAutoTurn();
    }, AUTO_DELAY_MS);
}

// ── One turn per click ─────────────────────────────────────────────────────

const FETCH_TIMEOUT_MS = 15000;

async function roll() {
    if (rolling || gameOver || !gameId) return;
    rolling = true;
    setBtnRoll(false, '⏳');

    try {
        // 1. Roll dice and show
        const dice = Math.floor(Math.random() * 6) + 1;
        showDice(dice);
        await sleep(DICE_SHOW_MS);

        // 2. Send to Drools agent (with timeout so a hung server can't freeze the UI)
        let res, data;
        const ac = new AbortController();
        const timer = setTimeout(() => ac.abort(), FETCH_TIMEOUT_MS);
        try {
            res  = await fetch(`/api/game/${gameId}/potez`, {
                method:  'POST',
                headers: { 'Content-Type': 'application/json' },
                body:    JSON.stringify({ dice }),
                signal:  ac.signal,
            });
            data = await res.json();
        } catch (e) {
            const msg = e.name === 'AbortError' ? 'Timeout — server didn\'t respond in 15 s' : e.message;
            console.error('roll() fetch error:', msg);
            return;
        } finally {
            clearTimeout(timer);
        }
        if (!res.ok) {
            console.error('Agent error', res.status, data);
            return;
        }

        // 3. Flash valid candidate squares
        if (typeof highlightCandidates === 'function' && data.kandidatPotezi?.length) {
            highlightCandidates(data.kandidatPotezi);
            await sleep(CANDIDATES_MS);
        }

        // 4. Animate the chosen move
        if (typeof animateMove === 'function' && data.odabranaFiguraId !== -1) {
            await animateMove(data.odabranaFiguraId, data.novaRelativnaPozicija, data.figure);
        } else if (typeof drawPieces === 'function') {
            drawPieces(data.figure);
        }

        // 5. Refresh all side panels
        updateUI(data);

        // 6. Game over?
        if (data.gameOver) {
            gameOver = true;
            showWinner(data.winnerId);
            setBtnRoll(false, '🏆 Kraj igre');
            _stopAuto();
        }
    } catch (e) {
        console.error('roll() unexpected error:', e);
    } finally {
        if (!gameOver) {
            rolling = false;
            // Only re-enable the manual button when auto-play is OFF
            if (!autoPlay) {
                setBtnRoll(true, '🎲 Baci kocku');
            }
        }
    }
}

// ── UI refresh ─────────────────────────────────────────────────────────────

function updateUI(data) {
    updateModusBadge(data.modus);

    toggle('bonus-badge',    !!data.bonusRoll);
    toggle('preskaci-badge', !!data.preskaciPotez);

    setText('active-player', PLAYER_NAMES[data.activePlayer] || '–');
    setText('next-player',   PLAYER_NAMES[data.nextPlayer]   || '–');

    const fid = data.odabranaFiguraId;
    setText('figura-id', fid != null && fid >= 0 ? 'F' + fid : '–');
    setText('razlog',    data.razlog || '–');
    setText('prioritet', starRating(data.prioritet));

    showDice(data.dice);

    appendEventLog(data.cepEventi || [], null);
    updateStatsTable(data.statistike || []);
}

// ── Formatting helpers ─────────────────────────────────────────────────────

function showDice(val) {
    if (!val) return;
    const faces = ['', '⚀', '⚁', '⚂', '⚃', '⚄', '⚅'];
    const el = document.getElementById('dice-display');
    if (el) el.textContent = (faces[val] || '') + '  ' + val;
}

function starRating(priority) {
    if (!priority || priority < 1) return '–';
    const filled = Math.max(0, 8 - priority);
    const empty  = Math.max(0, 7 - filled);
    return '★'.repeat(filled) + '☆'.repeat(empty) + '  (' + priority + ')';
}

function setBtnRoll(enabled, label) {
    const btn = document.getElementById('btn-roll');
    if (!btn) return;
    btn.disabled = !enabled;
    if (label != null) btn.textContent = label;
}

// ── Utilities ──────────────────────────────────────────────────────────────

function sleep(ms)  { return new Promise(r => setTimeout(r, ms)); }

function setText(id, val) {
    const el = document.getElementById(id);
    if (el) el.textContent = val != null ? String(val) : '–';
}

function toggle(id, show) {
    const el = document.getElementById(id);
    if (el) el.classList.toggle('hidden', !show);
}

window.addEventListener('DOMContentLoaded', newGame);
