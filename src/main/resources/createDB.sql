create schema batchCamp;
CREATE USER 'batch'@'localhost' IDENTIFIED BY 'batch';
GRANT ALL PRIVILEGES ON * . * TO 'batch'@'localhost';
FLUSH PRIVILEGES;