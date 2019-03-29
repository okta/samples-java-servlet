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

import com.okta.authn.sdk.AuthenticationStateHandlerAdapter;
import com.okta.authn.sdk.resource.AuthenticationResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * An {@link com.okta.authn.sdk.AuthenticationStateHandler AuthenticationStateHandler} that redirects to a JSP corresponding a given state.
 * The {@link #handleSuccess} is a special case, on "success" when there is a {@code sessionToken} the user is considered authenticated.
 * <p></p>
 * <strong>NOTE:</strong> the "success" state is also used in other flows, you MUST check for the presents of a {@code sessionToken}.
 */
class ExampleAuthenticationStateHandler extends AuthenticationStateHandlerAdapter {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    ExampleAuthenticationStateHandler(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void handleSuccess(AuthenticationResponse successResponse) {
        // the last request was a success, but if we do not have a session token
        // we need to force the flow to start over
        if (successResponse.getSessionToken() != null) {
            // if we have a Session Token add the corresponding user to the Session
            request.getSession(true).setAttribute(OktaFilter.USER_SESSION_KEY, successResponse.getUser());
        }

        String relayState = successResponse.getRelayState();
        String dest = relayState != null ? relayState : "/";
        redirect(dest, successResponse);
    }

    @Override
    public void handlePasswordExpired(AuthenticationResponse passwordExpired) {
        redirect("/authn/change-password", passwordExpired);
    }

    @Override
    public void handleMfaRequired(AuthenticationResponse mfaRequiredResponse) {
        redirect("/authn/mfa-required", mfaRequiredResponse);
    }

    @Override
    public void handleMfaChallenge(AuthenticationResponse mfaChallengeResponse) {
        String factorType = mfaChallengeResponse.getFactors().get(0).getType().name().toLowerCase(Locale.ENGLISH);
        redirect("/authn/mfa/verify/"+ factorType, mfaChallengeResponse);
    }

    @Override
    public void handleLockedOut(AuthenticationResponse lockedOutResponse) {
        redirect("/authn/unlock", lockedOutResponse);
    }

    @Override
    public void handleRecoveryChallenge(AuthenticationResponse recoveryChallenge) {
        redirect("/authn/unlock/recovery", recoveryChallenge);
    }

    @Override
    public void handleRecovery(AuthenticationResponse recoveryResponse) {
        redirect("/authn/recovery", recoveryResponse);
    }

    @Override
    public void handlePasswordReset(AuthenticationResponse passwordReset) {
        redirect("/authn/reset-password", passwordReset);
    }

    @Override
    public void handleUnknown(AuthenticationResponse unknownResponse) {
        redirect("/authn/login?error=Unsupported State: "+ unknownResponse.getStatus().name(), unknownResponse);
    }

    private void redirect(String location, AuthenticationResponse authenticationResponse) {
        try {
            setAuthNResult(authenticationResponse);
            response.sendRedirect(location);
        } catch (IOException e) {
            throw new IllegalStateException("failed to redirect.", e);
        }
    }

    private void setAuthNResult(AuthenticationResponse authenticationResponse) {
        request.getSession(true).setAttribute(AuthenticationActions.PREVIOUS_AUTHN_RESULT, authenticationResponse);
    }
}