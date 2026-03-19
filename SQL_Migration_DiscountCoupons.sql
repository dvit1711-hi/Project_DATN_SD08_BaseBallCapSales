/**
    Migration Script: Update DiscountCoupons Table
    Purpose: Synchronize database schema with updated DiscountCoupon Entity
    Date: 2026-03-18
**/

USE DvtdHub1;
GO

-- Check if columns exist, add them if they don't
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DiscountCoupons' AND COLUMN_NAME='name')
BEGIN
    ALTER TABLE DiscountCoupons
    ADD name NVARCHAR(100) NULL;
    PRINT 'Added column: name';
END
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DiscountCoupons' AND COLUMN_NAME='discountType')
BEGIN
    ALTER TABLE DiscountCoupons
    ADD discountType NVARCHAR(20) NULL;
    PRINT 'Added column: discountType';
END
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DiscountCoupons' AND COLUMN_NAME='minOrderValue')
BEGIN
    ALTER TABLE DiscountCoupons
    ADD minOrderValue DECIMAL(10,2) NULL;
    PRINT 'Added column: minOrderValue';
END
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DiscountCoupons' AND COLUMN_NAME='maxDiscountValue')
BEGIN
    ALTER TABLE DiscountCoupons
    ADD maxDiscountValue DECIMAL(10,2) NULL;
    PRINT 'Added column: maxDiscountValue';
END
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DiscountCoupons' AND COLUMN_NAME='quantity')
BEGIN
    ALTER TABLE DiscountCoupons
    ADD quantity INT NULL;
    PRINT 'Added column: quantity';
END
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DiscountCoupons' AND COLUMN_NAME='startDate')
BEGIN
    ALTER TABLE DiscountCoupons
    ADD startDate DATE NULL;
    PRINT 'Added column: startDate';
END
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DiscountCoupons' AND COLUMN_NAME='endDate')
BEGIN
    ALTER TABLE DiscountCoupons
    ADD endDate DATE NULL;
    PRINT 'Added column: endDate';
END
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DiscountCoupons' AND COLUMN_NAME='active')
BEGIN
    ALTER TABLE DiscountCoupons
    ADD active BIT DEFAULT 1;
    PRINT 'Added column: active';
END
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DiscountCoupons' AND COLUMN_NAME='description')
BEGIN
    ALTER TABLE DiscountCoupons
    ADD description NVARCHAR(500) NULL;
    PRINT 'Added column: description';
END
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DiscountCoupons' AND COLUMN_NAME='createdAt')
BEGIN
    ALTER TABLE DiscountCoupons
    ADD createdAt DATE DEFAULT CAST(GETDATE() AS DATE);
    PRINT 'Added column: createdAt';
END
GO

-- Verify the table structure
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME='DiscountCoupons'
ORDER BY ORDINAL_POSITION;
GO

-- Update existing records with default values (optional, for sample data)
UPDATE DiscountCoupons
SET 
    discountType = 'percent',
    minOrderValue = 0,
    maxDiscountValue = 0,
    quantity = 0,
    startDate = CAST(GETDATE() AS DATE),
    endDate = CAST(DATEADD(DAY, 30, GETDATE()) AS DATE),
    active = 1,
    createdAt = CAST(GETDATE() AS DATE)
WHERE discountType IS NULL
   OR startDate IS NULL;

PRINT 'Migration completed successfully!'
GO
