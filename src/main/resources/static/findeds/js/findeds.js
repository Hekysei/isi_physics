let currentData = {
    amperage: 6,
    inResistance: 400,
    outResistance: 1100,
    eds: 0,
    voltageOnLoad: 0,
    absoluteError: 0,
    withErrors: false
};
let isCircuitClosed = true;
let withErrors = false;

const circuitToggle = document.getElementById('circuit-toggle');
const errorToggle = document.getElementById('error-toggle');
const lever = document.getElementById('svg-switch-lever');
const bulb = document.getElementById('light-bulb');
const txtEds = document.getElementById('val-eds');
const txtRes = document.getElementById('val-res');
const txtAmp = document.getElementById('val-amp');
const txtVolt = document.getElementById('val-volt');
const edsDisplay = document.getElementById('eds-display');
const errorDisplay = document.getElementById('error-display');

const amperageInput = document.getElementById('amperage-input');
const inResistanceInput = document.getElementById('inResistance-input');
const outResistanceInput = document.getElementById('outResistance-input');

async function fetchCalculations() {
    if (!isCircuitClosed) return;

    const amperage = parseFloat(amperageInput.value);
    const inResistance = parseFloat(inResistanceInput.value);
    const outResistance = parseFloat(outResistanceInput.value);

    if (isNaN(amperage) || isNaN(inResistance) || isNaN(outResistance)) return;

    const params = new URLSearchParams();
    params.append('amperage', amperage);
    params.append('inResistance', inResistance);
    params.append('outResistance', outResistance);
    params.append('withErrors', withErrors);

    try {
        const response = await fetch('/findeds/api/calculate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        });

        if (!response.ok) {
            console.error('Ошибка HTTP:', response.status);
            return;
        }

        const data = await response.json();
        currentData = {
            amperage: data.amperage,
            inResistance: inResistance,
            outResistance: data.outResistance,
            eds: data.eds,
            voltageOnLoad: data.voltageOnLoad,
            absoluteError: data.absoluteError,
            withErrors: data.withErrors
        };
        updateDisplay();
    } catch (error) {
        console.error('Ошибка:', error);
    }
}

function updateDisplay() {
    if (isCircuitClosed) {
        txtEds.textContent = currentData.eds.toFixed(2) + ' В';
        txtRes.textContent = currentData.outResistance.toFixed(2) + ' Ом';
        txtAmp.textContent = currentData.amperage.toFixed(2) + ' А';
        txtVolt.textContent = currentData.voltageOnLoad.toFixed(2) + ' В';
        edsDisplay.textContent = currentData.eds.toFixed(2);
        bulb.style.fill = '#ffeb3b';
        errorDisplay.innerHTML = currentData.withErrors && currentData.absoluteError > 0
            ? `Абсолютная погрешность ЭДС: ± ${currentData.absoluteError.toFixed(2)} В`
            : '';
    } else {
        txtEds.textContent = '0.00 В';
        txtRes.textContent = currentData.outResistance.toFixed(2) + ' Ом';
        txtAmp.textContent = '0.00 А';
        txtVolt.textContent = '0.00 В';
        edsDisplay.textContent = '0.00';
        bulb.style.fill = 'lightgray';
        errorDisplay.innerHTML = '';
    }
}

function onToggleChange() {
    isCircuitClosed = circuitToggle.checked;
    if (isCircuitClosed) {
        lever.style.transform = 'rotate(0deg)';
        fetchCalculations();
    } else {
        lever.style.transform = 'rotate(-45deg)';
        updateDisplay();
    }
}

errorToggle.addEventListener('change', function() {
    withErrors = this.checked;
    if (isCircuitClosed) fetchCalculations();
});

circuitToggle.addEventListener('change', onToggleChange);
amperageInput.addEventListener('input', () => isCircuitClosed && fetchCalculations());
inResistanceInput.addEventListener('input', () => isCircuitClosed && fetchCalculations());
outResistanceInput.addEventListener('input', () => isCircuitClosed && fetchCalculations());

amperageInput.value = currentData.amperage;
inResistanceInput.value = currentData.inResistance;
outResistanceInput.value = currentData.outResistance;

onToggleChange();
fetchCalculations();
