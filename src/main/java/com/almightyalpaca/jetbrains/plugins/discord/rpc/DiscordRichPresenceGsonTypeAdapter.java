package com.almightyalpaca.jetbrains.plugins.discord.rpc;

import club.minnced.discord.rpc.DiscordRichPresence;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DiscordRichPresenceGsonTypeAdapter extends TypeAdapter<DiscordRichPresence>
{
    public DiscordRichPresence read(JsonReader reader) throws IOException
    {
        if (reader.peek() == JsonToken.NULL)
        {
            reader.nextNull();
            return null;
        }

        throw new IOException();
    }

    public void write(JsonWriter writer, DiscordRichPresence value) throws IOException
    {
        writer.setSerializeNulls(true);

        if (value == null)
        {
            writer.nullValue();
            return;
        }

        writer.beginObject();

        writer.name("state");
        writer.value(value.state);
        writer.name("details");
        writer.value(value.details);
        writer.name("startTimestamp");
        writer.value(value.startTimestamp);
        writer.name("endTimestamp");
        writer.value(value.endTimestamp);
        writer.name("largeImageKey");
        writer.value(value.largeImageKey);
        writer.name("largeImageText");
        writer.value(value.largeImageText);
        writer.name("smallImageKey");
        writer.value(value.smallImageKey);
        writer.name("smallImageText");
        writer.value(value.smallImageText);
        writer.name("partyId");
        writer.value(value.partyId);
        writer.name("partySize");
        writer.value(value.partySize);
        writer.name("partyMax");
        writer.value(value.partyMax);
        writer.name("matchSecret");
        writer.value(value.matchSecret);
        writer.name("joinSecret");
        writer.value(value.joinSecret);
        writer.name("spectateSecret");
        writer.value(value.spectateSecret);
        writer.name("instance");
        writer.value(value.instance);

        writer.endObject();
    }
}
