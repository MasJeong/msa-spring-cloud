# docker build -t masjeong/mariadb:1.0 .
# docker run -d -p 3307:3306 --network ecommerce-network --name mariadb masjeong/mariadb:1.0

FROM mariadb:11.4
ENV MYSQL_ROOT_PASSWORD root
ENV MYSQL_DATABASE user_db

# 초기화 SQL 파일 복사
COPY ./all_databases.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
CMD ["mariadbd"]