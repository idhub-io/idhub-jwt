# ID Hub JWT

## What is ID Hub JWT

Micro-service to manipulate JSON Web Token in your system.
Instead of using a JWT library, delegate to a service. This offer multiple advantages:

- No need to maintain the JWT library up to date in all your micro-services
- Centralised management of keys, particularly handy for building auto-scaling system
- Optimisation of your hardware: Manipulating JWT uses cryptography which is CPU demanding. You can therefore allocate this microservice in nodes with stronger CPU specification and your other microservice in more memory scaled nodes for example
- No need to implement a proper key rotation system in every of your microservices


We are hosting the latest version of ID Hub JWT as a service that you can consume for free using Postman.
https://www.postman.com/id-hub/workspace/idhub/overview

This idea is to simplify your development journey by using this JWT service for testing integration with third party API. For example, you can use ID Hub JWT to test OpenBanking API. Once you passed the integration exploration phases, you probably want to evaluate using ID Hub JWT docker image directly or bringing in house the functionality.
Eitherway, this repo will guide you in the good practice around JWT.

## Supported features

- Sign JWT using an asymmetric key
- Read any JWT
- Verify JWT signed with asymmetric key
- Manage asymmetric keys: RSA and EC
- Key rotation
- Reset your keys (in case they have been compromised)
- Expose your keys as public JWKS_URI
- Generate randmon key
- Import a pre-existing key and get it manage by the service

## Testing ID Hub JWT before hands

You can either use the docker image directly or our free online version of it. The reason that you may want to use the online version is it exposes the public keys externaly via an public URL. This is handy to simplify integration that requires to expose your keys as a JWK_URI.

### Use our online service

We protected our service using OAuth2, something you will be able to do too if you wish. The way we did it is by adding a gateway in front of ID Hub JWT that verifies access tokens and their scope. In order to get an access token, you will need to create an application first:

#### Create an application

You can create an application to https://console.idhub.io:
- a non public client
- no need of redirect uri
- the scope 'jwt' and 'keys'

As a result, you will get an Client ID and Client Secret. 

#### Setup the postman collection

Change the environmnent variables 'client_id' and 'client_secret' from the 'idhub.io' postman environment with the one you just received.

### Use the docker image

#### Start the service

```
docker-compose up
```

#### Use the service

You can use the same postman than for our online service, except that you need to change the authorization type:

The microservice is design to delegate the API protection to a gateway and instead trust a header called `x-idhub-principal-id`.
You can pass in this header the identifier of the caller.
In the case you want to share the same set of keys for all your microservices that will consume ID Hub JWT, you can then hardcode this header to `x-idhub-principal-id=idhub`. All the services that will call ID Hub JWT will sign JWT with the same keys for example.


In our online service, what we did is making our OAuth2 gateway extract the subject from the access token and pass this value to this header.
This way we could offer you to use the service without having to share keys with everyone else.


#### Keys storage

At the moment the only database storage supported is Postgres. Although we use Spring data, this should easily be configurable to another source of storage if you wishes to.
