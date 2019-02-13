FROM node:10-alpine

WORKDIR /

COPY package.json package.json
RUN npm install --no-progress --ignore-optional

COPY webapp webapp

EXPOSE 8082
CMD npm run dev-server