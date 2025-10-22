CREATE TABLE JavaOperators( 
	Operator NVARCHAR(50) NOT NULL UNIQUE,
);

INSERT INTO JavaOperators (Operator)

VALUES
    ('+'),
    ('-'),
    ('*'),
    ('/'),
    ('='),
    ('%'),
    ('!'),
    ('|'),
    ('&'),
    ('^'),
    ('<'),
    ('>'),
    ('?');

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
    ('.'),
    (':');