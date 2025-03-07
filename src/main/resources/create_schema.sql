DROP DATABASE IF EXISTS `IWAITLESS`;
CREATE DATABASE `IWAITLESS`;
USE `IWAITLESS`;

create table users(
	username varchar(50) not null primary key,
	password varchar(500) not null,
	enabled boolean not null
);

create table authorities (
    id int AUTO_INCREMENT primary key,
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);

create table HT_MENU_CATEGORY (
id varchar(50) not null,
order_no int,
name varchar(100) not null,
constraint fk_ht_menu_category primary key(id)
);
create table HT_NOTIFICATION_STATUS (
id varchar(50) not null,
name varchar(100) not null,
constraint fk_ht_notification_status primary key(id)
);
create table HT_NOTIFICATION_TYPE (
id varchar(50) not null,
name varchar(100) not null,
constraint fk_ht_notification_type primary key(id)
);
create table HT_ORDER_STATUS (
id varchar(50) not null,
name varchar(100) not null,
constraint fk_order_status primary key(id)
);
create table HT_PAYMENT_WAY (
id varchar(50) not null,
name varchar(100) not null,
constraint fk_ht_payment_way primary key(id)
);
create table HT_STAFF_ROLE (
id varchar(50) not null,
name varchar(100) not null,
constraint fk_ht_staff_role primary key(id)
);

CREATE TABLE MENU_ITEM (
  item_id int AUTO_INCREMENT primary key,
  name VARCHAR(50),
  description VARCHAR(4000),
  category VARCHAR(50),
  price double,
  currency VARCHAR(3),
  size double,
  time_to_process double,
  available boolean DEFAULT true,
  vegetarian boolean DEFAULT false,
  vegan boolean DEFAULT false,
  constraint fk_menu_category foreign key(category) references HT_MENU_CATEGORY(id)
);

CREATE TABLE STAFF (
  employee_id int AUTO_INCREMENT primary key,
  first_name varchar(50),
  last_name varchar(50),
  email varchar(50),
  phone varchar(20),
  address varchar(250),
  birthdate Date,
  role_id varchar(50),
  username varchar(50),
  constraint fk_staff_role foreign key(role_id) references ht_staff_role(id)
);

CREATE TABLE USER_STAFF_RELATION (
  id int AUTO_INCREMENT primary key,
  username varchar(50),
  employee_id int,
  constraint fk_staff_employee foreign key(employee_id) references staff(employee_id)
);

CREATE TABLE RESTAURANT_TABLE (
  table_id int AUTO_INCREMENT primary key,
  table_no varchar(50) not null,
  description varchar(250),
  qr_code varchar(250)
);

CREATE TABLE TABLE_EMPLOYEE_RELATION (
  id int AUTO_INCREMENT primary key,
  table_id int,
  employee_id int,
  status varchar(2),
  constraint fk_table_rel_employee foreign key(employee_id) references staff(employee_id),
  constraint fk_table_rel_table foreign key(table_id) references RESTAURANT_TABLE(table_id)
);

CREATE TABLE ORDERS (
  order_no int AUTO_INCREMENT primary key,
  table_relation_id int,
  ordered_on DATETIME,
  status varchar(50),
  payment_way varchar(50),
  constraint fk_table_rel_order foreign key(table_relation_id) references TABLE_EMPLOYEE_RELATION(id),
  constraint fk_order_status foreign key(status) references ht_order_status(id),
  constraint fk_order_pay_way foreign key(payment_way) references ht_payment_way(id)
);

CREATE TABLE ORDER_DETAILS (
  detail_id int AUTO_INCREMENT primary key,
  order_no int,
  item_id int,
  quantity int,
  comment varchar(4000),
  constraint fk_order_menu_item foreign key(item_id) references menu_item(item_id),
  constraint fk_order_to_detail foreign key(order_no) references orders(order_no)
);

