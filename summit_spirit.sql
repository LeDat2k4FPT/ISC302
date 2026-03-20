-- Kiem tra va xoa ket noi toi database neu dang co
USE master;
GO

-- Kiem tra va xoa database cu neu ton tai
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'ISP392')
BEGIN
    ALTER DATABASE ISP392 SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE ISP392;
END
GO

-- Tao co so du lieu voi ho tro Unicode cho tieng Viet
CREATE DATABASE ISP392
COLLATE SQL_Latin1_General_CP1254_CI_AS;
GO

-- Su dung co so du lieu
USE ISP392;
GO

-- ========================
-- Core Tables
-- ========================

-- ACCOUNT
CREATE TABLE Account (
    UserID INT PRIMARY KEY IDENTITY(1,1),
    FullName NVARCHAR(100),
	Address NVARCHAR(255),
	Phone NVARCHAR(100),
    Email NVARCHAR(100),
    Password NVARCHAR(255),
    Role NVARCHAR(20) -- 'admin', 'staff', 'customer'
);

-- Insert Account 
INSERT INTO Account (FullName, Address, Phone, Email, Password, Role) VALUES
(N'Admin 1', N'', N'', N'admin@gmail.com', N'12345678', N'Admin'),
(N'Khang Lanh', N'Nguyen Xi, Binh Thanh', N'0834283177', N'khangglanhh@gmail.com', N'12345', N'User'),
(N'Thanh Dat', N'Tran Hung Dao, Quan 1',N'0834283177', N'thanhdat@gmail.com', N'12345',  N'User'),
(N'Khanh Ly', N'Pham Van Dong, Thu Duc',N'0834283177',  N'khanhly@gmail.com', N'12345', N'Staff'),
(N'Nhat Anh', N'Dien Bien Phu, Quan 1',N'0834283177', N'nhatanh@gmail.com',N'12345', N'Shipper'),
(N'Thu Ha', N'Le Van Viet, Quan 9',N'0834283177', N'thuha@gmail.com', N'12345', N'Staff'),
(N'Minh Hai', N'Vo Van Ngan, Quan 9',N'0834283177', N'minhhai@gmail.com', N'12345', N'User'),
(N'user1', N'Quan 9',N'01234567', N'user1@gmail.com', N'12345', N'User'),
(N'user2', N'Quan 9',N'01234568', N'user2@gmail.com', N'12345', N'User'),
(N'user3', N'Quan 9',N'01234569', N'user3@gmail.com', N'12345', N'User'),
(N'user4', N'Quan 9',N'01234560', N'user4@gmail.com', N'12345', N'User'),
(N'user5', N'Quan 9',N'01234561', N'user5@gmail.com', N'12345', N'User'),
(N'user6', N'Quan 9',N'01234562', N'user6@gmail.com', N'12345', N'User'),
(N'user7', N'Quan 9',N'01234563', N'user7@gmail.com', N'12345', N'User'),
(N'user8', N'Quan 9',N'01234564', N'user8@gmail.com', N'12345', N'User'),
(N'user9', N'Quan 9',N'01234565', N'user9@gmail.com', N'12345', N'User'),
(N'user10', N'Quan 9',N'01234566', N'user10@gmail.com', N'12345', N'User');


-- CATEGORY
CREATE TABLE Category (
    CateID INT PRIMARY KEY IDENTITY(1,1),
    CateName NVARCHAR(100)
);
-- insert category
INSERT INTO Category(CateName) VALUES
( N'Pants'),
( N'Shirts'),
( N'Backpacks'),
( N'Camping Tools'),
( N'Tents'),
( N'Hats'),
( N'Cooking Equipment');


