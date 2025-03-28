CREATE DATABASE IF NOT EXISTS catalog_db;
USE catalog_db;
create table catalog
(
    id           bigint auto_increment
        primary key,
    product_id   varchar(120)                         not null,
    product_name varchar(255)                         not null,
    stock        int                                  not null,
    unit_price   int                                  not null,
    created_at timestamp default current_timestamp() null,
    updated_at timestamp default current_timestamp() null on update current_timestamp(),
    constraint uc_catalog_productid
        unique (product_id)
);
