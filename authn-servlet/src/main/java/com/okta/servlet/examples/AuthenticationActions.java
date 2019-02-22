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
import com.okta.authn.sdk.client.AuthenticationClient;
import com.okta.authn.sdk.resource.AuthenticationResponse;
import com.okta.authn.sdk.resource.VerifyFactorRequest;
import com.okta.authn.sdk.resource.VerifyPassCodeFactorRequest;
import com.okta.authn.sdk.resource.VerifyRecoveryRequest;
import com.okta.sdk.resource.user.factor.FactorType;
import com.okta.servlet.examples.models.Factor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains logic needed to collect and display JSPs in order to advance a user through <a href="https://developer.okta.com/docs/api/resources/authn#transaction-state">Okta's Authentication State Machine</a>.
 */
class AuthenticationActions {

    static final String PREVIOUS_AUTHN_RESULT = AuthenticationResponse.class.getName();

    private final AuthenticationClient authenticationClient;

    AuthenticationActions(AuthenticationClient authenticationClient) {
        this.authenticationClient = authenticationClient;
    }

    /**
     * /authn/login
     */
    void login(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        authenticationClient.authenticate(username, password.toCharArray(), "/", new ExampleAuthenticationStateHandler(request, response));
    }

    /**
     * /authn/logout
     */
    void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
        response.sendRedirect("/");
    }

    /**
     * /authn/change-password
     */
    void changePassword(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");

        authenticationClient.changePassword(oldPassword.toCharArray(),
                                            newPassword.toCharArray(),
                                            getPreviousAuthResult(request).getStateToken(),
                                            new ExampleAuthenticationStateHandler(request, response));
    }

    /**
     * /authn/mfa/verify/totp
     */
    void mfaVerifyTotp(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String passCode = request.getParameter("passCode");

        AuthenticationResponse previousAuthResult = getPreviousAuthResult(request);
        com.okta.authn.sdk.resource.Factor factor = getFactor("totp", previousAuthResult);

        VerifyFactorRequest verifyFactorRequest = authenticationClient.instantiate(VerifyPassCodeFactorRequest.class)
                                                            .setPassCode(passCode)
                                                            .setStateToken(previousAuthResult.getStateToken());

        authenticationClient.verifyFactor(factor.getId(), verifyFactorRequest, new ExampleAuthenticationStateHandler(request, response));
    }

    /**
     * /authn/mfa/verify/sms
     */
    void mfaVerifySms(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String passCode = request.getParameter("passCode");

        AuthenticationResponse previousAuthResult = getPreviousAuthResult(request);
        com.okta.authn.sdk.resource.Factor factor = getFactor("sms", previousAuthResult);

        VerifyFactorRequest verifyFactorRequest = authenticationClient.instantiate(VerifyPassCodeFactorRequest.class)
                                                            .setPassCode(passCode)
                                                            .setStateToken(previousAuthResult.getStateToken());

        authenticationClient.verifyFactor(factor.getId(), verifyFactorRequest, new ExampleAuthenticationStateHandler(request, response));
    }

    /**
     * /authn/mfa/resend/sms
     */
    void mfaResendSms(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        AuthenticationResponse previousAuthResult = getPreviousAuthResult(request);
        com.okta.authn.sdk.resource.Factor factor = getFactor("sms", previousAuthResult);
        authenticationClient.resendVerifyFactor(factor.getId(), previousAuthResult.getStateToken(), new ExampleAuthenticationStateHandler(request, response));
    }

    /**
     * /authn/mfa/verify/totp
     */
    void renderMfaVerifyTotp(HttpServletRequest request, HttpServletResponse response) {

        AuthenticationResponse authenticationResponse = getPreviousAuthResult(request);
        com.okta.authn.sdk.resource.Factor factor = getFactor("totp", authenticationResponse);
        request.setAttribute("factor", factor);
        forward("/WEB-INF/authn/mfa-verify-totp.jsp", request, response);
    }

    /**
     * /authn/mfa/verify/sms
     */
    void renderMfaVerifySms(HttpServletRequest request, HttpServletResponse response) {

        AuthenticationResponse authenticationResponse = getPreviousAuthResult(request);
        com.okta.authn.sdk.resource.Factor factor = getFactor("sms", authenticationResponse);
        request.setAttribute("factor", factor);
        forward("/WEB-INF/authn/mfa-verify-sms.jsp", request, response);
    }

    /**
     * /authn/unlock
     */
    void unlockAccount(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = request.getParameter("username");
        String factorType = request.getParameter("factor");
        authenticationClient.unlockAccount(username, FactorType.valueOf(factorType), "/", new ExampleAuthenticationStateHandler(request, response));
    }

    /**
     * /authn/unlock/recovery
     */
    void unlockRecovery(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String passCode = request.getParameter("passCode");

        AuthenticationResponse previousAuthResult = getPreviousAuthResult(request);
        VerifyRecoveryRequest recoveryRequest = authenticationClient.instantiate(VerifyRecoveryRequest.class)
                .setStateToken(previousAuthResult.getStateToken())
                .setPassCode(passCode);
        authenticationClient.verifyUnlockAccount(FactorType.valueOf(previousAuthResult.getFactorType()), recoveryRequest, new ExampleAuthenticationStateHandler(request, response));
    }

    /**
     * /authn/recovery
     */
    void renderRecovery(HttpServletRequest request, HttpServletResponse response) {

        AuthenticationResponse previousAuthResult = getPreviousAuthResult(request);
        String question = previousAuthResult.getUser().getRecoveryQuestion().get("question");
        request.setAttribute("question", question);
        forward("/WEB-INF/authn/recovery.jsp", request, response);
    }

    /**
     * /authn/recovery
     */
    void recovery(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String answer = request.getParameter("answer");
        AuthenticationResponse previousAuthResult = getPreviousAuthResult(request);
        authenticationClient.answerRecoveryQuestion(answer,
                                                    previousAuthResult.getStateToken(),
                                                    new ExampleAuthenticationStateHandler(request, response));
    }

    /**
     * /authn/mfa-required
     */
    void listMfaVerifyTypes(HttpServletRequest request, HttpServletResponse response) {

        AuthenticationResponse authResponse = getPreviousAuthResult(request);

        List<FactorType> supportedFactors = Arrays.asList(FactorType.CALL, FactorType.EMAIL, FactorType.SMS);

        List<Factor> factors = authResponse.getFactors().stream()
        .map(authFactor -> {
                String shortType = Factor.relativeLink(authFactor);

                String extraInfo = "";
                if (!supportedFactors.contains(authFactor.getType())) {
                    extraInfo = "Factor type '" + authFactor.getType() + "' is not supported by this sample application";
                }

                return new Factor(authFactor.getId(),
                                  shortType,
                                  authFactor.getProvider().name(),
                                  authFactor.getVendorName(),
                                  extraInfo,
                                  authFactor.getProfile(),
                                  "/authn/mfa/verify/" + shortType);
        })
        .collect(Collectors.toList());
        request.setAttribute("factors", factors);

        forward("/WEB-INF/authn/mfa-required.jsp", request, response);
    }

    /**
     * /authn/forgot-password
     */
    void forgotPassword(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = request.getParameter("username");
        String factorType = request.getParameter("factor");
        authenticationClient.recoverPassword(username, FactorType.valueOf(factorType), "/?breaking-the-law", new ExampleAuthenticationStateHandler(request, response));
    }

    /**
     * /authn/reset-password
     */
    void resetPassword(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String newPassword = request.getParameter("newPassword");
        authenticationClient.resetPassword(newPassword.toCharArray(),
                                            getPreviousAuthResult(request).getStateToken(),
                                            new ExampleAuthenticationStateHandler(request, response));
    }

    private com.okta.authn.sdk.resource.Factor getFactor(String type, AuthenticationResponse authenticationResponse) {

        FactorType oktaType = Factor.fromRelativeLink(type);
        return authenticationResponse.getFactors().stream()
                .filter(it -> it.getType().equals(oktaType))
                .findFirst().get();
    }

    private AuthenticationResponse getPreviousAuthResult(HttpServletRequest request) {
        return (AuthenticationResponse) request.getSession(true).getAttribute(PREVIOUS_AUTHN_RESULT);
    }

    static void forward(String path, HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (ServletException | IOException e) {
            throw new IllegalStateException("Unable to forward to path: "+ path, e);
        }
    }
}
