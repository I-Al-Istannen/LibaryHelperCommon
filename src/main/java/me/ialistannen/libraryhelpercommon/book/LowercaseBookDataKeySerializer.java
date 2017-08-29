package me.ialistannen.libraryhelpercommon.book;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.ialistannen.isbnlookuplib.book.BookDataKey;

/**
 * A {@link TypeAdapterFactory} that enables lower snake case serialization for {@link
 * BookDataKey}s.
 */
class LowercaseBookDataKeySerializer implements TypeAdapterFactory {

  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    if (!getTypeHierarchy(type.getRawType()).contains(BookDataKey.class)) {
      return null;
    }

    @SuppressWarnings("unchecked")
    TypeAdapter<T> typeAdapter = (TypeAdapter<T>) new TypeAdapter<BookDataKey>() {
      @Override
      public void write(JsonWriter out, BookDataKey value) throws IOException {
        String name = StringBookDataKey.getNormalizedName(value.name());

        out.value(name);
      }

      @Override
      public BookDataKey read(JsonReader in) throws IOException {
        // we will not deserialize them, as that is sadly not really possible here anyways
        return null;
      }
    };
    return typeAdapter;
  }

  /**
   * Returns the complete (including interfaces) type hierarchy for a class.
   *
   * @param start The start class
   * @return The complete type hierarchy including all implemented interfaces
   */
  private static List<Class<?>> getTypeHierarchy(Class<?> start) {
    if (start.getSuperclass() == null) {
      return Collections.emptyList();
    }
    List<Class<?>> result = new ArrayList<>();

    result.add(start.getSuperclass());
    result.addAll(getTypeHierarchy(start.getSuperclass()));

    for (Class<?> anInterface : start.getInterfaces()) {
      result.add(anInterface);
      result.addAll(getTypeHierarchy(anInterface));
    }

    return result;
  }

}
