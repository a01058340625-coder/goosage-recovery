WHY_THIS_STRUCTURE.md
Purpose

This document explains why GooSage uses its current structure.

This structure is not accidental.
This is not over-engineering.
This is intentional architecture for a long-term decision engine.

GooSage is designed to become a state-based learning OS and decision platform.

1. GooSage is NOT a CRUD system

Most applications follow:

Controller → Service → Repository → DB

GooSage does not.

GooSage is a:

State-based Decision Engine

It does not simply store data.

It:

Calculates user state

Interprets behavioral signals

Predicts risk

Suggests next actions

Drives habit loops

Therefore, the architecture must protect decision integrity.

CRUD-centric architecture cannot support this.

2. Core Principle: Single Source of Truth (SSOT)

The entire system revolves around a single source of truth.

Key objects:

StudyState → minimal decision state

StudySnapshot → full interpreted state + evidence

All decision logic must originate from StudySnapshot.

There must be:

No duplicated calculation

No controller-level decision logic

No rule execution outside the engine

InterpretationService is the only place where raw data becomes interpreted state.

This ensures:

Determinism

Traceability

Reproducibility

Regression safety

3. Separation of Responsibilities
Domain Layer (Pure Decision Logic)

Examples:

StudyState

StudySnapshot

PredictionRule

NextActionRule

StudyCoachPort

StudyCoachResult

Domain represents the brain of GooSage.

Domain must not know:

Spring

HTTP

DB

JSON

DTOs

Domain must be:

Deterministic

Testable

Framework-independent

Infra Layer (Execution & Integration)

Examples:

DAO

Adapter

InterpretationService

PredictionService

NextActionService

Infra is responsible for:

Data access

Snapshot assembly

Rule execution orchestration

Mapping between domain and API

Infra does not define business meaning.
It only executes domain contracts.

App Layer (Use-case Coordination)

The App layer:

Orchestrates use-cases

Connects domain and infra

Does not compute business rules

This prevents:

Fat controllers

Hidden business logic

Framework-driven design

4. Why StudySnapshot Exists

Prediction and action require many signals:

Prediction:

streakDays

daysSinceLastEvent

recentEventCount3d

NextAction:

wrongReviews

quizSubmits

eventsCount

Instead of recomputing everywhere, we:

Compute once → freeze → share

StudySnapshot is a frozen moment of user state.

This ensures:

Consistency

Debuggability

Explainable AI foundation

5. Why Rule Engine

Decision logic is implemented as rules because:

Rules are composable

Rules are testable

Rules are extendable

Rules can evolve into ML later

Prediction and NextAction follow the same pattern.

Future:

Rules → Hybrid AI → ML model

The architecture is intentionally ML-ready.

6. Why Port / Adapter Pattern

We enforce:

Controller → Service → Port → Adapter → DAO

This prevents:

Database leakage into domain

DTO leakage into domain

Tight coupling with frameworks

Vendor lock-in

It enables:

Storage replacement

External service expansion

Multi-domain growth

7. Regression Lock Philosophy

All structural changes must pass:

run-all regression

study loop validation

prediction safety contract

Fail-fast is preferred over silent corruption.

This ensures that decision quality never degrades unnoticed.

8. Decision Stability First

Feature development is secondary.

Before adding new features:

Decision stability must be proven

Prediction accuracy must be stable

Evidence must be trustworthy

This phase (v1.5) is focused on:

Decision reliability and structural integrity.

9. Long-Term Vision

GooSage is designed to evolve into:

A personal learning operating system

A behavioral decision platform

A predictive coaching engine

A SaaS-based habit and skill platform

This architecture enables:

Scalable rule evolution

Explainable AI

Multi-domain learning

Enterprise onboarding systems

Adaptive coaching

10. Architectural Guardrails

Any change that:

Duplicates state calculation

Bypasses ports

Spreads decision logic

Leaks infrastructure concerns into domain

Breaks snapshot integrity

must be rejected.

Architecture integrity > short-term speed.

11. Final Summary

GooSage is not about storing content.

It is about:

State → Interpretation → Decision → Prediction → Action → Feedback → Loop

This structure exists to protect this cycle.

This is the foundation of a long-term intelligent system.

12. Execution Evidence (v1.5)

Today, the system moved from design to behavioral simulation.

The following have been verified:

Persona-based simulation environment is operational.

Multiple users can generate behavioral events.

Daily loop execution has been automated through scripts.

Behavioral data is accumulated in study_events.

Coach prediction and NextAction are triggered from real behavior.

System determinism and reproducibility have been confirmed.

This marks the transition from a CRUD-like system to a real decision engine.

Future work will focus on:

Long-term behavioral pattern validation.

Prediction stability.

Risk detection.

Adaptive intervention strategies.

This phase establishes the foundation for ML-driven decision evolution.

## Package Guardrails (No Refactor Until Day7)
- api.controller에는 새 파일을 더 넣지 말고, 기능별 하위 패키지로만 추가한다. (study/academy/admin/internal)
- domain 패키지는 infra/entity/JPA를 import 하지 않는다.
- predict는 domain=규칙/모델, app=조립, infra=연동(있을 때만)으로 고정한다.
- academy 패키지는 당장 이동 금지(데이터 루프 7일 안정화 이후에만 정리).
- 리팩토링은 “기능 1개 추가에 파일 5개 이상 수정”이 반복될 때 시작한다.