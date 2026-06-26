// board.js — Person B implements this file
//
// Required functions (called from game.js):
//
//   drawBoard()
//     Draw the static 700×700 Ludo board on #board canvas.
//
//   drawPieces(figuraArray)
//     Draw all 16 pieces at their current positions.
//     figuraArray: array of FiguraDTO objects from the API.
//
//   highlightCandidates(kandidatPotezi)
//     Briefly flash valid-move squares.
//     kandidatPotezi: array of PotezDTO from the API (validan:true ones are the real candidates).
//
//   animateMove(figuraId, novaRelativnaPozicija, figuraArray)  → Promise
//     Smoothly move the piece from its current pixel to the new pixel.
//     Resolve the promise when animation is done.
//
// ---- Coordinate helper ----
// relPosToPixel(vlasnikId, relativnaPozicija) → {x, y}
//   Maps a piece's relative position to a canvas pixel center.
//
//   relPos  0        → player's home base area (corner)
//   relPos  1–52     → main board squares (shared, player-offset)
//   relPos  53–57    → home stretch (colored path to center)
//   relPos  58       → goal (center of board)
//
// Board geometry reference (15×15 cells, cell = 700/15 ≈ 46.7px):
//
//   Player 0 (Red)    — starts entering from bottom-left → goes right along row 12
//   Player 1 (Blue)   — starts entering from right       → goes down along col 12
//   Player 2 (Yellow) — starts entering from top-right   → goes left along row 2
//   Player 3 (Green)  — starts entering from left        → goes up along col 2
//
// The starting absolute positions are {1, 14, 27, 40} (BoardUtils.POCETNE_POZICIJE).
// Map abs positions 1–52 to grid coordinates first, then convert to pixels.

const CANVAS_SIZE = 700;
const CELL = CANVAS_SIZE / 15;  // ≈ 46.7

const PLAYER_COLORS = ['#e74c3c', '#3498db', '#f1c40f', '#2ecc71'];
const PLAYER_NAMES  = ['Crveni', 'Plavi', 'Zuti', 'Zeleni'];

const canvas = document.getElementById('board');
const ctx    = canvas.getContext('2d');

// TODO Person B: implement relPosToPixel
function relPosToPixel(vlasnikId, relativnaPozicija) {
    // Placeholder — returns a position in the corresponding corner
    // Replace with real coordinate mapping
    const corners = [
        { x: CANVAS_SIZE * 0.15, y: CANVAS_SIZE * 0.85 }, // Red
        { x: CANVAS_SIZE * 0.85, y: CANVAS_SIZE * 0.15 }, // Blue
        { x: CANVAS_SIZE * 0.85, y: CANVAS_SIZE * 0.85 }, // Yellow
        { x: CANVAS_SIZE * 0.15, y: CANVAS_SIZE * 0.15 }, // Green
    ];
    const base = corners[vlasnikId];
    const offset = relativnaPozicija * 3;
    return { x: base.x + (offset % 60) - 30, y: base.y + Math.floor(offset / 60) * 10 - 20 };
}

// TODO Person B: implement drawBoard
function drawBoard() {
    ctx.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

    // Background
    ctx.fillStyle = '#0d1b2a';
    ctx.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

    // Placeholder grid
    ctx.strokeStyle = '#2a4060';
    ctx.lineWidth = 0.5;
    for (let i = 0; i <= 15; i++) {
        ctx.beginPath(); ctx.moveTo(i * CELL, 0); ctx.lineTo(i * CELL, CANVAS_SIZE); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(0, i * CELL); ctx.lineTo(CANVAS_SIZE, i * CELL); ctx.stroke();
    }

    // Placeholder corner labels
    ctx.font = 'bold 14px sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    PLAYER_COLORS.forEach((c, i) => {
        ctx.fillStyle = c + '44';
        const rx = i % 2 === 1 ? CANVAS_SIZE - CELL * 3.5 : CELL * 3.5;
        const ry = i < 2 ? CELL * 3.5 : CANVAS_SIZE - CELL * 3.5;
        ctx.fillRect(rx - CELL * 3, ry - CELL * 3, CELL * 6, CELL * 6);
        ctx.fillStyle = c;
        ctx.fillText(PLAYER_NAMES[i], rx, ry);
    });

    ctx.fillStyle = '#444';
    ctx.fillRect(CANVAS_SIZE/2 - CELL*1.5, CANVAS_SIZE/2 - CELL*1.5, CELL*3, CELL*3);
    ctx.fillStyle = '#eee';
    ctx.font = '12px sans-serif';
    ctx.fillText('CILJ', CANVAS_SIZE/2, CANVAS_SIZE/2);
}

