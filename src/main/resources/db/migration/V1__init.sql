-- Create the car_park table
CREATE TABLE car_park (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    car_park_no VARCHAR(50) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    x_coord DOUBLE PRECISION NOT NULL,
    y_coord DOUBLE PRECISION NOT NULL,
    car_park_type VARCHAR(50) NOT NULL,
    type_of_parking_system VARCHAR(50) NOT NULL,
    short_term_parking VARCHAR(50) NOT NULL,
    free_parking VARCHAR(255) NOT NULL,
    night_parking VARCHAR(3) NOT NULL,
    car_park_desks INT NOT NULL DEFAULT 0,
    gantry_height DOUBLE PRECISION NOT NULL,
    car_park_basement VARCHAR(1) NOT NULL,
    car_park_info TEXT DEFAULT '[]'
);
