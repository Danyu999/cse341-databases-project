--when an option_price's price changes, updates the vehicles with that corresponding option_id
CREATE OR REPLACE TRIGGER UPDATE_PRICE_OPTIONS 
BEFORE UPDATE OF PRICE ON OPTIONS_PRICES for each row
BEGIN
  update vehicle
    set total_price = :new.price - discount
    where exists (select * from vehicle_options_prices where option_id = :new.option_id);
END;