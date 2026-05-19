FROM node:20-bookworm

WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install

COPY . .

EXPOSE 6969

CMD ["npm", "run", "dev"]