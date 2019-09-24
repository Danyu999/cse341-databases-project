--INSERT DATA
--address_id, address, city, state_province, country, planet
insert into address (address, city, state_province, country, planet) values('5151 Coolguy Street', 'Chillville', 'State1', 'United Affiliation of People', 'Moon');
insert into address (address, city, state_province, country, planet) values('4000 Wattsup Road', 'Electriton', 'Electrons', 'Zap Nation', 'Earth');
insert into address (address, city, state_province, country, planet) values('6666 Icy Avenue', 'Chillville', 'State1', 'United Affiliation of People', 'Moon');
insert into address (address, city, state_province, country, planet) values('9999 Wintershere Street', 'Coldtown', 'State2', 'United Affiliation of People', 'Moon');
insert into address (address, city, state_province, country, planet) values('8787 Greeny Road', 'Ivyton', 'Gothamiote', 'DC United', 'Venus');
insert into address (address, city, state_province, country, planet) values('4201 Sliding Lane', 'Birdsville', 'Penguin', 'DC United', 'Venus');
insert into address (address, city, state_province, country, planet) values('9650 Poison Street', 'Baneston', 'Penguin', 'DC United', 'Venus');
insert into address (address, city, state_province, country, planet) values('9750 Poison Street', 'Baneston', 'Penguin', 'DC United', 'Venus');
insert into address (address, city, state_province, country, planet) values('1000 Wayne Drive', 'Gotham', 'Gothamiote', 'DC United', 'Venus');
insert into address (address, city, state_province, country, planet) values('8700 Icy Road', 'Birdsville', 'Penguin', 'DC United', 'Venus'); --10
insert into address (address, city, state_province, country, planet) values('4200 Wattsup Road', 'Electriton', 'Electrons', 'Zap Nation', 'Earth');
insert into address (address, city, state_province, country, planet) values('1500 Fast Lane', 'Speedstown', 'Electrons', 'Zap Nation', 'Earth');
insert into address (address, city, state_province, country, planet) values('1000 Standard Street', 'Boreston', 'State2', 'United Affiliation of People', 'Moon');
insert into address (address, city, state_province, country, planet) values('1600 Main Street', 'Gotham', 'Gothamiote', 'DC United', 'Venus');

--location_id, address_id
insert into location (address_id) values(7);
insert into location (address_id) values(4);
insert into location (address_id) values(2);
insert into location (address_id) values(14);

--customer_id, name, email, address_id, password
insert into customer (name, email, address_id, password) values('Bob Dude', 'BobbyDuddy@gmail.com', 1, GET_HASH('bobbyduddy@gmail.com', 'bobiscool'));
insert into customer (name, email, address_id, password) values('Selina Kyle', 'kitty@hotmail.com', 5, GET_HASH('kitty@hotmail.com', 'catwoman'));
insert into customer (name, email, address_id, password) values('Robin Sidekick', 'bestbuddy@gmail.com', 6, GET_HASH('bestbuddy@gmail.com', 'notbatman'));
insert into customer (name, email, address_id, password) values('Jane Doe', 'givememoney@yahoo.com', 3, GET_HASH('givememoney@yahoo.com', 'willcode4money'));
insert into customer (name, email, address_id, password) values('John Doe', 'takemymoney@yahoo.com', 8, GET_HASH('takemymoney@yahoo.com', 'imrich'));
insert into customer (name, email, address_id, location_id, password) values('Bruce Wayne', 'richboy@gmail.com', 9, 4, GET_HASH('richboy@gmail.com', 'imbatman'));
insert into customer (name, email, address_id, password) values('Victor Fries', 'MrFreeze@gmail.com', 10, GET_HASH('mrfreeze@gmail.com', 'itscold'));
insert into customer (name, email, address_id, password) values('Laxus Tale', 'zapdragon@hotmail.com', 11, GET_HASH('zapdragon@hotmail.com', 'Electrifying'));
insert into customer (name, email, address_id, location_id, password) values('Barry Allen', 'fastestman@gmail.com', 12, 3, GET_HASH('fastestman@gmail.com', 'imtheFlash'));
insert into customer (name, email, address_id, password) values('Sarah Smith', 'SarahSmith@gmail.com', 13, GET_HASH('sarahsmith@gmail.com', 'password'));

