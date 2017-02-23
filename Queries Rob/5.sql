CREATE TABLE ActorsMovies(
    ActorID BIGINT NOT NULL references Actors(ActorID),
    MovieID BIGINT NOT NULL references Movies(MovieID)
);