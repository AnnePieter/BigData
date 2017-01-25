SELECT naam
FROM Acteurs t1
LEFT OUTER JOIN ActeursFilms
WHERE t2.FilmID == t2.FilmID AND t2.ActeurId =! t2.ActeurID;

SELECT COUNT FilmID
FROM ActeursFilms t1
LEFT OUTER JOIN Acteurs t2
ON t1.ActeurID = t2.ActeurID
WHERE t2.naam = ;

SELECT titel, MAX(buitenlandseOpbrangst)
FROM Films;

SELECT titel, MAX(beoordeling)
FROM Films;

SELECT titel, MAX(productiekosten)
FROM Films;