INSERT INTO actorsmovies (actorid, movieid)
SELECT actors.actorid, movies.movieid
FROM actors
LEFT OUTER JOIN actorscsv
ON actors.actor = actorscsv.actor
LEFT OUTER JOIN movies
ON actorscsv.movie_or_series = movies.movie_or_series
WHERE actors.actorid IS NOT NULL AND movies.movieid IS NOT NULL;
