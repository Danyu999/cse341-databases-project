--Updates the vehicle's total price to reflect the default cost of its options_prices setup
create or replace TRIGGER UPDATE_PRICE_NEW_VEHICLE 
BEFORE INSERT ON VEHICLE_OPTIONS_PRICES for each row
BEGIN
  update vehicle V set total_price = 
    (select price from options_prices where option_id = :new.option_id) where V.vec_id = :new.vec_id;
END;