# Запуск проекта isi_physics-master

## Требования

- Java Development Kit (JDK) 17 или выше
- Apache Maven 3.9.x
- Windows или любая операционная система с доступом к командной строке

> В этой рабочей папке проект расположен в `c:\Users\Lev\Desktop\SLIT\isi_physics-master`.

## 1. Проверка окружения

Откройте PowerShell или командную строку и выполните:

```powershell
java -version
mvn -version
```

Если команда `java` или `mvn` не найдена, задайте переменные окружения `JAVA_HOME` и `PATH`.

## 2. Переход в директорию проекта

```powershell
cd "c:\Users\Lev\Desktop\SLIT\isi_physics-master"
```

## 3. Сборка проекта

Рекомендуется сначала собрать проект и проверить зависимости:

```powershell
C:\Java\apache-maven-3.9.15\bin\mvn.cmd clean package
```

Если Maven доступен в `PATH`, можно запустить короче:

```powershell
mvn clean package
```

## 4. Запуск приложения

Запустите проект через Spring Boot:

```powershell
C:\Java\apache-maven-3.9.15\bin\mvn.cmd spring-boot:run
```

или, если Maven уже настроен в системе:

```powershell
mvn spring-boot:run
```

## 5. Доступ к приложению

После старта приложение будет доступно в браузере по адресу:

- http://localhost:8080/

## 6. Важные модули и маршруты

В проекте доступны ключевые разделы:

- `/` — главный интерфейс
- `/bulletvelocity` — модуль скорости полёта пули
- `/maluslaw` — модуль закона Малуса
- `/capacity` — модуль электроёмкости конденсаторов
- `/resistance` — модуль температурного сопротивления
- `/findeds` — модуль ЭДС и внутреннего сопротивления
- `/kirchhoff` — модуль законов Кирхгофа
- `/interference` — модуль интерференции света
- `/myapp` — модуль закона Гука

## 7. Альтернативный запуск через JAR

После успешной сборки можно запустить JAR-файл:

```powershell
java -jar target\isib-1.0.0.jar
```

## 8. Остановка приложения

В PowerShell нажмите `Ctrl+C`.

## 9. Отладка и проверка

- Если приложение не стартует, проверьте вывод Maven на ошибки.
- Убедитесь, что нет другого процесса, занятого портом `8080`.
- Для повторного запуска выполните `mvn clean package` и затем `mvn spring-boot:run`.
