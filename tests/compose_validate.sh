#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

PASS=0
FAIL=0

pass() { PASS=$((PASS + 1)); echo "  ✓ $1"; }
fail() { FAIL=$((FAIL + 1)); echo "  ✗ $1"; }

FAILED=0

# --- 1. docker compose config (syntax + schema) ---
if docker compose config >/dev/null 2>&1; then
    pass "docker compose config — valid syntax"
else
    fail "docker compose config — INVALID"
    docker compose config 2>&1
fi

# --- 2. .env file exists ---
if [[ -f .env ]]; then
    pass ".env file present"
else
    fail ".env file missing"
fi

# --- 3. All referenced build contexts exist ---
for ctx in sensors actuators managerApplication; do
    if [[ -f "$ctx/Dockerfile" ]]; then
        pass "build context $ctx/Dockerfile exists"
    else
        fail "build context $ctx/Dockerfile MISSING"
    fi
done

# --- 4. All referenced config files exist ---
if [[ -f mosquitto/config/mosquitto.conf ]]; then
    pass "mosquitto/config/mosquitto.conf exists"
else
    fail "mosquitto/config/mosquitto.conf MISSING"
fi

# --- 5. All referenced data directories exist ---
for dir in nodered grafana/provisioning/dashboards grafana/provisioning/datasources simulated_env; do
    if [[ -d "$dir" ]]; then
        pass "directory $dir exists"
    else
        fail "directory $dir MISSING"
    fi
done

# --- 6. All services have image or build ---
SERVICES="broker nodered influxdb grafana sensors actuators manager_app"
ALL_OK=1
for svc in $SERVICES; do
    svc_block=$(docker compose config 2>/dev/null | sed -n "/^  $svc:/,/^  [a-z_]/p" | head -n -1)
    if ! echo "$svc_block" | grep -Eq '^\s+image:'; then
        if ! echo "$svc_block" | grep -Eq '^\s+build:'; then
            fail "service $svc has neither image nor build"
            ALL_OK=0
        fi
    fi
done
[[ "$ALL_OK" -eq 1 ]] && pass "all services define image or build"

# --- 7. All services on the same network ---
ALL_OK=1
for svc in $SERVICES; do
    svc_block=$(docker compose config 2>/dev/null | sed -n "/^  $svc:/,/^  [a-z_]/p" | head -n -1)
    if ! echo "$svc_block" | grep -Eq '^\s+networks:'; then
        fail "service $svc has no networks"
        ALL_OK=0
    fi
done
[[ "$ALL_OK" -eq 1 ]] && pass "all services define networks"

# --- summary ---
echo "--------------------------------"
echo "Results: $PASS passed, $FAIL failed"
[[ "$FAIL" -eq 0 ]] || exit 1
