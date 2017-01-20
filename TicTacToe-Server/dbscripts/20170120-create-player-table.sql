
-- -Author: AAMer
-- -Date: 20-01-2017
-- -Description: create player table


create table players(
    id int auto_increment,
    display_name varchar (100) not null,
    email varchar (100) not null unique,
    password varchar (255) not null,
    points int default 0,
    status char (5),
    primary key (id)
);