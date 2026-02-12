# WHY_THIS_STRUCTURE.md

## ?? Purpose

This document explains why GooSage uses its current structure.

This is not accidental.
This is not over-engineering.
This is intentional architecture for a decision-based system.

---

# 1?? GooSage is NOT a CRUD system

Most applications:

Controller ¡æ Service ¡æ Repository ¡æ DB

GooSage is different.

GooSage is a:

> State-based Decision Engine

It does not just store data.
It calculates state.
It makes decisions.
It predicts risk.
It suggests next actions.

Therefore the structure must reflect that.

---

# 2?? Core Principle: Single Source of Truth

The system is built around:

- StudyState (engine minimal state)
- StudySnapshot (state + evidence)

All decisions must originate from this snapshot.

No controller-level logic.
No duplicated evidence calculation.
No scattered interpretation.

InterpretationService is the only place where state is assembled.

---

# 3?? Separation of Responsibilities

## Domain (Pure Logic)

- StudyState
- StudySnapshot
- StudyCoachPort
- StudyCoachResult

Domain does not know about:
- Spring
- DB
- HTTP
- DTOs

It represents the brain.

---

## Infra (Implementation)

- DAO
- Adapter
- PredictionService
- NextActionService
- InterpretationService

Infra knows:
- how to fetch data
- how to assemble snapshot
- how to apply rules

But it does not decide business direction.
It only executes contracts.

---

## App Layer

App coordinates.
It does not compute.

---

# 4?? Why Snapshot Exists

Snapshot exists because:

Prediction needs:
- streakDays
- daysSinceLastEvent
- recentEventCount3d

NextAction needs:
- wrongReviews
- quizSubmits
- eventsCount

Instead of recomputing everywhere,
we compute once and freeze it.

Snapshot is the frozen moment of user state.

---

# 5?? Why Port / Adapter Pattern

We enforce:

Controller ¡æ Service ¡æ Port ¡æ Adapter ¡æ DAO

Because:

- It prevents DB leakage.
- It prevents entity leakage.
- It allows replacement of storage.
- It keeps domain pure.

This is intentional.

---

# 6?? Regression Lock

Every structural change must pass:

- run-all regression
- study flow validation
- prediction SAFE contract

Fail fast > silent corruption.

---

# 7?? Long-Term Vision

GooSage is designed to evolve into:

- A personal learning engine
- A decision recommendation system
- A risk prediction loop
- Possibly a SaaS platform

This structure allows:

- Adding new rules without breaking controllers
- Changing storage without touching domain
- Expanding evidence model safely

---

# 8?? Rule

If a change:

- spreads logic across layers
- duplicates state calculation
- bypasses ports
- leaks entity to controller

It must be rejected.

---

# 9?? Summary

GooSage is not about storing posts.

It is about:

State ¡æ Interpretation ¡æ Decision ¡æ Prediction ¡æ Action

This structure exists to protect that flow.