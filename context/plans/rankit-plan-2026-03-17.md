---
problem: "Local Android app to rank custom lists of anything with flexible per-list scoring schemas"
date: 2026-03-17
adr: "rankit-adr-2026-03-17.md"
---

# Implementation Plan: RankIt

## Summary

Build a local-only Android app (Jetpack Compose + Room/SQLite + MVVM) where users create ranked lists of anything with fully custom field schemas and scoring categories. Items are sorted by the Kotlin-computed mean of their scoring sliders. This is also a first Android project — tasks are ordered to build understanding progressively.

---

## Tasks

### Task 1: Project Setup

**Files:** `app/build.gradle.kts`, `settings.gradle.kts`

Add dependencies: Jetpack Compose BOM, Room, Navigation Compose, Hilt (DI), Kotlinx Serialization (for JSON blob), Coil (image loading).

Enable `kapt` or `ksp` for Room annotation processing.

**Verify:** `./gradlew assembleDebug`
**Expect:** Build succeeds with no dependency resolution errors

---

### Task 2: Room Database — Entities

**Files:** `data/db/entities/RankingList.kt`, `data/db/entities/ComponentDefinition.kt`, `data/db/entities/Item.kt`

Define three `@Entity` data classes:

- `RankingList(id: String, name: String, description: String, createdAt: Long)`
- `ComponentDefinition(id: String, listId: String, name: String, type: ComponentType, configJson: String, orderIndex: Int)` — ForeignKey to RankingList with `onDelete = CASCADE`
- `Item(id: String, listId: String, valuesJson: String, isComplete: Boolean, createdAt: Long)` — ForeignKey to RankingList with `onDelete = CASCADE`

`ComponentType` is an enum: `TEXT, SLIDER, IMAGE, DESCRIPTION, LOCATION`

**Verify:** `./gradlew compileDebugKotlin`
**Expect:** No compilation errors; Room entities compile cleanly
**Depends on:** Task 1

---

### Task 3: Room DAOs

**Files:** `data/db/dao/RankingListDao.kt`, `data/db/dao/ComponentDefinitionDao.kt`, `data/db/dao/ItemDao.kt`

- `RankingListDao`: `getAll(): Flow<List<RankingList>>`, `insert`, `delete`
- `ComponentDefinitionDao`: `getForList(listId): Flow<List<ComponentDefinition>>`, `insert`, `deleteForList`, `insertAll`
- `ItemDao`: `getForList(listId): Flow<List<Item>>`, `getById`, `insert`, `update`, `delete`

**Verify:** `./gradlew compileDebugKotlin`
**Expect:** No compilation errors
**Depends on:** Task 2

---

### Task 4: Room Database + Repository

**Files:** `data/db/RankItDatabase.kt`, `data/RankItRepository.kt`

Create `@Database` class with all three entities, version 1.

`RankItRepository` wraps DAOs and exposes:
- `getLists(): Flow<List<RankingList>>`
- `createList(list, componentDefs)` — validates ≥1 scoring slider (type=SLIDER, configJson.isScoring=true), throws if not
- `updateListSchema(listId, newDefs)` — validates ≥1 scoring slider, updates all item `valuesJson` eagerly (add missing keys as null, recompute `isComplete`)
- `addItem(item)`, `updateItem(item)`, `deleteItem(id)`
- `deleteList(id)` — cascade handled by Room FK

**Verify:** Write a unit test for `createList` with zero scoring sliders — expect exception
**Expect:** Validation throws `IllegalArgumentException("At least one scoring slider required")`
**Depends on:** Task 3

---

### Task 5: Score Computation Utility

**Files:** `domain/ScoreCalculator.kt`

Pure Kotlin object. Given a list of `ComponentDefinition` and an `Item.valuesJson`:
- Parse `valuesJson` with Kotlinx Serialization
- Filter ComponentDefinitions where `type == SLIDER` and `configJson.isScoring == true`
- Parse their values from the blob as `Float`
- Return mean as `Float?` — null if no scoring values present (items with null sort last)
- Support `scoreForCategory(componentId)` for single-category sort

**Verify:** Unit test: 3 sliders (values 8.0, 6.0, 7.0, all scoring) → score = 7.0
**Expect:** `ScoreCalculator.computeScore(defs, valuesJson) == 7.0f`
**Depends on:** Task 2

---

### Task 6: Navigation Graph

**Files:** `ui/navigation/RankItNavGraph.kt`, `ui/navigation/Screen.kt`

