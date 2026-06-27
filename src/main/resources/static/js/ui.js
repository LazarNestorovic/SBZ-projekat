// ui.js — panel updaters and winner overlay
// Loaded after board.js; functions here are called from game.js updateUI().

const PLAYER_NAMES_UI  = ['Crveni', 'Plavi', 'Zuti', 'Zeleni'];
const PLAYER_COLORS_UI = ['#e74c3c', '#3498db', '#f1c40f', '#2ecc71'];
const EVENT_LOG_MAX    = 20;

// ── Modus badge ────────────────────────────────────────────────────────────

/**
 * Swaps the CSS class on the modus chip.
 * modus: 'NEUTRALNI' | 'AGRESIVNI' | 'DEFANZIVNI'  (or null → NEUTRALNI)
 */
function updateModusBadge(modus) {
    const badge = document.getElementById('modus-badge');
    if (!badge) return;
    const m = (modus || 'NEUTRALNI').toUpperCase();
    badge.textContent = m;
    badge.className   = 'badge ' + m.toLowerCase();
}

// ── BC / CEP event log ─────────────────────────────────────────────────────

/**
 * Prepends this turn's events to the scrolling feed.
 *
 * cepEventi : array of { tip, figuraId, pozicija }  (from API)
 * bcPoruka  : optional string — shown with a [BC] tag (backward-chaining result)
 */
function appendEventLog(cepEventi, bcPoruka) {
    const log = document.getElementById('events-log');
    if (!log) return;

    // BC entry goes on top
    if (bcPoruka) {
        log.prepend(_makeLogItem('bc', bcPoruka));
    }

    // CEP entries below BC (also prepended, so last CEP ends up just after BC)
    const items = (cepEventi || []).map(e => {
        const label = (e.tip || '').replace(/_/g, ' ');
        const figPart = (e.figuraId >= 0) ? '  F' + e.figuraId : '';
        const posPart = (e.pozicija  > 0) ? ' @' + e.pozicija   : '';
        return _makeLogItem(e.tip || '', label + figPart + posPart);
    });
    // Prepend in reverse so chronological order is preserved
    items.reverse().forEach(li => log.prepend(li));

    // Trim old entries
    while (log.children.length > EVENT_LOG_MAX) {
        log.removeChild(log.lastChild);
    }
}

function _makeLogItem(cssClass, text) {
    const li = document.createElement('li');
    if (cssClass) li.className = cssClass;
    li.textContent = text;
    return li;
}

// ── Statistics table ───────────────────────────────────────────────────────

/**
 * Rebuilds the 4-row stats table from the server's statistika[] array.
 * Each entry: { igracId, ukupnoPoteza, eliminacijeIzvedene,
 *               eliminacijePrimljene, figureUCilju, omiljeniStil, winRate }
 */
function updateStatsTable(statistika) {
    const tbody = document.getElementById('stats-body');
    if (!tbody) return;
    tbody.innerHTML = '';

    (statistika || []).forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML =
            `<td class="player-${s.igracId}">${PLAYER_NAMES_UI[s.igracId]}</td>` +
            `<td>${s.ukupnoPoteza}</td>`                                 +
            `<td>${s.eliminacijeIzvedene}</td>`                          +
            `<td>${s.eliminacijePrimljene}</td>`                         +
            `<td>${s.figureUCilju}</td>`                                 +
            `<td>${s.omiljeniStil || '–'}</td>`                         +
            `<td>${Number(s.winRate || 0).toFixed(0)}%</td>`;
        tbody.appendChild(tr);
    });
}

// ── Winner overlay ─────────────────────────────────────────────────────────

/**
 * Shows a full-screen translucent overlay in the winner's colour.
 * Auto-dismisses after 4 s; click also dismisses it.
 * Also reveals the small winner-panel in the sidebar.
 */
function showWinner(igracId) {
    const color = PLAYER_COLORS_UI[igracId] || '#888';
    const name  = PLAYER_NAMES_UI[igracId]  || 'Igrac ' + igracId;

    // ── Sidebar panel ────────────────────────────────────────────────────
    const panel = document.getElementById('winner-panel');
    if (panel) {
        panel.style.display     = 'block';
        panel.style.background  = color + '22';
        panel.style.borderColor = color;
    }
    const txt = document.getElementById('winner-text');
    if (txt) txt.textContent = '🏆 ' + name + ' POBJEDA!';

    // ── Full-screen overlay ──────────────────────────────────────────────
    _injectWinnerKeyframes();

    const ov = document.createElement('div');
    ov.id = 'winner-overlay';
    Object.assign(ov.style, {
        position:       'fixed',
        inset:          '0',
        display:        'flex',
        flexDirection:  'column',
        alignItems:     'center',
        justifyContent: 'center',
        background:     color + 'dd',
        zIndex:         '9999',
        cursor:         'pointer',
        animation:      'winnerIn 0.35s ease both',
        userSelect:     'none',
        fontFamily:     'sans-serif',
    });

    ov.innerHTML =
        `<div style="font-size:5.5rem;line-height:1;filter:drop-shadow(0 4px 8px #0006)">` +
            `🏆` +
        `</div>` +
        `<div style="font-size:3rem;font-weight:800;color:#fff;` +
             `text-shadow:0 3px 20px #0009;margin-top:16px;letter-spacing:1px">` +
            name +
        `</div>` +
        `<div style="font-size:2rem;font-weight:700;color:#fff;opacity:.92;` +
             `margin-top:8px;letter-spacing:6px">` +
            `POBJEDA!` +
        `</div>` +
        `<div style="font-size:.8rem;color:#fff;opacity:.55;margin-top:28px">` +
            `klikni za nastavak` +
        `</div>`;

    const dismiss = () => { ov.remove(); };
    ov.addEventListener('click', dismiss);
    setTimeout(dismiss, 4000);

    document.body.appendChild(ov);
}

function _injectWinnerKeyframes() {
    if (document.getElementById('winner-kf')) return;
    const s = document.createElement('style');
    s.id = 'winner-kf';
    s.textContent =
        '@keyframes winnerIn{' +
            'from{opacity:0;transform:scale(.88)}' +
            'to  {opacity:1;transform:scale(1)}' +
        '}';
    document.head.appendChild(s);
}
