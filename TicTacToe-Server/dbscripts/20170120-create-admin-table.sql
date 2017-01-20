-- -Author: AAMer
-- -Date: 20-01-2017
-- -Description: create admin table


create table admins(
    id int auto_increment,
    display_name varchar (100) not null,
    email varchar (100) not null unique,
    password varchar (255) not null,
    primary key (id)
);