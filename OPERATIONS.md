# GooSage Operations (v8 Lock)
> Non-negotiable operational rules.  
> If these rules are broken, the system must fail fast.

---

## 0. Purpose
GooSage is not “code that runs.”  
GooSage is a system that can be rolled back, reproduced, and proven.

Core question:
> “Does it run?” → “Can we revert it?”

---

## 1. Environment Separation
Profiles: local / edge / test

Rules:
- Each profile uses different ports
- Each profile uses different databases
- Config files must not leak values across profiles

---

## 2. Schema Management (Flyway is the only truth)
- `ddl-auto=validate`
- Schema changes are allowed **only via Flyway migrations**
- Startup must fail if schema mismatch is detected

No exceptions:
- No manual DB edits in runtime systems
- Migrations are additive only (no rewrite of old migrations)

---

## 3. Regression Lock (Run-All is the contract)
Any change to DAO / SQL / Flyway / DTO / request schema MUST pass regression.

The only acceptable evidence of success:
- `run-all.ps1` completes successfully
- `run-all.log` is generated as proof

If any step fails → the change is considered failed.

Regression flow (single source of truth):
Health → Login → Coach → Create → Quiz → Event → Coach

---

## 4. Samples are contracts (8-6 pending)
Request/response samples are not examples.
They are **operational contracts**.

Rules:
- UTF-8 enforced (no Korean broken text)
- Samples must match DTO fields/types exactly
- Any breaking change requires updating samples and rerunning regression

Status:
- 8-6 (samples folder standardization): TODO

---

## 5. Logging Standard (rid/uid/ep/st/ms)
Logs must support “failure classification” without guessing.

Required fields:
- rid, uid, ep, st, ms

Rule:
- Any log format change requires `run-all` verification.

---

## 6. Why this structure exists (Postmortem)
GooSage failed repeatedly not because of features, but because of trust.

Recurring failures:
- Column exists in code but not in DB
- Column exists in DB but not in samples
- Samples are correct but scripts/order differ
- Success is judged by “feeling,” not proof

Solution:
- Fix the evaluation 기준 to **one**
- Human memory is unreliable → scripts/logs become memory
- Convenience is sacrificed for safety

---

## 7. Safety Principle
> Fail fast is safer than silent corruption.

---

## 8. Signature (Lock Declaration)
From this point:
- We do not “try and see.”
- We change only what we can revert and prove.

Signed: ____________________  
Date: ______________________
