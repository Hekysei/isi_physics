// Элементы управления
const nI0 = document.getElementById('n-i0'), rI0 = document.getElementById('r-i0');
const nPhi = document.getElementById('n-phi'), rPhi = document.getElementById('r-phi');
const nDecimals = document.getElementById('n-decimals');
const btnRound = document.getElementById('btn-round');
let lastIa = null;

function getDecimals() {
    return Math.max(0, Math.min(6, parseInt(nDecimals.value) || 0));
}

function displayResult(iA) {
    document.getElementById('val-i').innerText = iA.toFixed(getDecimals());
}

// Элементы стенда
const rimP = document.getElementById('rim-p');
const rimA = document.getElementById('rim-a');
const beam1 = document.getElementById('beam-1');
const beam2 = document.getElementById('beam-2');
const beam3 = document.getElementById('beam-3');
const bCore1 = beam1 ? beam1.querySelector('.beam-core') : null;
const bCore2 = beam2 ? beam2.querySelector('.beam-core') : null;
const bEnd = beam3 ? beam3.querySelector('.beam-core') : null;
const statusText = document.getElementById('status');
const btnClear = document.getElementById('btn-clear');

let historyData = {};
const ctx = document.getElementById('mainChart').getContext('2d');
const chart = new Chart(ctx, {
    type: 'line',
    data: { labels: [], datasets: [{ label: 'Iₐ(φ)', data: [], borderColor: '#3b82f6', backgroundColor: 'rgba(59, 130, 246, 0.15)', borderWidth: 3, fill: true, tension: 0.4, pointRadius: 2 }] },
    options: { interaction: { mode: 'index', intersect: false }, scales: { y: { min: 0, grid: { color: '#1e293b' }, title: { display: true, text: 'Iₐ' } }, x: { grid: { display: false }, title: { display: true, text: 'φ (°)' } } }, plugins: { legend: { display: false } }, animation: false }
});

// Запрос расчёта на сервере и обновление UI
function recalculate() {
    const i0 = parseFloat(nI0.value) || 0;
    const phi = parseFloat(nPhi.value) || 0;

    // Вращение анализатора
    if (rimA) rimA.style.transform = 'rotate(' + phi + 'deg)';

    fetch('/api/calculate?intensity0=' + encodeURIComponent(i0) + '&phi=' + encodeURIComponent(phi), { cache: 'no-store' })
        .then(function(resp) { return resp.json(); })
        .then(function(data) {
            var iA = data.result;
            lastIa = iA;
            displayResult(iA);

            // Прозрачность лучей до анализатора (зависит от I₀)
            var maxI0 = parseFloat(rI0.max) || 1000;
            var beamOpacity = i0 > 0 ? Math.max(0.05, i0 / maxI0) : 0;
            if (beam1) beam1.style.opacity = beamOpacity;
            if (beam2) beam2.style.opacity = beamOpacity;

            // Прозрачность луча после анализатора (зависит от I_A)
            var maxIntensity = 0.5 * i0;
            if (beam3) beam3.style.opacity = maxIntensity > 0 ? iA / maxIntensity * beamOpacity : 0;
            historyData[phi] = iA;
            var sorted = Object.keys(historyData).sort(function(x, y) { return parseFloat(x) - parseFloat(y); });
            chart.data.labels = sorted.map(function(d) { return d + '°'; });
            chart.data.datasets[0].data = sorted.map(function(d) { return historyData[d]; });
            chart.options.scales.y.max = Math.max(i0 * 0.6, 1);
            chart.update();

            // Статус
            var phiMod = phi % 180;
            if (phiMod > 85 && phiMod < 95) statusText.innerHTML = "🎯 φ ≈ 90°: <span style='color:var(--gold)'>Интенсивность минимальна (I_A ≈ 0)</span>";
            else if (phiMod < 5 || phiMod > 175) statusText.innerHTML = "🎯 φ ≈ 0°: <span style='color:#22c55e'>Интенсивность максимальна (I_A = ½I₀)</span>";
            else statusText.innerText = "φ = " + phi + "° → I_A = " + iA.toFixed(getDecimals());
        });
}

btnClear.onclick = function() { historyData = {}; chart.data.labels = []; chart.data.datasets[0].data = []; chart.update(); };

nDecimals.addEventListener('input', function() {
    if (lastIa !== null) displayResult(lastIa);
});

btnRound.onclick = function() {
    nDecimals.value = 0;
    if (lastIa !== null) displayResult(lastIa);
};

// Связь ползунков и числовых полей
function bindInputs(slider, numberInput) {
    slider.addEventListener('input', function() { numberInput.value = slider.value; recalculate(); });
    numberInput.addEventListener('input', function() { slider.value = numberInput.value; recalculate(); });
}

bindInputs(rI0, nI0);
bindInputs(rPhi, nPhi);

// Синхронизация ползунков с полями формы при загрузке (Thymeleaf задаёт value полей)
rI0.value = nI0.value;
rPhi.value = nPhi.value;

// Drag-вращение анализатора мышкой
(function() {
    if (!rimA) return;
    var dragging = false;
    var startAngle = 0;
    var startPhi = 0;

    function getAngle(e) {
        var rect = rimA.getBoundingClientRect();
        var cx = rect.left + rect.width / 2;
        var cy = rect.top + rect.height / 2;
        return Math.atan2(e.clientY - cy, e.clientX - cx) * 180 / Math.PI;
    }

    rimA.style.cursor = 'grab';

    rimA.addEventListener('mousedown', function(e) {
        e.preventDefault();
        dragging = true;
        startAngle = getAngle(e);
        startPhi = parseFloat(nPhi.value) || 0;
        rimA.style.cursor = 'grabbing';
    });

    document.addEventListener('mousemove', function(e) {
        if (!dragging) return;
        var current = getAngle(e);
        var delta = current - startAngle;
        var newPhi = startPhi + delta;
        // Нормализация в 0..360
        newPhi = ((newPhi % 360) + 360) % 360;
        newPhi = Math.round(newPhi);
        nPhi.value = newPhi;
        rPhi.value = newPhi;
        recalculate();
    });

    document.addEventListener('mouseup', function() {
        if (dragging) {
            dragging = false;
            rimA.style.cursor = 'grab';
        }
    });
})();

recalculate();

