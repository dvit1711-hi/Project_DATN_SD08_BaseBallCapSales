-- SQL Migration for UserDiscountCoupons Table
-- Purpose: Create table to track which users have claimed which discount coupons

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'UserDiscountCoupons')
BEGIN
    CREATE TABLE UserDiscountCoupons (
        userCouponID INT PRIMARY KEY IDENTITY(1,1),
        accountID INT NOT NULL,
        couponID INT NOT NULL,
        claimedDate DATETIME2 NOT NULL DEFAULT GETDATE(),
        usedDate DATETIME2 NULL,
        status NVARCHAR(20) NOT NULL DEFAULT 'claimed',
        createdAt DATETIME2 NOT NULL DEFAULT GETDATE(),
        
        -- Foreign Keys
        CONSTRAINT FK_UserDiscountCoupons_Accounts FOREIGN KEY (accountID) 
            REFERENCES Accounts(accountID) ON DELETE CASCADE,
        CONSTRAINT FK_UserDiscountCoupons_DiscountCoupons FOREIGN KEY (couponID) 
            REFERENCES DiscountCoupons(couponID) ON DELETE CASCADE
    )

    -- Create indexes for better query performance
    CREATE INDEX IX_UserDiscountCoupons_AccountId ON UserDiscountCoupons(accountID)
    CREATE INDEX IX_UserDiscountCoupons_CouponId ON UserDiscountCoupons(couponID)
    CREATE INDEX IX_UserDiscountCoupons_Status ON UserDiscountCoupons(status)
    CREATE INDEX IX_UserDiscountCoupons_AccountId_Status ON UserDiscountCoupons(accountID, status)
    CREATE INDEX IX_UserDiscountCoupons_AccountId_CouponId ON UserDiscountCoupons(accountID, couponID)

    PRINT 'UserDiscountCoupons table created successfully'
END
ELSE
BEGIN
    PRINT 'UserDiscountCoupons table already exists'
END
