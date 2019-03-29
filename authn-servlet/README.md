# Okta Authentication SDK & Java Servlets

This example shows you how to use the [Okta Java Authentication SDK][] to login a user to a Servlet based application. This is a sample application meant to show how to integrate [Okta Java Authentication SDK][] into existing frameworks or applications and is NOT a production ready application (See [OWASP Top Ten Cheat Sheet](https://www.owasp.org/index.php/OWASP_Top_Ten_Cheat_Sheet) for more tips).

**NOTE:** Using OAuth 2.0 or OpenID Connect to integrate your application instead of this library will require much less work, and has a smaller risk profile. Please see [this guide](https://developer.okta.com/use_cases/authentication/) to see if using this API is right for your use case. You could also use our [Spring Boot Integration](https://github.com/okta/okta-spring-boot), or [Spring Security](https://developer.okta.com/blog/2017/12/18/spring-security-5-oidc) out of the box.

Okta's Authentication API is built around a [state machine](https://developer.okta.com/docs/api/resources/authn#transaction-state). In order to use this library you will need to be familiar with the available states. You will need to implement a handler for each state you want to support.  

![State Model Diagram](https://raw.githubusercontent.com/okta/okta.github.io/source/_source/_assets/img/auth-state-model.png "State Model Diagram")

# Supported Use Cases:

All pages are server side rendered with JSPs.

- Basic username & password login
- Display current user details
- Logout (`session.invalidate()`)
- Forgot Password
- User Lockout notification
- Self service password reset via `SMS` or `CALL`
- MFA support for `TOTP` and `SMS`

## Prerequisites

Before running this sample, you will need the following:

* An Okta Developer Account, you can sign up for one at https://developer.okta.com/signup/.
* The source code from this repository:

    ```
    git clone https://github.com/okta/samples-java-servlet.git
    cd samples-java-servlet
    ```

## Running This Example

There is a `pom.xml` at the root of this project, that exists to build all of the projects.  Each project is independent and could be copied out of this repo as a primer for your own application.

You also need to grab the your Okta Domain, for example, `https://dev-123456.okta.com`. 

Plug these values into the `mvn` commands used to start the application.

```bash
cd authn-servlet
mvn -Dokta.client.orgUrl=https://{yourOktaDomain}
```

Now navigate to http://localhost:8080 in your browser.

If you see a home page that prompts you to login, then things are working! 

You can login with the same account that you created when signing up for your Developer Org, or you can use a known username and password from your Okta Directory.

[Okta Java Authentication SDK]: https://github.com/okta/okta-auth-java
