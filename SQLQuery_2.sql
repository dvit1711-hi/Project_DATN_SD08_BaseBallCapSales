/**
    Project: DvtdHub_HaNOI_2026
    Member:  Dinh Trong Duong
             Nguyen Chi Tien
             Nguyen Duc VietBB
             Nguyen Hai Dang
    Description: Baseball cap sales project
**/
CREATE DATABASE DvtdHub1;
GO
USE DvtdHub1;
GO

CREATE TABLE Roles (
    roleID INT IDENTITY PRIMARY KEY,
    roleName NVARCHAR(50) NOT NULL
);
GO

CREATE TABLE Accounts (
    accountID INT IDENTITY PRIMARY KEY,
    account_code NVARCHAR(50) NOT NULL,
    username NVARCHAR(50) NOT NULL,
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
    addressID INT IDENTITY PRIMARY KEY,
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
    brandID INT IDENTITY PRIMARY KEY,
    name NVARCHAR(100) NOT NULL
);
GO
INSERT INTO Brands (name)
VALUES 
(N'NY'),
(N'yankees');



CREATE TABLE Products (
    productID INT IDENTITY PRIMARY KEY,
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
    1
);



CREATE TABLE Colors (
    colorID INT IDENTITY PRIMARY KEY,
    colorName NVARCHAR(50),
    colorCode NVARCHAR(20) -- ví dụ: #FF0000
);
INSERT INTO Colors (colorName, colorCode)
VALUES
(N'Đen', '#000000'),
(N'Đỏ', '#FF0000'),
(N'Be', '#F5F5DC');

CREATE TABLE ProductColors (
    productColorID INT IDENTITY PRIMARY KEY,
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
(1, 3, 40);  -- Be

CREATE TABLE Images (
    imageID INT IDENTITY PRIMARY KEY,
    productColorID INT NOT NULL,
    imageUrl NVARCHAR(255),
    isMain BIT DEFAULT 0,
    FOREIGN KEY (productColorID) REFERENCES ProductColors(productColorID)
);
INSERT INTO Images (productColorID, imageUrl, isMain)
VALUES
(1, 'D:\SOF203_JAVA3\DATN\Image\yankeesDEN1.jpg', 1),
(1, 'D:\SOF203_JAVA3\DATN\Image\yankeesDEN2.jpg', 0),
(1, 'D:\SOF203_JAVA3\DATN\Image\yankeesDEN3.jpg', 0),
(1, 'D:\SOF203_JAVA3\DATN\Image\yankeesDEN4.jpg', 0),
(1, 'D:\SOF203_JAVA3\DATN\Image\yankeesDEN5.jpg', 0)
INSERT INTO Images (productColorID, imageUrl, isMain)
VALUES
(2, 'D:\SOF203_JAVA3\DATN\Image\yankeesDO1.jpg', 1),
(2, 'D:\SOF203_JAVA3\DATN\Image\yankeesDO2.jpg', 0),
(2, 'D:\SOF203_JAVA3\DATN\Image\yankeesDO3.jpg', 0),
(2, 'D:\SOF203_JAVA3\DATN\Image\yankeesDO4.jpg', 0),
(2, 'D:\SOF203_JAVA3\DATN\Image\yankeesDO5.jpg', 0)
INSERT INTO Images (productColorID, imageUrl, isMain)
VALUES
(3, 'D:\SOF203_JAVA3\DATN\Image\yankeesBE1.jpg', 1),
(3, 'D:\SOF203_JAVA3\DATN\Image\yankeesBE2.jpg', 0),
(3, 'D:\SOF203_JAVA3\DATN\Image\yankeesBE3.jpg', 0),
(3, 'D:\SOF203_JAVA3\DATN\Image\yankeesBE4.jpg', 0),
(3, 'D:\SOF203_JAVA3\DATN\Image\yankeesBE5.jpg', 0);





CREATE TABLE Carts (
    cartID INT IDENTITY PRIMARY KEY,
    accountID INT NOT NULL,
    FOREIGN KEY (accountID) REFERENCES Accounts(accountID)
);
GO

CREATE TABLE CartItems (
    cartItemID INT IDENTITY PRIMARY KEY,
    cartID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (cartID) REFERENCES Carts(cartID),
    FOREIGN KEY (productID) REFERENCES Products(productID)
);
GO

CREATE TABLE DiscountCoupons (
    couponID INT IDENTITY PRIMARY KEY,
    couponCode NVARCHAR(50),
    discountValue DECIMAL(10,2),
    expiryDate DATE,
    status NVARCHAR(20)
);
GO

CREATE TABLE Orders (
    orderID INT IDENTITY PRIMARY KEY,
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
    orderDetailsID INT IDENTITY PRIMARY KEY,
    orderID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (orderID) REFERENCES Orders(orderID),
    FOREIGN KEY (productID) REFERENCES Products(productID)
);
GO

CREATE TABLE Payments (
    paymentID INT IDENTITY PRIMARY KEY,
    orderID INT NOT NULL,
    amount DECIMAL(18,2),
    method NVARCHAR(50),
    status NVARCHAR(50),
    createdAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (orderID) REFERENCES Orders(orderID)
);
GO

CREATE TABLE Reviews (
    reviewID INT IDENTITY PRIMARY KEY,
    productID INT NOT NULL,
    accountID INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment NVARCHAR(500),
    createdAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (productID) REFERENCES Products(productID),
    FOREIGN KEY (accountID) REFERENCES Accounts(accountID)
);
GO


INSERT INTO Roles (roleName)
VALUES 
(N'ROLE_ADMIN'),
(N'ROLE_USER');

INSERT INTO Accounts (
    account_code,
    username,
    password,
    email,
    phoneNumber,
    images
)
VALUES
(
    'ACC_ADMIN_001',
    'admin',
    '456',
    'admin@gmail.com',
    '0909000001',
    NULL
),
(
    'ACC_USER_001',
    'user',
    '123',
    'user@gmail.com',
    '0909000002',
    NULL
);

-- admin → ROLE_ADMIN
INSERT INTO AccountRoles (account_id, role_id)
VALUES (
    (SELECT accountID FROM Accounts WHERE username = 'admin'),
    (SELECT roleID FROM Roles WHERE roleName = 'ROLE_ADMIN')
);

-- user → ROLE_USER
INSERT INTO AccountRoles (account_id, role_id)
VALUES (
    (SELECT accountID FROM Accounts WHERE username = 'user'),
    (SELECT roleID FROM Roles WHERE roleName = 'ROLE_USER')
);

EXEC sp_rename 'Accounts.phoneNumber', 'phone_number', 'COLUMN';




-- ROLE
INSERT INTO Roles(roleName) VALUES ('ADMIN');
INSERT INTO Roles(roleName) VALUES ('USER');

-- ACCOUNT
INSERT INTO Accounts(account_code, username, password)
VALUES ('ACC001', 'admin', '124'); -- password đã bcrypt

INSERT INTO Accounts(account_code, username, password)
VALUES ('ACC002', 'user', '457');

-- GÁN ROLE
INSERT INTO AccountRoles(account_id, role_id) VALUES (1, 3); 
INSERT INTO AccountRoles(account_id, role_id) VALUES (2, 4);
