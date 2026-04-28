function renderLabs(container, labs) {
    container.innerHTML = "";

    if (!Array.isArray(labs) || labs.length === 0) {
        const message = document.createElement("p");
        message.className = "empty-state";
        message.textContent = "Список лабораторных пока пуст.";
        container.append(message);
        return;
    }

    for (const lab of labs) {
        const link = document.createElement("a");
        link.className = "lab-link";
        link.href = lab.url;

        const studentName = document.createElement("strong");
        studentName.textContent = lab.student || "Студент";

        const title = document.createElement("span");
        title.textContent = lab.title || "Лабораторная работа";

        link.append(studentName, title);
        container.append(link);
    }
}

async function loadLabs() {
    const container = document.getElementById("labs");
    if (!container) {
        return;
    }

    try {
        const response = await fetch(`/api/labs?t=${Date.now()}`, { cache: "no-store" });
        if (!response.ok) {
            showLoadError(container);
            return;
        }

        const labs = await response.json();
        renderLabs(container, labs);
    } catch {
        showLoadError(container);
    }
}

function showLoadError(container) {
    const message = document.createElement("p");
    message.className = "empty-state";
    message.textContent = "Не удалось загрузить список лабораторных.";
    container.replaceChildren(message);
}

void loadLabs();
