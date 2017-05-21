package com.example.mygson.internal.bind;

/**
 * Created by lusen on 2017/4/17.
 */

import com.example.mygson.MyGson;
import com.example.mygson.TypeAdapter;
import com.example.mygson.TypeAdapterFactory;
import com.example.mygson.internal.$Gson$Types;
import com.example.mygson.reflect.TypeToken;
import com.example.mygson.stream.JsonReader;
import com.example.mygson.stream.JsonToken;
import com.example.mygson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapt an array of objects.
 */
public final class ArrayTypeAdapter<E> extends TypeAdapter<Object> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override public <T> TypeAdapter<T> create(MyGson gson, TypeToken<T> typeToken) {
            Type type = typeToken.getType();
            if (!(type instanceof GenericArrayType || type instanceof Class && ((Class<?>) type).isArray())) {
                return null;
            }

            Type componentType = $Gson$Types.getArrayComponentType(type);
            TypeAdapter<?> componentTypeAdapter = gson.getAdapter(TypeToken.get(componentType));
            return new ArrayTypeAdapter(
                    gson, componentTypeAdapter, $Gson$Types.getRawType(componentType));
        }
    };

    private final Class<E> componentType;
    private final TypeAdapter<E> componentTypeAdapter;

    public ArrayTypeAdapter(MyGson context, TypeAdapter<E> componentTypeAdapter, Class<E> componentType) {
        this.componentTypeAdapter =
                new TypeAdapterRuntimeTypeWrapper<E>(context, componentTypeAdapter, componentType);
        this.componentType = componentType;
    }

    @Override public Object read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        List<E> list = new ArrayList<E>();
        in.beginArray();
        while (in.hasNext()) {
            E instance = componentTypeAdapter.read(in);
            list.add(instance);
        }
        in.endArray();
        Object array = Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override public void write(JsonWriter out, Object array) throws IOException {
        if (array == null) {
            out.nullValue();
            return;
        }

        out.beginArray();
        for (int i = 0, length = Array.getLength(array); i < length; i++) {
            E value = (E) Array.get(array, i);
            componentTypeAdapter.write(out, value);
        }
        out.endArray();
    }
}
