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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple example hello world servlet.
 */
@WebServlet(name = "MessagesServlet", urlPatterns = {"/api/messages"})
public class MessagesServlet extends HttpServlet {

    private ObjectMapper objectMapper;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // only return the claims if this token has the "email" scope
        if (AccessUtil.hasScope("email", (Jwt) request.getAttribute("accessToken"))) {
            respondWithMessages(response);
        } else {
            response.sendError(403, "Unauthorized");
        }
    }

    private void respondWithMessages(HttpServletResponse response) throws IOException {
         Map<String, Object> messages = new HashMap<>();
            messages.put("messages", Arrays.asList(
                    new Message("I am a robot."),
                    new Message("Hello, world!")
            ));

            response.setStatus(200);
            response.addHeader("Content-Type", "application/json");
            objectMapper.writeValue(response.getOutputStream(), messages);
    }

    class Message {
        public Date date = new Date();
        public String text;

        Message(String text) {
            this.text = text;
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