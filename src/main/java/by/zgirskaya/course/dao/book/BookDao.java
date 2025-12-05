package by.zgirskaya.course.dao.book;

import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.book.Book;

import java.util.List;
import java.util.UUID;

public interface BookDao {
  List<Book> getAllBooks() throws DaoException;
  Book findBookById(UUID id) throws DaoException;
  Integer findBookCountById(UUID id) throws DaoException;
}
