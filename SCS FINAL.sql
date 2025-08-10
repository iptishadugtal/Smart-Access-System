create database db2; 
use db2;
CREATE TABLE visitor (
    visitor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    contact VARCHAR(20),
    email VARCHAR(100)
);
SET GLOBAL max_connections = 200;

ALTER TABLE visitor ADD password VARCHAR(100);
select *from admin;
Select * from visitor;


DELETE FROM visitor
WHERE visitor_id IN (3, 4, 6, 7, 10, 11, 12, 14, 15, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27, 28, 29);







truncate table visitor;
truncate table visitor_access;

CREATE TABLE receptionist (
    receptionist_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(100)
);
CREATE TABLE visit_request (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    visitor_id INT,
    requested_time DATETIME,
    purpose TEXT,
    status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    receptionist_id INT,
    FOREIGN KEY (visitor_id) REFERENCES visitor(visitor_id),
    FOREIGN KEY (receptionist_id) REFERENCES receptionist(receptionist_id)
);
INSERT INTO visit_request (visitor_id, requested_time, purpose, status, receptionist_id)
VALUES (1, '2025-07-13 10:00:00', 'train', 'Approved', 1);

select * from visit_request;
ALTER TABLE visit_request
ADD COLUMN visit_date DATE AFTER visitor_id;

CREATE TABLE access_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    visitor_id INT,
    access_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    action ENUM('Entry', 'Exit'),
    FOREIGN KEY (visitor_id) REFERENCES visitor(visitor_id)
);

CREATE TABLE admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);
CREATE TABLE access_point (
    access_point_id INT AUTO_INCREMENT PRIMARY KEY,
    location_name VARCHAR(100) NOT NULL   
);

CREATE TABLE visitor_access (
    access_id INT AUTO_INCREMENT PRIMARY KEY,
    visitor_id INT,
    access_point_id INT,
    granted_by INT,  
    granted_on DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visitor_id) REFERENCES visitor(visitor_id),
    FOREIGN KEY (access_point_id) REFERENCES access_point(access_point_id),
    FOREIGN KEY (granted_by) REFERENCES receptionist(receptionist_id)
);
ALTER TABLE visitor_access ADD COLUMN access_area VARCHAR(100);


show tables;
truncate table receptionist;
INSERT INTO admin (username, password)
VALUES ('Admin', 'Password@123');
UPDATE receptionist
SET password = 'Password@123'
WHERE username = 'priya_r';
INSERT INTO receptionist (name, username, password) VALUES 
('Priya Sharma', 'priya_r', '123'),
('Ravi Mehta', 'ravi_m', '456'),
('Anjali Verma', 'anjali_v', '789');

ALTER TABLE visit_request ADD qr_code_path VARCHAR(255);

INSERT INTO visitor (name, contact, email, password)
VALUES 
('Aarav Mehta', '9876543210', 'aarav.mehta@gmail.com', 'aarav@123'),
('Priya Sharma', '9123456789', 'priya.sharma@gmail.com', 'priya123'),
('Aditya Iyer', '9090909090', 'aditya.iyer@gmail.com', 'aditya@321');

ALTER TABLE visit_request
DROP FOREIGN KEY visit_request_ibfk_1;

ALTER TABLE visit_request
ADD CONSTRAINT fk_visit_visitor
FOREIGN KEY (visitor_id)
REFERENCES visitor(visitor_id)
ON DELETE CASCADE;

ALTER TABLE access_log
DROP FOREIGN KEY access_log_ibfk_1;

ALTER TABLE access_log
ADD CONSTRAINT fk_log_visitor
FOREIGN KEY (visitor_id)
REFERENCES visitor(visitor_id)
ON DELETE CASCADE;



INSERT INTO access_point (location_name) VALUES ('Floor 1');
INSERT INTO access_point (location_name) VALUES ('Floor 2');
INSERT INTO access_point (location_name) VALUES ('Server Room');
INSERT INTO access_point (location_name) VALUES ('Lobby');
INSERT INTO access_point (location_name) VALUES ('Cafeteria');
use db2;
ALTER TABLE visit_request ADD COLUMN access_point_id INT;

-- If not already present, create the foreign key constraint:
ALTER TABLE visit_request 
ADD CONSTRAINT fk_access_point 
FOREIGN KEY (access_point_id) REFERENCES access_point(access_point_id);

SELECT * FROM visit_request;

select * from admin;



