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
package com.okta.servlet.examples.resourceserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.okta.jwt.Jwt;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Simple example servlet that displays the current user's details.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/api/userProfile"})
public class ProfileServlet extends HttpServlet {

    private ObjectMapper objectMapper;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Jwt accessToken = (Jwt) request.getAttribute("accessToken");

        // only return the claims if this token has the "profile" scope
        if (AccessUtil.hasScope("profile", accessToken)) {

            response.setStatus(200);
            response.addHeader("Content-Type", "application/json");
            objectMapper.writeValue(response.getOutputStream(), accessToken.getClaims());

        } else {
            response.sendError(403, "Unauthorized");
        }
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        objectMapper = new ObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void destroy() {
        super.destroy();
        objectMapper = null;
    }
}