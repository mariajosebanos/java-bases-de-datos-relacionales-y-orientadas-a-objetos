
-- Datos para tabla Picos
INSERT INTO PICOS (nombre, cordillera, localizacion, altitud) VALUES
('Everest',       'Himalaya',  'Nepal / China',     8849),
('K2',            'Karakórum', 'Pakistán / China',  8611),
('Kangchenjunga', 'Himalaya',  'Nepal / India',     8586),
('Lhotse',        'Himalaya',  'Nepal / China',     8516),
('Makalu',        'Himalaya',  'Nepal / China',     8485);

-- Datos para tabla Alpinistas
INSERT INTO ALPINISTAS (nombre, apellidos, nacionalidad, fechaNacimiento) VALUES
('Edmund',   'Hillary',      'Neozelandés', '1919-07-20'),
('Tenzing',  'Norgay',       'Nepalés',     '1914-05-29'),
('Ernst',    'Schmied',      'Suizo',       '1905-09-17'),
('Jürg',     'Marmet',       'Suizo',       '1918-04-06'),
('Achille',  'Compagnoni',   'Italiano',    '1914-09-26'),
('Lino',     'Lacedelli',    'Italiano',    '1925-05-05'),
('George',   'Bell',         'Estadounidense', '1919-06-22'),
('Charles',  'Houston',      'Estadounidense', '1913-08-24'),
('George',   'Band',         'Británico',   '1929-03-07'),
('Joe',      'Brown',        'Británico',   '1930-10-26'),
('Fritz',    'Luchsinger',   'Suizo',       '1921-01-07'),
('Ernst',    'Reiss',        'Suizo',       '1922-04-02'),
('Jean',     'Couzy',        'Francés',     '1923-09-07'),
('Lionel',   'Terray',       'Francés',     '1921-07-25');

-- Datos para tabla Ascensiones
INSERT INTO ASCENSIONES (idPico, idAlpinista, fechaAscension, expedicion, ruta) VALUES
(1,  1, '1953-05-29', 1, 'Cresta Sur'),
(1,  2, '1953-05-29', 1, 'Cresta Sur'),
(1,  3, '1956-05-23', 2, 'Cresta Sur'),
(1,  4, '1956-05-23', 2, 'Cresta Sur'),
(2,  5, '1954-07-31', 1, 'Espolón Abruzzi'),
(2,  6, '1954-07-31', 1, 'Espolón Abruzzi'),
(2,  7, '1978-08-06', 2, 'Espolón Abruzzi'),
(2,  8, '1978-08-06', 2, 'Espolón Abruzzi'),
(3,  9, '1955-05-25', 1, 'Cara Suroeste'),
(3, 10, '1955-05-25', 1, 'Cara Suroeste'),
(4, 11, '1956-05-18', 1, 'Cara Oeste'),
(4, 12, '1956-05-18', 1, 'Cara Oeste'),
(5, 13, '1955-05-15', 1, 'Pilar Noroeste'),
(5, 14, '1955-05-15', 1, 'Pilar Noroeste');