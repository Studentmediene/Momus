FROM node:10-alpine

WORKDIR /

COPY package.json package.json
RUN npm install --no-progress --ignore-optional

COPY tsconfig.json tsconfig.json
COPY webapp-beta webapp-beta

EXPOSE 8081
CMD npm run dev-server:beta