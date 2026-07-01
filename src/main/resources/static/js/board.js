// board.js — Ludo board geometry & rendering

const CANVAS_SIZE = 700;
const CELL = CANVAS_SIZE / 15;   // ≈ 46.667 px per grid cell

const PLAYER_COLORS = ['#e74c3c', '#3498db', '#f1c40f', '#2ecc71'];
const PLAYER_NAMES  = ['Crveni', 'Plavi', 'Zuti', 'Zeleni'];
const POCETNE_POZICIJE = [1, 14, 27, 40];   // absolute starting squares per player
const STAR_ABS = new Set([9, 22, 35, 48]);  // universally safe squares

// ─── Board coordinate table ────────────────────────────────────────────────
// Absolute board position (1–52) → [row, col] in 15×15 grid.
// Path walks clockwise starting at Red's entry square (abs 1).
//
//  Left arm → (row 6, cols 0-5)
//  Up (top-left) → (rows 0-6, col 6)
//  Across top → (row 0, cols 6-8)
//  Down (top-right) → (rows 0-6, col 8)
//  Right arm → (row 6, cols 9-14)
//  Down (right) → (rows 7-8, col 14)
//  Left (bottom-right half) → (row 8, cols 9-14)
//  Down (bottom-right) → (rows 9-14, col 8)
//  Across bottom → (row 14, cols 6-8)
//  Up (bottom-left) → (rows 9-14, col 6)
//  Left (bottom-left half) → (row 8, cols 0-5)
//  Up (left edge) → (rows 7-6, col 0)
const ABS_TO_GRID = [
    null,                                                    // 0 — unused
    [6,1],[6,2],[6,3],[6,4],[6,5],                          // 1–5
    [5,6],[4,6],[3,6],[2,6],[1,6],                          // 6–10
    [0,6],[0,7],[0,8],                                       // 11–13
    [1,8],[2,8],[3,8],[4,8],[5,8],                          // 14–18
    [6,9],[6,10],[6,11],[6,12],[6,13],[6,14],               // 19–24
    [7,14],[8,14],                                           // 25–26
    [8,13],[8,12],[8,11],[8,10],[8,9],                      // 27–31
    [9,8],[10,8],[11,8],[12,8],[13,8],[14,8],               // 32–37
    [14,7],[14,6],                                           // 38–39
    [13,6],[12,6],[11,6],[10,6],[9,6],                      // 40–44
    [8,5],[8,4],[8,3],[8,2],[8,1],[8,0],                    // 45–50
    [7,0],[6,0],                                             // 51–52
];

// Home-stretch [row,col] for relPos 53–57, per player.
// Each player's stretch runs along the middle row/col of their arm.
const HOME_STRETCH = [
    [[7,1],[7,2],[7,3],[7,4],[7,5]],        // Red   (0): row 7, left → right
    [[1,7],[2,7],[3,7],[4,7],[5,7]],        // Blue  (1): col 7, top  → down
    [[7,13],[7,12],[7,11],[7,10],[7,9]],    // Yellow(2): row 7, right → left
    [[13,7],[12,7],[11,7],[10,7],[9,7]],    // Green (3): col 7, bottom → up
];

// Home-base piece slots [row,col] — 4 slots per player, index = figuraId % 4.
// Each player owns a 6×6 corner; slots sit in a 2×2 arrangement inside it.
const HOME_POS = [
    [[1.5,1.5],[1.5,3.5],[3.5,1.5],[3.5,3.5]],               // Red   (0): rows 0–5,  cols 0–5  (top-left)
    [[1.5,10.5],[1.5,12.5],[3.5,10.5],[3.5,12.5]],           // Blue  (1): rows 0–5,  cols 9–14 (top-right)
    [[10.5,10.5],[10.5,12.5],[12.5,10.5],[12.5,12.5]],       // Yellow(2): rows 9–14, cols 9–14 (bottom-right)
    [[10.5,1.5],[10.5,3.5],[12.5,1.5],[12.5,3.5]],           // Green (3): rows 9–14, cols 0–5  (bottom-left)
];

