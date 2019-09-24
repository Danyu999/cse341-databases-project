create or replace TRIGGER UPDATE_VEC_TRANSACTION_SELL
AFTER INSERT ON TRANSACTIONS_SELL for each row
BEGIN
  update vehicle set customer_id = :new.customer_id, status = 'used' where vec_id = :new.vec_id;
END;