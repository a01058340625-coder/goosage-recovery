# GooSage Recovery

Recovery-focused decision engine built on the GooSage core architecture.

---

## 1. Overview

GooSage Recovery applies the GooSage decision engine to recovery behavior.

It analyzes user signals such as urges, attempts, and recovery actions, then predicts risk and recommends interventions.

---

## 2. Domain Focus

This domain models behavioral recovery patterns.

### Key Signals
- urge logs
- bet attempts
- blocked attempts
- recovery actions
- relapse signals

---

## 3. Core Flow

Event → Recovery State → Risk Prediction → Intervention

---

## 4. Prediction Goals

- detect relapse risk
- identify recovery progress
- detect blocked or stuck states
- guide intervention timing

---

## 5. Example Concepts

- relapse risk detection
- recovery progression tracking
- false recovery detection
- blocked behavior patterns

---

## 6. Why this matters

Recovery systems often lack structured decision logic.

This system provides:
- measurable state interpretation
- consistent risk prediction
- actionable intervention guidance

---

## 7. Relation to GooSage

- goosage-api → core engine
- goosage-recovery → recovery domain extension

Same engine, different behavioral space.

---

## 8. Long-Term Goal

- addiction recovery modeling
- intervention optimization
- cross-domain behavioral consistency