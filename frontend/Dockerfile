FROM node:22-alpine3.20 as build
WORKDIR /app

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build


FROM nginx:1.27-alpine
ARG NGINX_ENV=""
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx${NGINX_ENV}.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