// Home corner areas [rowStart, colStart], each 6×6 cells.
const HOME_AREAS = [[0,0],[0,9],[9,9],[9,0]]; // Red=top-left, Blue=top-right, Yellow=bottom-right, Green=bottom-left

const canvas = document.getElementById('board');
const ctx    = canvas.getContext('2d');

// Snapshot taken in drawPieces(); used as animation "from" source on the next turn.
let _prevFigure = null;

// ─── Coordinate helpers ───────────────────────────────────────────────────

function cellCenter(row, col) {
    return { x: (col + 0.5) * CELL, y: (row + 0.5) * CELL };
}

/**
 * Returns {x, y} pixel center for a piece at the given relative position.
 * pieceIndex (0–3, default 0) spreads same-player pieces apart in baza.
 */
function relPosToPixel(vlasnikId, relativnaPozicija, pieceIndex = 0) {
    // relPos 0 → home base
    if (relativnaPozicija === 0) {
        const [r, c] = HOME_POS[vlasnikId][pieceIndex % 4];
        return cellCenter(r, c);
    }
    // relPos 1–51 → main board (convert to shared absolute position first)
    if (relativnaPozicija >= 1 && relativnaPozicija <= 51) {
        const abs = (POCETNE_POZICIJE[vlasnikId] + relativnaPozicija - 2) % 52 + 1;
        const [r, c] = ABS_TO_GRID[abs];
        return cellCenter(r, c);
    }
    // relPos 52–56 → home stretch
    if (relativnaPozicija >= 52 && relativnaPozicija <= 56) {
        const [r, c] = HOME_STRETCH[vlasnikId][relativnaPozicija - 52];
        return cellCenter(r, c);
    }
    // relPos 57 → player's colored triangle inside the centre star.
    // Each triangle's centroid sits one cell from the centre in the player's approach direction:
    //   Red  enters from the left  → (7, 6)
    //   Blue enters from the top   → (6, 7)
    //   Yellow enters from right   → (7, 8)
    //   Green enters from bottom   → (8, 7)
    const GOAL_CELLS = [[7,6],[6,7],[7,8],[8,7]];
    const [gr, gc] = GOAL_CELLS[vlasnikId];
    return cellCenter(gr, gc);
}

// ─── Board drawing ────────────────────────────────────────────────────────

function fillCell(row, col, color) {
    ctx.fillStyle = color;
    ctx.fillRect(col * CELL + 0.5, row * CELL + 0.5, CELL - 1, CELL - 1);
}

function borderCell(row, col) {
    ctx.strokeStyle = '#b8a888';
    ctx.lineWidth = 0.5;
    ctx.strokeRect(col * CELL + 0.5, row * CELL + 0.5, CELL - 1, CELL - 1);
}

