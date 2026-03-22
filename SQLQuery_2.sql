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
INSERT INTO Accounts (username, password, email, phoneNumber, images)
VALUES
(N'admin', N'$2a$12$f2lpXSJxQCCvHRAy0VonJOqIdwh/lTktQGuBvCIrPYHieb0ky5kPm', N'admin@gmail.com', N'0900000001', N'/images/admin.jpg'),
(N'duong', N'$2a$12$f2lpXSJxQCCvHRAy0VonJOqIdwh/lTktQGuBvCIrPYHieb0ky5kPm', N'duong@gmail.com', N'0900000003', N'/images/duong.jpg');
GO

-- ACCOUNT ROLES
INSERT INTO AccountRoles (accountID, roleID)
VALUES
(1, 1), -- admin
(2, 2); -- user
GO

INSERT INTO Status (statusName) VALUES
('Active'), ('Inactive'), ('Pending'), ('Banned'), ('Locked');

GO
-- ADDRESS
INSERT INTO Address (unit_number, street_number, address_line1, city, region, postal_code, accountID)
VALUES
(N'A1', N'12', N'Hoàng Quốc Việt', N'Hà Nội', N'Cầu Giấy', N'100000', 1),
(N'B3', N'45', N'Trần Duy Hưng', N'Hà Nội', N'Cầu Giấy', N'100000', 2);
GO


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

SELECT * FROM Accounts WHERE accountID = 1

-- UPDATE Accounts
-- SET images = '/images/duong.jpg'
-- WHERE accountID = 2;

-- Brand mới
    INSERT INTO Brands (name) VALUES
    (N'MLB'),
    (N'LA Dodgers');
GO

-- Màu mới
INSERT INTO Colors (colorName, colorCode) VALUES
(N'Trắng', '#FFFFFF'),
(N'Xanh dương', '#0000FF'),
(N'Xám', '#808080');
GO

-- Sản phẩm mới
INSERT INTO Products (productName, description, price, status, brandID) VALUES
(N'Nón New Era Classic',      N'Nón basic dễ phối đồ',              990000,  'ACTIVE', 3),
(N'Nón Yankees Vintage Logo', N'Nón logo Yankees phong cách cổ điển',1290000, 'ACTIVE', 2),
(N'Nón MLB Curved Cap',       N'Nón MLB vành cong',                 1090000, 'ACTIVE', 4),
(N'Nón LA Dodgers Premium',   N'Nón LA Dodgers cao cấp',            1390000, 'ACTIVE', 5);
GO
-- Tồn kho theo màu
INSERT INTO ProductColors (productID, colorID, stockQuantity) VALUES
(3, 1, 18), -- New Era Classic - Đen
(3, 4, 4),  -- New Era Classic - Trắng

(4, 1, 15), -- Yankees Vintage - Đen
(4, 6, 5),  -- Yankees Vintage - Xám

(5, 4, 12), -- MLB Curved - Trắng
(5, 2, 3),  -- MLB Curved - Đỏ

(6, 5, 14), -- LA Dodgers - Xanh dương
(6, 4, 2);  -- LA Dodgers - Trắng
GO

INSERT INTO DiscountCoupons (couponCode, discountValue, expiryDate, status) VALUES
(N'NEWYEAR10', 10.00,    '2026-01-31', N'ACTIVE'),
(N'SUMMER15',  15.00,    '2026-08-31', N'ACTIVE'),
(N'FREESHIP50',50000.00, '2026-12-31', N'ACTIVE'),
(N'HOLIDAY20', 20.00,    '2026-12-31', N'ACTIVE');
GO

INSERT INTO Orders (accountID, couponID, orderDate, status, totalAmount) VALUES
(2,  1, '2026-01-05', N'Delivered', 1071000),
(3,  NULL, '2026-01-18', N'Completed', 1650000),

(4,  3, '2026-02-08', N'Shipping',   940000),
(1,  NULL, '2026-02-25', N'Delivered',2140000),

(5,  NULL, '2026-03-03', N'Pending', 1090000),
(2,  NULL, '2026-03-21', N'Delivered',1390000),

(6,  NULL, '2026-04-11', N'Cancelled',1190000),
(7,  NULL, '2026-04-24', N'Delivered',1780000),

(1,  NULL, '2026-05-06', N'Completed',2680000),
(9,  NULL, '2026-05-28', N'Pending',  1520000),

(2,  NULL, '2026-06-22', N'Shipping', 1880000),

(1,  NULL, '2026-07-04', N'Delivered',2380000),
(3,  NULL, '2026-07-19', N'Cancelled',1390000),

