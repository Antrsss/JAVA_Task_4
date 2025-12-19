package by.zgirskaya.course.service;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.AbstractUserModel;

import java.util.Optional;
import java.util.UUID;

public interface AuthService {
  Optional<AbstractUserModel> authenticateUser(String identifier, String password) throws ServiceException;
  AbstractUserModel registerUser(String name, String identifier, String role, String password,
                                 String username, String passportId) throws ServiceException;
  String findRoleById(UUID roleId) throws ServiceException;
}