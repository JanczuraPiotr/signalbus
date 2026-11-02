# SignalBus

## Baza danych i migracje

Aplikacja korzysta z PostgreSQL i Flyway. Domyślna konfiguracja połączenia:

- adres: `jdbc:postgresql://localhost:5432/signalbus`
- użytkownik: `signalbys`
- hasło: `password`
- schemat: `public`

Przed pierwszym uruchomieniem utwórz użytkownika i bazę, wykonując jako
administrator PostgreSQL (np. przez `sudo -u postgres psql`):

```sql
CREATE ROLE signalbys LOGIN PASSWORD 'password';
CREATE DATABASE signalbus OWNER signalbys;
```

Jeśli baza lub użytkownik już istnieją, pomiń odpowiednie polecenie.
Użytkownik `signalbys` musi mieć uprawnienia `USAGE` i `CREATE` do schematu
`public` oraz możliwość utworzenia rozszerzenia `uuid-ossp`.

Konfigurację można nadpisać zmiennymi `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
lub standardowymi `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`
i `SPRING_DATASOURCE_PASSWORD`. Poza środowiskiem lokalnym ustaw własne dane
dostępowe.

Flyway korzysta z tego samego połączenia i automatycznie wykonuje migracje
z `src/main/resources/db/migration` podczas startu aplikacji w schemacie
`public`. Pierwsza migracja tworzy rozszerzenie `uuid-ossp` oraz tabelę
`console`. Historię wykonanych migracji zapisuje w
`public.flyway_schema_history`.

Kolejne migracje dodawaj jako pliki `V2__opis_zmiany.sql`,
`V3__opis_zmiany.sql` itd. Nie zmieniaj migracji już wykonanych w bazie.

## Uruchamianie lokalne

```bash
./gradlew bootRun
```

Zadanie `bootRun` domyślnie używa profilu Spring `dev`, który włącza tryb
developerski Vaadin oraz Copilot. Jawnie ustawiony profil aktywny ma pierwszeństwo.
Przy uruchamianiu klasy `SignalbusApplication` bezpośrednio z IntelliJ ustaw
aktywny profil `dev` albo argument programu `--spring.profiles.active=dev`.

Otwórz http://localhost:8080/app/ lub http://localhost:8080/app/panel,
aby korzystać z panelu developerskiego Vaadin. Strona główna `/`, logowanie
i kreator są renderowane przez Thymeleaf i nie wyświetlają panelu Vaadin.

Jeśli korzystasz z BrowserSync na porcie 3000, uruchamiaj jego proxy z opcją
`--ws`, aby przekazywało również połączenia WebSocket panelu developerskiego:

```bash
npx browser-sync start --proxy "http://localhost:8080" --ws --files "src/main/resources/message*.properties" "src/main/resources/static/**/*.css" "src/main/resources/templates/**/*.html" --port 3000 --no-open
```

Bez `--ws` widoki mogą działać poprawnie, ale połączenie
`/app/VAADIN/push?...&debug_window...` nie zostanie zestawione i panel Copilot
może pozostać niewidoczny.

Konfiguracja bazowa pozostaje w trybie produkcyjnym. Uruchomienie przez
`java -jar` nie otrzymuje domyślnego profilu `dev` z zadania `bootRun`.

## Testy

```bash
./gradlew test
```

Gradle automatycznie aktywuje profil `test`. Testy z `@SpringBootTest` mają
również `@ActiveProfiles("test")`, aby korzystać z niego przy uruchamianiu z IDE.
Profil uruchamia tymczasową bazę PostgreSQL 17 przez Testcontainers, z migracjami
Flyway. Wymaga działającego Dockera dostępnego dla użytkownika uruchamiającego
testy; obraz `postgres:17-alpine` jest pobierany przy pierwszym uruchomieniu.
Baza kontenera jest usuwana po testach i nie korzysta z developerskich zmiennych
`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. Nie ustawiaj dla testów zewnętrznych
nadpisań `SPRING_DATASOURCE_*` ani `SPRING_FLYWAY_*` wskazujących bazę developerską.

Serwer HTTP i ewentualny serwer zarządzania używają losowych wolnych portów.
Copilot, serwer developerski Vaadin, automatyczne otwieranie przeglądarki
oraz DevTools restart/LiveReload są wyłączone w profilu testowym.

Aby uruchomić aplikację z zależnościami testowymi w odseparowanym środowisku:

```bash
./gradlew bootTestRun --continuous
```

Zadanie `bootTestRun` automatycznie aktywuje profil `test`, korzysta z tymczasowej
bazy i losowego portu HTTP (numer portu jest wypisywany w logach). Uruchamia
aplikację, a nie testy JUnit; do wykonania testów służy `./gradlew test`.
Kontener bazy działa do zakończenia aplikacji.
