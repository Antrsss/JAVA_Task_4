package by.zgirskaya.course.dao;

import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.Book;

import java.util.List;
import java.util.UUID;

public interface BookDao {
  List<Book> findAllBooks() throws DaoException;
  Book findBookById(UUID id) throws DaoException;
}
