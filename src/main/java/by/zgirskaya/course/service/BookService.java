package by.zgirskaya.course.service;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.Book;

import java.util.List;
import java.util.UUID;

public interface BookService {
  List<Book> findAllBooks() throws ServiceException;
  Book findBookById(UUID id) throws ServiceException;
}
