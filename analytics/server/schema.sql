/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

-- Versions
CREATE TABLE IF NOT EXISTS versions
(
    id      SMALLSERIAL PRIMARY KEY,
    version VARCHAR(16) NOT NULL UNIQUE
);

-- Application codes (IU, PY etc.)
CREATE TABLE IF NOT EXISTS application_codes
(
    id   SMALLSERIAL PRIMARY KEY,
    code VARCHAR(8) NOT NULL UNIQUE
);

-- Application names
CREATE TABLE IF NOT EXISTS application_names
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

-- Editors
CREATE TABLE IF NOT EXISTS editors
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

-- File Extensions
CREATE TABLE IF NOT EXISTS extensions
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(16) NOT NULL UNIQUE
);

-- Icons
CREATE TABLE IF NOT EXISTS icons
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

-- Languages
CREATE TABLE IF NOT EXISTS languages
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(16) NOT NULL UNIQUE
);

-- Themes
CREATE TABLE IF NOT EXISTS themes
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(16) NOT NULL UNIQUE
);

-- Crowdsource unmatched file types and editors
-- Once per file and day
CREATE TABLE IF NOT EXISTS file_stats
(
    time        TIMESTAMP WITH TIME ZONE NOT NULL,
    editor_id   SMALLINT                 NOT NULL REFERENCES editors,
    extension   INT REFERENCES extensions,
    language_id SMALLINT REFERENCES languages
);

-- Track icon usage
-- Once per matched file and day
CREATE TABLE IF NOT EXISTS icon_stats
(
    time                TIMESTAMP WITH TIME ZONE NOT NULL,
    language_id         SMALLINT                 NOT NULL REFERENCES languages,
    theme_id            SMALLINT                 NOT NULL REFERENCES themes,
    application_name_id SMALLINT                 NOT NULL REFERENCES application_names,
    icon_wanted_id      SMALLINT                 NOT NULL REFERENCES icons,
    icon_used_id        SMALLINT                 NOT NULL REFERENCES icons
);

-- Track version
-- Once per day
CREATE TABLE IF NOT EXISTS version_stats
(
    time                TIMESTAMP WITH TIME ZONE NOT NULL,
    version_id          SMALLINT                 NOT NULL REFERENCES versions,
    application_code_id SMALLINT                 NOT NULL REFERENCES application_codes
);
