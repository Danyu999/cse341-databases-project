--"sends" an email to the customer when the condition of their car becomes bad if vehicle is owned by a customer
create or replace TRIGGER CONDITION_EMAIL 
AFTER UPDATE OF CONDITION ON VEHICLE for each row
BEGIN
    if(:new.customer_id > 0 and (:new.condition = 'bad' 
    or (:new.condition = 'used' and :old.condition = 'great'))) then
        insert into email_maintenance (vec_id, customer_id, email, date_email, condition)
            values(
                :new.vec_id, 
                :new.customer_id, 
                (select email from customer where customer_id = :new.customer_id),
                (select CURRENT_DATE from dual),
                :new.condition
                );
    end if;
END;