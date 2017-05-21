package com.example.mygson.internal;

/**
 * Created by lusen on 2017/4/17.
 */

import java.io.IOException;
import com.example.mygson.stream.JsonReader;

/**
 * Internal-only APIs of JsonReader available only to other classes in Gson.
 */
public abstract class JsonReaderInternalAccess {
    public static JsonReaderInternalAccess INSTANCE;

    /**
     * Changes the type of the current property name token to a string value.
     */
    public abstract void promoteNameToValue(JsonReader reader) throws IOException;
}

