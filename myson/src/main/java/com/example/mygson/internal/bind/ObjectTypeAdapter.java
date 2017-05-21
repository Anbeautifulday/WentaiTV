package com.example.mygson.internal.bind;

/**
 * Created by lusen on 2017/4/17.
 */

import com.example.mygson.MyGson;
import com.example.mygson.TypeAdapter;
import com.example.mygson.TypeAdapterFactory;
import com.example.mygson.internal.LinkedTreeMap;
import com.example.mygson.reflect.TypeToken;
import com.example.mygson.stream.JsonReader;
import com.example.mygson.stream.JsonToken;
import com.example.mygson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adapts types whose static type is only 'Object'. Uses getClass() on
 * serialization and a primitive/Map/List on deserialization.
 */
public final class ObjectTypeAdapter extends TypeAdapter<Object> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked")
        @Override public <T> TypeAdapter<T> create(MyGson gson, TypeToken<T> type) {
            if (type.getRawType() == Object.class) {
                return (TypeAdapter<T>) new ObjectTypeAdapter(gson);
            }
            return null;
        }
    };

    private final MyGson gson;

    ObjectTypeAdapter(MyGson gson) {
        this.gson = gson;
    }

    @Override public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<Object>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(read(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<String, Object>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), read(in));
                }
                in.endObject();
                return map;

            case STRING:
                return in.nextString();

            case NUMBER:
                return in.nextDouble();

            case BOOLEAN:
                return in.nextBoolean();

            case NULL:
                in.nextNull();
                return null;

            default:
                throw new IllegalStateException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override public void write(JsonWriter out, Object value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        TypeAdapter<Object> typeAdapter = (TypeAdapter<Object>) gson.getAdapter(value.getClass());
        if (typeAdapter instanceof ObjectTypeAdapter) {
            out.beginObject();
            out.endObject();
            return;
        }

        typeAdapter.write(out, value);
    }
}

