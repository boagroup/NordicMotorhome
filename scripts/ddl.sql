/*
  Script responsible for generating the SQL tables required for the back-end of the application
  Author(s): Octavian Roman, Bartosz Birylo
 */
USE heroku_e8f7f82549e360a;
-- USE motorhome;

/* staff and users */

CREATE TABLE IF NOT EXISTS staff (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(20) NOT NULL DEFAULT '',
    lastName VARCHAR(20) DEFAULT '',
    image VARCHAR(255) DEFAULT '/assets/users/user_placeholder.png',
    telephone VARCHAR(16) DEFAULT '',
    role VARCHAR(25) DEFAULT '',
    gender ENUM('M', 'F', 'N', 'D') NOT NULL DEFAULT 'D'
    /*
    M = Male
    F = Female
    N = Non-Binary
    D = Decline to state
    */
);

CREATE TABLE IF NOT EXISTS users (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    staff_id INT(10) NOT NULL,
    username VARCHAR(25) UNIQUE,
    password BLOB,
    admin BOOLEAN NOT NULL DEFAULT 0,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE
);

/* motorhomes and its attributes */

CREATE TABLE IF NOT EXISTS brands (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25) UNIQUE NOT NULL,
    price DOUBLE(10,2) DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS models (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    brand_id INT,
    name VARCHAR(35) DEFAULT '',
    price DOUBLE(10,2) DEFAULT 0.00,
    FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS motorhomes (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    model_id INT,
    image VARCHAR(255) DEFAULT '/assets/motorhomes/motorhome_placeholder.png',
    rented BOOLEAN DEFAULT 0,
    type VARCHAR(35) NOT NULL DEFAULT '',
    beds INT(2) DEFAULT 0,
    FOREIGN KEY (model_id) REFERENCES models(id) ON DELETE CASCADE
);

/* rentals and its attributes */

CREATE TABLE IF NOT EXISTS rentals (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    motorhome_id INT(10),
    state ENUM('N','O','C') NOT NULL DEFAULT 'N',
    /*
    N = New
    M = Ongoing
    P = Completed
    */
    distance INT,
    season ENUM('L','M','P') NOT NULL DEFAULT 'L',
    /*
    L = Low Season
    M = Middle Season
    P = Peak Season
    */
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    notes TEXT,
    FOREIGN KEY (motorhome_id) REFERENCES motorhomes(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS extras (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25) DEFAULT '',
    price DOUBLE(10,2) DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS rentalExtras (
    rental_id INT(10),
    extra_id INT(10),
    FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE CASCADE,
    FOREIGN KEY (extra_id) REFERENCES extras(id) ON DELETE CASCADE
);

/* clients and its attributes */

CREATE TABLE IF NOT EXISTS clients (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    rental_id INT(10),
    firstName VARCHAR(20) NOT NULL DEFAULT '',
    lastName VARCHAR(20) DEFAULT '',
    telephone VARCHAR(16) DEFAULT '',
    FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE CASCADE
);
