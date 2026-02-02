# GooSage Operations

## Purpose
This document defines **non-negotiable operational rules**.
If these rules are broken, the system must fail fast.

---

## Environment Separation
- Profiles: local / edge / test
- Each profile uses:
  - different ports
  - different databases
  - separate configuration files

---

## Schema Management
- JPA ddl-auto is set to `validate`
- Schema changes are allowed **only via Flyway migrations**
- Application startup must fail if schema mismatch is detected

---

## Safety Principle
> “Fail fast is safer than silent corruption.”
