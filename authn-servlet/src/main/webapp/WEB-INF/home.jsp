<%--

  ~ Copyright 2019-Present Okta, Inc.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.

  --%>
<jsp:include page="includes/header.jsp"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="user" value="${sessionScope.get('com.okta.authn.sdk.resource.User')}"/>

<body id="samples">
<jsp:include page="includes/menu.jsp"/>

<div id="content" class="container">
    <h2>Okta Authentication SDK + Servlet </h2>

    <c:if test="${empty user}">
        <div>
            <p>Hello!</p>
            <p>If you're viewing this page then you have successfully configured and started this example server.</p>
            <p>This example shows you how to use the <a href="https://github.com/okta/okta-auth-java">Okta
                Authentication SDK</a> configured for a basic Servlet application.</p>

            <div class="alert alert-warning" role="alert">
                <h4 class="alert-heading">Is This Library Right for Me?</h4>
                <p>
                The Authentication SDK is a convenient HTTP client wrapper for  <a href="https://developer.okta.com/docs/api/resources/authn/">Okta's Authentication API</a>. These APIs are powerful and useful if you need to achieve one of these cases:
                </p>
                <ul>
                    <li>You have an existing application that needs to accept primary credentials (username and password) and do custom logic before communicating with Okta.</li>
                    <li>You have significantly custom authentication workflow or UI needs, such that Okta's hosted sign-in page or <a href="https://github.com/okta/okta-signin-widget">Sign-In Widget</a> do not give you enough flexibility.</li>
                </ul>

                <p>
                The power of this SDK comes with more responsibility and maintenance: you will have to design your authentication workflow and UIs by hand, respond to all relevant states in Okta's authentication state machine, and keep up to date with new features and states in Okta.
                </p>
            <p>
            Otherwise, most applications can use the Okta hosted sign-in page or the Sign-in Widget. For these cases, you should use <a href="https://github.com/okta/okta-spring-boot">Okta's Spring Boot Starter</a> or other OIDC/OAuth 2.0 library.
            </p>
            </div>
        </div>

        <form method="get" action="<c:url value="/authn/login" />">
            <button id="login-button" class="btn btn-primary" type="submit">Login</button>
        </form>
    </c:if>

    <c:if test="${not empty user}">
        <div>
            <p>Welcome home, <span>${user.getFirstName()} ${user.getLastName()}</span>!</p>
            <p>You have successfully authenticated against your Okta org, and have been redirected back to this
                application.</p>
            <p>Visit the <a href="<c:url value="/profile" />">My Profile</a> page in this application to view your user's details.</p>
        </div>
    </c:if>
</div>
</body>

<jsp:include page="includes/footer.jsp"/>