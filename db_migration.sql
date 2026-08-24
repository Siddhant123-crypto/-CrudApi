-- 1. Create Customer Table
CREATE TABLE customer (
    customer_id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 2. Insert all current users into customer table since they were acting as customers
-- We will use the existing user_id and a generated UUID for customer_id
INSERT INTO customer (customer_id, user_id)
SELECT UUID(), user_id FROM users;

-- 3. Change the role of existing users from 'USER' to 'CUSTOMER' (if enum is used, you may need to alter the enum first, but usually in MySQL a string is used)
-- If enum column needs altering:
-- ALTER TABLE users MODIFY COLUMN role ENUM('CUSTOMER', 'FARMER');
UPDATE users SET role = 'CUSTOMER' WHERE role = 'USER';

-- 4. Move farmer_profile data into users
-- Since farmers didn't have user_id links yet, we need to generate UUIDs for them and insert into users
-- Note: Replace 'Admin' with proper user if needed for created_by/updated_by.
INSERT INTO users (
    user_id, name, email, mobile, password, state, village, address, postal_code, profile_photo,
    created_on, created_by, updated_on, updated_by, is_active, is_delete, login_count, last_login, role, status
)
SELECT 
    UUID(), name, email, mobile, password, state, village, address, postal_code, profile_photo,
    created_on, created_by, updated_on, updated_by, is_active, is_delete, login_count, last_login, 'FARMER', status
FROM farmer_profile;

-- 5. Update farmer_profile to point to the newly created users
-- We can link them by email or mobile since they are unique
UPDATE farmer_profile fp
JOIN users u ON fp.email = u.email
SET fp.user_id = u.user_id;

-- 6. Add foreign key constraint to farmer_profile
ALTER TABLE farmer_profile 
ADD CONSTRAINT fk_farmer_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

-- 7. Add new columns
ALTER TABLE farmer_profile ADD COLUMN farm_name VARCHAR(255);

-- 8. Drop duplicate columns from farmer_profile
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