-- PRODUCT
CREATE TABLE Product(
    ProductID INT PRIMARY KEY IDENTITY(1,1),
    ProductName NVARCHAR(100),
    Description NVARCHAR(MAX),
    CateID INT,
    Status NVARCHAR(20), -- 'Active', 'Inactive'
    FOREIGN KEY (CateID) REFERENCES Category(CateID)
);
INSERT INTO Product (ProductName, Description, CateID, Status) VALUES 
(N'Ktom K116 waterproof pants', N'An improved version of the K56 waterproof pants with better water resistance and fabric quality. It features an elegant design that provides the wearer with confidence and comfort.', 1, N'Active'),
(N'Ktom K112 quick-dry pants with detachable legs ', N'Ktom K112 climbing pants are detachable pants with the advantages of quick drying, lightweight, and good UV resistance.', 1, N'Active'),
(N'Ktom K155 quick-dry men''s pants ', N'The K155 quick-dry connecting pants are very suitable for trekking, equipped with a small zippered pocket on the top of the right side pocket, this small pocket is intended to hold small necessary items and accessories.', 1, N'Active'),
(N'Ktom K154 men''s stretch trekking pants', N'With a very practical design, combining thick, abrasion-resistant fleece fabric in areas that frequently experience friction such as the buttocks, knees, and pant legs, with quick-drying and stretchy fabric in active joint areas like the crotch, back, and knee.', 1, N'Active'),
(N'Ktom K109 quick-dry shorts', N'Quick-drying shorts are made from fabric with quick-dry technology, helping you feel more comfortable during physical activities.', 1, N'Active'),
(N'K128 men''s quick-dry shirt (long sleeve) ', N'Men''s quick-dry shirt (long sleeve) k128 is suitable for days with unpredictable weather or simply if you prefer lightweight and breathable apparel. The fabric is quick-drying, has good UV protection, and the comfortable design is suitable for use.', 2, N'Active'),
(N'Ktom K142 women''s quick-dry shirt', N'The Ktom K142 women''s quick-dry shirt is made from high-quality fabric with UV protection technology that blocks harmful sunlight during the summer.', 2, N'Active'),
(N'Ktom K151 UV protection quick-dry outdoor jacket', N'The Ktom K151 quick-dry UV protection jacket for outdoor use is made from sun-proof fabric that protects your skin from UV rays.', 2, N'Active'),
(N'KTOM K129 men''s quick-dry short-sleeve shirt ', N'using materials mainly made of breathable, quick-drying fabric that is UV resistant.', 2, N'Active'),
(N'COLUMBIA WOMEN''S QUICK DRY LONG SLEEVE SHIRT', N'Columbia women''s hoodie is made of 100% cotton, providing all-day comfort for the user.', 2, N'Active'),
(N'Jack Wolfskin Moab Jam 24 Backpack', N'Lightweight durable  backpack', 3, N'Active'),
(N'Naturehike NH18Y045-Q 45L ', N'Spacious hiking backpack with multiple pockets ', 3, N'Active'),
(N'Naturehike NH16Y065-Q 55L Mountaineering Backpack', N'Large capacity backpack for long trips', 3, N'Active'),
(N'Naturehike NH16Y065-Q 65L Mountaineering Backpack', N'Extra large hiking backpack with durable fabric', 3, N'Active'),
(N'Matador Beast 28 Ultralight Mountaineering Backpack MATBE28001BK', N'Ultra-light backpack designed for minimalists', 3, N'Active'),
(N'Victorinox Trailmaster 0.8463', N'Victorinox Trailmaster 0.8463 multi-purpose knife is a multi-purpose knife suitable for camping, picnicking, traveling or daily use. With special steel from Vicorinox - You can rest assured about the quality, and the product''s rust resistance, sharpness retention and significant abrasion resistance.', 4, N'Active'),
(N'Morakniv Outdoor classic Kit - 12096', N'The Morakniv Outdoor classic Kit​ survival kit is a pair of Morakniv Lightweight Axe 12058 and Morakniv 2000 knife. Chopping trees and splitting firewood is easy with the axe, and light cutting is so simple with the Mora 2000. Buy the Outdoor classic Kit Combo now to make your trips easier and more convenient!', 4, N'Active'),
(N'Camo Form LT Gear Aid 2,44m', N'Camo Form LT Gear Aid 2.44m Ultra-Light Elastic Camouflage Tape - A product of Gear Aid with the effect of camouflaging guns, binoculars,... helps protect objects from scratches and environmental impacts while supporting camouflage in tactical games,...', 4, N'Active'),
(N'VICTORINOX PIONEER HARVESTER', N'VICTORINOX Pioneer Harvester 7-function multi-purpose knife belongs to the Pioneer travel folding knife line of Victorinox, suitable for travel, picnics, or as a gift for relatives and friends.', 4, N'Active'),
(N'Coghlans Multi-Pack Biners 0355', N'There is no description for this product yet.', 4, N'Active'),
(N'Naturehike MG NH21ZP001 3-4 person hexagonal camping tent', N'Naturehike MG NH21ZP001 3-4 person hexagonal camping tent, a hexagonal camping tent model, creates a large space for 3 to 4 people. Outstanding with a ventilation door system that brings a feeling of closeness to nature and the surrounding environment.', 5, N'Active'),
(N'Madfox Trekker 1 2023 1 Person 2 Layer Camping Tent', N'Madfox Trekker 1 2-layer 1-person camping tent 2023 improved 1-person tent model with 2 convenient doors. The tent is designed with 2 layers with the inner layer using more than 80% mesh, helping you feel submerged in nature even when lying in the tent, enjoying the trip in the most interesting way!', 5, N'Active'),
(N'BlackDog BD-ZP003 3-4 person hexagonal pyramid glamping tent', N'BlackDog BD-ZP003 3-4 person hexagonal pyramid glamping tent - Genuine high-end glamping tent model with the main black color for a mysterious and interesting camping space. The tent is designed in a pyramid style with a wide bottom for you to freely arrange and decorate items inside the tent, the high top of the tower gives a spacious and comfortable interior space. With a height of 2m, you can enter and exit the tent and live in the tent comfortably without having to bend over.', 5, N'Active'),
(N'NATUREHIKE CAMPING TENT NH21ZP005', N'NatureHike NH21ZP005 Camping Toilet Tent or also known by the shorter name "Toilet Tent" - suitable for changing clothes, bathing, personal hygiene... making your trip more convenient. Your relatives, especially women, will no longer be shy and feel inconvenient about finding a place to go to the toilet during camping trips in the forest. Naturehike''s tent overcomes most of the disadvantages of toilet tents currently on the market with too narrow or too low area, causing difficulties for users during use.', 5, N'Active'),
(N'Madfox Footprint 2021 6 Person Tent Floor Mat', N'Waterproof fabric, typically Oxford, PU-coated, or polyester with silver coating. Resistant to dirt and easy to clean.', 5, N'Active'),
(N'5.11 Tactical Flag Bearer Cap', N'The hat is equipped with a verlo velcro to adjust to fit many head sizes. The front and back of the hat have a place to stick patches according to your preference. Using cotton canvas material - soft material but still ensures good shape retention', 6, N'Active'),
(N'5.11 Tactical Hi-VIS Foldable Uniform Cap', N'The hat is equipped with a Velcro closure to adjust to fit many head sizes. The soft, sweat-absorbent brim is padded with foam for the most comfortable feeling. The visor is designed to be folded in half, making it easy to fold the hat without fear of breaking the brim.', 6, N'Active'),
(N'KTOM K52 sun protection baseball cap', N'Using imported quick-drying fabric. The fabric uses Omni-wick technology to help evaporate quickly, giving a very cool and comfortable feeling. Omni-shade technology helps block UV rays (UPF 50+), protecting you better when outdoors.', 6, N'Active'),
(N'Ranger Tactical Cap', N'The hat is made of cotton and polyester fabric that absorbs sweat well. Designed with many ventilation holes to prevent feeling hot when wearing the hat. Simple design, but no less fashionable and individual.', 6, N'Active'),
(N'KTOM K120 sun protection bucket hat', N'The brim is not as large as a wide-brimmed hat but is enough to protect you from the sun when climbing, picnicking, etc. The hat is designed with a compact strap, helping to fix the hat in a neatly folded state. The hat is made of soft, quick-drying material with the ability to absorb sweat and dry quickly.', 6, N'Active'),
(N'Vultura VTR-GS2401 Foldable Outdoor Gas Stove with Piezo Ignition and Windshield', N'A gas stove saves time on fire-starting, especially in wet weather when dry wood is hard to find. It features a wind guard for a stable flame in light wind and a built-in Piezo ignition for easy, match-free lighting.', 7, N'Active'),
(N'Vultura VTR-GS2402 Ultra-Compact Outdoor Gas Stove with Piezo Ignition', N'A compact gas stove designed for solo cooking needs. It features built-in ignition for easy use and a foldable design that saves valuable backpack space.', 7, N'Active'),
(N'Blackdog CBD2450CF010 Outdoor Charcoal BBQ Grill', N'The double-layer design with multiple ventilation holes helps maintain consistent heat, ensuring your grilled food cooks quickly and evenly. Heat-resistant wooden handles on both sides make it easy to move the grill when needed. The separate charcoal tray allows for easy cleaning and extends the product''s lifespan compared to standard single-layer models.', 7, N'Active'),
(N'Naturehike CNH22CJ008 Mini Cassette Gas Stove', N'This vintage-style outdoor gas stove features a compact design, perfect for camping and outdoor trips. With a 2100W output, it ensures fast cooking while remaining fuel-efficient thanks to its blue flame. The adjustable knob lets you control the flame size to suit your cooking needs.', 7, N'Active'),
(N'Naturehike CNH22CJ007 Multi-Purpose Camping Gas Stove', N'A new gas grill from Naturehike offers a quick, clean, and stylish outdoor BBQ experience. It can also be easily converted into a convenient hot pot stove without the need to start a fire.With a powerful 2300W heat output, it meets top cooking performance standards. The flame is evenly distributed on both sides of the stove, ensuring even heating of the grill or hot pot tray.', 7, N'Active');

