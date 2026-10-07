-- Upgrade an older HotelReservationSystem database without deleting reservations.
-- Run this as the same Oracle user configured in db.properties.

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE reservations ADD (status VARCHAR2(12) DEFAULT ''BOOKED'' NOT NULL)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1430 THEN
            RAISE;
        END IF;
END;
/

UPDATE reservations
SET status = 'BOOKED'
WHERE status IS NULL;

COMMIT;