(4,  NULL, '2026-08-02', N'Delivered', 760000),
(2,  NULL, '2026-08-27', N'Completed',2280000),

(5,  NULL, '2026-09-10', N'Delivered',2180000),
(1,  NULL, '2026-09-29', N'Pending',   890000),

(2,  NULL, '2026-10-26',N'Shipping', 1700000),

(3,  NULL, '2026-11-08',N'Delivered',2080000),
(1,  NULL, '2026-11-25',N'Delivered',2580000),

(4,  NULL, '2026-12-06',N'Completed',1950000),
(2,  4,    '2026-12-24',N'Delivered',2280000);
GO

INSERT INTO OrderDetails (orderID, productID, quantity, price) VALUES
(32, 1, 1, 1190000),
(33, 2, 1, 890000),
(31, 3, 1, 760000),

(31, 3, 1, 990000),
(32, 4, 1, 1290000),
(29, 2, 1, 850000),

(27, 5, 1, 1090000),
(28, 6, 1, 1390000),

(27, 1, 1, 1190000),
(28, 2, 2, 890000),

(19, 4, 1, 1290000),
(19, 6, 1, 1390000),

(15,3, 2, 760000),
(13,5, 1, 1090000),
(16,2, 1, 850000),

(12,3, 1, 990000),
(12,2, 1, 890000),

(13,1, 2, 1190000),
(14,6, 1, 1390000),

(15,3, 1, 760000),
(16,4, 1, 1290000),
(16,3, 1, 990000),

(17,5, 2, 1090000),
(18,2, 1, 890000),

(19,6, 1, 1390000),
(19,1, 1, 1190000),

(20,2, 2, 850000),
(21,3, 1, 990000),
(21,5, 1, 1090000),

(22,4, 2, 1290000),
(23,1, 1, 1190000),
(23,3, 1, 760000),

(24,6, 1, 1390000),
(24,2, 1, 890000);
GO

INSERT INTO Payments (orderID, amount, method, status, createdAt) VALUES
(32,  1071000, N'VNPAY',   N'SUCCESS', '2026-01-05'),
(33,  1650000, N'COD',     N'SUCCESS', '2026-01-18'),

(31,   940000, N'MOMO',    N'PENDING', '2026-02-08'),
(31,  2140000, N'BANKING', N'SUCCESS', '2026-02-25'),

(32,  1090000, N'COD',     N'PENDING', '2026-03-03'),
(29,  1390000, N'VNPAY',   N'SUCCESS', '2026-03-21'),

(27,  1190000, N'MOMO',    N'FAILED',  '2026-04-11'),
(28,  1780000, N'COD',     N'SUCCESS', '2026-04-24'),

(27,  2680000, N'BANKING', N'SUCCESS', '2026-05-06'),
(28, 1520000, N'COD',     N'PENDING', '2026-05-28'),

(19, 1940000, N'VNPAY',   N'SUCCESS', '2026-06-09'),
(19, 1880000, N'MOMO',    N'PENDING', '2026-06-22'),

(15, 2380000, N'COD',     N'SUCCESS', '2026-07-04'),
(13, 1390000, N'VNPAY',   N'FAILED',  '2026-07-19'),

(16,  760000, N'MOMO',    N'SUCCESS', '2026-08-02'),
(17, 2280000, N'BANKING', N'SUCCESS', '2026-08-27'),

(18, 2180000, N'COD',     N'SUCCESS', '2026-09-10'),
(19,  890000, N'MOMO',    N'PENDING', '2026-09-29'),

(20, 2580000, N'VNPAY',   N'SUCCESS', '2026-10-07'),
(20, 1700000, N'COD',     N'PENDING', '2026-10-26'),

(21, 2080000, N'BANKING', N'SUCCESS', '2026-11-08'),
(22, 2580000, N'VNPAY',   N'SUCCESS', '2026-11-25'),

(23, 1950000, N'COD',     N'SUCCESS', '2026-12-06'),
(24, 2280000, N'VNPAY',   N'SUCCESS', '2026-12-24');
GO

