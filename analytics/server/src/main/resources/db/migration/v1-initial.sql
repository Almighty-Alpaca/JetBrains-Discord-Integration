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
CREATE TABLE versions
(
    id      SMALLSERIAL PRIMARY KEY,
    version VARCHAR(16) NOT NULL UNIQUE
);

-- Application codes (IU, PY etc.)
CREATE TABLE application_codes
(
    id   SMALLSERIAL PRIMARY KEY,
    code VARCHAR(8) NOT NULL UNIQUE
);

-- Application names
CREATE TABLE application_names
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

-- Editors
CREATE TABLE editors
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

-- File Types
CREATE TABLE types
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

-- File Extensions
CREATE TABLE extensions
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(16) NOT NULL UNIQUE
);

-- Icons
CREATE TABLE icons
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

-- Languages
CREATE TABLE languages
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(16) NOT NULL UNIQUE
);

-- Themes
CREATE TABLE themes
(
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(16) NOT NULL UNIQUE
);

-- Crowdsource unmatched file types and editors
-- Once per file and day
CREATE TABLE file_stats
(
    time         TIMESTAMP,
    editor_id    SMALLINT NOT NULL REFERENCES editors,
    type_id      SMALLINT NOT NULL REFERENCES types,
    extension_id INT REFERENCES extensions,
    language_id  SMALLINT REFERENCES languages
);

-- Track icon usage
-- Once per matched file and day
CREATE TABLE icon_stats
(
    time                TIMESTAMP,
    language_id         SMALLINT NOT NULL REFERENCES languages,
    theme_id            SMALLINT NOT NULL REFERENCES themes,
    application_name_id SMALLINT NOT NULL REFERENCES application_names,
    icon_wanted_id      SMALLINT NOT NULL REFERENCES icons,
    icon_used_id        SMALLINT NOT NULL REFERENCES icons
);

-- Track version
-- Once per day
CREATE TABLE version_stats
(
    time                TIMESTAMP,
    version_id          SMALLINT NOT NULL REFERENCES versions,
    application_code_id SMALLINT NOT NULL REFERENCES application_codes
);
