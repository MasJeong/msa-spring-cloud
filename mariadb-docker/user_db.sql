CREATE DATABASE IF NOT EXISTS user_db;
USE user_db;
create table users
(
    id         bigint auto_increment
        primary key,
    user_id    varchar(255)                          not null,
    name       varchar(50)                           not null,
    email      varchar(50)                           not null,
    password   varchar(255)                          not null,
    created_at timestamp default current_timestamp() null,
    updated_at timestamp default current_timestamp() null on update current_timestamp(),
    constraint uc_users_email
        unique (email),
    constraint uc_users_userid
        unique (user_id)
);

create table roles
(
    id          bigint auto_increment
        primary key,
    role_id     varchar(255)                          not null,
    role_name   varchar(20)                           not null,
    description varchar(255)                          null,
    created_at  timestamp default current_timestamp() null,
    updated_at  timestamp default current_timestamp() null on update current_timestamp(),
    constraint uc_roles_roleid
        unique (role_id)
);

create table user_roles
(
    id         bigint auto_increment
        primary key,
    role_id    varchar(255)                          not null,
    user_id    varchar(255)                          not null,
    created_at timestamp default current_timestamp() null,
    updated_at timestamp default current_timestamp() null on update current_timestamp(),
    constraint UK_USER_ROLE
        unique (role_id, user_id)
);

create table my_topic_users
(
    id            bigint not null,
    user_id       text   not null,
    name          text   not null,
    email         text   not null,
    encrypted_pwd text   not null
);
