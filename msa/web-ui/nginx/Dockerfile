
FROM nginx:latest

RUN mkdir /app
COPY target/web/public/ /app

COPY ./nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx","-g","daemon off;"]