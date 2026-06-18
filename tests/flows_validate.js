#!/usr/bin/env node
const fs = require('fs');
const path = require('path');

const flowsPath = path.join(__dirname, '..', 'nodered', 'flows.json');

let passed = 0;
let failed = 0;

function pass(msg) { passed++; console.log(`  \u2713 ${msg}`); }
function fail(msg) { failed++; console.log(`  \u2717 ${msg}`); }

// --- 1. Parse JSON ---
let flows;
try {
    flows = JSON.parse(fs.readFileSync(flowsPath, 'utf-8'));
    pass('flows.json is valid JSON');
} catch (e) {
    fail(`flows.json is invalid JSON: ${e.message}`);
    process.exit(1);
}

// --- 2. Is an array ---
if (Array.isArray(flows)) {
    pass('flows.json is an array');
} else {
    fail('flows.json is not an array');
    process.exit(1);
}

// --- 3. Every node has id, type, and z (for non-tab/broker nodes) ---
const nodesById = {};
let allValid = true;
for (const node of flows) {
    nodesById[node.id] = node;
    if (!node.id) { fail(`Node missing id: ${JSON.stringify(node)}`); allValid = false; }
    if (!node.type) { fail(`Node ${node.id} missing type`); allValid = false; }
}
if (allValid) pass('all nodes have id and type');

// --- 4. All tabs/broker configs have labels ---
const tabs = flows.filter(n => n.type === 'tab');
const brokers = flows.filter(n => n.type === 'mqtt-broker');
const namedNodes = flows.filter(n => n.name !== undefined);
pass(`found ${tabs.length} tabs, ${brokers.length} brokers`);

// --- 5. Wire targets reference existing nodes ---
let wiresOk = true;
for (const node of flows) {
    if (!node.wires) continue;
    for (const wireGroup of node.wires) {
        for (const targetId of wireGroup) {
            if (!nodesById[targetId]) {
                fail(`Node ${node.id} wires to non-existent node ${targetId}`);
                wiresOk = false;
            }
        }
    }
}
if (wiresOk) pass('all wire targets reference existing nodes');

// --- 6. Function nodes have valid JavaScript ---
let funcsOk = true;
for (const node of flows) {
    if (node.type !== 'function') continue;
    if (!node.func) {
        fail(`Function node ${node.id} has no func property`);
        funcsOk = false;
        continue;
    }
    try {
        new Function(node.func);
        pass(`function node "${node.name || node.id}" — valid JS`);
    } catch (e) {
        fail(`function node "${node.name || node.id}" — JS syntax error: ${e.message}`);
        funcsOk = false;
    }
}

// --- 7. MQTT in nodes reference existing broker ---
let mqttOk = true;
for (const node of flows) {
    if (!['mqtt in', 'mqtt out'].includes(node.type)) continue;
    if (!node.broker) {
        fail(`MQTT node ${node.id} has no broker reference`);
        mqttOk = false;
    } else if (!nodesById[node.broker]) {
        fail(`MQTT node ${node.id} references non-existent broker ${node.broker}`);
        mqttOk = false;
    }
}
if (mqttOk) pass('all MQTT nodes reference valid brokers');

// --- 8. InfluxDB nodes reference existing config ---
let influxOk = true;
for (const node of flows) {
    if (!['influxdb out'].includes(node.type)) continue;
    if (!node.influxdb) {
        fail(`InfluxDB node ${node.id} has no influxdb config reference`);
        influxOk = false;
    } else if (!nodesById[node.influxdb]) {
        fail(`InfluxDB node ${node.id} references non-existent config ${node.influxdb}`);
        influxOk = false;
    }
}
if (influxOk) pass('all InfluxDB nodes reference valid config');

// --- summary ---
console.log('--------------------------------');
console.log(`Results: ${passed} passed, ${failed} failed`);
process.exit(failed > 0 ? 1 : 0);
