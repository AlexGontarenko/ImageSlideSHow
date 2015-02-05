package ru.redmadrobot.alexgontarenko.slideshow.parcers;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class BaseJSONParser implements Parcelable {
    private ArrayList<String> nodes;

    private JsonToken previousToken;

    public BaseJSONParser() {
        super();
    }

    public void readInputStream(InputStream is) throws IOException {
        startReadJson(new JsonReader(new BufferedReader(new InputStreamReader(is, "UTF-8"))));
    }

    protected void startReadJson(JsonReader reader) throws IOException {
        nodes = new ArrayList<String>();

        while (!reader.peek().equals(JsonToken.END_DOCUMENT)) {

            if (reader.peek().equals(JsonToken.NAME)) {
                final String name = reader.nextName();

                if (reader.peek().equals(JsonToken.BEGIN_OBJECT))
                    onBeginObject(reader, name);
                else if (reader.peek().equals(JsonToken.BEGIN_ARRAY))
                    onBeginArray(reader, name);
                else {
                    if (!parse(reader, reader.peek(), buildPath(), name)) {
                        consume(reader, reader.peek());
                    }
                }
            }
            else if (reader.peek().equals(JsonToken.BEGIN_OBJECT))
                onBeginObject(reader, null);
            else if (reader.peek().equals(JsonToken.BEGIN_ARRAY))
                onBeginArray(reader, null);
            else if (reader.peek().equals(JsonToken.END_OBJECT))
                onEndObject(reader);
            else if (reader.peek().equals(JsonToken.END_ARRAY))
                onEndArray(reader);
            else {
                consume(reader, reader.peek());
            }

            previousToken = reader.peek();
        }

        nodes = null;
    }

    /**
     * @param reader
     * @param token
     * @param path
     * @param nodeName
     * @return catched?
     */
    protected abstract boolean parse(JsonReader reader, JsonToken token, String path, String nodeName)
            throws IOException;

    protected void onBeginArray(JsonReader reader, String name) throws IOException {
        nodes.add(name);

        reader.beginArray();
    }

    protected void onEndArray(JsonReader reader) throws IOException {
        reader.endArray();

        if (!nodes.isEmpty())
            nodes.remove(nodes.size() - 1);
    }

    protected void onBeginObject(JsonReader reader, String name) throws IOException {
        if (name != null && (previousToken == null || previousToken.equals(JsonToken.NAME)))
            nodes.add(name);
        else if (nodes.size() > 0)
            nodes.add(null);
        else
            nodes.add("/");

        reader.beginObject();
    }

    protected void onEndObject(JsonReader reader) throws IOException {
        reader.endObject();

        nodes.remove(nodes.size() - 1);
    }

    protected String getCurrentNodeName() {
        return nodes.get(nodes.size() - 1);
    }

    protected String buildPath() {
        final StringBuilder pathBuilder = new StringBuilder();

        for (String node : nodes) {
            if (!TextUtils.isEmpty(node)) {
                if (pathBuilder.length() > 1)
                    pathBuilder.append('/');

                pathBuilder.append(node);
            }
        }

        return pathBuilder.toString();
    }

    protected boolean findNextTokenType(JsonReader reader, JsonToken type) throws IOException {
        JsonToken token = reader.peek();
        while (!token.equals(JsonToken.END_DOCUMENT)) {
            if (token == type) {
                return true;
            }

            consume(reader, token);
            token = reader.peek();
        }

        return false;
    }

    protected boolean findArray(JsonReader reader, String objectName) throws IOException {
        while (findNextTokenType(reader, JsonToken.NAME)) {
            final String name = reader.nextName();
            if (name.equals(objectName)) {
                final JsonToken token = reader.peek();
                if (token == JsonToken.BEGIN_ARRAY) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean findObject(JsonReader reader, String objectName) throws IOException {
        while (findNextTokenType(reader, JsonToken.NAME)) {
            final String name = reader.nextName();
            if (name.equals(objectName)) {
                final JsonToken token = reader.peek();
                if (token == JsonToken.BEGIN_OBJECT) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void consume(JsonReader reader, JsonToken type) throws IOException {
        switch (type) {
            case BEGIN_ARRAY:
                reader.beginArray();
                break;
            case BEGIN_OBJECT:
                reader.beginObject();
                break;
            case END_ARRAY:
                reader.endArray();
                break;
            case END_OBJECT:
                reader.endObject();
                break;
            default:
                reader.skipValue();
        }
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
    }
}