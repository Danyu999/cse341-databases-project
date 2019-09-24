/*CREATE TABLES*/
create table address( --needs to be created before customer or payment_card
    address_id          number(5) generated always as identity
                        minvalue 1
                        maxvalue 99999
                        increment by 1 start with 1
                        cycle
                        cache 10,
    address             varchar(25) not null,
    city                varchar(25),
    state_province      varchar(25),
    country             varchar(30),
    planet              varchar(20) not null,
    primary key(address_id)
);

create table location(
    location_id     number(5) generated always as identity
                        minvalue 1
                        maxvalue 99999
                        increment by 1 start with 1
                        cycle
                        cache 10,
    address_id      number(5) not null,
    primary key(location_id),
    foreign key(address_id) references address
);

create table customer(
    customer_id         number(5) generated always as identity
                        minvalue 1
                        maxvalue 99999
                        increment by 1 start with 1
                        cycle
                        cache 10,
    name                varchar(20) not null,
    email               varchar(30) not null unique, --must be unique
    address_id          number(5) not null,
    location_id         number(5) default 0, --represents the preferred location of the customer for ease
    password            varchar(40) not null, --hashed with md5
    primary key(customer_id),
    foreign key(address_id) references address
    --foreign key(location_id) references location --Not possible because we want 0 to represent no preferred location set
);

create table payment_card( --insertion handled by interface (ensure occurs after customer creation)
    card_number      numeric(16),
    customer_id      number(5),
    card_holder      varchar(20) not null,
    csv              numeric(3) not null,
    exp_date         varchar(5) not null, --mm/yy
    address_id       number(5), --billing address, if null, use customer's address
    primary key(card_number, customer_id),
    foreign key(customer_id) references customer,
    foreign key(address_id) references address
);

create table vehicle(
    vec_id          number(5) generated always as identity
                        minvalue 1
                        maxvalue 99999
                        increment by 1 start with 1
                        cycle
                        cache 10,
    customer_id     number(5), --null if not sold to a customer yet, has id otherwise. Trigger to set when transaction occurs
    model           varchar(1) not null check(model in ('M','U','S','K')),
    year            numeric(4) not null,
    status          varchar(4) not null check(status in ('used','new')),
    discount        numeric(10) not null check(discount >= 0), --discount for coupons/sales or used status. Discount set by onsite employee after inspection
    condition       varchar(10) not null check(condition in ('great', 'okay', 'bad')), --Great, Okay(get a checkup if wanted), Bad(need to bring in for maintenance). Use trigger to send email if becomes bad
    total_price     numeric(10) not null check(total_price >= 0), --use trigger to calculate total_price if discount or options_prices price changes
    primary key(vec_id),
    foreign key(customer_id) references customer
);

create table options_prices(
    option_id       number(5) generated always as identity
                        minvalue 1
                        maxvalue 99999
                        increment by 1 start with 1
                        cycle
                        cache 10,
    auto_pilot      numeric(1), --does not have if 0 or null. Can cost different amount for each model. Bundles of different options cost differently too (discounted)
    ejector_seats   numeric(1),
    phone_sync      numeric(1),
    model           varchar(1) not null check(model in ('M','U','S','K')), --need to check that each vehicle that is related to options_prices matchs model and year
    year            numeric(4) not null,
    price           numeric(10) not null, --set price for each combination of model, year, and options
    primary key(option_id)
);

create table vehicle_options_prices( --have to match model and year
    vec_id          number(5), --customer interface can search for an existing car or design their own car to be made
    option_id       number(5),
    model           varchar(1) check(model in ('M','U','S','K')),
    year            numeric(4),
    primary key(vec_id, option_id, model, year),
    foreign key(vec_id) references vehicle,
    foreign key(option_id) references options_prices
            on delete cascade
);

