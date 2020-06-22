-- application distribution (per day)
SELECT DATE(time) AS time, code, count(*) AS amount
FROM version_stats
         JOIN application_codes ON application_code_id = application_codes.id
GROUP BY DATE(time), code
ORDER BY DATE(time), code, amount;

-- version distribution (per day)
SELECT DATE(time) AS time, version, count(*) AS amount
FROM version_stats
         JOIN versions ON version_id = versions.id
GROUP BY DATE(time), version
ORDER BY DATE(time), version, amount;

-- icons
SELECT DATE(time)             AS date,
       themes.name            AS theme,
       languages.name         AS language,
       application_names.name as application_name,
       icons_wanted.name      AS icon_wanted,
       icons_used.name        AS icon_used,
       count(*)               AS amount
FROM icon_stats
         JOIN languages ON icon_stats.language_id = languages.id
         JOIN themes ON icon_stats.theme_id = themes.id
         JOIN application_names ON icon_stats.application_name_id = application_names.id
         JOIN icons icons_wanted ON icon_stats.icon_wanted_id = icons_wanted.id
         JOIN icons icons_used ON icon_stats.icon_used_id = icons_used.id
GROUP BY DATE(time), theme, language, application_name, icon_wanted, icon_used
ORDER BY DATE(time), theme, language;

-- files
SELECT DATE(time)      AS date,
       editors.name    AS editor,
       types.name      AS type,
       extensions.name AS extension,
       languages.name  AS language,
       count(*)               AS amount
FROM file_stats
         JOIN editors ON file_stats.editor_id = editors.id
         JOIN types ON file_stats.type_id = types.id
         JOIN extensions ON file_stats.extension_id = extensions.id
         JOIN languages ON file_stats.language_id = languages.id
GROUP BY DATE(time), editor, type, language, extension
ORDER BY DATE(time), editor, type, language, extension;
