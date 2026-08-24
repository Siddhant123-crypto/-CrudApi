-- Update duplicate emails to be unique
UPDATE farmer_profile t1
JOIN (
    SELECT id, email, ROW_NUMBER() OVER (PARTITION BY email ORDER BY id) as rn
    FROM farmer_profile
) t2 ON t1.id = t2.id
SET t1.email = CONCAT(t1.email, '_dup_', t2.rn)
WHERE t2.rn > 1;

-- Now run the remaining migration
CREATE TEMPORARY TABLE temp_farmer_users (
    new_user_id BINARY(16),
    farmer_email VARCHAR(255)
);

INSERT INTO temp_farmer_users (new_user_id, farmer_email)
SELECT UUID_TO_BIN(UUID()), email FROM farmer_profile;

INSERT INTO users (
    user_id, name, email, mobile, password, state, village, address, postal_code, profile_photo,
    created_on, created_by, updated_on, updated_by, is_active, is_delete, login_count, last_login, role, status
)
SELECT 
    t.new_user_id, f.name, f.email, CONCAT(f.mobile, '_', SUBSTRING(UUID(), 1, 4)), f.password, f.state, f.village, f.address, f.postal_code, f.profile_photo,
    f.created_on, f.created_by, f.updated_on, f.updated_by, f.is_active, f.is_delete, f.login_count, f.last_login, 'FARMER', f.status
FROM farmer_profile f
JOIN temp_farmer_users t ON f.email = t.farmer_email;

UPDATE farmer_profile fp
JOIN temp_farmer_users t ON fp.email = t.farmer_email
SET fp.user_id = t.new_user_id;

ALTER TABLE farmer_profile
DROP COLUMN name,
DROP COLUMN village,
DROP COLUMN address,
DROP COLUMN postal_code,
DROP COLUMN profile_photo,
DROP COLUMN mobile,
DROP COLUMN email,
DROP COLUMN state,
DROP COLUMN password,
DROP COLUMN created_by,
DROP COLUMN created_on,
DROP COLUMN updated_by,
DROP COLUMN updated_on,
DROP COLUMN is_active,
DROP COLUMN is_delete,
DROP COLUMN login_count,
DROP COLUMN last_login,
DROP COLUMN role,
DROP COLUMN status;
