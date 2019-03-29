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
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty error}">
<div class="alert alert-danger" role="alert">
  <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
  <span>${error.message}</span>
  <p>Error Code: ${error.code}</p>
  <p>Status Code: ${error.status}</p>
  <p>Error ID: ${error.id}</p>
    <c:if test="${not empty error.causes}">
      Causes:<p>
      <ul>
      <c:forEach items="${error.causes}" var="item">
          <li>${item.summary}</li>
      </c:forEach>
      </ul>
      </p>
    </c:if>
</div>
</c:if>

<c:if test="${not empty param['error']}">
  <div class="alert alert-danger" role="alert">
    <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
    <span>${param['error']}</span>
  </div>
</c:if>
