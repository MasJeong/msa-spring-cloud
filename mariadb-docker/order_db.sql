CREATE DATABASE IF NOT EXISTS order_db;
USE order_db;
create table orders
(
    id          bigint auto_increment
        primary key,
    product_id  varchar(120)                         not null,
    qty         int                                  not null,
    unit_price  int                                  not null,
    total_price int                                  not null,
    user_id     varchar(255)                         not null,
    order_id    varchar(255)                         not null,
    created_at timestamp default current_timestamp() null,
    updated_at timestamp default current_timestamp() null on update current_timestamp(),
    constraint uc_order_orderid
        unique (order_id)
);
