import json

with open('nodered/flows.json', 'r') as f:
    flows = json.load(f)

for node in flows:
    if node.get('name') == 'format alert mqtt':
        node['func'] = "var d = new Date();\nvar val = msg.payload.value;\nmsg.topic = 'SmartHome/alerts/' + msg.payload.room + '/' + msg.payload.sensorType;\nmsg.payload = '[' + d.toLocaleString() + '] Exceeded threshold! Recorded value: ' + val;\nreturn msg;"

with open('nodered/flows.json', 'w') as f:
    json.dump(flows, f, indent=4)

print("Updated Node-RED flow.")