-- COLOR
CREATE TABLE Color (
    ColorID INT PRIMARY KEY IDENTITY(1,1),
    ColorName NVARCHAR(50)
);
-- Insert color 
INSERT INTO Color (ColorName) VALUES
(N'Black'),
(N'Red'),
(N'Blue');

-- SIZE
CREATE TABLE Size (
    SizeID INT PRIMARY KEY IDENTITY (1,1),
    SizeName NVARCHAR(20) -- 'S', 'M', 'L', '5L', '15L', etc.
);
-- Insert size
INSERT INTO Size (SizeName) VALUES
(N'S'),
(N'M'),
(N'L'),
(N'XL'),
(N'XXL'),
(N'small'),
(N'big');

-- VARIANT (ATTRIBUTE)
CREATE TABLE ProductVariant (
    AttributeID INT IDENTITY(1,1) PRIMARY KEY ,
    ProductID INT,
    ColorID INT,
    SizeID INT,
    Price DECIMAL(10,3),
    Quantity INT,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    FOREIGN KEY (ColorID) REFERENCES Color(ColorID),
    FOREIGN KEY (SizeID) REFERENCES Size(SizeID)
);
-- Insert Variant
INSERT INTO ProductVariant (ProductID, ColorID, SizeID, Price, Quantity) VALUES
(1, 1, 1, 390000, 20),
(1, 1, 2, 390000, 20),
(1, 1, 3, 400000, 20),
(1, 1, 4, 400000, 20),
(1, 2, 1, 390000, 20),
(1, 2, 2, 390000, 20),
(1, 2, 3, 400000, 20),
(1, 2, 4, 400000, 20),
(1, 3, 1, 390000, 20),
(1, 3, 2, 390000, 20),
(1, 3, 3, 400000, 20),
(1, 3, 4, 400000, 20),
(2, 1, 1, 520000, 20),
(2, 1, 2, 520000, 20),
(2, 1, 3, 550000, 20),
(2, 1, 4, 550000, 20),
(2, 2, 1, 520000, 20),
(2, 2, 2, 520000, 20),
(2, 2, 3, 550000, 20),
(2, 2, 4, 550000, 20),
(2, 3, 1, 520000, 20),
(2, 3, 2, 520000, 20),
(2, 3, 3, 550000, 20),
(2, 3, 4, 550000, 20),
(3, 1, 1, 590000, 20),
(3, 1, 2, 590000, 20),
(3, 1, 3, 600000, 20),
(3, 1, 4, 600000, 20),
(3, 2, 1, 590000, 20),
(3, 2, 2, 590000, 20),
(3, 2, 3, 600000, 20),
(3, 2, 4, 600000, 20),
(3, 3, 1, 590000, 20),
(3, 3, 2, 590000, 20),
(3, 3, 3, 600000, 20),
(3, 3, 4, 600000, 20),
(4, 1, 1, 580000, 20),
(4, 1, 2, 580000, 20),
(4, 1, 3, 600000, 20),
(4, 1, 4, 600000, 20),
(4, 2, 1, 580000, 20),
(4, 2, 2, 580000, 20),
(4, 2, 3, 600000, 20),
(4, 2, 4, 600000, 20),
(4, 3, 1, 580000, 20),
(4, 3, 2, 580000, 20),
(4, 3, 3, 600000, 20),
(4, 3, 4, 600000, 20),
(5, 1, 1, 270000, 20),
(5, 1, 2, 270000, 20),
(5, 1, 3, 290000, 20),
(5, 1, 4, 290000, 20),
(5, 2, 1, 270000, 20),
(5, 2, 2, 270000, 20),
(5, 2, 3, 290000, 20),
(5, 2, 4, 290000, 20),
(5, 3, 1, 270000, 20),
(5, 3, 2, 270000, 20),
(5, 3, 3, 290000, 20),
(5, 3, 4, 290000, 20),
(6, 1, 1, 460000, 20),
(6, 1, 2, 460000, 20),
(6, 1, 3, 480000, 20),
(6, 1, 4, 500000, 20),
(6, 1, 5, 500000, 20),
(6, 2, 1, 460000, 20),
(6, 2, 2, 460000, 20),
(6, 2, 3, 480000, 20),
(6, 2, 4, 500000, 20),
(6, 2, 5, 500000, 20),
(6, 3, 1, 460000, 20),
(6, 3, 2, 460000, 20),
(6, 3, 3, 480000, 20),
(6, 3, 4, 500000, 20),
(6, 3, 5, 500000, 20),
(7, 1, 1, 355000, 20),
(7, 1, 2, 355000, 20),
(7, 1, 3, 375000, 20),
(7, 1, 4, 400000, 20),
(7, 1, 5, 400000, 20),
(7, 2, 1, 355000, 20),
(7, 2, 2, 355000, 20),
(7, 2, 3, 375000, 20),
(7, 2, 4, 400000, 20),
(7, 2, 5, 400000, 20),
(7, 3, 1, 355000, 20),
(7, 3, 2, 355000, 20),
(7, 3, 3, 375000, 20),
(7, 3, 4, 400000, 20),
(7, 3, 5, 400000, 20),
(8, 1, 1, 565000, 20),
(8, 1, 2, 565000, 20),
(8, 1, 3, 575000, 20),
(8, 1, 4, 590000, 20),
(8, 1, 5, 590000, 20),
(8, 2, 1, 565000, 20),
(8, 2, 2, 565000, 20),
(8, 2, 3, 575000, 20),
(8, 2, 4, 590000, 20),
(8, 2, 5, 590000, 20),
(8, 3, 1, 565000, 20),
(8, 3, 2, 565000, 20),
(8, 3, 3, 575000, 20),
(8, 3, 4, 590000, 20),
(8, 3, 5, 590000, 20),
(9, 1, 1, 420000, 20),
(9, 1, 2, 420000, 20),
(9, 1, 3, 430000, 20),
(9, 1, 4, 450000, 20),
(9, 1, 5, 450000, 20),
(9, 2, 1, 420000, 20),
(9, 2, 2, 420000, 20),
(9, 2, 3, 430000, 20),
(9, 2, 4, 450000, 20),
(9, 2, 5, 450000, 20),
(9, 3, 1, 420000, 20),
(9, 3, 2, 420000, 20),
(9, 3, 3, 430000, 20),
(9, 3, 4, 450000, 20),
(9, 3, 5, 450000, 20),
(10, 1, 1, 380000, 20),
(10, 1, 2, 380000, 20),
(10, 1, 3, 390000, 20),
(10, 1, 4, 400000, 20),
(10, 1, 5, 400000, 20),
(10, 2, 1, 380000, 20),
(10, 2, 2, 380000, 20),
(10, 2, 3, 390000, 20),
(10, 2, 4, 400000, 20),
(10, 2, 5, 400000, 20),
(10, 3, 1, 380000, 20),
(10, 3, 2, 380000, 20),
(10, 3, 3, 390000, 20),
(10, 3, 4, 400000, 20),
(10, 3, 5, 400000, 20),
(11, 1, NULL, 680000, 20),
(11, 2, NULL, 680000, 20),
(11, 3, NULL, 680000, 20),
(12, 1, NULL, 1585000, 20),
(12, 2, NULL, 1585000, 20),
(12, 3, NULL, 1585000, 20),
(13, 1, NULL, 1861000, 20),
(13, 2, NULL, 1861000, 20),
(13, 3, NULL, 1861000, 20),
(14, 1, NULL, 1962000, 20),
(14, 2, NULL, 1962000, 20),
(14, 3, NULL, 1962000, 20),
(15, 1, NULL, 4299000, 20),
(15, 2, NULL, 4299000, 20),
(15, 3, NULL, 4299000, 20),
(16, NULL, NULL, 1584000, 20),
(17, NULL, NULL, 1839200, 20),
(18, NULL, NULL, 385000, 20),
(19, NULL, NULL, 1432000, 20),
(20, NULL, NULL, 85150, 20),
(21, 1, NULL, 7442000, 20),
(21, 2, NULL, 7442000, 20),
(21, 3, NULL, 7442000, 20),
(22, 1, NULL, 1690000, 20),
(22, 2, NULL, 1690000, 20),
(22, 3, NULL, 1690000, 20),
(23, 1, NULL, 3100000, 20),
(23, 2, NULL, 3100000, 20),
(23, 3, NULL, 3100000, 20),
(24, 1, NULL, 2130000, 20),
(24, 2, NULL, 2130000, 20),
(24, 3, NULL, 2130000, 20),
(25, 1, NULL, 380000, 20),
(25, 2, NULL, 380000, 20),
(25, 3, NULL, 380000, 20),
(26, 1, NULL, 209000, 20),
(26, 2, NULL, 209000, 20),
(26, 3, NULL, 209000, 20),
(27, 1, NULL, 209000, 20),
(27, 2, NULL, 209000, 20),
(27, 3, NULL, 209000, 20),
(28, 1, NULL, 200000, 20),
(28, 2, NULL, 200000, 20),
(28, 3, NULL, 200000, 20),
(29, 1, NULL, 250000, 20),
(29, 2, NULL, 250000, 20),
(29, 3, NULL, 250000, 20),
(30, 1, NULL, 180000, 20),
(30, 2, NULL, 180000, 20),
(30, 3, NULL, 180000, 20),
(31, NULL, 6, 395000, 20),
(31, NULL, 7, 430000, 20),
(32, NULL, 6, 249000, 20),
(32, NULL, 7, 300000, 20),
(33, NULL, 6, 480000, 20),
(33, NULL, 7, 500000, 20),
(34, NULL, 6, 1049000, 20),
(34, NULL, 7, 1200000, 20),
(35, NULL, 6, 1692000, 20),
(35, NULL, 7, 1900000, 20);

