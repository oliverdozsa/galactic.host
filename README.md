# Galactic Host
This is the server part of [Galactic Pub](https://galactic.pub). Includes the following sub-projects.

## Voting
A privacy first voting platform powered by blockchain technology. Inspired by [stellot](https://github.com/stanbar/stellot).
Work-in-progress.
### What are the differences?
One of the main goals would be to support multiple blockchains (currently only stellar).
### How does it work?
#### Casting a vote
It's based on [blind signatures](https://en.wikipedia.org/wiki/Blind_signature#Blind_RSA_signatures).
1. The voter first authenticates with server. In order to get a vote token anonymously, it creates a concealed request, which contains information
   about the voter's account where the vote token should be delivered.
2. The concealed request will be sent to the server for signing
3. From the signature on the concealed request voter creates the signature for the revealed request.
4. Voter becomes anonymous, and sends the revealed signature, and request to the server.
5. The server checks the revealed signature, so that it knows the anonymous voter is a participant of the voting in question.
6. Server sends back the transaction so that voter can obtain the vote token.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/galactic.host-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
