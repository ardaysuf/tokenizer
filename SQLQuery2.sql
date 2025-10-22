DROP TABLE JavaSeparators;

CREATE TABLE JavaSeparators(
    Separator NVARCHAR(50) NOT NULL UNIQUE,
);

INSERT INTO JavaSeparators (Separator)
VALUES
    (';'),
    ('{'),
    ('}'),
    ('('),
    (')'),
    ('['),
    (']'),
    (','),
    (':');