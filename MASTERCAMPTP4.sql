DROP DATABASE IF EXISTS MASTERCAMPTP4;

CREATE DATABASE MASTERCAMPTP4;

USE MASTERCAMPTP4;

CREATE TABLE Category(
   CategoryID INT AUTO_INCREMENT,
   CategoryName VARCHAR(50) NOT NULL,
   PRIMARY KEY(CategoryID)
);

CREATE TABLE Topic(
   TopicID INT AUTO_INCREMENT,
   TopicName VARCHAR(50),
   PRIMARY KEY(TopicID)
);

CREATE TABLE Tag(
   TagID INT AUTO_INCREMENT,
   TagName VARCHAR(50),
   PRIMARY KEY(TagID)
);

CREATE TABLE Document(
   DocumentID INT AUTO_INCREMENT,
   DocumentName VARCHAR(50),
   DocumentDate DATE,
   StorageAddress VARCHAR(50),
   TopicID INT,
   CategoryID INT,
   PRIMARY KEY(DocumentID),
   FOREIGN KEY(TopicID) REFERENCES Topic(TopicID),
   FOREIGN KEY(CategoryID) REFERENCES Category(CategoryID)
);

CREATE TABLE Detenir(
   DocumentID INT,
   TagID INT,
   PRIMARY KEY(DocumentID, TagID),
   FOREIGN KEY(DocumentID) REFERENCES Document(DocumentID),
   FOREIGN KEY(TagID) REFERENCES Tag(TagID)
);

INSERT INTO Category (CategoryName) VALUES
('policy'),
('plan'),
('report'),
('receipt'),
('order');

INSERT INTO Topic (TopicName) VALUES
('CS243 Course Files in Fall 2021'),
('Cluster Graduation Projet en 2022'),
('CS243 Course Files in Fall 2022');

INSERT INTO Tag (TagName) VALUES
('legal'),
('medical'),
('administrative'),
('technical'),
('2022'),
('reporting');

INSERT INTO Document (DocumentName, DocumentDate, StorageAddress, CategoryID, TopicID) VALUES
('TP1', STR_TO_DATE('2021-09-11', '%Y-%m-%d'),'Paris',3,1),
('TD3', STR_TO_DATE('2021-10-23', '%Y-%m-%d'),'Lille',1,1),
('ReportOfficiamCGP', STR_TO_DATE('2022-09-03', '%Y-%m-%d'),'Bordeaux',2,1),
('Proj1', STR_TO_DATE('2022-11-12', '%Y-%m-%d'),'Lyon',3,3);

INSERT INTO Detenir (DocumentID, TagID) VALUES
(1,1),
(2,1),
(3,5),
(3,6),
(4,4),
(4,5);

SELECT COUNT(Tag.TagID), Tag.TagName FROM Detenir
JOIN Tag ON Tag.TagID = Detenir.TagID
GROUP BY Tag.TagID
ORDER BY COUNT(Tag.TagID) DESC;