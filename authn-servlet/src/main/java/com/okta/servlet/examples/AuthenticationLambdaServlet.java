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

import com.okta.authn.sdk.AuthenticationException;
import com.okta.sdk.resource.ResourceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.okta.servlet.examples.AuthenticationActions.forward;

/**
 * Boiler plate code reduction to help make this sample project easier to follow. Authentication related servlets are configured in
 * {@code OktaFilter.init} by calling:
 * <pre>
 *     registerAction(filterConfig, "/authn/login","/WEB-INF/authn/login.jsp", actions::login);
 * </pre>
 */
class AuthenticationLambdaServlet extends HttpServlet {
    private final AuthenticationServletHandler renderConsumer;
    private final AuthenticationServletHandler postConsumer;

    AuthenticationLambdaServlet(String path,
                                        AuthenticationServletHandler postConsumer) {
        this((request, response) -> forward(path, request, response), postConsumer);
    }

    AuthenticationLambdaServlet(AuthenticationServletHandler renderConsumer,
                                        AuthenticationServletHandler postConsumer) {
        this.renderConsumer = renderConsumer;
        this.postConsumer = postConsumer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (renderConsumer == null) {
            super.doGet(req, resp);
            return;
        }

        try {
            renderConsumer.service(req, resp);
        } catch (AuthenticationException | ResourceException e) {
            req.setAttribute("error", e);
            forward("/WEB-INF/authn/login.jsp", req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (postConsumer == null) {
            super.doGet(req, resp);
            return;
        }

        try {
            postConsumer.service(req, resp);
        } catch (AuthenticationException | ResourceException e) {

            // on error, set the error attribute then render the page again
            req.setAttribute("error", e);
            doGet(req, resp);
        }
    }

    @FunctionalInterface
    interface AuthenticationServletHandler {
        void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, AuthenticationException;
    }
}
