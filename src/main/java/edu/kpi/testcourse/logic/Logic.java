package edu.kpi.testcourse.logic;

import edu.kpi.testcourse.entities.UrlAlias;
import edu.kpi.testcourse.entities.User;
import edu.kpi.testcourse.storage.UrlRepository;
import edu.kpi.testcourse.storage.UrlRepository.AliasAlreadyExist;
import edu.kpi.testcourse.storage.UserRepository;
import java.util.Random;

/**
 * Business logic of the URL shortener application.
 */
public class Logic {
  private final UserRepository users;
  private final UrlRepository urls;
  private final HashUtils hashUtils;

  /**
   * Creates an instance.
   */
  public Logic(UserRepository users, UrlRepository urls) {
    this.users = users;
    this.urls = urls;
    this.hashUtils = new HashUtils();
  }

  /**
   * Create a new user.
   *
   * @param email users email
   * @param password users password
   * @throws UserIsAlreadyCreated is thrown if user is already created
   */
  public void createNewUser(String email, String password) throws UserIsAlreadyCreated {
    if (users.findUser(email) != null) {
      throw new UserIsAlreadyCreated();
    } else {
      users.createUser(new User(email, hashUtils.generateHash(password)));
    }
  }

  /**
   * Gives an answer if user is registered and password is correct.
   *
   * @param email a users email
   * @param password a users password
   * @return if user is registered and password is correct
   */
  public boolean isUserValid(String email, String password) {
    User user = users.findUser(email);
    if (user == null) {
      return false;
    }

    return hashUtils.validatePassword(password, user.passwordHash());
  }

  /**
   * Return class object UrlsRepository.
   */
  public UrlRepository getUrlRepository() {
    return urls;
  }

  /**
   * Generate new alias until it's alias not exits in database.
   *
   * @param pathToShort is a path to our shorted url command
   */
  public String generateAlias(String pathToShort) {
    String alias;
    char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    alias = pathToShort;
    Random random = new Random();
    StringBuilder aliasBuilder = new StringBuilder(alias);
    for (int i = 0; i < 5; i++) {
      aliasBuilder.append(chars[random.nextInt(chars.length)]);
    }
    alias = aliasBuilder.toString();
    return alias;
  }

  /**
   * Create a new URL alias (shortened version).
   *
   * @param email an email of a user that creates the alias
   * @param url a full URL
   * @param alias a proposed alias
   *
   * @return a shortened URL
   */
  public String createNewAlias(String email, String url, String alias, String pathToShort)
                              throws AliasAlreadyExist {
    String finalAlias;
    if (urls.aliasIsExist(alias)) {
      throw new UrlRepository.AliasAlreadyExist();
    }
    if (alias == null || alias.isEmpty()) {
      finalAlias = generateAlias(pathToShort);
    } else {
      finalAlias = alias;
    }

    urls.createUrlAlias(new UrlAlias(finalAlias, url, email));

    return finalAlias;
  }

  /**
   * Get full URL by alias.
   *
   * @param alias a short URL alias
   * @return a full URL
   */
  public String findFullUrl(String alias) {
    UrlAlias urlAlias = urls.findUrlAlias(alias);

    if (urlAlias != null) {
      return urlAlias.destinationUrl();
    }

    return null;
  }

  /**
   * Error for situation when we are trying to register already registered user.
   */
  public static class UserIsAlreadyCreated extends Throwable {
    public UserIsAlreadyCreated() {
      super("User with such email is already created");
    }
  }

}