-- IMAGE
CREATE TABLE ProductImage (
    ImageID INT IDENTITY(1,1) PRIMARY KEY,
    ImageURL NVARCHAR(MAX),
	ProductID INT,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

INSERT INTO ProductImage (ImageURL, ProductID) VALUES
(N'https://product.hstatic.net/200000467803/product/fanfan-quan-chong-tham-ktom-k116-02_0071549_7f2467d3be8d41eeafc5f6060a1923b0.png', 1),
(N'https://product.hstatic.net/200000467803/product/fanfan-quan-nhanh-kho-thao-ong-ktom-k112-be-4_6dd54c5c858c43fa85d6337293e9d4a2.jpg', 2),
(N'https://cdn.hstatic.net/products/200000467803/ktom-k155-ghi_6dd14e08a0414c36b0f75871ee6b63ae.jpg', 3),
(N'https://product.hstatic.net/200000467803/product/quan-trekking-ktom-k154_4ef9360ff48746efacf644921761fbe5.jpg', 4),
(N'https://product.hstatic.net/200000467803/product/fanfan-quan-short-nhanh-kho-ktom-k109-20_0066303_819a4a16845a4b5baaf949836d2b39c0.jpeg', 5),
(N'https://product.hstatic.net/200000467803/product/fanfan-ao-so-mi-nhanh-kho-nam-tay-dai-k128-cric4ndpxeemcvv9rv1ghw_d232f206bfe843a78338c18954b4be03.png', 6),
(N'https://product.hstatic.net/200000467803/product/fanfan-ao-so-mi-nu-nhanh-kho-ktom-k142-__3__6a4351a44b7740eeb4f4fa00870ca383_1024x1024.jpg', 7),
(N'https://product.hstatic.net/200000467803/product/fanfan-ao-khoac-nhanh-kho-chong-uv-da-ngoai-ktom-k151-__16__56982d60a2f44be4b8cdb629ee6a4f85.jpg', 8),
(N'https://product.hstatic.net/200000467803/product/fanfan-ao-so-mi-tay-ngan-nhanh-kho-ktom-k129-16-ux6xqjpy6e-zybednnejgq_5a7d2643cb2b4356a9c393800ad2c43f.png', 9),
(N'https://product.hstatic.net/200000467803/product/u-columbia-tay-dai-women-times-two-hooded-long-sleeve-shirt-04_0062420_b91e7502f70745fea51cec700d4af5d2.jpeg', 10),
(N'https://product.hstatic.net/200000467803/product/fanfan-balo-leo-nui-naturehike-45l-10_0067513_74437b368004411aa8df6c967da4db22_1024x1024.jpeg', 11),
(N'https://product.hstatic.net/200000467803/product/fanfan-ba-lo-leo-nui-55l-naturehike-10_0066101_8109cb189f7d4b60a9621ee210dc77df_1024x1024.jpeg', 12),
(N'https://product.hstatic.net/200000467803/product/fanfan-ba-lo-leo-nui-65l-naturehike-08_0066326_64eaf0b683844de4a63a176a4939ad34_1024x1024.jpeg', 13),
(N'https://product.hstatic.net/200000467803/product/fanfan-ba-lo-leo-nui-65l-naturehike-nh16y065-q-__6__37ab3f540dfb461eaf23166c5582ff25_1024x1024.jpg', 14),
(N'https://product.hstatic.net/200000467803/product/fanfan-balo-leo-nui-sieu-nhe-beast28-matbe28001bk__19__7b775c1a98b943fc99b02a6b3dcc495d_1024x1024.png', 15),
(N'https://product.hstatic.net/200000467803/product/fanfan-dao-da-nang-victorinox-trailmaster-08463-__1__7f59f5e0eb91466aa8df86a214588196_1024x1024.jpg', 16),
(N'https://product.hstatic.net/200000467803/product/com-bo-sinh-ton-morakiniv_0068608_c25c68e1824b468ca8f2e8fa02df33c8_1024x1024.jpeg', 17),
(N'https://product.hstatic.net/200000467803/product/o-gian-sieu-nhe-camo-form-lt-gear-aid-244m-camo-form-lt-lightw_0060301_af6ea9d9a4f14979b6839fe7903291ec_1024x1024.jpg', 18),
(N'https://product.hstatic.net/200000467803/product/dao-da-nang-cao-cap-victorinox-pionier-alox-scales-7-functions_0061642_72096dce60b94aa599d74cbcbebbf19f_1024x1024.jpg', 19),
(N'https://product.hstatic.net/200000467803/product/fanfan-bo-5-moc-coghlans-multi-pack-biners-0355__1__3ddbe7cca7a94f4ba4fa22153f67a9c6_1024x1024.jpg', 20),
(N'https://product.hstatic.net/200000467803/product/uc-giac-3-4-nguoi-naturehike-mg-nh21zp001-thumb-lqlxxiufnue4wvxuyiuhbg_390c391219d84d6ebc150a7c44667744_1024x1024.jpg', 21),
(N'https://product.hstatic.net/200000467803/product/fanfan-leu-cam-trai-1-nguoi-2-lop-madfox-trekker-1-2023-__1__54e42bedba784e838298274d2c4bc8a0_1024x1024.jpg', 22),
(N'https://product.hstatic.net/200000467803/product/fanfan-leu-3-4-nguoi-luc-giac-kim-tu-thap-blackdog-bd-zp003-__1__b1465b82676942399d227573e56aacd8_1024x1024.jpg', 23),
(N'https://product.hstatic.net/200000467803/product/fanfan-leu-ve-sinh-cam-trai-naturehike-nh21zp005-2_58da5b3e5d0342349ad8edd073ad4b47_1024x1024.jpg', 24),
(N'https://product.hstatic.net/200000467803/product/tam-lot-san-leu-6-nguoi-madfox-footprint-2021-5-dcy17xk3ousgf9jyyw0fnq_6a48ac5b663f465691a0e254f66d82a6_1024x1024.png', 25),
(N'https://product.hstatic.net/200000467803/product/fanfan-non-511-tactical-flag-bearer-1-zye0yyfyxk2eloqibwtouw_1b16c7bcb4a54479acb65e116d453afa.jpg', 26),
(N'https://product.hstatic.net/200000467803/product/nfan-non-511-tactical-hi-vis-foldable-uniform-1-tlssd5n_i0sgaghumfyqua_cc04d54aabf14d9996d8eb878cf0b41f.jpg', 27),
(N'https://product.hstatic.net/200000467803/product/fanfan-mu-luoi-trai-chong-nang-ktom-08_0064253_8e21e2082ea048139eac8490db20431d.jpeg', 28),
(N'https://product.hstatic.net/200000467803/product/thumb-1_0064367_e9d3c875d1a241f28b8d4c7465b43904.jpeg', 29),
(N'https://product.hstatic.net/200000467803/product/fanfan-mu-le-nui-non-treking-trang-phuc-da-ngoai-k120-thumb_0071241_a4704a568ed2462b85ad867ad5030e89.png', 30),
(N'https://product.hstatic.net/200000467803/product/vultura-gs2401_44c2878634b74ce8846009d87a15cd6e_1024x1024.jpg', 31),
(N'https://product.hstatic.net/200000467803/product/vultura-gs2402_f185a2da2daf490b89ca906c174fadb9_1024x1024.jpg', 32),
(N'https://product.hstatic.net/200000467803/product/fanfan-bep-nuong-than-bbq-da-ngoai-blackdog-cbd2450cf010__1__7570db6745614213a590230372cdf3bb_1024x1024.png', 33),
(N'https://product.hstatic.net/200000467803/product/fanfan-bep-gas-mini-cassette-naturehike-cnh22cj008-__5__36df06ae69a34b27a6db7ede54b28b13_1024x1024.png', 34),
(N'https://product.hstatic.net/200000467803/product/fanfan-bep-gas-cam-trai-da-nang-naturehike-cnh22cj007-__5__8453275383ed4737b7f03273ec169e9c_1024x1024.jpg', 35);



-- VOUCHER
CREATE TABLE Voucher (
    VoucherID INT PRIMARY KEY IDENTITY(1,1),
    VoucherCode NVARCHAR(50) UNIQUE,
    DiscountValue DECIMAL(10,2),
    ExpiryDate DATE,
    Status NVARCHAR(20) -- 'Active', 'Inactive'
);

INSERT INTO Voucher ( VoucherCode, DiscountValue, ExpiryDate , Status) VALUES
( 'FANFAN10', 10.0, '2026-12-31', 'Active'),
( 'TREK5OFF', 5.0, '2026-09-30', 'Active'),
( 'NEWCUSTOMER', 15.0, '2026-01-01', 'Active'),
( 'FLASHSALE20', 20.0, '2025-06-20', 'Inactive'),
('CAMPING15', 15.0, '2026-12-15', 'Active'),
( 'HIKER2025', 12.5, '2026-11-01', 'Active'),
( 'WELCOMEBACK', 8.0, '2026-07-15', 'Active'),
( 'MOUNTAINLOVE', 18.0, '2026-08-31', 'Active'),
('SPRINGDEAL', 7.5, '2026-03-31', 'Active'),
('LIMITED50', 50.0, '2026-06-18', 'Inactive');


-- ORDER
CREATE TABLE Orders(
    OrderID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT,
    OrderDate DATE,
    Status NVARCHAR(20),
    TotalAmount DECIMAL(13,2),
	ShipFee DECIMAL(10,2),
    VoucherID INT NULL,
	Note NVARCHAR(MAX),
    FOREIGN KEY (UserID) REFERENCES Account(UserID),
    FOREIGN KEY (VoucherID) REFERENCES Voucher(VoucherID)
);
-- Insert Orrder
INSERT INTO Orders (UserID, OrderDate, Status, TotalAmount, ShipFee, VoucherID, Note) VALUES
(6, '2025-04-18', N'Shipped', 1937875.0, 30.000, 9, N''),
(7, '2025-03-19', N'Delivered', 8022000.0, 30.000, 6, N''),
(7, '2025-05-31', N'Delivered', 578000.0, 30.000, 5, N''),
(3, '2025-02-21', N'Shipped', 3890250.0, 30.000, 2, N''),
(6, '2025-06-06', N'Delivered', 522000.0, 30.000, 1, N''),
(5, '2025-03-09', N'Shipped', 603500.0, 30.000, 5, N''),
(3, '2025-04-18', N'Cancelled', 328337.5, 30.000, 9, N'Customer does not receive the order'),
(6, '2025-04-07', N'Processing', 3711280.0, 30.000, 7, N''),
(3, '2025-05-06', N'Cancelled', 9836800.0, 30.000, 7, N'Customer does not receive the order'),
(6, '2025-03-01', N'Delivered', 13893750.0, 30.000, 2, N''),
(3, '2025-01-11', N'Delivered', 2223000.0, 30.000, 2, N''),
(3, '2025-05-09', N'Shipped', 400000.0, 30.000, NULL, N''),
(3, '2025-06-06', N'Cancelled', 3685240.0, 30.000, 2, N'Customer does not receive the order'),
(3, '2025-03-05', N'Cancelled', 1326000.0, 30.000, 5, N'Customer does not receive the order'),
(4, '2025-06-01', N'Delivered', 954000.0, 30.000, 1, N''),
(7, '2025-02-22', N'Shipped', 1171400.0, 30.000, 8, N''),
(5, '2025-02-11', N'Processing', 2617400.0, 30.000, 7, N''),
(7, '2025-06-03', N'Cancelled', 2738800.0, 30.000, 8, N'Customer does not reveive the order'),
(5, '2025-03-11', N'Processing', 932062.5, 30.000, 6, N''),
(6, '2025-05-14', N'Cancelled', 12231500.0, 30.000, 5, N'Customer does not receive the order');

-- ORDER DETAILS
CREATE TABLE OrderDetail (
    OrderDetailID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT,
    AttributeID INT,
    Quantity INT,
    UnitPrice DECIMAL(13,2),
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (AttributeID) REFERENCES ProductVariant(AttributeID)
);
-- Insert OrderDetail
INSERT INTO OrderDetail (OrderID, AttributeID, Quantity, UnitPrice) VALUES
(1, 170, 2, 400000),
(1, 71, 3, 1695000),
(2, 1, 3, 1170000),
(2, 139, 1, 1432000),
(2, 119, 3, 5886000),
(2, 98, 1, 680000),
(3, 93, 1, 680000),
(4, 103, 1, 1585000),
(4, 23, 1, 590000),
(4, 18, 3, 1560000),
(4, 184, 2, 360000),
(5, 36, 1, 580000),
(6, 58, 2, 710000),
(7, 61, 1, 355000),
(8, 58, 2, 710000),
(8, 139, 3, 2864000),
(8, 47, 1, 460000),
(9, 80, 3, 1260000),
(9, 57, 2, 710000),
(9, 96, 1, 680000),
(9, 141, 1, 7442000),
(10, 120, 3, 12897000),
(10, 12, 1, 520000),
(10, 58, 1, 355000),
(10, 92, 1, 680000),
(11, 77, 2, 840000),
(11, 173, 3, 750000),
(12, 167, 2, 400000),
(13, 137, 1, 1839200),
(13, 94, 3, 2040000),
(14, 53, 3, 1380000),
(14, 182, 1, 180000),
(15, 83, 1, 380000),
(15, 97, 1, 680000),
(16, 51, 1, 460000),
(16, 29, 1, 580000),
(16, 7, 1, 390000),
(17, 109, 1, 1861000),
(17, 162, 2, 418000),
(17, 68, 1, 565000),
(18, 170, 2, 400000),
(18, 12, 3, 1560000),
(18, 48, 3, 1380000),
(19, 58, 2, 1065000),
(20, 38, 1, 270000),
(20, 50, 2, 920000),
(20, 65, 1, 656000),
(20, 131, 3, 12897000);

-- HISTORY
CREATE TABLE UpdateHistory (
    LogID INT PRIMARY KEY,
    ActionType NVARCHAR(50),     -- 'UPDATE_PRICE', 'DELETE_USER', etc.
    TableName NVARCHAR(50),      -- 'ProductVariant', 'UserAccount', etc.
    RecordID INT,                -- Affected ID
    OldValue NVARCHAR(MAX),      -- e.g., { "price": 60 }
    NewValue NVARCHAR(MAX),      -- e.g., { "price": 70 }
    PerformedBy INT,             -- FK to UserAccount(UserID)
    PerformedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (PerformedBy) REFERENCES Account(UserID)
);

-- REVIEW
CREATE TABLE Review (
    ReviewID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT,
    ProductID INT,
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(MAX),
    ReviewDate DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES Account(UserID),
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

INSERT INTO Review (UserID, ProductID, Rating, Comment, ReviewDate) VALUES
(4, 3, 5, N'Comfortable and dries quickly. Great for trekking.', '2025-05-20'),
(2, 5, 4, N'Short pants are nice but stitching could be better.', '2025-05-21'),
(3, 11, 5, N'Compact backpack, fits a lot. Ideal for hiking.', '2025-05-22'),
(4, 16, 3, N'Sharp blade but lacks a carrying sheath.', '2025-05-23'),
(2, 21, 5, N'Spacious and easy-to-set-up tent. Very breathable.', '2025-05-24'),
(3, 28, 4, N'Lightweight hat with good sun protection.', '2025-05-25'),
(5, 33, 5, N'Great grill, easy to clean and cooks evenly.', '2025-05-26'),
(2, 6, 4, N'Comfortable shirt with quick-dry fabric.', '2025-05-26'),
(3, 9, 3, N'Shirt is good but sizing runs a bit large.', '2025-05-27'),
(4, 19, 5, N'Compact multi-tool. Perfect for camping trips.', '2025-05-28');

CREATE TABLE Payment (
    PaymentID INT PRIMARY KEY IDENTITY(1,1),
	OrderID INT UNIQUE,
    PaymentMethod NVARCHAR(50),
    PaymentStatus NVARCHAR(50),
    PaymentDate DATETIME DEFAULT GETDATE(),
    TransactionCode NVARCHAR(100),
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
); 
-- add payment
INSERT INTO Payment (OrderID, PaymentMethod, PaymentStatus, PaymentDate, TransactionCode) VALUES
(1, 'QR', 'Paid', '2025-04-18', 'TXN0001'),
(2, 'QR', 'Paid', '2025-03-19', 'TXN0002'),
(3, 'QR', 'Paid', '2025-05-31', 'TXN0003'),
(4, 'QR', 'Paid', '2025-02-21', 'TXN0004'),
(5, 'QR', 'Paid', '2025-06-06', 'TXN0005'),
(6, 'QR', 'Paid', '2025-03-09', 'TXN0006'),
(7, 'QR', 'Return', '2025-04-18', 'TXN0007'),
(8, 'QR', 'Paid', '2025-04-07', 'TXN0008'),
(9, 'QR', 'Return', '2025-05-06', 'TXN0009'),
(10, 'QR', 'Paid', '2025-03-01', 'TXN0010'),
(11, 'QR', 'Paid', '2025-01-11', 'TXN0011'),
(12, 'QR', 'Paid', '2025-05-09', 'TXN0012'),
(13, 'QR', 'Return', '2025-06-06', 'TXN0013'),
(14, 'QR', 'Return', '2025-03-05', 'TXN0014'),
(15, 'QR', 'Paid', '2025-06-01', 'TXN0015'),
(16, 'QR', 'Paid', '2025-02-22', 'TXN0016'),
(17, 'QR', 'Paid', '2025-02-11', 'TXN0017'),
(18, 'QR', 'Return', '2025-06-03', 'TXN0018'),
(19, 'QR', 'Paid', '2025-03-11', 'TXN0019'),
(20, 'QR', 'Return', '2025-05-14', 'TXN0020');

CREATE TABLE UserAddressInfo (
    InfoID INT PRIMARY KEY IDENTITY(1,1),
	OrderID INT UNIQUE,
	Country NVARCHAR(100),
	FullName NVARCHAR(100),
	Phone NVARCHAR(20),
	Email NVARCHAR(100),
	Address NVARCHAR(200),
	District NVARCHAR(100),
	City NVARCHAR(100),
	FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
);

INSERT INTO UserAddressInfo (OrderID, Country, FullName, Phone, Email, Address, District, City) VALUES
(1, N'Vietnam', N'Thu Ha', N'0834283177', N'thuha@gmail.com', N'123 Le Van Viet', N'Thu Duc', N'Ho Chi Minh'),
(2, N'Vietnam', N'Minh Hai', N'0834283177', N'minhhai@gmail.com', N'45 Vo Van Ngan', N'Thu Duc', N'Ho Chi Minh'),
(3, N'Vietnam', N'Minh Hai', N'0834283177', N'minhhai@gmail.com', N'45 Vo Van Ngan', N'Thu Duc', N'Ho Chi Minh'),
(4, N'Vietnam', N'Thanh Dat', N'0834283177', N'thanhdat@gmail.com', N'22 Tran Hung Dao', N'Quan 1', N'Ho Chi Minh'),
(5, N'Vietnam', N'Thu Ha', N'0834283177', N'thuha@gmail.com', N'123 Le Van Viet', N'Quan 9', N'Ho Chi Minh'),
(6, N'Vietnam', N'Nhat Anh', N'0834283177', N'nhatanh@gmail.com', N'89 Dien Bien Phu', N'Binh Thanh', N'Ho Chi Minh'),
(7, N'Vietnam', N'Thanh Dat', N'0834283177', N'thanhdat@gmail.com', N'22 Tran Hung Dao', N'Quan 1', N'Ho Chi Minh'),
(8, N'Vietnam', N'Thu Ha', N'0834283177', N'thuha@gmail.com', N'123 Le Van Viet', N'Quan 9', N'Ho Chi Minh'),
(9, N'Vietnam', N'Thanh Dat', N'0834283177', N'thanhdat@gmail.com', N'22 Tran Hung Dao', N'Quan 1', N'Ho Chi Minh'),
(10, N'Vietnam', N'Thu Ha', N'0834283177', N'thuha@gmail.com', N'123 Le Van Viet', N'Quan 9', N'Ho Chi Minh'),
(11, N'Vietnam', N'Thanh Dat', N'0834283177', N'thanhdat@gmail.com', N'22 Tran Hung Dao', N'Quan 1', N'Ho Chi Minh'),
(12, N'Vietnam', N'Thanh Dat', N'0834283177', N'thanhdat@gmail.com', N'22 Tran Hung Dao', N'Quan 1', N'Ho Chi Minh'),
(13, N'Vietnam', N'Thanh Dat', N'0834283177', N'thanhdat@gmail.com', N'22 Tran Hung Dao', N'Quan 1', N'Ho Chi Minh'),
(14, N'Vietnam', N'Thanh Dat', N'0834283177', N'thanhdat@gmail.com', N'22 Tran Hung Dao', N'Quan 1', N'Ho Chi Minh'),
(15, N'Vietnam', N'Khanh Ly', N'0834283177', N'khanhly@gmail.com', N'78 Pham Van Dong', N'Thu Duc', N'Ho Chi Minh'),
(16, N'Vietnam', N'Minh Hai', N'0834283177', N'minhhai@gmail.com', N'45 Vo Van Ngan', N'Thu Duc', N'Ho Chi Minh'),
(17, N'Vietnam', N'Nhat Anh', N'0834283177', N'nhatanh@gmail.com', N'89 Dien Bien Phu', N'Binh Thanh', N'Ho Chi Minh'),
(18, N'Vietnam', N'Minh Hai', N'0834283177', N'minhhai@gmail.com', N'45 Vo Van Ngan', N'Thu Duc', N'Ho Chi Minh'),
(19, N'Vietnam', N'Nhat Anh', N'0834283177', N'nhatanh@gmail.com', N'89 Dien Bien Phu', N'Binh Thanh', N'Ho Chi Minh'),
(20, N'Vietnam', N'Thu Ha', N'0834283177', N'thuha@gmail.com', N'123 Le Van Viet', N'Quan 9', N'Ho Chi Minh');

INSERT INTO Account (FullName, Address, Phone, Email, Password, Role)
VALUES (N'Shipper 1', N'Quan 9', N'0999999999', N'shipper1@gmail.com', N'12345', N'Shipper');

CREATE TABLE Shipping (
    ShippingID INT PRIMARY KEY IDENTITY(1,1),
    OrderID INT UNIQUE,
    UserID INT,
    DeliveryTime DATETIME NULL,
    DeliveryImageURL NVARCHAR(MAX) NULL,
    Note NVARCHAR(MAX) NULL,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (UserID) REFERENCES Account(UserID)
);



