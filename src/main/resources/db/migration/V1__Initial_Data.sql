CREATE TABLE IF NOT EXISTS PINNED (ID INTEGER PRIMARY KEY, name VARCHAR);

CREATE TABLE IF NOT EXISTS HISTORY (ID INTEGER PRIMARY KEY,
             stationuuid VARCHAR, name VARCHAR,
             url_resolved VARCHAR, homepage VARCHAR,
             favicon VARCHAR, tags VARCHAR, country VARCHAR,
             countrycode VARCHAR, state VARCHAR, language VARCHAR, codec VARCHAR, bitrate INTEGER
             );

CREATE TABLE IF NOT EXISTS FAVOURITES (ID INTEGER PRIMARY KEY,
             stationuuid VARCHAR, name VARCHAR,
             url_resolved VARCHAR, homepage VARCHAR,
             favicon VARCHAR, tags VARCHAR, country VARCHAR,
             countrycode VARCHAR, state VARCHAR, language VARCHAR, codec VARCHAR, bitrate INTEGER
             );