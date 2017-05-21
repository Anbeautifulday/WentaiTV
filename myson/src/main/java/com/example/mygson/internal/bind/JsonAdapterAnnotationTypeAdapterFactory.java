package com.example.mygson.internal.bind;

/**
 * Created by lusen on 2017/4/17.
 */

import com.example.mygson.MyGson;
import com.example.mygson.JsonDeserializer;
import com.example.mygson.JsonSerializer;
import com.example.mygson.TypeAdapter;
import com.example.mygson.TypeAdapterFactory;
import com.example.mygson.annotations.JsonAdapter;
import com.example.mygson.internal.ConstructorConstructor;
import com.example.mygson.reflect.TypeToken;

/**
 * Given a type T, looks for the annotation {@link JsonAdapter} and uses an instance of the
 * specified class as the default type adapter.
 *
 * @since 2.3
 */
public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;

    public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
        this.constructorConstructor = constructorConstructor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(MyGson gson, TypeToken<T> targetType) {
        Class<? super T> rawType = targetType.getRawType();
        JsonAdapter annotation = rawType.getAnnotation(JsonAdapter.class);
        if (annotation == null) {
            return null;
        }
        return (TypeAdapter<T>) getTypeAdapter(constructorConstructor, gson, targetType, annotation);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) // Casts guarded by conditionals.
    TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor, MyGson gson,
                                  TypeToken<?> type, JsonAdapter annotation) {
        Object instance = constructorConstructor.get(TypeToken.get(annotation.value())).construct();

        TypeAdapter<?> typeAdapter;
        if (instance instanceof TypeAdapter) {
            typeAdapter = (TypeAdapter<?>) instance;
        } else if (instance instanceof TypeAdapterFactory) {
            typeAdapter = ((TypeAdapterFactory) instance).create(gson, type);
        } else if (instance instanceof JsonSerializer || instance instanceof JsonDeserializer) {
            JsonSerializer<?> serializer = instance instanceof JsonSerializer
                    ? (JsonSerializer) instance
                    : null;
            JsonDeserializer<?> deserializer = instance instanceof JsonDeserializer
                    ? (JsonDeserializer) instance
                    : null;
            typeAdapter = new TreeTypeAdapter(serializer, deserializer, gson, type, null);
        } else {
            throw new IllegalArgumentException(
                    "@JsonAdapter value must be TypeAdapter, TypeAdapterFactory, "
                            + "JsonSerializer or JsonDeserializer reference.");
        }

        if (typeAdapter != null && annotation.nullSafe()) {
            typeAdapter = typeAdapter.nullSafe();
        }

        return typeAdapter;
    }
}