create table transactions_sell( --sell vehicle to a customer
    transaction_id      number(5) generated always as identity
                        minvalue 1
                        maxvalue 33333
                        increment by 1 start with 1
                        cycle
                        cache 10,
    customer_id         number(5) not null,
    vec_id              number(5) not null,
    date_transaction    date not null,
    card_number         numeric(16) not null,
    total_cost          numeric(10) not null, --calculate from options_prices price and discount
    primary key(transaction_id),
    foreign key(customer_id) references customer,
    foreign key(vec_id) references vehicle,
    foreign key(card_number, customer_id) references payment_card
);

create table transactions_buy( --done at a service location. Updated with worker interface
    transaction_id      number(5) generated always as identity
                        minvalue 33334
                        maxvalue 66666
                        increment by 1 start with 33334
                        cycle
                        cache 10,
    customer_id         number(5) not null,
    vec_id              number(5) not null,
    date_transaction    date not null,
    card_number         numeric(16) not null, --customer has to have valid debit card on record
    total_cost          numeric(10) not null, --calculate from options_prices price and discount
    primary key(transaction_id),
    foreign key(customer_id) references customer,
    foreign key(vec_id) references vehicle,
    foreign key(card_number, customer_id) references payment_card
);

create table transactions_make( --occurs when a vehicle is created. can be used (total cost) to calculate Alset total spent
    transaction_id      number(5) generated always as identity
                        minvalue 66667
                        maxvalue 99999
                        increment by 1 start with 66667
                        cycle
                        cache 10,
    vec_id              number(5) not null,
    date_transaction    date not null,
    total_cost          numeric(10) not null, --calculate from options_prices price and discount
    primary key(transaction_id),
    foreign key(vec_id) references vehicle
);

create table email_maintenance(
    email_id    number(5) generated always as identity
                        minvalue 1
                        maxvalue 99999
                        increment by 1 start with 1
                        cycle
                        cache 10,
    vec_id      number(5) not null,
    customer_id number(5) not null,
    email       varchar(30) not null,
    date_email  date not null,
    condition   varchar(10) not null,
    primary key(email_id),
    foreign key(vec_id) references vehicle,
    foreign key(customer_id) references customer
);

create table hours(
    location_id     number(5),
    day             varchar(1) check(day in ('M','T','W','R','F','S','U')), --if hours don't exist for a day, assume closed
    open_hr         numeric(2) check(open_hr >= 0 and open_hr < 24),
    open_min        numeric(2) check(open_min >= 0 and open_min < 60),
    close_hr        numeric(2) check(close_hr >= 0 and close_hr < 24),
    close_min       numeric(2) check(close_min >= 0 and close_min < 60),
    primary key(location_id, day),
    foreign key(location_id) references location
);

create table location_models( --check here to see what models a location supports
    location_id     number(5),
    model           varchar(10) not null check(model in ('M','U','S','K')),
    primary key(location_id, model),
    foreign key(location_id) references location
                on delete cascade
);

create table location_display( --need trigger to convert vehicle to used when removed from this table
    location_id     number(5),
    vec_id          number(5),
    primary key(location_id, vec_id),
    foreign key(location_id) references location
                on delete cascade,
    foreign key(vec_id) references vehicle
);

create table location_pickup( --when a pickup occurs, remove vehicle from here with worker interface
    location_id     number(5),
    vec_id          number(5),
    customer_id     number(5),
    primary key(location_id, vec_id, customer_id),
    foreign key(vec_id) references vehicle,
    foreign key(customer_id) references customer
);

create table location_used( --when pickup occurs, remove vehicle from here
    location_id     number(5),
    vec_id          number(5),
    primary key(location_id, vec_id),
    foreign key(location_id) references location
                on delete cascade,
    foreign key(vec_id) references vehicle
);

create table repairs(
    location_id     number(5),
    vec_id          number(5),
    date_repair     date,
    cost            numeric(10) check(cost > 0),
    primary key(location_id, vec_id, date_repair, cost),
    foreign key(location_id) references location, --a location should never be deleted
    foreign key(vec_id) references vehicle --a vehicle should never be deleted
);