INSERT INTO Reviews (productID, accountID, rating, comment, createdAt) VALUES
(1, 2,  5, N'Form đẹp, đội rất vừa đầu.',           '2026-01-12'),
(2, 3,  4, N'Chất vải ổn, màu đẹp.',                '2026-01-25'),
(3, 4,  5, N'Tối giản nhưng rất sang.',             '2026-02-20'),
(4, 1,  5, N'Logo thêu đẹp, xứng đáng tiền.',       '2026-03-01'),
(6, 2,  5, N'Đội lên rất nổi bật.',                 '2026-03-28'),
(2, 7,  4, N'Giao hàng nhanh, đúng mô tả.',         '2026-04-30'),
(5, 9, 5, N'Vành cong đẹp, đeo thoải mái.',        '2026-06-18'),
(2, 9, 4, N'Giá tốt, dễ phối đồ.',                 '2026-06-18'),
(1, 1,  5, N'Mẫu này bán rất ổn.',                  '2026-07-10'),
(3, 4,  4, N'Nhẹ đầu, đi nắng rất hợp.',            '2026-08-10'),
(5, 5,  5, N'Rất hài lòng về chất lượng.',          '2026-09-20'),
(6, 9, 5, N'Bản premium nhìn xịn thật.',           '2026-10-12'),
(3, 3,  4, N'Nón đẹp, đóng gói cẩn thận.',          '2026-11-15'),
(4, 1,  5, N'Dòng vintage rất đáng mua.',           '2026-11-30'),
(1, 4,  5, N'Sản phẩm best seller là có lý do.',    '2026-12-15'),
(6, 2,  5, N'Cuối năm mua vẫn rất ưng.',            '2026-12-28'),
(2, 5,  3, N'Ổn trong tầm giá.',                    '2026-12-29'),
(3, 3,  4, N'Dùng hằng ngày khá tiện.',             '2026-12-30');
GO
ALTER TABLE Accounts
ALTER COLUMN images NVARCHAR(MAX);

--Tổng quan

SELECT COUNT(*) AS TongTaiKhoan FROM Accounts;
SELECT COUNT(*) AS TongSanPham FROM Products;
SELECT COUNT(*) AS TongDonHang FROM Orders;
SELECT SUM(totalAmount) AS TongDoanhThu FROM Orders;

--Đơn hàng

SELECT status, COUNT(*) AS SoLuong
FROM Orders
GROUP BY status;

SELECT MONTH(orderDate) AS Thang, COUNT(*) AS SoDon, SUM(totalAmount) AS DoanhThu
FROM Orders
GROUP BY MONTH(orderDate)
ORDER BY Thang;

SELECT AVG(totalAmount) AS GiaTriDonTrungBinh
FROM Orders;

--Sản phẩm

SELECT p.productName, SUM(od.quantity) AS DaBan
FROM OrderDetails od
         JOIN Products p ON od.productID = p.productID
GROUP BY p.productName
ORDER BY DaBan DESC;

SELECT b.name, SUM(od.quantity) AS DaBan
FROM OrderDetails od
         JOIN Products p ON od.productID = p.productID
         JOIN Brands b ON p.brandID = b.brandID
GROUP BY b.name
ORDER BY DaBan DESC;

SELECT c.colorName, SUM(pc.stockQuantity) AS TongTon
FROM ProductColors pc
         JOIN Colors c ON pc.colorID = c.colorID
GROUP BY c.colorName;

SELECT p.productName, c.colorName, pc.stockQuantity
FROM ProductColors pc
         JOIN Products p ON pc.productID = p.productID
         JOIN Colors c ON pc.colorID = c.colorID
WHERE pc.stockQuantity <= 5;

--Khách hàng

SELECT a.username, COUNT(o.orderID) AS SoDon
FROM Accounts a
         LEFT JOIN Orders o ON a.accountID = o.accountID
GROUP BY a.username
ORDER BY SoDon DESC;

SELECT a.username, ISNULL(SUM(o.totalAmount),0) AS TongChi
FROM Accounts a
         LEFT JOIN Orders o ON a.accountID = o.accountID
GROUP BY a.username
ORDER BY TongChi DESC;

SELECT s.statusName, COUNT(*) AS SoLuong
FROM Accounts a
         JOIN Status s ON a.statusID = s.statusID
GROUP BY s.statusName;

--Thanh toán

SELECT method, COUNT(*) AS SoLan, SUM(amount) AS TongTien
FROM Payments
GROUP BY method;

SELECT status, COUNT(*) AS SoLuong
FROM Payments
GROUP BY status;

SELECT SUM(amount) AS TongTienThanhToanThanhCong
FROM Payments
WHERE status = N'SUCCESS';

--Đánh giá

SELECT AVG(CAST(rating AS FLOAT)) AS DiemTrungBinh
FROM Reviews;

SELECT p.productName, AVG(CAST(r.rating AS FLOAT)) AS DiemTB
FROM Reviews r
         JOIN Products p ON r.productID = p.productID
GROUP BY p.productName
ORDER BY DiemTB DESC;

SELECT rating, COUNT(*) AS SoLuong
FROM Reviews
GROUP BY rating
ORDER BY rating;


SELECT * FROM [Status];
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
SELECT * FROM Reviews