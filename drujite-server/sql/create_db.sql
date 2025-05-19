CREATE OR REPLACE FUNCTION convert_to_moscow_time(ts timestamptz)
    RETURNS timestamptz AS
$$
BEGIN
    RETURN (ts AT TIME ZONE 'Etc/GMT-3');
END;
$$ LANGUAGE plpgsql;

-- Helper function to update the timestamp (using Moscow time)
CREATE OR REPLACE FUNCTION update_modified_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = convert_to_moscow_time(now());
    RETURN NEW;
END;
$$ language 'plpgsql';


--users
CREATE TABLE users
(
    id         UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    username   VARCHAR(255)       NOT NULL,
    phone      VARCHAR(15) UNIQUE NOT NULL,
    password   VARCHAR(255)       NOT NULL,
    gender     VARCHAR(15)        NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);

CREATE TRIGGER update_users_modtime
    BEFORE UPDATE
    ON users
    FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();


--sessions
CREATE TABLE sessions
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255)             NOT NULL,
    description TEXT                     NOT NULL,
    start_date   TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date     TIMESTAMP WITH TIME ZONE NOT NULL,
    image_url   VARCHAR(255),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);

CREATE TRIGGER update_sessions_modtime
    BEFORE UPDATE
    ON sessions
    FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();


--timetables
CREATE TABLE timetables
(
    id         SERIAL PRIMARY KEY,
    session_id  INT REFERENCES sessions (id) ON DELETE CASCADE,
    date       DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);

CREATE TRIGGER update_timetables_modtime
    BEFORE UPDATE
    ON timetables
    FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();


--events
CREATE TABLE events
(
    id          SERIAL PRIMARY KEY,
    timetable_id INT REFERENCES timetables (id) ON DELETE CASCADE,
    num         INT,
    name        VARCHAR(65) NOT NULL,
    time        TIME WITHOUT TIME ZONE NOT NULL,
    is_title     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);
ALTER TABLE events
    ALTER COLUMN time DROP NOT NULL;

CREATE TRIGGER update_events_modtime
    BEFORE UPDATE
    ON events
    FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();


--news
CREATE TABLE news
(
    id         SERIAL PRIMARY KEY,
    session_id INT REFERENCES sessions (id) ON DELETE CASCADE,
    title      VARCHAR(255) NOT NULL,
    content    TEXT NOT NULL ,
    time       TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    image_url   VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);

CREATE TRIGGER update_news_modtime
    BEFORE UPDATE
    ON news
    FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();


--clans
CREATE TABLE clans
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);


CREATE TRIGGER update_clan_modtime
    BEFORE UPDATE
    ON clans
    FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();


--characters
CREATE TABLE characters
(
    id         SERIAL PRIMARY KEY,
    clan_id    INT REFERENCES clans(id) ON DELETE CASCADE,
    name       VARCHAR(255) NOT NULL ,
    story      TEXT NOT NULL,
    image_url   VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);

CREATE TRIGGER update_characters_modtime
    BEFORE UPDATE
    ON characters
    FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();


--sessions_clans
CREATE TABLE sessions_clans
(
    id         SERIAL PRIMARY KEY,
    session_id INT REFERENCES sessions (id) ON DELETE CASCADE,
    clan_id    INT REFERENCES clans(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);

CREATE TRIGGER update_sessions_clans_modtime
    BEFORE UPDATE
    ON sessions_clans
    FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();


--users_sessions
CREATE TABLE users_sessions
(
    id           SERIAL PRIMARY KEY,
    user_id      UUID REFERENCES users (id) ON DELETE CASCADE,
    session_id   INT REFERENCES sessions (id) ON DELETE CASCADE,
    character_id INT REFERENCES characters (id) ON DELETE SET NULL,
    transfer_reason TEXT,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);

CREATE TRIGGER update_users_sessions_modtime
    BEFORE UPDATE
    ON users_sessions
    FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();


--goals
CREATE TABLE goals
(
    id           SERIAL PRIMARY KEY,
    users_session_id INT REFERENCES users_sessions(id) ON DELETE CASCADE,
    name         VARCHAR(255) NOT NULL,
    is_completed  BOOLEAN DEFAULT FALSE,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);

CREATE TRIGGER update_goals_modtime
    BEFORE UPDATE
    ON goals
    FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();


--clothing

-- clothing_type table
CREATE TABLE clothing_type (
                               id SERIAL PRIMARY KEY,
                               name VARCHAR(255) NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
                               updated_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);

-- clothing_item table
CREATE TABLE clothing_item (
                               id SERIAL PRIMARY KEY,
                               type_id INT REFERENCES clothing_type(id) ON DELETE CASCADE,  -- Corrected FK column name
                               image_url VARCHAR(255),
                               image_icon_url VARCHAR(255),
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
                               updated_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())
);


-- character_clothing table
CREATE TABLE character_clothing (
                                    id SERIAL PRIMARY KEY,
                                    character_id INT REFERENCES characters(id) ON DELETE CASCADE, -- Added ON DELETE CASCADE
                                    clothing_item_id INT REFERENCES clothing_item(id) ON DELETE CASCADE, -- Added ON DELETE CASCADE
                                    created_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now()),
                                    updated_at TIMESTAMP WITH TIME ZONE DEFAULT convert_to_moscow_time(now())

);

