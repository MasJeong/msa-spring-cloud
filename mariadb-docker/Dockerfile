# docker build -t masjeong/mariadb:1.0 .
# docker run -d -p 3306:3306 --network ecommerce-network --name mariadb masjeong/mariadb:1.0

FROM mariadb:11.4
ENV MYSQL_ROOT_PASSWORD root
ENV MYSQL_DATABASES=user_db

# 초기화 SQL 파일 복사
COPY ./user_db.sql /docker-entrypoint-initdb.d/
COPY ./order_db.sql /docker-entrypoint-initdb.d/
COPY ./catalog_db.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
CMD ["mariadbd"]