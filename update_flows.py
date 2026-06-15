import json
from datetime import datetime

with open('nodered/flows.json', 'r') as f:
    flows = json.load(f)

broker_id = next((node['id'] for node in flows if node.get('type') == 'mqtt-broker'), None)
threshold_check = next((node for node in flows if node.get('name') == 'thresholds check'), None)

if not broker_id or not threshold_check:
    print("Could not find broker or thresholds check node.")
    exit(1)

# Create the function node that formats the alert message
import uuid
def get_id(): return uuid.uuid4().hex[:16]

alert_func_id = get_id()
alert_mqtt_id = get_id()

alert_func = {
    "id": alert_func_id,
    "type": "function",
    "z": threshold_check.get("z"),
    "name": "format alert mqtt",
    "func": "var d = new Date();\nmsg.topic = 'SmartHome/alerts/' + msg.payload.room + '/' + msg.payload.sensorType;\nmsg.payload = 'Exceeded threshold at ' + d.toLocaleTimeString();\nreturn msg;",
    "outputs": 1,
    "timeout": 0,
    "noerr": 0,
    "initialize": "",
    "finalize": "",
    "libs": [],
    "x": threshold_check.get("x", 0) + 200,
    "y": threshold_check.get("y", 0) + 60,
    "wires": [[alert_mqtt_id]]
}

alert_mqtt = {
    "id": alert_mqtt_id,
    "type": "mqtt out",
    "z": threshold_check.get("z"),
    "name": "publish alert",
    "topic": "",
    "qos": "1",
    "retain": "",
    "respTopic": "",
    "contentType": "",
    "userProps": "",
    "correl": "",
    "expiry": "",
    "broker": broker_id,
    "x": alert_func["x"] + 200,
    "y": alert_func["y"],
    "wires": []
}

# Add wires from threshold check to the new function
if len(threshold_check['wires']) == 0:
    threshold_check['wires'].append([])
threshold_check['wires'][0].append(alert_func_id)

flows.extend([alert_func, alert_mqtt])

with open('nodered/flows.json', 'w') as f:
    json.dump(flows, f, indent=4)

print("Successfully updated flows.json")
