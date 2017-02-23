INSERT INTO movies (movie_or_series, release_year, country, rating, revenue, currency)
SELECT c.movie_or_series, c.release_year, c.country, r.rating, b.revenue, b.currency
FROM countriescsv c
LEFT OUTER JOIN ratingscsv r
ON c.movie_or_series = r.movie_or_series
LEFT OUTER JOIN businesscsv b
ON c.movie_or_series = b.movie_or_series
WHERE c.movie_or_series IS NOT NULL AND c.release_year IS NOT NULL;