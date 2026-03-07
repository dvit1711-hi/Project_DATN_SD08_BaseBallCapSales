/**
    Project: DvtdHub_HaNOI_2026
    Member:  Dinh Trong Duong
             Nguyen Chi Tien
             Nguyen Duc VietBB
             Nguyen Hai Dang
    Description: Baseball cap sales project
**/
-- DROP DATABASE DvtdHub1
CREATE DATABASE DvtdHub1;
GO
USE DvtdHub1;
GO

CREATE TABLE Roles (
    roleID INT IDENTITY(1,1) PRIMARY KEY,
    roleName NVARCHAR(50) NOT NULL
);
GO

CREATE TABLE Accounts (
    accountID INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(100),
    phoneNumber NVARCHAR(20),
    images NVARCHAR(255),
    createDate DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE AccountRoles (
    accountID INT NOT NULL,
    roleID INT NOT NULL,
    PRIMARY KEY (accountID, roleID),
    FOREIGN KEY (accountID) REFERENCES Accounts(accountID),
    FOREIGN KEY (roleID) REFERENCES Roles(roleID)
);
GO

CREATE TABLE Address (
    addressID INT IDENTITY(1,1) PRIMARY KEY,
    unit_number NVARCHAR(20),
    street_number NVARCHAR(20),
    address_line1 NVARCHAR(255),
    address_line2 NVARCHAR(255),
    city NVARCHAR(100),
    region NVARCHAR(100),
    postal_code NVARCHAR(20),
    accountID INT NOT NULL,
    FOREIGN KEY (accountID) REFERENCES Accounts(accountID)
);
GO

CREATE TABLE Brands (
    brandID INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL
);
GO
INSERT INTO Brands (name)
VALUES 
(N'NY'),
(N'yankees'),
(N'New Era');



CREATE TABLE Products (
    productID INT IDENTITY(1,1) PRIMARY KEY,
    productName NVARCHAR(200),
    description NVARCHAR(500),
    price DECIMAL(12,2),
    status VARCHAR(20),
    brandID INT,
    FOREIGN KEY (brandID) REFERENCES Brands(brandID)
);
INSERT INTO Products (
    productName, description, price, status, brandID
)
VALUES (
    N'Nón bóng chày unisex Street Oval',
    N'Nón bóng chày MLB thiết kế unisex, phong cách street, form oval',
    1190000,
    'ACTIVE',
    2
),
(N'Nón NY', N'Mũ thể thao NY thoáng khí', 890000, N'ACTIVE', 1)
;
-- SELECT*
-- FROM Products p
-- JOIN Images i ON 


CREATE TABLE Colors (
    colorID INT IDENTITY(1,1) PRIMARY KEY,
    colorName NVARCHAR(50),
    colorCode NVARCHAR(20) -- ví dụ: #FF0000
);
INSERT INTO Colors (colorName, colorCode)
VALUES
(N'Đen', '#000000'),
(N'Đỏ', '#FF0000'),
(N'Be', '#F5F5DC');

CREATE TABLE ProductColors (
    productColorID INT IDENTITY(1,1) PRIMARY KEY,
    productID INT NOT NULL,
    colorID INT NOT NULL,
    stockQuantity INT,
    FOREIGN KEY (productID) REFERENCES Products(productID),
    FOREIGN KEY (colorID) REFERENCES Colors(colorID)
);
INSERT INTO ProductColors (productID, colorID, stockQuantity)
VALUES
(1, 1, 100), -- Đen
(1, 2, 60),  -- Đỏ
(1, 3, 40),  -- Be
(2,1,100);

CREATE TABLE Images (
    imageID INT IDENTITY(1,1) PRIMARY KEY,
    productColorID INT NOT NULL,
    imageUrl NVARCHAR(255),
    isMain BIT DEFAULT 0,
    FOREIGN KEY (productColorID) REFERENCES ProductColors(productColorID)
);
INSERT INTO Images (productColorID, imageUrl, isMain)
VALUES
(1, '/images/yankeesDEN1.jpg', 1),
(1, '/images/Image\yankeesDEN2.jpg', 0),
(1, '/images/yankeesDEN3.jpg', 0),
(1, '/images/yankeesDEN4.jpg', 0),
(1, 'D:/images/yankeesDEN5.jpg', 0)
INSERT INTO Images (productColorID, imageUrl, isMain)
VALUES
(2, '/images/yankeesDO1.jpg', 1),
(2, '/images/yankeesDO2.jpg', 0),
(2, '/images/yankeesDO3.jpg', 0),
(2, '/images/yankeesDO4.jpg', 0),
(2, '/images/yankeesDO5.jpg', 0)
INSERT INTO Images (productColorID, imageUrl, isMain)
VALUES
(3, '/images/yankeesBE1.jpg', 1),
(3, '/images/yankeesBE2.jpg', 0),
(3, '/images/yankeesBE3.jpg', 0),
(3, '/images/yankeesBE4.jpg', 0),
(3, '/images/yankeesBE5.jpg', 0);
INSERT INTO Images (productColorID, imageUrl, isMain)
VALUES
(4, '/images/ny1.jpg', 1),
(4, '/images/nyden2.jpg', 0),
(4, '/images/nyden3.jpg', 0),
(4, '/images/nyden4.jpg', 0),
(4, '/images/nyden5.jpg', 0);


CREATE TABLE Carts (
    cartID INT IDENTITY(1,1) PRIMARY KEY,
    accountID INT NOT NULL,
    FOREIGN KEY (accountID) REFERENCES Accounts(accountID)
);
GO

CREATE TABLE CartItems (
    cartItemID INT IDENTITY(1,1) PRIMARY KEY,
    cartID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (cartID) REFERENCES Carts(cartID),
    FOREIGN KEY (productID) REFERENCES Products(productID)
);
GO

CREATE TABLE DiscountCoupons (
    couponID INT IDENTITY(1,1) PRIMARY KEY,
    couponCode NVARCHAR(50),
    discountValue DECIMAL(10,2),
    expiryDate DATE,
    status NVARCHAR(20)
);
GO

CREATE TABLE Orders (
    orderID INT IDENTITY(1,1) PRIMARY KEY,
    accountID INT NOT NULL,
couponID INT,
    orderDate DATETIME DEFAULT GETDATE(),
    status NVARCHAR(50),
    totalAmount DECIMAL(18,2),
    FOREIGN KEY (accountID) REFERENCES Accounts(accountID),
    FOREIGN KEY (couponID) REFERENCES DiscountCoupons(couponID)
);
GO

CREATE TABLE OrderDetails (
    orderDetailsID INT IDENTITY(1,1) PRIMARY KEY,
    orderID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (orderID) REFERENCES Orders(orderID),
    FOREIGN KEY (productID) REFERENCES Products(productID)
);
GO

CREATE TABLE Payments (
    paymentID INT IDENTITY(1,1) PRIMARY KEY,
    orderID INT NOT NULL,
    amount DECIMAL(18,2),
    method NVARCHAR(50),
    status NVARCHAR(50),
    createdAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (orderID) REFERENCES Orders(orderID)
);
GO

CREATE TABLE Reviews (
    reviewID INT IDENTITY(1,1) PRIMARY KEY,
    productID INT NOT NULL,
    accountID INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment NVARCHAR(500),
    createdAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (productID) REFERENCES Products(productID),
    FOREIGN KEY (accountID) REFERENCES Accounts(accountID)
);
GO

-- ROLES
INSERT INTO Roles (roleName)
VALUES 
(N'ROLE_ADMIN'),
(N'ROLE_USER');
GO

-- ACCOUNTS
INSERT INTO Accounts (account_code, username, password, email, phoneNumber, images)
VALUES
(N'ACC001', N'admin', N'$2a$12$f2lpXSJxQCCvHRAy0VonJOqIdwh/lTktQGuBvCIrPYHieb0ky5kPm', N'admin@gmail.com', N'0900000001', N'admin.jpg'),
(N'ACC003', N'duong', N'$2a$12$f2lpXSJxQCCvHRAy0VonJOqIdwh/lTktQGuBvCIrPYHieb0ky5kPm', N'duong@gmail.com', N'0900000003', N'duong.jpg');
GO

-- ACCOUNT ROLES
INSERT INTO AccountRoles (accountID, roleID)
VALUES
(1, 1), -- admin
(2, 2); -- user
GO

-- ADDRESS
INSERT INTO Address (unit_number, street_number, address_line1, city, region, postal_code, accountID)
VALUES
(N'A1', N'12', N'Hoàng Quốc Việt', N'Hà Nội', N'Cầu Giấy', N'100000', 1),
(N'B3', N'45', N'Trần Duy Hưng', N'Hà Nội', N'Cầu Giấy', N'100000', 2);
GO
