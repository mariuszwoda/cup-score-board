# Football World Cup Score Board

A Java-based in-memory sport score board tracking live match scores.

---

## Features

The board supports four operations:

| Operation | Description |
|---|---|
| **Start a game** | Registers a new match with an initial score of `0 – 0` |
| **Update score** | Updates the home and away score of a live game |
| **Finish a game** | Removes a game from the board |
| **Get summary** | Returns all live games ordered by total score (desc); ties broken by most recently started |

## Test Coverage

All production code is covered by unit tests with **100% coverage** across all service metrics.

Tests are organized using AssertJ semantics when possible and JUnit 5 `@Nested` classes with descriptive `@DisplayName` annotations, covering:
- Happy path scenarios
- Edge cases (null inputs, blank names, case-insensitivity)
- Exception handling (`GameNotFoundException`, `GameAlreadyExistsException`)


## Code Quality

Static analysis performed with **SonarQube** — no issues reported.  
The codebase follows clean code principles with immutable domain models, interface-based design, and meaningful exception hierarchy.

---

## Getting Started

### Prerequisites

- Java 25+
- Maven 3.8+ (or use the included `./mvnw` wrapper)
- Junit 5 / AssertJ

### Run tests

```bash
./mvnw test
```
### Use as a dependency

Install to your local Maven repository:

```bash
./mvnw install
```

Then add to your project's `pom.xml`:

```xml
<dependency>
    <groupId>pl.where2play</groupId>
    <artifactId>cup-score-board</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Usage example

```java
ScoreBoard board = new InMemoryScoreBoard();

// Start games
board.startGame("Mexico",    "Canada");
board.startGame("Spain",     "Brazil");
board.startGame("Germany",   "France");
board.startGame("Uruguay",   "Italy");
board.startGame("Argentina", "Australia");

// Update scores
board.updateScore("Mexico",    "Canada",    0, 5);
board.updateScore("Spain",     "Brazil",   10, 2);
board.updateScore("Germany",   "France",    2, 2);
board.updateScore("Uruguay",   "Italy",     6, 6);
board.updateScore("Argentina", "Australia", 3, 1);

// Get summary
List<Game> summary = board.getSummary();
// 1. Uruguay   6 – Italy     6
// 2. Spain    10 – Brazil    2
// 3. Mexico    0 – Canada    5
// 4. Argentina 3 – Australia 1
// 5. Germany   2 – France    2

// Finish a game
board.finishGame("Mexico", "Canada");
```

### Using a custom store

The default `InMemoryScoreBoard` uses `LinkedHashMapGameStore` internally.
You can provide your own `GameStore` implementation — for example, a
thread-safe store or a JDBC-backed one — without touching the board logic:

```java
GameStore myStore = new MyCustomGameStore();
ScoreBoard board  = new InMemoryScoreBoard(myStore);
```

---

## Design Decisions

### Immutability
`Team`, `Score`, and `Game` are all immutable — `Team` and `Score` as Java records, `Game` as a final class with a `withScore(Score)` copy-constructor pattern. This eliminates accidental mutation and makes the domain model easy to reason about.

### In-memory store
`InMemoryScoreBoard` uses a `LinkedHashMap<GameKey, Game>` which preserves **insertion order** for deterministic iteration.

### Ordering in `getSummary`
Games are sorted by **total goals descending**. When two games share the same total, the game with the **most recent `startedAt` timestamp** appears first. This is an O(n log n) sort using `Game#startedAt()` as the tiebreaker — avoiding the O(n² log n) `indexOf`-based approach.

### Exception hierarchy
All domain exceptions (`GameNotFoundException`, `GameAlreadyExistsException`) extend `ScoreBoardException`, which itself extends `RuntimeException`. This lets callers catch the entire hierarchy with a single handler when fine-grained handling is not needed.

### Case-insensitive team names
`GameKey` normalises all team names to trimmed upper-case, so `"spain"`, `"Spain"`, and `"SPAIN"` all refer to the same team in any scoreboard operation.

### Score corrections
`updateScore` allows a score to be set to **any non-negative value**, including lower than the current score. This supports score corrections (e.g. reverting a wrongly entered goal). The specification does not restrict this, and disallowing it would add accidental complexity.

### No Spring coupling in the library
`InMemoryScoreBoard` is a plain POJO — no `@Service` or other Spring annotations. This keeps the library framework-agnostic. Callers can instantiate it directly or wire it into any DI container they prefer.

### Thread safety
`InMemoryScoreBoard` is **not thread-safe** by design. The specification describes a simple library without concurrency requirements. Adding synchronisation would be straightforward (e.g. wrapping operations with `synchronized` or using `ConcurrentHashMap`) and is left as a potential extension.

---

## Assumptions

1. A team may only participate in **one live game at a time** (duplicate fixture check is on `homeTeam + awayTeam` pair).
2. Team name comparison is **case-insensitive** and **whitespace-trimmed**.
3. **Score corrections downward** are allowed (e.g. changing `5` to `4` to fix a mistake).
4. After a game is finished it may be **restarted** as a new entry.
5. The `getSummary` list is **unmodifiable** — callers receive a snapshot, not a live view.

---
## Potential Extensions

- **Persistence adapter** — implement `ScoreBoard` backed by a database (e.g. JPA repository).
- **REST API** — expose the board via a Spring MVC or WebFlux controller.
- **Thread safety** — add `synchronized` blocks or use `ConcurrentHashMap` for concurrent access.
- **Event sourcing** — emit domain events (`GameStarted`, `ScoreUpdated`, `GameFinished`) for audit trails or projections.

