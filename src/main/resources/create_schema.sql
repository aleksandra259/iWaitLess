DROP DATABASE IF EXISTS `IWAITLESS`;
CREATE DATABASE `IWAITLESS`;
USE `IWAITLESS`;

create table users(
	username varchar(50) not null primary key,
	password varchar(500) not null,
	enabled boolean not null
);

create table authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);

create table HT_MENU_CATEGORY (
id varchar(50) not null,
order_no int,
name varchar(100) not null
);
create table HT_NOTIFICATION_STATUS (
id varchar(50) not null,
name varchar(100) not null
);
create table HT_NOTIFICATION_TYPES (
id varchar(50) not null,
name varchar(100) not null
);
create table HT_ORDER_STATUS (
id varchar(50) not null,
name varchar(100) not null
);
create table HT_PAYMENT_WAY (
id varchar(50) not null,
name varchar(100) not null
);
create table HT_STAFF_ROLE (
id varchar(50) not null,
name varchar(100) not null
);

CREATE TABLE MENU_ITEM (
  item_id int NOT NULL primary key,
  name VARCHAR(50),
  description VARCHAR(4000),
  category VARCHAR(50),
  price double,
  currency VARCHAR(3),
  size double,
  time_to_process double,
  available boolean DEFAULT true,
  constraint fk_menu_category foreign key(category) references HT_MENU_CATEGORY(id)
);

CREATE TABLE STAFF (
  employee_id int NOT NULL primary key,
  first_name varchar(50),
  last_name varchar(50),
  email varchar(50),
  phone varchar(20),
  address varchar(250),
  birthdate Date,
  role varchar(50),
  username varchar(50),
  constraint fk_staff_role foreign key(role) references ht_staff_role(item_id)
);

CREATE TABLE USER_STAFF_RELATION (
  id int NOT NULL primary key,
  username varchar(50),
  employee_id int,
  constraint fk_staff_employee foreign key(employee_id) references staff(employee_id)
);

CREATE TABLE RESTAURANT_TABLE (
  table_id int NOT NULL primary key,
  table_no varchar(50) not null,
  description varchar(250),
  qr_code varchar(250)
);

CREATE TABLE TABLE_EMPLOYEE_RELATION (
  id int NOT NULL primary key,
  table_id int,
  employee_id int,
  constraint fk_table_rel_employee foreign key(employee_id) references staff(employee_id),
  constraint fk_table_rel_table foreign key(table_id) references RESTAURANT_TABLE(table_id)
);

CREATE TABLE ORDERS (
  order_no int NOT NULL primary key,
  table_relation_id int,
  ordered_on date,
  status varchar(50),
  payment_way varchar(50),
  constraint fk_table_rel_order foreign key(table_relation_id) references TABLE_EMPLOYEE_RELATION(id),
  constraint fk_order_status foreign key(status) references ht_order_status(id),
  constraint fk_order_pay_way foreign key(payment_way) references ht_payment_way(id)
);

CREATE TABLE ORDER_DETAILS (
  detail_id int NOT NULL primary key,
  order_no int,
  item_id int,
  quantity int,
  comment varchar(500),
  status varchar(50),
  constraint fk_order_menu_item foreign key(item_id) references menu_item(item_id),
  constraint fk_order_detail_status foreign key(status) references ht_order_status(id),
  constraint fk_order_to_detail foreign key(order_no) references orders(order_no)
);

CREATE TABLE NOTIFICATIONS (
  id int NOT NULL primary key,
  employee_id int,
  type varchar(50),
  status varchar(50),
  constraint fk_employee_notification foreign key(employee_id) references staff(employee_id),
  constraint fk_notification_status foreign key(status) references ht_notification_status(id),
  constraint fk_notification_type foreign key(type) references ht_notification_type(id)
);