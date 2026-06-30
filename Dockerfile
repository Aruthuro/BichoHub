FROM node:20-bookworm

RUN apt-get update && apt-get install -y \
    python3 python3-pip python-is-python3 \
    libgl1-mesa-glx libglib2.0-0 \
    --no-install-recommends && rm -rf /var/lib/apt/lists/*

RUN pip3 install --break-system-packages --no-cache-dir torch torchvision --index-url https://download.pytorch.org/whl/cpu && \
    pip3 install --break-system-packages --no-cache-dir ultralytics

WORKDIR /usr/src/app

COPY server/package*.json ./
RUN npm install

COPY server/ ./
COPY models/ ./models/

ENV YOLO_MODELS_DIR=/usr/src/app/models/runs/classify/Triagem
ENV NODE_ENV=production

RUN npm run build

EXPOSE 6969

CMD ["node", "build/index.js"]
