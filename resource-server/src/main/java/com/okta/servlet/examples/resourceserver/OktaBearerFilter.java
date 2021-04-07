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

import com.okta.jwt.AccessTokenVerifier;
import com.okta.jwt.Jwt;
import com.okta.jwt.JwtVerificationException;
import com.okta.jwt.JwtVerifiers;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.StringTokenizer;

@WebFilter(urlPatterns = "/*")
public class OktaBearerFilter implements Filter {

    AccessTokenVerifier verifier = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        verifier = JwtVerifiers.accessTokenVerifierBuilder()
                .setIssuer(getIssuer(filterConfig)) // https://{yourOktaDomain}/oauth2/default
                .build();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 1. grab the Authorization header
        String authHeader = request.getHeader("Authorization");
        if( authHeader != null) {

            // 2. it must be in the format of <type> <value>
            StringTokenizer tokenizer = new StringTokenizer(authHeader);
            if (tokenizer.countTokens() == 2) {
                String type = tokenizer.nextToken();
                String accessToken = tokenizer.nextToken();

                // 3. make sure this is a "Bearer" token
                if ("Bearer".equals(type)) {
                    try {

                        // 4. validate access token
                        // only valid JWT access tokens will be returned
                        Jwt jwt = validateAccessToken(accessToken);

                        // set a request attribute so we can get this data from a servlet
                        request.setAttribute("accessToken", jwt);

                        // continue
                        chain.doFilter(request, response);
                        return;
                    } catch (JwtVerificationException e) {
                        request.getServletContext().log("Invalid access token", e);
                    }
                } else {
                    request.getServletContext().log("Unsupported Authorization type");
                }

            } else {
                request.getServletContext().log("Invalid Authorization header format");
            }

        }
        // return 401
        authorizationFailure(response);
    }

    @Override
    public void destroy() {
        verifier = null;
    }

    private Jwt validateAccessToken(String token) throws JwtVerificationException {
        return verifier.decode(token);
    }

    private void authorizationFailure(HttpServletResponse response) throws IOException {
        response.setHeader("WWW-Authenticate", "Bearer realm=Okta");
        response.sendError(401, "Unauthorized");
    }

    private String getIssuer(FilterConfig filterConfig) {

        // 1. try filter config
        String issuer = filterConfig.getInitParameter("okta.oauth2.issuer");

        // 2. try System Property
        if (issuer == null) {
            issuer = System.getProperty("okta.oauth2.issuer");
        }

        // 2. try Env var
        if (issuer == null) {
            issuer = System.getenv("OKTA_OAUTH2_ISSUER");
        }

        // returning null will result in a useful validation message from the verifier
        return issuer;
    }
}
