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

CREATE TABLE Products (
    productID INT IDENTITY(1,1) PRIMARY KEY,
    productName NVARCHAR(200),
    description NVARCHAR(500),
    price DECIMAL(12,2),
    status VARCHAR(20),
    brandID INT,
    FOREIGN KEY (brandID) REFERENCES Brands(brandID)
);

CREATE TABLE Colors (
    colorID INT IDENTITY(1,1) PRIMARY KEY,
    colorName NVARCHAR(50),
    colorCode NVARCHAR(20) -- ví dụ: #FF0000
);

CREATE TABLE ProductColors (
    productColorID INT IDENTITY(1,1) PRIMARY KEY,
    productID INT NOT NULL,
    colorID INT NOT NULL,
    stockQuantity INT,
    FOREIGN KEY (productID) REFERENCES Products(productID),
    FOREIGN KEY (colorID) REFERENCES Colors(colorID)
);

CREATE TABLE Images (
    imageID INT IDENTITY(1,1) PRIMARY KEY,
    productColorID INT NOT NULL,
    imageUrl NVARCHAR(255),
    isMain BIT DEFAULT 0,
    FOREIGN KEY (productColorID) REFERENCES ProductColors(productColorID)
);

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
CREATE TABLE Status (
    statusID INT IDENTITY(1,1) PRIMARY KEY,
    statusName NVARCHAR(50) NOT NULL UNIQUE
);

ALTER TABLE Accounts
ADD statusID INT DEFAULT 1;
GO

ALTER TABLE Accounts
ADD CONSTRAINT FK_Accounts_Status
FOREIGN KEY (statusID) REFERENCES Status(statusID);
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

-- BRANDS
INSERT INTO Brands (name)
VALUES
(N'MLB'),
(N'Nike'),
(N'Adidas'),
(N'New Era');
GO

-- PRODUCTS
INSERT INTO Products (productName, description, price, status, brandID)
VALUES
(N'Nón MLB Street Oval', N'Nón bóng chày MLB thiết kế unisex', 1190000, N'ACTIVE', 1),
(N'Nón New Era Classic', N'Mũ lưỡi trai phong cách Mỹ', 990000, N'ACTIVE', 4),
(N'Nón Adidas Sport', N'Mũ thể thao Adidas thoáng khí', 890000, N'ACTIVE', 3),
(N'Nón Nike Air Jordan Cap', N'Mũ thời trang Nike Jordan', 1290000, N'ACTIVE', 2);
GO

-- COLORS
INSERT INTO Colors (colorName, colorCode)
VALUES
(N'Đen', N'#000000'),
(N'Trắng', N'#FFFFFF'),
(N'Đỏ', N'#FF0000'),
(N'Be', N'#F5F5DC');
GO

-- PRODUCT COLORS
INSERT INTO ProductColors (productID, colorID, stockQuantity) VALUES
(2, 1, 50), (2, 2, 40), (2, 3, 30), (2, 4, 20),
(3, 1, 60), (3, 2, 50), (3, 3, 30), (3, 4, 20),
(4, 1, 80), (4, 2, 70), (4, 3, 40), (4, 4, 30);
GO

DROP TABLE Images


-- IMAGES
INSERT INTO Images (productColorID, imageUrl, isMain)
VALUES
(5, '/images/nyHONG1.jpg', 1),
(5, '/images/nyHONG2.jpg', 0),
(5 , '/images/nyHONG3.jpg', 0),
(5, '/images/nyHONG4.jpg', 0)


INSERT INTO Images (productColorID, imageUrl, isMain)
VALUES
(6, '/images/nyXANH1.jpg', 1),
(6, '/images/nyXANH2.jpg', 0),
(6, '/images/nyXANH3.jpg', 0),
(6, '/images/nyXANH4.jpg', 0)

INSERT INTO Images (productColorID, imageUrl, isMain)
VALUES
(7, '/images/nyXAM1.jpg', 1),
(7, '/images/nyXAM2.jpg', 0),
(7, '/images/nyXAM3.jpg', 0),
(7, '/images/nyXAM4.jpg', 0)


-- INSERT INTO Images (productColorID, imageUrl, isMain)
-- VALUES
-- (1, '/images/yankeesDEN1.jpg', 1),
-- (1, '/images/yankeesDEN2.jpg', 0),
-- (1, '/images/yankeesDEN3.jpg', 0),
-- (1, '/images/yankeesDEN4.jpg', 0),
-- (1, '/images/yankeesDEN5.jpg', 0)
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

-- CARTS (chỉ có 2 tài khoản → chỉ tạo 2 cart)
INSERT INTO Carts (accountID)
VALUES (1), (2);
GO

-- CART ITEMS (sửa lại cartID cho đúng)
INSERT INTO CartItems (cartID, productID, quantity)
VALUES
(1, 1, 2),
(2, 2, 1);
GO

-- COUPONS
INSERT INTO DiscountCoupons (couponCode, discountValue, expiryDate, status)
VALUES
(N'SALE10', 10, '2026-12-31', N'ACTIVE'),
(N'NEW20', 20, '2026-06-30', N'ACTIVE'),
(N'FREESHIP', 0, '2026-12-31', N'ACTIVE'),
(N'VIP30', 30, '2027-01-01', N'ACTIVE');
GO

-- ORDERS (chỉ có 2 user → sửa lại order hợp lý)
INSERT INTO Orders (accountID, couponID, status, totalAmount)
VALUES
(1, 1, N'PAID', 2380000),
(2, 2, N'PAID', 1890000);
GO

-- ORDER DETAILS
INSERT INTO OrderDetails (orderID, productID, quantity, price)
VALUES
(1, 2, 2, 1190000),
(2, 3, 1, 990000);
GO

-- PAYMENTS
INSERT INTO Payments (orderID, amount, method, status)
VALUES
(1, 2380000, N'BANKING', N'SUCCESS'),
(2, 1890000, N'COD', N'SUCCESS');
GO

-- REVIEWS
INSERT INTO Reviews (productID, accountID, rating, comment)
VALUES
(2, 1, 5, N'Sản phẩm chất lượng, form đẹp'),
(3, 2, 4, N'Mũ đẹp, giá hợp lý');
GO

DELETE FROM Images
WHERE imageID = 18

ALTER TABLE Accounts
ALTER COLUMN images NVARCHAR(MAX);

SELECT * FROM Roles;
SELECT * FROM Accounts;
SELECT * FROM AccountRoles;
SELECT * FROM Address;
SELECT * FROM Brands;
SELECT * FROM Products;
SELECT * FROM Colors;
SELECT * FROM ProductColors;
SELECT * FROM Images;
SELECT * FROM Carts;
SELECT * FROM CartItems;
SELECT * FROM DiscountCoupons;
SELECT * FROM Orders;
SELECT * FROM OrderDetails;
SELECT * FROM Payments;
SELECT * FROM Reviews;