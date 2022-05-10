/*
  Script responsible for generating the SQL tables required for the back-end of the application
  Author(s): Octavian Roman, Bartosz Birylo
 */
USE heroku_e8f7f82549e360a;

/* staff and users */

CREATE TABLE IF NOT EXISTS staff (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255),
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
    staff_id INT NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    admin BOOLEAN NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE
);

/* motorhomes and its attributes */

CREATE TABLE IF NOT EXISTS motorhomes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    image VARCHAR(255) DEFAULT '/assets/motorhome_placeholder.png',
    model_id INT,
    type VARCHAR(255) NOT NULL,
    beds INT(2),
    FOREIGN KEY (model_id) REFERENCES models(id) ON DELETE SET NULL,
    FOREIGN KEY (beds) REFERENCES beds(amount) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS brands (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DOUBLE(10,2)
);

CREATE TABLE IF NOT EXISTS models (
    id INT AUTO_INCREMENT PRIMARY KEY,
    brand_id INT,
    name VARCHAR(255),
    price DOUBLE(10,2),
    FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS beds (
    amount INT PRIMARY KEY,
    price DOUBLE(10,2)
);

/* rentals and its attributes */

CREATE TABLE IF NOT EXISTS rentals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    motorhome_id INT,
    state ENUM('N','O','C') NOT NULL,
    /*
    N = New
    M = Ongoing
    P = Completed
    */
    distance INT,
    season ENUM('L','M','P') NOT NULL,
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
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DOUBLE(10,2)
);

CREATE TABLE IF NOT EXISTS rentalExtras (
    rental_id INT,
    extra_id INT,
    FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE CASCADE,
    FOREIGN KEY (extra_id) REFERENCES extras(id) ON DELETE CASCADE
);

/* clients and its attributes */

CREATE TABLE IF NOT EXISTS clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rental_id INT,
    firstName VARCHAR(20) NOT NULL,
    lastName VARCHAR(20),
    telephone VARCHAR(16),
    FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE CASCADE
);