function drawBoard() {
    ctx.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

    // 1. Board background (parchment)
    ctx.fillStyle = '#c8b98a';
    ctx.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

    // 2. Home corner areas (6×6 each)
    HOME_AREAS.forEach(([r0, c0], i) => {
        const color = PLAYER_COLORS[i];

        // Tinted fill
        ctx.fillStyle = color + '44';
        ctx.fillRect(c0 * CELL, r0 * CELL, 6 * CELL, 6 * CELL);

        // Large coloured circle inside the corner
        ctx.fillStyle = color + '88';
        ctx.beginPath();
        ctx.arc((c0 + 3) * CELL, (r0 + 3) * CELL, 2.0 * CELL, 0, Math.PI * 2);
        ctx.fill();

        // 4 piece slot circles
        HOME_POS[i].forEach(([r, c]) => {
            const { x, y } = cellCenter(r, c);
            ctx.beginPath();
            ctx.arc(x, y, CELL * 0.33, 0, Math.PI * 2);
            ctx.fillStyle = '#ffffff55';
            ctx.fill();
            ctx.strokeStyle = color;
            ctx.lineWidth = 1.5;
            ctx.stroke();
        });

        // Corner border
        ctx.strokeStyle = color;
        ctx.lineWidth = 2;
        ctx.strokeRect(c0 * CELL + 1, r0 * CELL + 1, 6 * CELL - 2, 6 * CELL - 2);
    });

    // 3. Cross path cells (white), skipping the centre 3×3 (drawn separately)
    for (let r = 0; r < 15; r++) {
        for (let c = 0; c < 15; c++) {
            const onCross  = (r >= 6 && r <= 8) || (c >= 6 && c <= 8);
            const inCentre = (r >= 6 && r <= 8) && (c >= 6 && c <= 8);
            if (onCross && !inCentre) {
                fillCell(r, c, '#f5f0e8');
                borderCell(r, c);
            }
        }
    }

    // 4. Home-stretch cells (player colour)
    HOME_STRETCH.forEach((squares, i) => {
        squares.forEach(([r, c]) => {
            fillCell(r, c, PLAYER_COLORS[i] + 'cc');
            borderCell(r, c);
        });
    });

    // 5. Player starting squares (solid colour)
    POCETNE_POZICIJE.forEach((abs, i) => {
        const [r, c] = ABS_TO_GRID[abs];
        fillCell(r, c, PLAYER_COLORS[i]);
        borderCell(r, c);
    });

    // 6. Centre goal area
    drawCentre();

    // 7. Star markers — distinct gold background so safe status is clear even under a piece
    STAR_ABS.forEach(abs => {
        const [r, c] = ABS_TO_GRID[abs];
        const { x, y } = cellCenter(r, c);
        fillCell(r, c, '#fff5b0');  // gold cell background
        borderCell(r, c);
        ctx.font = `bold ${Math.round(CELL * 0.52)}px sans-serif`;
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillStyle = '#c8900a';
        ctx.fillText('★', x, y);
    });
}

function drawCentre() {
    // The centre 3×3 occupies canvas coords [6·CELL … 9·CELL] × [6·CELL … 9·CELL].
    const x0 = 6 * CELL, y0 = 6 * CELL;
    const x1 = 9 * CELL, y1 = 9 * CELL;
    const cx = 7.5 * CELL, cy = 7.5 * CELL;

    // 4 coloured triangles, one per player, pointing toward the shared goal.
    // Each player's triangle covers the side their home stretch enters from.
    [
        { color: PLAYER_COLORS[0], pts: [[x0,y0],[x0,y1],[cx,cy]] }, // Red   — left
        { color: PLAYER_COLORS[1], pts: [[x0,y0],[x1,y0],[cx,cy]] }, // Blue  — top
        { color: PLAYER_COLORS[2], pts: [[x1,y0],[x1,y1],[cx,cy]] }, // Yellow — right
        { color: PLAYER_COLORS[3], pts: [[x0,y1],[x1,y1],[cx,cy]] }, // Green  — bottom
    ].forEach(({ color, pts }) => {
        ctx.fillStyle = color;
        ctx.beginPath();
        ctx.moveTo(pts[0][0], pts[0][1]);
        ctx.lineTo(pts[1][0], pts[1][1]);
        ctx.lineTo(pts[2][0], pts[2][1]);
        ctx.closePath();
        ctx.fill();
    });

    // Goal circle
    ctx.beginPath();
    ctx.arc(cx, cy, CELL * 0.52, 0, Math.PI * 2);
    ctx.fillStyle = '#fff9e0';
    ctx.fill();
    ctx.strokeStyle = '#c8a000';
    ctx.lineWidth = 2;
    ctx.stroke();

    ctx.font = `bold ${Math.round(CELL * 0.45)}px sans-serif`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillStyle = '#c8a000';
    ctx.fillText('★', cx, cy);
}

// ─── Piece rendering ──────────────────────────────────────────────────────

/**
 * Draw a single piece at explicit pixel coordinates.
 * All other draw functions go through here.
 */
