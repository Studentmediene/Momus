FROM node:lts-alpine

WORKDIR /

COPY package.json package.json
COPY package-lock.json package-lock.json
RUN npm ci

COPY webapp webapp

EXPOSE 8082
CMD npm run dev-server