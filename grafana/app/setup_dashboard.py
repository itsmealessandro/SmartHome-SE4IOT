import requests
import json
import time
import os

# --- 1. Configurazioni e Caricamento Dati ---

# Variabili d'ambiente (dovrebbero essere impostate nel tuo ambiente di esecuzione)
INFLUXDB_BUCKET = os.getenv('INFLUXDB_BUCKET')
GRAFANA_URL = os.getenv('GRAFANA_URL')
GRAFANA_API_KEY = os.getenv('GRAFANA_API_KEY')

# Stampa per debug (utile per verificare che le variabili siano caricate)
print(f"INFLUXDB_BUCKET: {INFLUXDB_BUCKET}", flush=True)
print(f"GRAFANA_URL: {GRAFANA_URL}", flush=True)
print(f"GRAFANA_API_KEY: {GRAFANA_API_KEY}", flush=True)

# Funzione per ottenere la configurazione delle stanze/ambienti dal tuo env.json
def get_environments_config(file_path='/app/env.json'):
    """Carica la configurazione degli ambienti dal file env.json."""
    if not os.path.exists(file_path):
        print(f"Errore: File '{file_path}' non trovato.", flush=True)
        return {}
    try:
        with open(file_path, 'r') as file:
            return json.load(file)
    except json.JSONDecodeError as e:
        print(f"Errore nella lettura di '{file_path}': {e}", flush=True)
        return {}

# Definizione delle proprietà generiche per ogni tipo di sensore.
# Questo è il cuore della parametricità: qui dici a Grafana come visualizzare
# ogni 'tipo' di sensore, indipendentemente dalla stanza.
def get_sensor_types_properties():
    """Definisce le proprietà di visualizzazione per i diversi tipi di sensori."""
    return {
        "light": {"unit": "lux", "threshold_min": 100, "threshold_max": 500, "chart_type": "timeseries"},
        "temperature": {"unit": "°C", "threshold_min": 18, "threshold_max": 25, "chart_type": "timeseries"},
        "lightAct": {"unit": "on/off", "threshold_min": None, "threshold_max": None, "chart_type": "gauge"},
        "temperatureAct": {"unit": "setpoint", "threshold_min": None, "threshold_max": None, "chart_type": "gauge"}
        # Aggiungi qui altri tipi di sensori e le loro proprietà specifiche.
        # Se un sensore non ha soglie, imposta threshold_min e threshold_max a None.
    }

