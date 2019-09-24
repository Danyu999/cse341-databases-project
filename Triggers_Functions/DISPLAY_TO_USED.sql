--when a vehicle leaves display, update it to be used
CREATE OR REPLACE TRIGGER DISPLAY_TO_USED 
BEFORE DELETE ON LOCATION_DISPLAY for each row
BEGIN
  update vehicle set status = 'used' where vec_id = :new.vec_id;
END;