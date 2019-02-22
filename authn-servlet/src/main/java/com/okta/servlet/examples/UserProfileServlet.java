/*
 * Copyright 2019-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.servlet.examples;

import com.okta.authn.sdk.resource.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Simple example servlet that displays the current user's details.
 */
@WebServlet(name = "UserProfile", urlPatterns = {"/profile"})
public class UserProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the currently logged in user
        User user = (User) request.getSession(true).getAttribute(OktaFilter.USER_SESSION_KEY);

        // add the user to the request context and render the JSP
        request.setAttribute("user", user);
        // This has also been added the example JSPs directly using:
        // <c:set var="user" value="${sessionScope.get('com.okta.authn.sdk.resource.User')}"/>

        request.getRequestDispatcher("/WEB-INF/user-profile.jsp").forward(request, response);
    }
}