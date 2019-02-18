FROM node:lts-alpine

WORKDIR /

COPY package.json package.json
COPY package-lock.json package-lock.json
RUN npm ci

COPY tsconfig.json tsconfig.json
COPY webapp-beta webapp-beta

EXPOSE 8081
CMD npm run dev-server:beta