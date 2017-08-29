package me.ialistannen.libraryhelpercommon.book;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import me.ialistannen.isbnlookuplib.book.BookDataKey;
import me.ialistannen.isbnlookuplib.book.StandardBookDataKeys;
import me.ialistannen.isbnlookuplib.isbn.Isbn;
import me.ialistannen.isbnlookuplib.isbn.IsbnConverter;
import me.ialistannen.isbnlookuplib.util.Optional;
import me.ialistannen.isbnlookuplib.util.Pair;
import me.ialistannen.isbnlookuplib.util.Price;

/**
 * A class that mediates between {@link LoanableBook}s and a serialize form.
 */
public class IntermediaryBook {

  private static final Set<BookDataKey> STANDARD_KEYS = new HashSet<BookDataKey>() {{
    add(BorrowerKey.INSTANCE);
    Collections.addAll(this, StandardBookDataKeys.values());
  }};

  private String title;
  private List<Pair<String, String>> authors;
  private String publisher;
  private String coverType;
  private String description;
  private Integer pageCount;
  private String language;
  private String isbnString;
  private Isbn isbn;
  private Price price;
  private Double rating;
  private String borrower;
  private Map<String, Object> extra;

  private IntermediaryBook(String title,
      List<Pair<String, String>> authors, String publisher, String coverType,
      String description, Integer pageCount, String language, String isbnString,
      Isbn isbn, Price price, Double rating, String borrower, Map<String, Object> extra) {
    this.title = title;
    this.authors = authors;
    this.publisher = publisher;
    this.coverType = coverType;
    this.description = description;
    this.pageCount = pageCount;
    this.language = language;
    this.isbnString = isbnString;
    this.isbn = isbn;
    this.price = price;
    this.rating = rating;
    this.borrower = borrower;
    this.extra = extra;
  }

  /**
   * Creates the intermediary book from a {@link LoanableBook}.
   *
   * @param book The {@link LoanableBook} to create it from
   * @return The created {@link IntermediaryBook}
   */
  public static IntermediaryBook fromLoanableBook(LoanableBook book) {
    Map<String, Object> extra = new HashMap<>();
    for (Entry<BookDataKey, Object> entry : book.getAllData().entrySet()) {
      if (STANDARD_KEYS.contains(entry.getKey())) {
        continue;
      }
      extra.put(StringBookDataKey.getNormalizedName(entry.getKey().name()), entry.getValue());
    }
    return new IntermediaryBook(
        book.getData(StandardBookDataKeys.TITLE),
        book.getData(StandardBookDataKeys.AUTHORS),
        book.getData(StandardBookDataKeys.PUBLISHER),
        book.getData(StandardBookDataKeys.COVER_TYPE),
        book.getData(StandardBookDataKeys.DESCRIPTION),
        book.getData(StandardBookDataKeys.PAGE_COUNT),
        book.getData(StandardBookDataKeys.LANGUAGE),
        book.getData(StandardBookDataKeys.ISBN_STRING),
        book.getData(StandardBookDataKeys.ISBN),
        book.getData(StandardBookDataKeys.PRICE),
        book.getData(BorrowerKey.INSTANCE),
        book.getData(StandardBookDataKeys.RATING),
        extra
    );
  }

  /**
   * Converts this book to a {@link LoanableBook}.
   *
   * @return This as a {@link LoanableBook}
   */
  public LoanableBook toLoanableBook() {
    LoanableBook book = new LoanableBook();

    setBookDataRespectNull(book, StandardBookDataKeys.TITLE, title);
    setBookDataRespectNull(book, StandardBookDataKeys.AUTHORS, authors);
    setBookDataRespectNull(book, StandardBookDataKeys.PUBLISHER, publisher);
    setBookDataRespectNull(book, StandardBookDataKeys.COVER_TYPE, coverType);
    setBookDataRespectNull(book, StandardBookDataKeys.DESCRIPTION, description);
    setBookDataRespectNull(book, StandardBookDataKeys.PAGE_COUNT, pageCount);
    setBookDataRespectNull(book, StandardBookDataKeys.LANGUAGE, language);
    setBookDataRespectNull(book, StandardBookDataKeys.ISBN_STRING, isbnString);
    setBookDataRespectNull(book, StandardBookDataKeys.ISBN, isbn);
    setBookDataRespectNull(book, StandardBookDataKeys.PRICE, price);
    setBookDataRespectNull(book, BorrowerKey.INSTANCE, borrower);
    setBookDataRespectNull(book, StandardBookDataKeys.RATING, rating);

    for (Entry<String, Object> entry : extra.entrySet()) {
      book.setData(new StringBookDataKey(entry.getKey()), entry.getValue());
    }

    return book;
  }

