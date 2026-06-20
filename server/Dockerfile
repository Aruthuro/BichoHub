FROM node:20-bookworm

# dependencias do Puppeteer (WhatsApp bot)
RUN apt-get update && apt-get install -y \
    libnspr4 libnss3 libatk-bridge2.0-0 libdrm2 libxkbcommon0 \
    libxcomposite1 libxdamage1 libxrandr2 libgbm1 libpango-1.0-0 \
    libcairo2 libasound2 libatspi2.0-0 libgtk-3-0 libxshmfence1 \
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