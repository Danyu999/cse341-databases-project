--updates the condition to be great when a vehicle is repaired
CREATE OR REPLACE TRIGGER FIX_VEHICLE_AFTER_REPAIR 
AFTER INSERT ON REPAIRS for each row
BEGIN
  update vehicle set condition = 'great' where vec_id = :new.vec_id;
END;