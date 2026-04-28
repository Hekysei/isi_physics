# Простой handoff для миграции

## Цель

Перенести проект Кирхгофа в общий Spring Boot репозиторий как feature-модуль.

Важно:
- в системе остаётся один общий `@SpringBootApplication`;
- Кирхгоф больше не отдельный сервис;
- его auth и storage считаются частью этой feature.

## Коротко о принципе

Есть общая основа проекта.  
Новый модуль добавляется по шаблону: переносим свои файлы в свою feature-папку и подключаем маршруты.

Минимум изменений, только обязательные для встраивания в общий проект.

## Что переносить в первую очередь

Сначала переносим стабильное ядро (model + тесты):
- `model/KirchhoffCircuitModel`
- `model/KirchhoffCircuitData`
- `model/KirchhoffCircuitResults`
- `model/KirchhoffSimulationOutcome`
- `model/KirchhoffErrorRate`
- `KirchhoffCircuitModelTest`

Потом переносим адаптеры:
- UI controller;
- REST controller и response DTO;
- templates/fragments;
- css/js.

## Что не переносится как standalone

Не тащим в feature как есть:
- `KirchhoffApplication`;
- `server.port`;
- `server.servlet.context-path=/kirchhoff`;
- `spring.application.name`;
- глобальные auth настройки.

Это всё должно оставаться на уровне общего приложения.

## Обязательные правила при переносе

- У каждой feature свой namespace (packages, templates, static).
- Auth одной feature не должен влиять на другие feature.
- Не использовать глобальный `context-path` для feature.
- Shared слой менять только если это действительно нужно нескольким модулям.

## Простой порядок миграции

1. Создать пакет `features/kirchhoff`.
2. Перенести `model` и тесты.
3. Перенести `ui/api/templates/static`.
4. Адаптировать auth внутри этой feature.
5. Убрать standalone настройки.
6. Проверить, что ничего не конфликтует с другими feature.

## Что проверить после переноса

- модель и тесты проходят;
- маршруты feature не пересекаются с другими;
- шаблоны и статика не перекрывают чужие файлы;
- security-правила не открывают/не ломают чужие маршруты;
- storage/auth остаются изолированными внутри feature.

## Готовый результат

Считаем миграцию успешной, если:
- Кирхгоф работает внутри общего приложения;
- добавлен как обычная feature;
- не требует отдельного запуска или специальных глобальных настроек;
- следующий проект можно добавить по той же схеме.
