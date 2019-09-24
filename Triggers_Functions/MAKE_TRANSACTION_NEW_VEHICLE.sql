--make a transaction_make tuple whenever a new vehicle is made
create or replace TRIGGER MAKE_TRANSACTION_NEW_VEHICLE
AFTER INSERT ON VEHICLE_OPTIONS_PRICES for each row
BEGIN
  insert into transactions_make (vec_id, date_transaction, total_cost) values(:new.vec_id, sysdate, (select price from options_prices where option_id = :new.option_id) * .8);
END;