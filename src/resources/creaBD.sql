DROP TABLE IF EXISTS Ascensiones ;
DROP TABLE IF EXISTS Picos ;
DROP TABLE IF EXISTS Alpinistas ;

--
-- Estructura de las tablas
--

CREATE TABLE PICOS (
    idPico INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cordillera VARCHAR(100),
    localizacion VARCHAR(100),
    altitud INT
);

CREATE TABLE ALPINISTAS (
    idAlpinista INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    apellidos VARCHAR (120),
    nacionalidad VARCHAR (80),
    fechaNacimiento DATE
);

CREATE TABLE ASCENSIONES (
    idAscension INT AUTO_INCREMENT PRIMARY KEY,
    idPico INT NOT NULL,
    idAlpinista INT NOT NULL,
    fechaAscension DATE,
    expedicion INT,
    ruta VARCHAR (150),
    CONSTRAINT FK_ASC_PIC FOREIGN KEY (idPico)
        REFERENCES Picos(idPico)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_ASC_ALP FOREIGN KEY (idAlpinista)
        REFERENCES Alpinistas(idAlpinista)
        ON UPDATE CASCADE ON DELETE CASCADE
);
