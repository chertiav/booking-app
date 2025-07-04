INSERT INTO telegram_links (id, user_id, token, expires_at)
VALUES (1, 2, 'current-test-token', (CURRENT_TIMESTAMP AT TIME ZONE 'UTC') + interval '10 minutes'),
       (2, 3, 'expired-test-token', (CURRENT_TIMESTAMP AT TIME ZONE 'UTC') - interval '10 minutes');