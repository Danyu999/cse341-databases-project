--sets the vehicle to ownerless when Alset buys back a vehicle
CREATE OR REPLACE TRIGGER UPDATE_VEC_TRANSACTION_BUY 
BEFORE INSERT ON TRANSACTIONS_BUY for each row
BEGIN
  update vehicle set customer_id = null where vec_id = :new.vec_id;
END;