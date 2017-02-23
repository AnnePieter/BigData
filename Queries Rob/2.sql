CREATE TABLE ActorsCSV(
    actor varchar(255) NOT NULL,
    movie_or_series varchar NOT NULL,
    release_year int,
    episode_name varchar(255),
    actor_role varchar
);

CREATE TABLE ActressesCSV(
    actor varchar(255) NOT NULL,
    movie_or_series varchar NOT NULL,
    release_year int,
    episode_name varchar(255),
    actor_role varchar
);

CREATE TABLE BiographiesCSV(
    actor varchar (255) NOT NULL,
    birth_country varchar (255),
    birth_date date
);


CREATE TABLE CountriesCSV(
    movie_or_series varchar NOT NULL,
    release_year int,
    country varchar(255)
);

CREATE TABLE RatingsCSV(
    rating decimal NOT NULL,
    movie_or_series varchar NOT NULL
);


CREATE TABLE Actors(
    ActorID BIGSERIAL PRIMARY KEY NOT NULL,
    actor varchar(255) NOT NULL,
    birth_country varchar (255),
    birth_date date,
    gender varchar(10) NOT NULL,
    country varchar(255)
);

CREATE TABLE Movies(
    MovieID BIGSERIAL PRIMARY KEY NOT NULL,
    movie_or_series varchar NOT NULL,
    release_year int NOT NULL,
    country varchar(255),
    rating decimal,
    revenue int,
    currency varchar(10)
);

CREATE TABLE ActorsMovies(
    ActorID BIGINT NOT NULL references Actors(ActorID),
    MovieID BIGINT NOT NULL references Movies(MovieID),
    PRIMARY KEY (ActorID, MovieID)
);