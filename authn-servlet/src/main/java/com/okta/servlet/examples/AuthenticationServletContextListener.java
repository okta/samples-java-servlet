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

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.okta.authn.sdk.client.AuthenticationClient;
import com.okta.authn.sdk.client.AuthenticationClients;
import com.okta.servlet.examples.AuthenticationLambdaServlet.AuthenticationServletHandler;

import java.util.EnumSet;

@WebListener
public class AuthenticationServletContextListener implements ServletContextListener {

    private AuthenticationActions actions;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // configuration can be pulled from various sources, see https://github.com/okta/okta-auth-java#configuration-reference
        AuthenticationClient authenticationClient = AuthenticationClients.builder().build();

        actions = new AuthenticationActions(authenticationClient);

        ServletContext servletContext = sce.getServletContext();
        registerFilter(servletContext, "/*", new OverlySimpleCsrfFilter());
        registerFilter(servletContext, "/*", new OktaFilter());

        registerAction(servletContext, "/authn/login","/WEB-INF/authn/login.jsp", actions::login);
        registerAction(servletContext, "/authn/logout", (String) null, actions::logout);
        registerAction(servletContext, "/authn/change-password","/WEB-INF/authn/change-password.jsp", actions::changePassword);
        registerAction(servletContext, "/authn/mfa-required", actions::listMfaVerifyTypes, null);
        registerAction(servletContext, "/authn/mfa/verify/totp", actions::renderMfaVerifyTotp, actions::mfaVerifyTotp);
        registerAction(servletContext, "/authn/mfa/verify/sms", actions::renderMfaVerifySms, actions::mfaVerifySms);
        registerAction(servletContext, "/authn/mfa/resend/sms", actions::renderMfaVerifySms, actions::mfaResendSms);
        registerAction(servletContext, "/authn/unlock", "/WEB-INF/authn/unlock-account.jsp", actions::unlockAccount);
        registerAction(servletContext, "/authn/unlock/recovery", "/WEB-INF/authn/unlock-recovery.jsp", actions::unlockRecovery);
        registerAction(servletContext, "/authn/recovery", actions::renderRecovery, actions::recovery);
        registerAction(servletContext, "/authn/forgot-password", "/WEB-INF/authn/forgot-password.jsp", actions::forgotPassword);
        registerAction(servletContext, "/authn/reset-password", "/WEB-INF/authn/reset-password.jsp", actions::resetPassword);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        actions = null;
    }

    private void registerAction(ServletContext servletContext, String path, String view, AuthenticationServletHandler postHandler) {
        servletContext.addServlet(path, new AuthenticationLambdaServlet(view, postHandler))
                      .addMapping(path);
    }

    private void registerAction(ServletContext servletContext, String path, AuthenticationServletHandler renderHandler, AuthenticationServletHandler postHandler) {
        servletContext.addServlet(path, new AuthenticationLambdaServlet(renderHandler, postHandler))
                      .addMapping(path);
    }

    private void registerFilter(ServletContext servletContext, String path, Filter filter) {
        servletContext.addFilter(filter.getClass().getName(), filter)
                      .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, path);
    }
}
