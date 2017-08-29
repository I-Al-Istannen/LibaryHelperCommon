package me.ialistannen.libraryhelpercommon.book;

import java.util.Objects;
import me.ialistannen.isbnlookuplib.book.AbstractBookDataKey;
import me.ialistannen.isbnlookuplib.book.BookDataKey;

/**
 * A {@link BookDataKey} with only a name.
 */
public class StringBookDataKey extends AbstractBookDataKey {

  private final String name;

  /**
   * @param name The name. Will be normlized using {@link #getNormalizedName(String)}
   */
  public StringBookDataKey(String name) {
    this.name = getNormalizedName(name);
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * @param value The name of a {@link BookDataKey} to normalize
   * @return The normalized name
   */
  public static String getNormalizedName(String value) {
    return value
        .toLowerCase()
        .replace('-', '_')
        .replaceAll("\\s", "_");
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StringBookDataKey that = (StringBookDataKey) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
