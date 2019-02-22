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
<jsp:include page="includes/header.jsp" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<body id="samples">
    <jsp:include page="includes/menu.jsp" />

    <div id="content" class="container">

        <div>
            <h2>My Profile</h2>
        </div>

        <table class="table table-striped">
            <thead>
            <tr>
                <th>Key</th>
                <th>Value</th>
            </tr>
            </thead>
            <tbody>
                <c:forEach items="${user.profile}" var="item">
                    <tr>
                        <td>${item.key}</td>
                        <td>${item.value}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>

<jsp:include page="includes/footer.jsp" />