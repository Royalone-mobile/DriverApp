package com.sawatruck.driver.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sawatruck.driver.entities.User;

public class UserManager {
  private final Context _context;

  private static UserManager instance;

  private UserManager(Context context) {
    this._context = context;
  }

  public static UserManager with(@NonNull Context context) {
    if (instance == null) {
      instance = new UserManager(context);
    }
      return instance;
  }
  /////  0 - guest 1 - loader


  public void setUserType(int userType) {
    AppSettings.with(_context).setUserType(userType);
  }

  public int getUserType() {
    return AppSettings.with(_context).getUserType();
  }


  public User getCurrentUser() {
    String jsonString = AppSettings.with(_context).getUser();
    User user = new User();
    if(!jsonString.equals(""))
      user = Serializer.getInstance().deserializeUser(jsonString);
//    Logger.error(user.getToken());
    return user;
  }

  public void setCurrentUser(User user){
    String prefUser = Serializer.getInstance().serializeUser(user);
    AppSettings.with(_context).setUser(prefUser);
  }
}
