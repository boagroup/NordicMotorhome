/*
  Script responsible for generating the SQL tables required for the back-end of the application
  Author(s): Octavian Roman
 */
USE heroku_e8f7f82549e360a;

CREATE TABLE IF NOT EXISTS staff (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(20),
    lastName VARCHAR(20),
    image VARCHAR(255) DEFAULT '/assets/user_placeholder.png',
    telephone VARCHAR(16),
    role VARCHAR(25),
    gender ENUM('M', 'F', 'N', 'D') NOT NULL
    /*
    M = Male
    F = Female
    N = Non-Binary
    D = Decline to state
    */
);

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    staff_id INT,
    username VARCHAR(16) UNIQUE,
    password VARCHAR(32),
    admin BOOLEAN NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS motorhomes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    image VARCHAR(255) DEFAULT '/assets/motorhome_placeholder.png',
    type VARCHAR(30),
    beds INT(2),
    price FLOAT(10)
);

CREATE TABLE IF NOT EXISTS brands (
    id INT AUTO_INCREMENT PRIMARY KEY,
    motorhome_id INT,
    name VARCHAR(25),
    price FLOAT(10),
    FOREIGN KEY (motorhome_id) REFERENCES motorhomes(id)
);

CREATE TABLE IF NOT EXISTS models (
    id INT AUTO_INCREMENT PRIMARY KEY,
    brand_id INT,
    name VARCHAR(25),
    price FLOAT(10),
    FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rentals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    new BOOLEAN NOT NULL,
    ongoing BOOLEAN NOT NULL,
    completed BOOLEAN NOT NULL,
    distance INT,
    season ENUM('L','M','P') NOT NULL,
    /*
    L = Low Season
    M = Middle Season
    P = Peak Season
    */
    price FLOAT(10),
    notes TEXT
);

CREATE TABLE IF NOT EXISTS extras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS rentalExtras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rental_id INT,
    extra_id INT,
    price FLOAT,
    FOREIGN KEY (rental_id) REFERENCES rentals(id),
    FOREIGN KEY (extra_id) REFERENCES extras(id)
);

CREATE TABLE IF NOT EXISTS clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rental_id INT,
    firstName VARCHAR(20),
    lastName VARCHAR(20),
    telephone VARCHAR(16),
    FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE CASCADE
);