Define `sealed class Screen` with routes: `Home`, `ListDetail(listId)`, `CreateList`, `AddItem(listId, itemId?)`, `ItemDetail(listId, itemId)`.

Wire `NavHost` with Compose Navigation. Screens are stubs for now.

**Verify:** App launches and navigates Home → CreateList → back without crash
**Expect:** No `IllegalArgumentException` on navigation
**Depends on:** Task 1

---

### Task 7: HomeScreen

**Files:** `ui/screens/home/HomeScreen.kt`, `ui/screens/home/HomeViewModel.kt`

`HomeViewModel` exposes `lists: StateFlow<List<RankingList>>` from repository.

`HomeScreen` shows a `LazyColumn` of list cards (name, description, item count). FAB navigates to `CreateList`. Tap card → `ListDetail`.

**Verify:** Run app, create a list via DB seed, confirm it appears on HomeScreen
**Expect:** List name and description visible; tap navigates correctly
**Depends on:** Task 4, Task 6

---

### Task 8: CreateListScreen

**Files:** `ui/screens/createlist/CreateListScreen.kt`, `ui/screens/createlist/CreateListViewModel.kt`

ViewModel holds draft state: `name`, `description`, `componentDefs: List<ComponentDefinitionDraft>`.

UI: name + description fields, dynamic list of ComponentDefinition editors (add/remove/reorder). Each ComponentDefinition editor shows: name, type selector, type-specific config (slider: min, max, isScoring toggle).

Save button calls `repository.createList(...)`. Validation error shown inline if no scoring slider. On success → navigate Home.

**Verify:** Attempt save with no scoring slider → inline error shown. Add scoring slider → save succeeds → list appears on Home.
**Expect:** Validation message visible; successful save returns to Home with new list
**Depends on:** Task 7

---

### Task 9: AddItemScreen

**Files:** `ui/screens/additem/AddItemScreen.kt`, `ui/screens/additem/AddItemViewModel.kt`

ViewModel loads ComponentDefinitions for the list, builds a dynamic form. Draft state held in ViewModel (survives rotation).

Renders a field per ComponentDefinition:
- `TEXT` / `DESCRIPTION` → `TextField`
- `SLIDER` → `Slider` with min/max from config + current value label
- `IMAGE` → image picker button; stores file path; shows thumbnail or broken-image placeholder (emoji) if file missing
- `LOCATION` → text field for now (v1)

Save → `repository.addItem` → navigate back.

**Verify:** Open AddItem for a list, fill all fields, save → item appears in ListDetail
**Expect:** Item saved with correct `valuesJson`; `isComplete = true`
**Depends on:** Task 4, Task 5, Task 6

---

### Task 10: ListDetailScreen

**Files:** `ui/screens/listdetail/ListDetailScreen.kt`, `ui/screens/listdetail/ListDetailViewModel.kt`

ViewModel loads items + componentDefs for the list. Computes scores via `ScoreCalculator`. Exposes `sortedItems: StateFlow<List<RankedItem>>` where `RankedItem` wraps `Item` + computed `score: Float?`.

Default sort: by overall mean score descending (nulls last). Sort toggle: dropdown of scoring ComponentDefinitions → re-sort by that category.

`LazyColumn` shows compact item cards: title (from first TEXT component), score.

FAB → AddItem. Tap card → ItemDetail.

**Verify:** Add 3 items with different scores → confirm correct ranking order in UI
**Expect:** Highest score first
**Depends on:** Task 9

---

### Task 11: ItemDetailScreen

**Files:** `ui/screens/itemdetail/ItemDetailScreen.kt`, `ui/screens/itemdetail/ItemDetailViewModel.kt`

Read-only view of all component values. Each component rendered by type (same renderers as AddItemScreen but non-editable).

Broken image paths show emoji placeholder instead of crashing.

**Verify:** Open an item → all fields shown. Open item with broken image path → placeholder shown.
**Expect:** No crash; image missing degrades gracefully
**Depends on:** Task 10

---

## Definition of Done

- [ ] App builds and runs on emulator/device: `./gradlew assembleDebug`
- [ ] Can create a beer list with: name field (TEXT), tasting notes (DESCRIPTION), photo (IMAGE), bitterness slider (SLIDER, isScoring=true, 0–10), overall slider (SLIDER, isScoring=true, 0–10)
- [ ] Can add 3 beers with different scores and see them ranked correctly on ListDetail
- [ ] Deleting a list removes all its ComponentDefinitions and Items (no orphans)
- [ ] Broken image path shows placeholder, not crash
- [ ] Attempting to create a list with no scoring slider shows validation error
