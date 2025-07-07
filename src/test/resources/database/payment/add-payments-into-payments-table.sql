INSERT INTO payments (id, status, booking_id, session_url, session_id, amount_to_pay)
VALUES (1, 'PENDING', 1, 'session_id_1234567890', '1234567890', 75.50),
       (2, 'PAID', 2, 'session_id_2345678901', '2345678901', 100.50),
       (3, 'EXPIRED', 5, 'session_id_3456789012', '3456789012', 100.50);