  private void setBookDataRespectNull(LoanableBook book, BookDataKey key, Object value) {
    if (value != null) {
      book.setData(key, value);
    }
  }

  /**
   * Configures the builder to handle this class.
   *
   * @param builder The {@link GsonBuilder}
   * @return The same {@link GsonBuilder}
   */
  public static GsonBuilder configureGson(GsonBuilder builder) {
    return builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .enableComplexMapKeySerialization()
        .registerTypeAdapter(IntermediaryBook.class, Deserializer.INSTANCE)
        .registerTypeAdapterFactory(new LowercaseBookDataKeySerializer())
        .registerTypeAdapter(Isbn.class,
            (JsonSerializer<Isbn>) (src, type, ctx) -> new JsonPrimitive(
                src.getDigitsAsString()))
        .registerTypeAdapter(Isbn.class, new IsbnJsonDeserializer());
  }

  @Override
  public String toString() {
    return "IntermediaryBook{"
        + "title='" + title + '\''
        + ", authors=" + authors
        + ", publisher='" + publisher + '\''
        + ", coverType='" + coverType + '\''
        + ", description='" + description + '\''
        + ", pageCount=" + pageCount
        + ", language='" + language + '\''
        + ", isbnString='" + isbnString + '\''
        + ", isbn=" + isbn
        + ", price=" + price
        + ", rating=" + rating
        + ", borrower='" + borrower + '\''
        + ", extra=" + extra
        + '}';
  }

  private static class Deserializer implements JsonDeserializer<IntermediaryBook> {

    static final Deserializer INSTANCE = new Deserializer();

    private Deserializer() {
      // prevent instantiation
    }

    @Override
    public IntermediaryBook deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {

      JsonObject root = json.getAsJsonObject();

      String title = getValueOrNull(context, root, "title", String.class);
      String publisher = getValueOrNull(context, root, "publisher", String.class);
      String coverType = getValueOrNull(context, root, "coverType", String.class);
      String description = getValueOrNull(context, root, "description", String.class);
      Integer pageCount = getValueOrNull(context, root, "pageCount", Integer.class);
      String language = getValueOrNull(context, root, "language", String.class);
      String isbnString = getValueOrNull(context, root, "isbnString", String.class);
      Isbn isbn = getValueOrNull(context, root, "isbn", Isbn.class);
      Price price = getValueOrNull(context, root, "price", Price.class);
      Double rating = getValueOrNull(context, root, "rating", Double.class);
      String borrower = getValueOrNull(context, root, "borrower", String.class);
      Map<String, Object> extra = getValueOrNull(context, root, "extra", Map.class);

      Type type = new TypeToken<List<Pair<String, String>>>() {
      }.getType();
      List<Pair<String, String>> authors = getValueOrNull(context, root, "authors", type);

      return new IntermediaryBook(title, authors, publisher, coverType, description, pageCount,
          language, isbnString, isbn, price, rating, borrower, extra);
    }

    private <T> T getValueOrNull(JsonDeserializationContext context, JsonObject root, String name,
        Type type) {
      if (!root.has(name)) {
        return null;
      }
      return context.deserialize(root.get(name), type);
    }
  }

  private static class IsbnJsonDeserializer implements JsonDeserializer<Isbn> {

    private IsbnConverter isbnConverter = new IsbnConverter();

    @Override
    public Isbn deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context)
        throws JsonParseException {

      Optional<Isbn> isbnOptional = isbnConverter.fromString(json.getAsString());

      if (isbnOptional.isPresent()) {
        return isbnOptional.get();
      }

      return null;
    }
  }
}
