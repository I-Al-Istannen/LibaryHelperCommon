package me.ialistannen.libraryhelpercommon.book;

import me.ialistannen.isbnlookuplib.book.AbstractBookDataKey;
import me.ialistannen.isbnlookuplib.book.BookDataKey;

/**
 * The {@link BookDataKey} for the one borrowing it.
 */
public final class BorrowerKey extends AbstractBookDataKey {

  private static final String NAME = "BORROWER";

  public static final BorrowerKey INSTANCE = new BorrowerKey();

  private BorrowerKey() {
    // prevent instantiation
  }

  @Override
  public String name() {
    return NAME;
  }
}
