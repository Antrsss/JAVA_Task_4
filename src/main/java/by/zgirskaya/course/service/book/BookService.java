package by.zgirskaya.course.service.book;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.book.Book;

import java.util.List;
import java.util.UUID;

public interface BookService {
  List<Book> getAllBooks() throws ServiceException;
  Book findBookById(UUID id) throws ServiceException;
}