CREATE TABLE NOTIFICATIONS (
  notif_id int AUTO_INCREMENT primary key,
  employee_id int,
  type varchar(50),
  status varchar(50),
  table_id int,
  order_no int,
  registration_date datetime,
  constraint fk_employee_notification foreign key(employee_id) references staff(employee_id),
  constraint fk_notification_table foreign key(table_id) references restaurant_table(table_id),
  constraint fk_notification_order foreign key(order_no) references orders(order_no),
  constraint fk_notification_type foreign key(type) references ht_notification_type(id)
);

INSERT INTO users (username, password, enabled) VALUES
 ('admin','{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 1),
 ('user_waiter','{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 1),
 ('user_kitchen','{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 1);
INSERT INTO authorities (id, username, authority) VALUES
 (1, 'admin', 'ROLE_ADMIN'),
 (2, 'user_waiter', 'ROLE_USER_ST'),
 (3, 'user_kitchen', 'ROLE_USER_KT');
INSERT INTO HT_STAFF_ROLE (id, name) VALUES
 ('MG','Мениджър'),
 ('ST','Сервитьор'),
 ('KT','Кухня');
INSERT INTO STAFF (employee_id, first_name, last_name, email, phone, address, birthdate, role_id, username) VALUES
 (999999, 'dummy', null, null, null, null, null, 'ST', null),
 (999998, 'admin', null, null, null, null, null, 'MG', 'admin'),
 (999997, 'user_waiter', null, null, null, null, null, 'ST', 'user_waiter'),
 (999996, 'user_kitchen', null, null, null, null, null, 'KT', 'user_kitchen');
INSERT INTO HT_NOTIFICATION_STATUS (id, name) VALUES
 ('R','Прочетено'),
 ('U','Непрочетено');
INSERT INTO HT_NOTIFICATION_TYPE (id, name) VALUES
 ('1','Нова поръчка'),
 ('2','Повикай сервитьор'),
 ('3','Повикай сметката');
INSERT INTO HT_ORDER_STATUS (id, name) VALUES
 ('-1','Отказана'),
 ('1','Получена'),
 ('2','Приета'),
 ('3','Подготовка'),
 ('4','Проверка на качеството'),
 ('5','Готова за сервиране'),
 ('6','Завършена');
INSERT INTO HT_MENU_CATEGORY (id, order_no, name) VALUES
 ('303_5', 1, 'Закуски'),
 ('330_D', 2, 'Сандвичи'),
 ('285_P', 3, 'Салати'),
 ('297_P', 4, 'Бургери'),
 ('272_1', 5, 'Паста'),
 ('337_N', 6, 'Десерти'),
 ('272_2', 7, 'Безалкохолни напитки'),
 ('333_Z', 8, 'Алкохолни напитки');
INSERT INTO MENU_ITEM (item_id, name, description, category, price, currency, size, time_to_process, available, vegetarian, vegan) VALUES
(1, 'Класическа закуска', 'бъркани яйца, хрупкав бекон, наденички и препечен хляб', '303_5', 15.9, 'BGN', 400, 20, 1, 0, 0),
(2, 'Палачинки с кленов сироп', NULL, '303_5', 13.5, 'BGN', 300, 20, 1, 1, 0),
(3, 'Авокадо тост', 'препечена филийка с разбито авокадо, чери домати и поръсена с чили', '303_5', 14.9, 'BGN', 250, 15, 1, 1, 1),
(4, 'Баница', 'с яйце и сирене', '303_5', 8.9, 'BGN', 250, 30, 1, 1, 0),
(5, 'Клуб сандвич', 'троен сандвич с пуешко месо, бекон, маруля, домат и майонеза, сервиран с пържени картофки', '330_D', 15.9, 'BGN', 450, 40, 1, 0, 0),
(6, 'Капрезе панини', 'моцарела, домати и босилково песто между хрупкав хляб чабата', '330_D', 13.5, 'BGN', 250, 15, 1, 1, 0),
(7, 'Сандвич с риба тон', 'риба тон, зелени зеленчуци, нарязана на кубчета краставица и авокадо, увити в мека тортила', '330_D', 15.9, 'BGN', 350, 20, 1, 0, 0),
(8, 'Салата Цезар с пиле', 'гриловано пилешко филе, чери домати, чеснови крутони и пармезан, зелена салата с кремообразен дресинг', '285_P', 16.9, 'BGN', 350, 25, 1, 0, 0),
(9, 'Шопска салата', 'домати, краставици, пиперки, лук и сирене, поръсени с магданоз', '285_P', 9.9, 'BGN', 250, 15, 1, 1, 0),
(10, 'Снежанка', 'кисело мляко с краставици, чесън и магданоз, поръсена с натрошени орехи', '285_P', 6.9, 'BGN', 250, 10, 1, 1, 0),
(11, 'Бургер с говеждо месо', '100% говеждо месо, маруля, домат, лук, кисели краставички и домашно приготвен сос', '297_P', 16.9, 'BGN', 400, 30, 1, 0, 0),
(12, 'Бургер с пиле', 'пилешки гърди на скара, хрупкав лук, сирене чедър, домат, маруля и сос BBQ', '297_P', 16.9, 'BGN', 400, 30, 1, 0, 0),
(13, 'Веган бургер', 'домашно приготвено пилешко месо от черен боб и киноа, маруля, домат, лук, авокадо и пикантен айоли', '297_P', 17.9, 'BGN', 400, 30, 1, 1, 1),
(14, 'Спагети карбонара', 'с кремообразен сос от яйца, пармезан, хрупкав бекон и черен пипер', '272_1', 15.9, 'BGN', 350, 30, 1, 0, 0),
(15, 'Лазаня болонезе', 'със сос болонезе, кремообразен бешамел и топено сирене', '272_1', 15.9, 'BGN', 400, 20, 1, 0, 0),
(16, 'Баклава', 'с орехи и сироп от мед', '337_N', 7.9, 'BGN', 150, 25, 1, 1, 1),
(17, 'Палачинки', 'тънки палачинки с шоколад и банан', '337_N', 9.9, 'BGN', 150, 20, 1, 0, 0),
(18, 'Шоколадовo брауни', 'сервиранo с ванилов сладолед', '337_N', 8.9, 'BGN', 250, 20, 1, 0, 0),
(19, 'Еспресо', NULL, '272_2', 3.9, 'BGN', NULL, NULL, 1, 1, 1),
(20, 'Капучино', 'Еспресо с мляко на пара и дебел слой пяна', '272_2', 5.9, 'BGN', NULL, NULL, 1, 0, 0),
(21, 'Студено лате', 'Еспресо, смесено със студено мляко и поднесено с лед', '272_2', 5.9, 'BGN', NULL, NULL, 1, 0, 0),
(22, 'Портокалов сок', NULL, '272_2', 5.9, 'BGN', NULL, NULL, 1, 1, 1),
(23, 'Минерална вода', '500мл.', '272_2', 2.5, 'BGN', NULL, NULL, 1, 1, 1),
(24, 'Мохито', 'бял ром, пресен сок от лайм, листа от мента, обикновен сироп и газирана вода', '333_Z', 15.9, 'BGN', NULL, NULL, 1, 1, 1),
(25, 'Космополитан', 'водка, трипъл сек, сок от червена боровинка и прясно изцеден сок от лайм', '333_Z', 15.9, 'BGN', NULL, NULL, 1, 1, 1),
(26, 'Крафт бира', 'ротационна селекция от местни крафт бири', '333_Z', 5.5, 'BGN', NULL, NULL, 1, 1, 1),
(27, 'Чаша червено вино', NULL, '333_Z', 6.5, 'BGN', NULL, NULL, 1, 1, 1),
(28, 'Чаша бяло вино', NULL, '333_Z', 6.5, 'BGN', NULL, NULL, 1, 1, 1);
INSERT INTO RESTAURANT_TABLE (table_id, table_no, description, qr_code) VALUES
(1, 1, 'до прозореца, с гледка към улицата', NULL),
(2, 2, 'голяма маса, подходяща за групи, разположена в тих ъгъл', NULL),
(3, 3, 'висока маса в близост до бара', NULL),
(4, 4, 'маса до ъгъла', NULL),
(5, 5, 'маса до бара', NULL),
(9, 6, 'малка маса на терасата', NULL);