--card_number, customer_id, card_holder, csv, exp_date, address_id
insert into payment_card values(1111222233334444, 1, 'Bob Dude', 101, '09/25', NULL); --null should default to customer address
insert into payment_card values(7070606050504040, 2, 'Jane Doe', 404, '10/24', 3);
insert into payment_card values(5151222297970000, 3, 'Robin Sidekick', 222, '11/24', NULL);
insert into payment_card values(7070606050504040, 4, 'Jane Doe', 404, '10/24', 3);
insert into payment_card values(3842509313417384, 5, 'John Doe', 858, '11/26', NULL);
insert into payment_card values(4599948234882121, 6, 'Bruce Wayne', 553, '07/24', 9);
insert into payment_card values(6574589234769654, 6, 'Bruce Wayne', 909, '08/25', 9);
insert into payment_card values(1236956900076523, 6, 'Bruce Wayne', 338, '04/25', 9);
insert into payment_card values(1212323335486868, 7, 'Victor Fries', 513, '02/25', 10);
insert into payment_card values(9909044283233432, 8, 'Laxus Tale', 562, '01/27', NULL);
insert into payment_card values(7776455777754577, 9, 'Barry Allen', 853, '09/25', NULL);
insert into payment_card values(4454454421112112, 10, 'Sarah Smith', 124, '07/26', NULL);