function drawOnePieceAt(figura, x, y, radius = CELL * 0.28) {
    const color = PLAYER_COLORS[figura.vlasnikId];
    const idx   = figura.id % 4;

    // Gold safe-field halo (star squares only — visible even when stacked)
    if (STAR_ABS.has(figura.apsolutnaPozicija)) {
        ctx.beginPath();
        ctx.arc(x, y, radius + 5, 0, Math.PI * 2);
        ctx.strokeStyle = '#e8b800';
        ctx.lineWidth = 3.5;
        ctx.stroke();
    }

    // Drop shadow
    ctx.beginPath();
    ctx.arc(x + 1.5, y + 1.5, radius, 0, Math.PI * 2);
    ctx.fillStyle = '#00000038';
    ctx.fill();

    // Body
    ctx.beginPath();
    ctx.arc(x, y, radius, 0, Math.PI * 2);
    ctx.fillStyle = color;
    ctx.fill();

    // Ring: thick gold for finished pieces, thin white otherwise
    ctx.strokeStyle = figura.status === 'ZAVRSENA' ? '#ffd700' : '#ffffffcc';
    ctx.lineWidth   = figura.status === 'ZAVRSENA' ? 2.5 : 1.5;
    ctx.stroke();

    // Piece-index label
    ctx.fillStyle = '#fff';
    ctx.font = `bold ${Math.round(radius * 0.78)}px sans-serif`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(idx, x, y);
}

/** Wrapper used by animateMove for static pieces (no stacking logic needed during flight). */
function drawOnePiece(figura) {
    const idx = figura.id % 4;
    const { x, y } = relPosToPixel(figura.vlasnikId, figura.relativnaPozicija, idx);
    drawOnePieceAt(figura, x, y);
}

/**
 * Compute (dx, dy) offset for stacked pieces.
 * Pieces at the same pixel coordinate get spread into a small cluster.
 */
function stackOffset(total, i) {
    if (total <= 1) return { dx: 0, dy: 0 };
    const d = CELL * 0.20;
    if (total === 2) return i === 0 ? { dx: -d, dy: 0 } : { dx: d, dy: 0 };
    if (total === 3) {
        return [{ dx: -d, dy: d * 0.5 }, { dx: d, dy: d * 0.5 }, { dx: 0, dy: -d * 0.85 }][i] || { dx: 0, dy: 0 };
    }
    // 4+ pieces: 2×2 grid
    return { dx: (i % 2 - 0.5) * d * 1.4, dy: (Math.floor(i / 2) - 0.5) * d * 1.4 };
}

function drawPieces(figuraArray) {
    if (!figuraArray) return;
    _prevFigure = figuraArray.map(f => ({ ...f }));
    drawBoard();

    // Compute base pixel positions for every piece
    const entries = figuraArray.map(f => {
        const { x, y } = relPosToPixel(f.vlasnikId, f.relativnaPozicija, f.id % 4);
        return { f, x, y };
    });

    // Group by rounded coordinate to detect stacking
    const groups = {};
    entries.forEach(e => {
        const key = `${Math.round(e.x)},${Math.round(e.y)}`;
        (groups[key] = groups[key] || []).push(e);
    });

    // Draw with stacking offsets; smaller radius when crowded
    entries.forEach(e => {
        const key   = `${Math.round(e.x)},${Math.round(e.y)}`;
        const group = groups[key];
        const i     = group.indexOf(e);
        const total = group.length;
        const { dx, dy } = stackOffset(total, i);
        const radius = total > 1 ? CELL * 0.21 : CELL * 0.28;
        drawOnePieceAt(e.f, e.x + dx, e.y + dy, radius);
    });
}

// ─── Candidate highlighting ───────────────────────────────────────────────

/**
 * Draws coloured rings on all valid target squares.
 * Call AFTER drawPieces() so rings render on top of the board.
 */
