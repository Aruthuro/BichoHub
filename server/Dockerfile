FROM node:20-bookworm

# dependencias Python (YOLO / ultralytics)
RUN apt-get update && apt-get install -y \
    python3 python3-pip python-is-python3 \
    --no-install-recommends && rm -rf /var/lib/apt/lists/*

# instala dependencias Python (YOLO / ultralytics)
RUN pip3 install --break-system-packages --no-cache-dir torch torchvision --index-url https://download.pytorch.org/whl/cpu && \
    pip3 install --break-system-packages --no-cache-dir ultralytics

WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install

COPY . .

EXPOSE 6969

CMD ["npm", "run", "dev"]