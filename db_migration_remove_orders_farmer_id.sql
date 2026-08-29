-- SQL migration to remove the direct relationship of farmer_id from the orders table
-- and ensure the order_items table has a status column.

-- 1. If there is a foreign key on orders.farmer_id, we need to drop it.
-- Depending on your database, the constraint name might vary (e.g. FK_orders_farmer_id).
-- For MySQL/MariaDB:
-- Determine constraint name via: SHOW CREATE TABLE orders;
-- Then: ALTER TABLE orders DROP FOREIGN KEY <constraint_name>;

-- Generic drop statement for orders.farmer_id column:
ALTER TABLE orders DROP COLUMN IF EXISTS farmer_id;

-- 2. Ensure order_items table has a status column for individual item status tracking.
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'PENDING';
