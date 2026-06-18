#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

JAVA_DIR="managerApplication/app/managerApp"
COMPOSE_SCRIPT="$ROOT/tests/compose_validate.sh"
FLOWS_SCRIPT="$ROOT/tests/flows_validate.js"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

OVERALL_PASS=0
OVERALL_FAIL=0
OVERALL_TOTAL=0

print_banner() {
    clear 2>/dev/null || true
    echo -e "${CYAN}╔══════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║     ${BOLD}SmartHome-SE4IOT — Test Suite${NC}       ${CYAN}║${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════╝${NC}"
    echo
}

run_java_tests() {
    echo -e "${YELLOW}━━━ Java Tests (Manager App) ━━━${NC}"
    echo
    if [[ ! -f "$JAVA_DIR/pom.xml" ]]; then
        echo -e "${RED}✗ pom.xml non trovato in $JAVA_DIR${NC}"
        return 1
    fi
    if [[ ! -x "$JAVA_DIR/mvnw" ]]; then
        echo -e "${YELLOW}! mvnw non eseguibile — provo con mvn...${NC}"
        if command -v mvn &>/dev/null; then
            (cd "$JAVA_DIR" && mvn test)
        else
            echo -e "${RED}✗ Nessun Maven disponibile${NC}"
            return 1
        fi
    else
        (cd "$JAVA_DIR" && ./mvnw test)
    fi
}

run_compose_tests() {
    echo -e "${YELLOW}━━━ Infrastructure Tests (Docker Compose) ━━━${NC}"
    echo
    if [[ ! -f "$COMPOSE_SCRIPT" ]]; then
        echo -e "${RED}✗ $COMPOSE_SCRIPT non trovato${NC}"
        return 1
    fi
    bash "$COMPOSE_SCRIPT"
}

run_flows_tests() {
    echo -e "${YELLOW}━━━ Node-RED Flow Validation ━━━${NC}"
    echo
    if [[ ! -f "$FLOWS_SCRIPT" ]]; then
        echo -e "${RED}✗ $FLOWS_SCRIPT non trovato${NC}"
        return 1
    fi
    node "$FLOWS_SCRIPT"
}

run_all() {
    local any_failed=0

    print_banner
    echo -e "${BOLD}Esecuzione di tutti i test in sequenza...${NC}"
    echo

    if run_java_tests; then
        echo -e "\n${GREEN}✓ Java Tests superati${NC}"
    else
        echo -e "\n${RED}✗ Java Tests falliti${NC}"
        any_failed=1
    fi

    echo "──────────────────────────────────────────"
    echo

    if run_compose_tests; then
        echo -e "${GREEN}✓ Infrastructure Tests superati${NC}"
    else
        echo -e "${RED}✗ Infrastructure Tests falliti${NC}"
        any_failed=1
    fi

    echo "──────────────────────────────────────────"
    echo

    if run_flows_tests; then
        echo -e "${GREEN}✓ Node-RED Flow Validation superati${NC}"
    else
        echo -e "${RED}✗ Node-RED Flow Validation falliti${NC}"
        any_failed=1
    fi

    echo
    if [[ "$any_failed" -eq 0 ]]; then
        echo -e "${GREEN}╔══════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║     ${BOLD}TUTTI I TEST SUPERATI${NC}                ${GREEN}║${NC}"
        echo -e "${GREEN}╚══════════════════════════════════════════╝${NC}"
    else
        echo -e "${RED}╔══════════════════════════════════════════╗${NC}"
        echo -e "${RED}║     ${BOLD}QUALCHE TEST HA FALLITO${NC}               ${RED}║${NC}"
        echo -e "${RED}╚══════════════════════════════════════════╝${NC}"
    fi
    echo
    read -rp "Premi INVIO per tornare al menu..." _
}

interactive_menu() {
    while true; do
        print_banner
        echo -e "${BOLD}Scegli cosa testare:${NC}"
        echo
        echo -e "  ${CYAN}1)${NC} Java Tests (Manager App — JUnit + Mockito)"
        echo -e "  ${CYAN}2)${NC} Infrastructure Tests (Docker Compose)"
        echo -e "  ${CYAN}3)${NC} Node-RED Flow Validation"
        echo -e "  ${CYAN}a)${NC} ${BOLD}Tutti i test in sequenza${NC}"
        echo -e "  ${CYAN}q)${NC} Esci"
        echo
        read -rp "Selezione: " choice

        case "$choice" in
            1)
                print_banner
                run_java_tests
                echo
                read -rp "Premi INVIO per tornare al menu..." _
                ;;
            2)
                print_banner
                run_compose_tests
                echo
                read -rp "Premi INVIO per tornare al menu..." _
                ;;
            3)
                print_banner
                run_flows_tests
                echo
                read -rp "Premi INVIO per tornare al menu..." _
                ;;
            a|A)
                run_all
                ;;
            q|Q)
                echo
                echo -e "${CYAN}Arrivederci!${NC}"
                echo
                exit 0
                ;;
            *)
                echo -e "${RED}Opzione non valida${NC}"
                sleep 1
                ;;
        esac
    done
}

if [[ $# -eq 0 ]]; then
    interactive_menu
else
    case "$1" in
        java|j)
            print_banner
            run_java_tests
            ;;
        compose|c)
            print_banner
            run_compose_tests
            ;;
        flows|f)
            print_banner
            run_flows_tests
            ;;
        all|a)
            run_all
            ;;
        *)
            echo "Uso: $0 [java|compose|flows|all]"
            echo "     Senza argomenti avvia la modalità interattiva"
            exit 1
            ;;
    esac
fi
