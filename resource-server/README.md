# Okta Resource Server Example

This sample application authenticates requests, using an OAuth 2.0 access tokens and [Okta JWT Verifier for Java][]

The access tokens are obtained via the [Implicit Flow][].  One easy way to obtain an access token is to use the [OpenID Connect Debugger][]. Take a look at this [blog post][].

## Prerequisites

Before running this sample, you will need the following:

* An Okta Developer Account, you can sign up for one at https://developer.okta.com/signup/.
* An access token, obtained with the [OpenID Connect Debugger][] (instructions via this [blog post][])

## Running This Example

```bash
cd resource-server
mvn -Dokta.oauth2.issuer=https://{yourOktaDomain}/oauth2/default
```

## Access the resource

```bash
curl http://localhost:8000/api/messages

> GET /api/messages HTTP/1.1
> Host: localhost:8000
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 401 Unauthorized
< WWW-Authenticate: Bearer realm=Okta
```

Try again with an access token:

```bash

curl http://localhost:8000/api/messages -H"Authorization: Bearer ${accessToken}"

< HTTP/1.1 200 OK
< Date: Wed, 27 Feb 2019 16:55:55 GMT
< Content-Type: application/json

{
  "messages": [
    {
      "date": "2019-02-27T16:55:42.740+0000",
      "text": "I am a robot."
    },
    {
      "date": "2019-02-27T16:55:42.740+0000",
      "text": "Hello, world!"
    }
  ]
}
```

[Implicit Flow]: https://developer.okta.com/authentication-guide/implementing-authentication/implicit
[Okta JWT Verifier for Java]: https://github.com/okta/okta-jwt-verifier-java
[OpenID Connect Debugger]: https://oidcdebugger.com/
[blog post for instructions]: https://developer.okta.com/blog/2018/12/18/secure-spring-rest-api#generate-tokens-in-your-spring-rest-api