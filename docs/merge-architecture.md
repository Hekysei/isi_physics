# Простая схема объединения проектов

## Главное правило

Есть **один общий Spring Boot проект** с одним `@SpringBootApplication`.
Каждый симулятор добавляется как отдельная feature-папка.

Идея простая:
- общая основа уже готова;
- разработчик добавляет свои файлы в свою feature-зону;
- всё должно запускаться без отдельного сервиса и без ручной настройки "под конкретный проект".

## Как делим код

### 1) Core (общее приложение)

Здесь только то, что реально общее:
- единый запуск приложения;
- общая навигация и базовые страницы;
- общие настройки приложения.

### 2) Shared (минимум общего кода)

Сюда переносим только то, что нужно минимум двум feature.

Если код нужен только одному симулятору, он остаётся внутри его feature.

### 3) Features (папки проектов)

Каждый проект живёт отдельно, например:
- `features/kirchhoff`
- `features/ohm`
- `features/optics`

Внутри feature находятся:
- бизнес-логика;
- контроллеры UI/API;
- шаблоны и статика;
- auth и storage (если они уникальны для этой feature);
- тесты этой feature.

## Шаблон новой feature

```text
features/<project>/
  domain/
  auth/
  ui/
  api/
  templates/
  static/
  config/
  test/
```

Цель: все проекты добавляются по одному и тому же шаблону.

## Что переносим из текущего проекта Кирхгофа

Переносим в `features/kirchhoff`:
- `model/*`
- `ui/KirchhoffSimulationController`
- `api/KirchhoffSimulationRestController`
- `api/KirchhoffSimulationResponse`
- `templates/kirchhoff-main.html`
- `templates/fragments/kirchhoff-*.html`
- `static/css/kirchhoff-style.css`
- `static/js/kirchhoff-script.js`
- `KirchhoffCircuitModelTest`

Это и есть основа feature.

## Что не должно жить как отдельное приложение

Не переносим как "самостоятельный сервис":
- `KirchhoffApplication`;
- `server.port`;
- `server.servlet.context-path=/kirchhoff`;
- `spring.application.name`;
- глобальные auth/storage настройки.

Это относится к общему приложению, не к feature.

## Простые правила интеграции

- У feature не должно быть своего глобального `context-path`.
- Маршруты и ресурсы должны быть в namespace feature (чтобы не конфликтовать с другими).
- Auth одной feature не должна ломать маршруты другой.
- Shared меняем только при реальной необходимости.

## Порядок добавления нового проекта

1. Создать новую feature-папку по шаблону.
2. Перенести туда `domain/auth/ui/api/resources`.
3. Убрать standalone bootstrap и standalone properties.
4. Проверить, что маршруты, шаблоны и статика не конфликтуют с другими feature.
5. Не трогать shared, если можно обойтись без этого.

## Что считается хорошим результатом

После переноса проект:
- работает как feature внутри общего приложения;
- не имеет своего `@SpringBootApplication`;
- не требует отдельного `context-path`;
- хранит свою специфичную auth-логику внутри feature;
- добавляется по тому же шаблону, что и остальные проекты.
