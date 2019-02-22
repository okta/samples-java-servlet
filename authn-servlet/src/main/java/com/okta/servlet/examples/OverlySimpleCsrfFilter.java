/*
 * Copyright 2018 Okta, Inc.
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

import com.okta.commons.lang.Strings;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Simplistic CSRF filter
 */
public class OverlySimpleCsrfFilter implements Filter {

    private static final String CSRF_KEY = "_csrf";
    private static final String CSRF_HEADER = "X-CSRF-TOKEN";

    private final Set<String> allowedMethods = new HashSet<>(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String expectedCsrf = (String) request.getSession().getAttribute(CSRF_KEY);

        // figure out the next CSRF token
        String nextCSRF = UUID.randomUUID().toString();
        request.setAttribute(CSRF_KEY, nextCSRF);

        // add next csrf token to the session
        request.getSession().setAttribute(CSRF_KEY, nextCSRF);

        if (shouldFilter(request)) {

            String actualCsrf = request.getHeader(CSRF_HEADER);
            if (actualCsrf == null) {
                actualCsrf = request.getParameter(CSRF_KEY);
            }

            if (Strings.isEmpty(expectedCsrf) || !expectedCsrf.equals(actualCsrf)) {

                String errorMessage = "CSRF token did not match";
                request.getServletContext().log(errorMessage);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

    private boolean shouldFilter(HttpServletRequest request) {
        return !allowedMethods.contains(request.getMethod());
    }
}