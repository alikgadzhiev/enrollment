CREATE TABLE IF NOT EXISTS shopunits(
    id varchar(255) NOT NULL UNIQUE,
    name varchar(255) NOT NULL,
    date varchar(255) NOT NULL,
    parentid varchar(255) NULL,
    type varchar(255) NOT NULL,
    price BIGINT NULL
) ;

CREATE TABLE IF NOT EXISTS connections(
    childid varchar(255) NOT NULL,
    parentid varchar(255) NULL
) ;

CREATE TABLE IF NOT EXISTS updates(
    id varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    date varchar(255) NOT NULL,
    parentid varchar(255) NULL,
    type varchar(255) NOT NULL,
    price BIGINT NULL
) ;