function highlightCandidates(kandidatPotezi) {
    if (!kandidatPotezi) return;
    kandidatPotezi.filter(p => p.validan).forEach(p => {
        const { x, y } = relPosToPixel(p.vlasnikId, p.novaRelativnaPozicija);
        const col = priorityColor(p.prioritet);

        // Glow halo
        ctx.beginPath();
        ctx.arc(x, y, CELL * 0.44, 0, Math.PI * 2);
        ctx.strokeStyle = col + '55';
        ctx.lineWidth = 7;
        ctx.stroke();

        // Inner ring
        ctx.beginPath();
        ctx.arc(x, y, CELL * 0.38, 0, Math.PI * 2);
        ctx.strokeStyle = col;
        ctx.lineWidth = 2.5;
        ctx.stroke();
    });
}

function priorityColor(p) {
    return (
        p === 1 ? '#27ae60' :
        p === 2 ? '#2980b9' :
        p === 3 ? '#8e44ad' :
        p === 4 ? '#f39c12' :
        p === 5 ? '#e67e22' :
        p === 6 ? '#c0392b' :
        '#7f8c8d'
    );
}

// ─── Move animation ───────────────────────────────────────────────────────

/**
 * Smoothly moves figuraId from its *previous* position to novaRelPos.
 *
 * The caller (game.js) passes newFiguraArray, which already contains the
 * updated server state. This function reads _prevFigure (saved by the last
 * drawPieces call) for the "from" coordinate, so both old and new states
 * are available without modifying game.js.
 *
 * During flight other pieces stay at their OLD positions; on landing the
 * whole board snaps to newFiguraArray via drawPieces().
 */
function animateMove(figuraId, novaRelPos, newFiguraArray) {
    return new Promise(resolve => {
        const oldFig = _prevFigure && _prevFigure.find(f => f.id === figuraId);

        // No previous state or piece didn't actually move — just snap
        if (!oldFig || oldFig.relativnaPozicija === novaRelPos) {
            drawPieces(newFiguraArray);
            resolve();
            return;
        }

        const idx   = figuraId % 4;
        const from  = relPosToPixel(oldFig.vlasnikId, oldFig.relativnaPozicija, idx);
        const to    = relPosToPixel(oldFig.vlasnikId, novaRelPos, idx);
        const color = PLAYER_COLORS[oldFig.vlasnikId];

        // All pieces except the moving one, at their OLD positions for the duration
        const staticPieces = (_prevFigure || []).filter(f => f.id !== figuraId);

        const DURATION = 420; // ms
        const tStart   = performance.now();

        function frame(now) {
            const raw  = Math.min((now - tStart) / DURATION, 1);
            // Ease-in-out quadratic
            const ease = raw < 0.5 ? 2 * raw * raw : -1 + (4 - 2 * raw) * raw;

            drawBoard();
            staticPieces.forEach(f => drawOnePiece(f));

            const mx = from.x + (to.x - from.x) * ease;
            const my = from.y + (to.y - from.y) * ease;

            // Pop scale: 1.0 → 1.3 → 1.0 during the last 20 % of travel
            const popT  = Math.max(0, (raw - 0.8) / 0.2);
            const scale = 1 + 0.3 * Math.sin(popT * Math.PI);
            const r     = CELL * 0.3 * scale;

            // Shadow
            ctx.beginPath();
            ctx.arc(mx + 2, my + 2, r, 0, Math.PI * 2);
            ctx.fillStyle = '#00000050';
            ctx.fill();

            // Body
            ctx.beginPath();
            ctx.arc(mx, my, r, 0, Math.PI * 2);
            ctx.fillStyle = color;
            ctx.fill();
            ctx.strokeStyle = '#ffffffcc';
            ctx.lineWidth = 2;
            ctx.stroke();

            // Label
            ctx.fillStyle = '#fff';
            ctx.font = `bold ${Math.round(CELL * 0.22 * scale)}px sans-serif`;
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(idx, mx, my);

            if (raw < 1) {
                requestAnimationFrame(frame);
            } else {
                // Snap entire board to the new server state
                drawPieces(newFiguraArray);
                resolve();
            }
        }

        requestAnimationFrame(frame);
    });
}
