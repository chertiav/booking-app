INSERT INTO bookings (id, check_in, check_out, accommodation_id, user_id, status)
VALUES (1, CURRENT_DATE, CURRENT_DATE + interval '5 days', 1, 2, 'PENDING'),
       (2, CURRENT_DATE, CURRENT_DATE + interval '5 days', 2, 3,'CANCELED'),
       (3, CURRENT_DATE, CURRENT_DATE + interval '5 days', 2, 3,'EXPIRED'),
       (4, CURRENT_DATE + interval '10 days', CURRENT_DATE + interval '25 days', 2, 2, 'CONFIRMED'),
       (5, CURRENT_DATE, CURRENT_DATE + interval '5 days', 2, 3, 'PENDING');
