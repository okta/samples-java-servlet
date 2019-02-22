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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var = "user" value = "${sessionScope.get('com.okta.authn.sdk.resource.User')}"/>

<nav class="navbar navbar-default">
    <div class="container-fluid">
        <ul class="nav navbar-nav">
            <li><a href="<c:url value="/" />">Home</a></li>
            <c:if test="${not empty user}">
                <li><a id="profile-button" href="<c:url value="/profile" />">My Profile</a></li>
            </c:if>
        </ul>

        <c:if test="${not empty user}">
            <form method="post" action="<c:url value="/authn/logout"/>" class="navbar-form navbar-right">
                <jsp:include page="/WEB-INF/includes/csrf.jsp"/>
                <button id="logout-button" type="submit" class="btn btn-danger">Logout</button>
            </form>
        </c:if>
    </div>
</nav>