set client_encoding to 'latin1';

COPY ActorsCSV (actor,movie_or_series,release_year,episode_name,actor_role)
FROM 'C:\csv\actors.list.csv' DELIMITER ',' CSV;

COPY ActressesCSV (actor,movie_or_series,release_year,episode_name,actor_role)
FROM 'C:\csv\actresses.list.csv' DELIMITER ',' CSV;

COPY BiographiesCSV (actor,birth_country,birth_date)
FROM 'C:\csv\biographies.list.csv' DELIMITER ',' CSV;

COPY RatingsCSV (rating,movie_or_series)
FROM 'C:\csv\ratings.list.csv' DELIMITER ',' CSV;

COPY CountriesCSV(movie_or_series,release_year,country)
FROM 'C:\csv\countries.list.csv' DELIMITER ',' CSV;