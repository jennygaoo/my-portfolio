// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.UserLoginInfo;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@WebServlet("/authentication")
public class AuthenticationServlet extends HttpServlet {
  private static String REDIRECT_URL = "/index.html";
  private static Gson gson = new Gson();
  private static Logger log = Logger.getLogger("AuthenticationServlet");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Boolean userLoginStatus;
    String redirectUrl;
    UserService userService = UserServiceFactory.getUserService();
    
    if (userService.isUserLoggedIn()) {
      userLoginStatus = true;
      
      String logoutUrl = userService.createLogoutURL(REDIRECT_URL);
      redirectUrl = logoutUrl;
    } else {
      userLoginStatus = false;

      String loginUrl = userService.createLoginURL(REDIRECT_URL);
      redirectUrl = loginUrl;
    } 

    UserLoginInfo userLoginInfo = new UserLoginInfo(userLoginStatus, redirectUrl);
    String json = gson.toJson(userLoginInfo);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }
}
