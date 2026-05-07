package com.example.isib.bulletvelocity.model;

/**
 * Интерфейс сервиса физических расчетов.
 * Выполнение принципа Dependency Inversion (SOLID):
 * контроллер будет зависеть от абстракции, а не от конкретной реализацией.
 */
public interface PhysicsService {
    CalculationResponse calculateVelocity(CalculationRequest request);
}