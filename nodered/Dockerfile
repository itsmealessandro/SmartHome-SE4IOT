FROM nodered/node-red

# Copy package.json to the WORKDIR so npm builds all
# of your added nodes modules for Node-RED
WORKDIR /data
RUN npm install --unsafe-perm --no-update-notifier --no-fund --only=production
WORKDIR /usr/src/node-red

# Copy _your_ Node-RED project files into place
# NOTE: This will only work if you DO NOT later mount /data as an external volume.
#       If you need to use an external volume for persistence then
#       copy your settings and flows files to that volume instead.
COPY flows.json /data/flows.json