--vec_id, customer_id, model, year, status, discount, condition, total_price --trigger to calculate total price when discount changes
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'S', 2015, 'new', 0, 'great', 850000); 
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(3, 'S', 2015, 'used', 30000, 'great', 820000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(2, 'S', 2015, 'used', 60000, 'great', 700000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'S', 2015, 'used', 30000, 'great', 820000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'S', 2015, 'new', 0, 'great', 750000);
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'S', 2015, 'new', 0, 'great', 750000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'S', 2015, 'new', 0, 'great', 650000);
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'S', 2015, 'used', 10000, 'great', 840000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(1, 'M', 2015, 'used', 100000, 'bad', 350000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'K', 2015, 'used', 60000, 'okay', 460000);-- --10
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'K', 2015, 'used', 20000, 'great', 430000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'K', 2015, 'new', 0, 'great', 520000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'K', 2015, 'new', 0, 'great', 520000);
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'K', 2015, 'used', 15000, 'great', 465000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'M', 2016, 'new', 0, 'great', 430000);
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'M', 2016, 'used', 50000, 'okay', 370000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'M', 2016, 'used', 25000, 'great', 425000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'M', 2016, 'new', 0, 'great', 450000);
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(2, 'U', 2017, 'used', 25000, 'great', 595000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'U', 2017, 'new', 0, 'great', 620000);-- --20
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'U', 2017, 'new', 0, 'great', 620000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'U', 2017, 'used', 20000, 'okay', 480000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'U', 2017, 'used', 15000, 'great', 445000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'S', 2015, 'used', 25000, 'okay', 825000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(6, 'S', 2015, 'used', 20000, 'great', 830000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(6, 'S', 2015, 'used', 25000, 'okay', 825000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(6, 'S', 2015, 'used', 10000, 'great', 640000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(6, 'U', 2017, 'used', 30000, 'great', 590000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(6, 'U', 2017, 'used', 40000, 'bad', 360000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(7, 'S', 2015, 'used', 10000, 'great', 840000);-- --30
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'S', 2015, 'used', 10000, 'great', 740000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'U', 2017, 'used', 10000, 'great', 490000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'S', 2015, 'new', 0, 'great', 750000);--
insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(NULL, 'U', 2017, 'new', 0, 'great', 620000);--

--option_id, auto_pilot, ejector_seats, phone_sync, model, year, price
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(1, 1, 0, 'S', 2015, 850000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(1, 0, 0, 'S', 2015, 760000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(0, 1, 0, 'S', 2015, 750000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(0, 0, 0, 'S', 2015, 650000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(1, 0, 1, 'U', 2017, 620000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(1, 0, 0, 'U', 2017, 500000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(0, 0, 1, 'U', 2017, 460000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(0, 0, 0, 'U', 2017, 400000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(1, 1, 0, 'M', 2016, 450000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(1, 0, 0, 'M', 2016, 430000); --10
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(0, 1, 0, 'M', 2016, 420000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(0, 0, 0, 'M', 2016, 400000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(1, 0, 1, 'K', 2015, 520000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(0, 0, 1, 'K', 2015, 480000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(1, 0, 0, 'K', 2015, 500000);
insert into options_prices (auto_pilot, ejector_seats, phone_sync, model, year, price) values(0, 0, 0, 'K', 2015, 450000);

--vec_id, option_id, model, year
insert into vehicle_options_prices values(1, 1, 'S', 2015);
insert into vehicle_options_prices values(2, 1, 'S', 2015);
insert into vehicle_options_prices values(3, 2, 'S', 2015);
insert into vehicle_options_prices values(4, 1, 'S', 2015);
insert into vehicle_options_prices values(5, 3, 'S', 2015);
insert into vehicle_options_prices values(6, 3, 'S', 2015);
insert into vehicle_options_prices values(7, 4, 'S', 2015);
insert into vehicle_options_prices values(8, 1, 'S', 2015);
insert into vehicle_options_prices values(9, 9, 'M', 2015);
insert into vehicle_options_prices values(10, 13, 'K', 2015);
insert into vehicle_options_prices values(11, 16, 'K', 2015);
insert into vehicle_options_prices values(12, 13, 'K', 2015);
insert into vehicle_options_prices values(13, 13, 'K', 2015);
insert into vehicle_options_prices values(14, 14, 'K', 2015);
insert into vehicle_options_prices values(15, 10, 'M', 2016);
insert into vehicle_options_prices values(16, 11, 'M', 2016);
insert into vehicle_options_prices values(17, 9, 'M', 2016);
insert into vehicle_options_prices values(18, 9, 'M', 2016);
insert into vehicle_options_prices values(19, 5, 'U', 2017);
insert into vehicle_options_prices values(20, 5, 'U', 2017);
insert into vehicle_options_prices values(21, 5, 'U', 2017);
insert into vehicle_options_prices values(22, 6, 'U', 2017);
insert into vehicle_options_prices values(23, 7, 'U', 2017);
insert into vehicle_options_prices values(24, 1, 'S', 2015);
insert into vehicle_options_prices values(25, 1, 'S', 2015);
insert into vehicle_options_prices values(26, 1, 'S', 2015);
insert into vehicle_options_prices values(27, 4, 'S', 2015);
insert into vehicle_options_prices values(28, 5, 'U', 2017);
insert into vehicle_options_prices values(29, 8, 'U', 2017);
insert into vehicle_options_prices values(30, 1, 'S', 2015);
insert into vehicle_options_prices values(31, 3, 'S', 2015);
insert into vehicle_options_prices values(32, 6, 'U', 2017);
insert into vehicle_options_prices values(33, 3, 'S', 2015);
insert into vehicle_options_prices values(34, 5, 'U', 2017);

--transaction_id, customer_id, vec_id, date_transaction, card_number, total_cost
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(3, 2, TO_DATE('2015/05/03', 'yyyy/mm/dd'), 5151222297970000, 850000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(2, 3, TO_DATE('2016/12/22', 'yyyy/mm/dd'), 7070606050504040, 760000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(1, 9, TO_DATE('2017/01/14', 'yyyy/mm/dd'), 1111222233334444, 450000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(2, 19, TO_DATE('2014/07/30', 'yyyy/mm/dd'), 7070606050504040, 620000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(2, 24, TO_DATE('2017/01/01', 'yyyy/mm/dd'), 7070606050504040, 850000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 25, TO_DATE('2015/04/20', 'yyyy/mm/dd'), 4599948234882121, 850000); --Bruce Wayne vehicles
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 26, TO_DATE('2015/01/31', 'yyyy/mm/dd'), 4599948234882121, 850000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 27, TO_DATE('2017/09/13', 'yyyy/mm/dd'), 4599948234882121, 650000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 28, TO_DATE('2014/07/30', 'yyyy/mm/dd'), 6574589234769654, 620000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 29, TO_DATE('2017/11/10', 'yyyy/mm/dd'), 6574589234769654, 400000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 30, TO_DATE('2015/12/17', 'yyyy/mm/dd'), 6574589234769654, 850000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 31, TO_DATE('2017/12/25', 'yyyy/mm/dd'), 6574589234769654, 750000);
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 32, TO_DATE('2015/12/27', 'yyyy/mm/dd'), 1236956900076523, 500000); --end Wayne vehicles
insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(7, 30, TO_DATE('2017/12/24', 'yyyy/mm/dd'), 1212323335486868, 840000);

--transaction_id, customer_id, vec_id, date_transaction, card_number, total_cost
insert into transactions_buy (customer_id, vec_id, date_transaction, card_number, total_cost) values(2, 24, TO_DATE('2017/03/14', 'yyyy/mm/dd'), 7070606050504040, 800000);
insert into transactions_buy (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 30, TO_DATE('2016/10/15', 'yyyy/mm/dd'), 6574589234769654, 790000);
insert into transactions_buy (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 31, TO_DATE('2015/12/30', 'yyyy/mm/dd'), 6574589234769654, 650000);
insert into transactions_buy (customer_id, vec_id, date_transaction, card_number, total_cost) values(6, 32, TO_DATE('2015/04/09', 'yyyy/mm/dd'), 1236956900076523, 450000);

--transaction_id, vec_id, date_transaction, total_cost
insert into transactions_make (vec_id, date_transaction, total_cost) values(1, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 850000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(2, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 850000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(3, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 760000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(4, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 850000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(5, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 750000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(6, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 750000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(7, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 650000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(8, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 850000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(9, TO_DATE('2016/01/01', 'yyyy/mm/dd'), 450000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(10, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 520000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(11, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 450000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(12, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 520000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(13, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 520000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(14, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 480000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(15, TO_DATE('2016/01/01', 'yyyy/mm/dd'), 430000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(16, TO_DATE('2016/01/01', 'yyyy/mm/dd'), 420000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(17, TO_DATE('2016/01/01', 'yyyy/mm/dd'), 450000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(18, TO_DATE('2016/01/01', 'yyyy/mm/dd'), 450000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(19, TO_DATE('2017/01/01', 'yyyy/mm/dd'), 620000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(20, TO_DATE('2017/01/01', 'yyyy/mm/dd'), 620000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(21, TO_DATE('2017/01/01', 'yyyy/mm/dd'), 620000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(22, TO_DATE('2017/01/01', 'yyyy/mm/dd'), 500000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(23, TO_DATE('2017/01/01', 'yyyy/mm/dd'), 460000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(24, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 850000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(25, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 850000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(26, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 850000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(27, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 650000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(28, TO_DATE('2017/01/01', 'yyyy/mm/dd'), 620000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(29, TO_DATE('2017/01/01', 'yyyy/mm/dd'), 400000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(30, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 850000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(31, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 750000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(32, TO_DATE('2017/01/01', 'yyyy/mm/dd'), 500000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(33, TO_DATE('2015/01/01', 'yyyy/mm/dd'), 750000 * .8);
insert into transactions_make (vec_id, date_transaction, total_cost) values(34, TO_DATE('2017/01/01', 'yyyy/mm/dd'), 620000 * .8);

--location_id, day, open_hr, open_min, close_hr, close_min
insert into hours values(1, 'M', 8, 30, 18, 0);
insert into hours values(1, 'T', 8, 0, 18, 0);
insert into hours values(1, 'W', 8, 30, 18, 0);
insert into hours values(1, 'R', 8, 0, 18, 0);
insert into hours values(1, 'F', 9, 0, 18, 0);
insert into hours values(2, 'M', 6, 0, 16, 0);
insert into hours values(2, 'T', 6, 0, 16, 0);
insert into hours values(2, 'W', 6, 0, 16, 0);
insert into hours values(2, 'R', 6, 0, 16, 0);
insert into hours values(2, 'F', 8, 0, 18, 0);
insert into hours values(3, 'M', 7, 0, 17, 0);
insert into hours values(3, 'T', 7, 0, 17, 0);
insert into hours values(3, 'W', 7, 0, 17, 0);
insert into hours values(3, 'R', 7, 0, 17, 0);
insert into hours values(3, 'F', 8, 0, 18, 0);
insert into hours values(4, 'M', 8, 30, 18, 0);
insert into hours values(4, 'T', 8, 0, 18, 0);
insert into hours values(4, 'W', 8, 30, 18, 0);
insert into hours values(4, 'R', 8, 0, 18, 0);
insert into hours values(4, 'F', 9, 0, 18, 0);

--location_id, model
insert into location_models values(1, 'U');
insert into location_models values(1, 'S');
insert into location_models values(2, 'S');
insert into location_models values(2, 'M');
insert into location_models values(3, 'U');
insert into location_models values(3, 'S');
insert into location_models values(3, 'K');
insert into location_models values(4, 'U');
insert into location_models values(4, 'S');

--location_id, vec_id
insert into location_display values(1, 21); --trigger to set to used and set discount when vehicle is added to here
insert into location_display values(1, 5);
insert into location_display values(2, 6);
insert into location_display values(2, 12);
insert into location_display values(3, 20);
insert into location_display values(3, 4);
insert into location_display values(3, 11);
insert into location_display values(4, 33);
insert into location_display values(4, 34);

--location_id, vec_id, customer_id
insert into location_pickup values(1, 28, 6);
insert into location_pickup values(4, 30, 7);

--location_id, vec_id
insert into location_used values(1, 24);
insert into location_used values(1, 22);
insert into location_used values(2, 16);
insert into location_used values(2, 17);
insert into location_used values(3, 10);
insert into location_used values(3, 14);
insert into location_used values(4, 8);
insert into location_used values(4, 23);

--location_id, vec_id, date_repair, cost
insert into repairs values(1, 2, TO_DATE('2017/08/10', 'yyyy/mm/dd'), 1000);
insert into repairs values(1, 2, TO_DATE('2017/12/22', 'yyyy/mm/dd'), 500);
insert into repairs values(2, 9, TO_DATE('2017/11/23', 'yyyy/mm/dd'), 7000);
insert into repairs values(1, 27, TO_DATE('2017/09/22', 'yyyy/mm/dd'), 5000);
insert into repairs values(4, 28, TO_DATE('2017/09/26', 'yyyy/mm/dd'), 3000);

--email_id, vec_id, customer_id, email, date_email, conditon
insert into email_maintenance (vec_id, customer_id, email, date_email, condition) values(9, 1, 'bobbyduddy@gmail.com', TO_DATE('2017/01/30', 'yyyy/mm/dd'), 'okay');
insert into email_maintenance (vec_id, customer_id, email, date_email, condition) values(9, 1, 'bobbyduddy@gmail.com', TO_DATE('2017/06/10', 'yyyy/mm/dd'), 'bad');
insert into email_maintenance (vec_id, customer_id, email, date_email, condition) values(26, 6, 'richboy@gmail.com', TO_DATE('2017/08/11', 'yyyy/mm/dd'), 'okay');
insert into email_maintenance (vec_id, customer_id, email, date_email, condition) values(27, 6, 'richboy@gmail.com', TO_DATE('2016/11/02', 'yyyy/mm/dd'), 'okay');
insert into email_maintenance (vec_id, customer_id, email, date_email, condition) values(27, 6, 'richboy@gmail.com', TO_DATE('2017/08/22', 'yyyy/mm/dd'), 'bad');
insert into email_maintenance (vec_id, customer_id, email, date_email, condition) values(28, 6, 'richboy@gmail.com', TO_DATE('2017/04/20', 'yyyy/mm/dd'), 'okay');
insert into email_maintenance (vec_id, customer_id, email, date_email, condition) values(29, 6, 'richboy@gmail.com', TO_DATE('2016/03/06', 'yyyy/mm/dd'), 'okay');
insert into email_maintenance (vec_id, customer_id, email, date_email, condition) values(29, 6, 'richboy@gmail.com', TO_DATE('2017/09/11', 'yyyy/mm/dd'), 'bad');