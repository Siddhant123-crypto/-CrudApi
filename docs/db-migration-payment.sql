-- =============================================================================
-- Database Migration Script for Farm2Home Payment Feature (Razorpay / Online & COD)
-- NOTE: Do NOT execute this file automatically during tests. Run manually on target database.
-- =============================================================================

-- 1. Ensure `orders` table has the updated `payment_method` column supporting:
--    'CARD', 'COD', 'NET_BANKING', 'UPI', 'ONLINE'
ALTER TABLE orders 
    MODIFY COLUMN payment_method ENUM('CARD', 'COD', 'NET_BANKING', 'UPI', 'ONLINE') DEFAULT 'COD';

-- 2. Ensure `orders` table has `payment_status` column supporting:
--    'PENDING', 'PAID', 'FAILED', 'REFUNDED'
-- ALTER TABLE orders 
--     ADD COLUMN payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING';

-- 3. Ensure Razorpay tracking columns exist on `orders` table
-- ALTER TABLE orders 
--     ADD COLUMN razorpay_order_id VARCHAR(255) NULL,
--     ADD COLUMN razorpay_payment_id VARCHAR(255) NULL,
--     ADD COLUMN transaction_id VARCHAR(255) NULL,
--     ADD COLUMN payment_id VARCHAR(255) NULL,
--     ADD COLUMN paid_at DATETIME NULL;

-- 4. Ensure Razorpay Route linked account ID exists on `farmer_profile` table
-- ALTER TABLE farmer_profile 
--     ADD COLUMN razorpay_linked_account_id VARCHAR(255) NULL;

-- 5. Backfill existing legacy orders that have NULL payment_method so they default to COD
UPDATE orders SET payment_method = 'COD' WHERE payment_method IS NULL;