// TODO Person B: implement drawPieces
function drawPieces(figuraArray) {
    if (!figuraArray) return;
    drawBoard();
    figuraArray.forEach(f => {
        const pos = relPosToPixel(f.vlasnikId, f.relativnaPozicija);
        ctx.beginPath();
        ctx.arc(pos.x, pos.y, CELL * 0.35, 0, Math.PI * 2);
        ctx.fillStyle = PLAYER_COLORS[f.vlasnikId];
        ctx.fill();
        ctx.strokeStyle = f.status === 'ZAVRSENA' ? '#fff' : '#000';
        ctx.lineWidth = f.status === 'ZAVRSENA' ? 2.5 : 1;
        ctx.stroke();
        ctx.fillStyle = '#000';
        ctx.font = 'bold 10px sans-serif';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(f.id % 4, pos.x, pos.y);
    });
}

// TODO Person B: implement highlightCandidates
function highlightCandidates(kandidatPotezi) {
    if (!kandidatPotezi) return;
    kandidatPotezi.filter(p => p.validan).forEach(p => {
        const pos = relPosToPixel(p.vlasnikId, p.novaRelativnaPozicija);
        ctx.beginPath();
        ctx.arc(pos.x, pos.y, CELL * 0.42, 0, Math.PI * 2);
        ctx.strokeStyle = priorityColor(p.prioritet);
        ctx.lineWidth = 2.5;
        ctx.stroke();
    });
}

function priorityColor(p) {
    const colors = ['', '#27ae60','#2980b9','#8e44ad','#f39c12','#e67e22','#c0392b','#7f8c8d','#bdc3c7'];
    return colors[Math.min(p, 8)] || '#fff';
}

// TODO Person B: implement animateMove  (must return a Promise)
function animateMove(figuraId, novaRelPos, figuraArray) {
    return new Promise(resolve => {
        // Find the figure
        const figura = figuraArray.find(f => f.id === figuraId);
        if (!figura) { drawPieces(figuraArray); resolve(); return; }

        const from = relPosToPixel(figura.vlasnikId, figura.relativnaPozicija);
        const to   = relPosToPixel(figura.vlasnikId, novaRelPos);

        const duration = 400;
        const start    = performance.now();

        // Temporarily store old position for animation
        const oldRelPos = figura.relativnaPozicija;

        function frame(now) {
            const t = Math.min((now - start) / duration, 1);
            const ease = t < 0.5 ? 2*t*t : -1+(4-2*t)*t; // ease in-out

            drawBoard();
            // Draw all pieces at their original positions except the moving one
            figuraArray.forEach(f => {
                if (f.id === figuraId) return;
                const p = relPosToPixel(f.vlasnikId, f.relativnaPozicija);
                ctx.beginPath(); ctx.arc(p.x, p.y, CELL*0.35, 0, Math.PI*2);
                ctx.fillStyle = PLAYER_COLORS[f.vlasnikId]; ctx.fill();
                ctx.strokeStyle = '#000'; ctx.lineWidth = 1; ctx.stroke();
            });

            // Draw moving piece at interpolated position
            const cx = from.x + (to.x - from.x) * ease;
            const cy = from.y + (to.y - from.y) * ease;
            ctx.beginPath(); ctx.arc(cx, cy, CELL*0.38*(1 + 0.2*Math.sin(t*Math.PI)), 0, Math.PI*2);
            ctx.fillStyle = PLAYER_COLORS[figura.vlasnikId]; ctx.fill();
            ctx.strokeStyle = '#fff'; ctx.lineWidth = 2; ctx.stroke();

            if (t < 1) {
                requestAnimationFrame(frame);
            } else {
                // Snap to final state
                figura.relativnaPozicija = novaRelPos;
                drawPieces(figuraArray);
                resolve();
            }
        }
        requestAnimationFrame(frame);
    });
}
