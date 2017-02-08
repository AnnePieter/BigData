SELECT t1.naam
FROM Acteurs t1
LEFT OUTER JOIN ActeursFilms t2
ON t1.ActeurID = t2.ActeurID
WHERE t2.FilmID == t2.FilmID AND t2.ActeurId =! t2.ActeurID;

SELECT COUNT FilmID
FROM ActeursFilms t1
LEFT OUTER JOIN Acteurs t2
ON t1.ActeurID = t2.ActeurID
WHERE t2.naam = ;

SELECT t1.titel
FROM Films t1
LEFT OUTER JOIN ActeursFilms t2
ON t1.FilmID = t2.FilmID
RIGHT OUTER JOIN Acteurs t3
ON t2.ActeurID = t3.ActeurID
WHERE t1.naam == "Joop Braakhekke";

SELECT titel
FROM Films
WHERE lengte = (SELECT MAX(lengte)
				FROM Films
				WHERE beoordeling >= 8.5);

SELECT titel
FROM Films
WHERE beoordeling = (SELECT MAX(beoordeling)
					 FROM Films);

SELECT t1.naam, t2.titel
FROM Acteurs t1, Films t2
LEFT OUTER JOIN ActeursFilms t2
ON t1.FilmID = t2.FilmID
RIGHT OUTER JOIN Acteurs t3
ON t2.ActeurID = t3.ActeurID
WHERE lengte = (SELECT MAX(lengte)
				FROM Films);