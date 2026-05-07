const EPS0 = 8.854187817e-12;

const toM = (cm) => cm / 100;
const mmToM = (mm) => mm / 1000;
const cm2ToM2 = (cm2) => cm2 / 10000;

function computeFlat() {
    const S = parseFloat(document.getElementById('flatSquareNum').value);
    const eps = parseFloat(document.getElementById('flatEpsilonNum').value);
    const d = parseFloat(document.getElementById('flatDistanceNum').value);

    const C = (EPS0 * eps * cm2ToM2(S)) / mmToM(d);
    document.getElementById('flatCapValue').innerText = (C * 1e12).toFixed(2);

    const side = Math.sqrt(S) * 4 + 40;
    const topPlate = document.getElementById('flatTopPlate');
    const bottomPlate = document.getElementById('flatBottomPlate');

    if (topPlate && bottomPlate) {
        topPlate.setAttribute('width', side);
        topPlate.setAttribute('height', side);
        bottomPlate.setAttribute('width', side);
        bottomPlate.setAttribute('height', side);

        const offset = 10 + d * 3;
        bottomPlate.setAttribute('x', 65 - offset);
        bottomPlate.setAttribute('y', 20 + offset);
    }
}

function computeCyl() {
    const L = parseFloat(document.getElementById('cylLengthNum').value);
    const R1 = parseFloat(document.getElementById('cylR1Num').value);
    const R2 = parseFloat(document.getElementById('cylR2Num').value);
    const warn = document.getElementById('cylWarning');

    if (R2 <= R1) {
        if (warn) warn.innerText = "R2 должен быть больше R1";
        document.getElementById('cylCapValue').innerText = "---";
        return;
    }
    if (warn) warn.innerText = "";

    const C = (2 * Math.PI * EPS0 * toM(L)) / Math.log(R2 / R1);
    document.getElementById('cylCapValue').innerText = (C * 1e12).toFixed(2);

    const centerX = 110;
    const topY = 30;
    const scaleR = 6;
    const scaleL = 4;
    const currentHeight = L * scaleL;
    const bottomY = topY + currentHeight;

    const rx1 = R1 * scaleR;
    const ry1 = rx1 * 0.3;
    const rx2 = R2 * scaleR;
    const ry2 = rx2 * 0.3;

    updateElement('outer-top', { cx: centerX, cy: topY, rx: rx2, ry: ry2 });
    updateElement('outer-bottom', { cx: centerX, cy: bottomY, rx: rx2, ry: ry2 });
    updateElement('outer-body', { x: centerX - rx2, y: topY, width: rx2 * 2, height: currentHeight });
    updateElement('inner-top', { cx: centerX, cy: topY, rx: rx1, ry: ry1 });
    updateElement('inner-bottom', { cx: centerX, cy: bottomY, rx: rx1, ry: ry1 });
    updateElement('inner-body', { x: centerX - rx1, y: topY, width: rx1 * 2, height: currentHeight });
    updateElement('axis-line', { y2: bottomY + 20 });
}

function updateElement(id, attrs) {
    const el = document.getElementById(id);
    if (el) {
        for (let key in attrs) {
            el.setAttribute(key, attrs[key]);
        }
    }
}

function computeSphere() {
    const R1 = parseFloat(document.getElementById('sphR1Num').value);
    const R2 = parseFloat(document.getElementById('sphR2Num').value);
    const warn = document.getElementById('sphWarning');

    if (R2 <= R1) {
        if (warn) warn.innerText = "R2 должен быть больше R1";
        document.getElementById('sphCapValue').innerText = "---";
        return;
    }
    if (warn) warn.innerText = "";

    const C = 4 * Math.PI * EPS0 * (toM(R1) * toM(R2)) / (toM(R2) - toM(R1));
    document.getElementById('sphCapValue').innerText = (C * 1e12).toFixed(2);

    const centerX = 95;
    const scale = 8;
    const inner = document.getElementById('innerSphere');
    const outer = document.getElementById('outerSphere');
    const orbit = document.getElementById('outerSphereOrbit');
    const axis = document.getElementById('sphAxisLine');

    if (inner) {
        inner.setAttribute('r', R1 * scale + 10);
    }

    if (outer && orbit && axis) {
        const finalR2 = R2 * scale + 15;
        outer.setAttribute('r', finalR2);
        orbit.setAttribute('rx', finalR2);
        orbit.setAttribute('ry', finalR2 * 0.33);
        axis.setAttribute('x1', centerX - finalR2);
        axis.setAttribute('x2', centerX + finalR2);
    }
}

function setupBinding(sliderId, numId, callback) {
    const s = document.getElementById(sliderId), n = document.getElementById(numId);
    if (s && n) {
        s.addEventListener('input', () => { n.value = s.value; callback(); });
        n.addEventListener('input', () => { s.value = n.value; callback(); });
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const dielectricSelect = document.getElementById('dielectricMaterial');
    if (dielectricSelect) {
        dielectricSelect.addEventListener('change', function() {
            const val = this.value;
            document.getElementById('flatEpsilonNum').value = val;
            document.getElementById('flatEpsilonSlider').value = val;
            computeFlat();
        });
    }

    setupBinding('flatSquareSlider', 'flatSquareNum', computeFlat);
    setupBinding('flatEpsilonSlider', 'flatEpsilonNum', computeFlat);
    setupBinding('flatDistanceSlider', 'flatDistanceNum', computeFlat);

    setupBinding('cylLengthSlider', 'cylLengthNum', computeCyl);
    setupBinding('cylR1Slider', 'cylR1Num', computeCyl);
    setupBinding('cylR2Slider', 'cylR2Num', computeCyl);

    setupBinding('sphR1Slider', 'sphR1Num', computeSphere);
    setupBinding('sphR2Slider', 'sphR2Num', computeSphere);

    computeFlat();
    computeCyl();
    computeSphere();
});
