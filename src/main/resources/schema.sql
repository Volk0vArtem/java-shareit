drop table if exists users, items, bookings cascade;

CREATE TABLE  if not exists users (
  id integer generated by default as identity not NULL PRIMARY KEY,
  email varchar(200) not NULL unique,
  name varchar(200) not NULL
);

CREATE TABLE if not exists items (
  id integer generated by default as identity not NULL PRIMARY KEY,
  name varchar(200) not NULL,
  description varchar(200) not NULL,
  user_id integer not NULL,
  is_available boolean,
  request_id integer
);

CREATE TABLE  if not exists bookings (
  id integer generated by default as identity not NULL PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE not null,
  end_date TIMESTAMP WITHOUT TIME ZONE not null,
  item_id integer not null,
  booker_id integer not null,
  status varchar(30) not null
);


ALTER TABLE items ADD FOREIGN KEY (user_id) REFERENCES users (id) on delete cascade;
ALTER TABLE bookings ADD FOREIGN KEY (booker_id) REFERENCES users (id) on delete cascade;
ALTER TABLE bookings ADD FOREIGN KEY (item_id) REFERENCES items (id) on delete cascade;