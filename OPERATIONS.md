# GooSage Operations (v8 Lock)

> Non-negotiable operational rules.  
> If these rules are broken, the system must fail fast.

---

## 0. Purpose
GooSage is not “code that runs.”  
GooSage is a system that can be **rolled back, reproduced, and proven**.

Core question:
> “Does it run?” → “Can we revert it and prove it?”

---

## 1. Environment Separation
Profiles: local / edge / test

Rules:
- Each profile uses different ports
- Each profile uses different databases
- Config files must not leak values across profiles
- Profile mixing is an operational violation

---

## 2. Schema Management (Flyway is the only truth)
- `ddl-auto=validate`
- Schema changes are allowed **only via Flyway migrations**
- Startup must fail if schema mismatch is detected

No exceptions:
- No manual DB edits in runtime systems
- Migrations are additive only  
  (No rewrite, no delete, no reorder of applied migrations)

---

## 3. Regression Lock (Run-All is the contract)
Any change to DAO / SQL / Flyway / DTO / request schema  
**MUST pass regression**.

The only acceptable evidence of success:
- `run-all.ps1` completes successfully
- `run-all.log` is generated as proof

If any step fails → the change is considered failed.

Regression flow (single source of truth):
Health → Login → Coach → Create → Quiz → Event → Coach

---

## 4. Request/Response Samples are Contracts
Request/response samples are not examples.  
They are **operational contracts**.

Rules:
- UTF-8 enforced
- Samples must match DTO fields and types exactly
- Any breaking change requires:
  1. Sample update
  2. Regression re-run
  3. Log proof

Status:
- 8-6 Sample standardization: **DONE**

---

## 5. Logging Standard (rid/uid/ep/st/ms)
Logs must support failure classification without guessing.

Required fields:
- rid, uid, ep, st, ms

Rule:
- Any log format change requires regression verification

---

## 6. Why this structure exists (Postmortem)
GooSage failed repeatedly not because of features, but because of trust.

Recurring failures:
- Column exists in code but not in DB
- Column exists in DB but not in samples
- Samples are correct but execution order differs
- Success judged by “feeling,” not proof

Solution:
- Reduce evaluation criteria to **one**
- Human memory is unreliable → scripts and logs replace memory
- Convenience is sacrificed for safety

---

## 7. Safety Principle
> Fail fast is safer than silent corruption.

---

## 8. Signature (Operations Lock Declaration)

From this point forward:
- We do not “try and see.”
- We change only what we can revert and prove.
- Violations are operational failures, not bugs.

Signed: GooSage Maintainer  
Date: 2026-02-05  
Status: **Operations Locked**
