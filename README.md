# Instructions for candidates

This is the Java version of the Payment Gateway challenge. If you haven't already read this [README.md](https://github.com/cko-recruitment/) on the details of this exercise, please do so now.

## Requirements
- JDK 17
- Docker

## Template structure

src/ - A skeleton SpringBoot Application

test/ - Some simple JUnit tests

imposters/ - contains the bank simulator configuration. Don't change this

.editorconfig - don't change this. It ensures a consistent set of rules for submissions when reformatting code

docker-compose.yml - configures the bank simulator


## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**

**Feel free to change the structure of the solution, use a different library etc.**

# How to run the application
After starting up the Bank client simulator
```bash
  docker-compose up
```
We either start Java application from IDE by running the main class `PaymentGatewayApplication` or by running it from the
command line. First we need to compile and build the project using
```bash
  ./gradlew build
```
then we run the application with the following command
```bash
  java -jar build/libs/payment-gateway-challenge-java-0.0.1-SNAPSHOT.jar
```

# Approach and key decisions

## Payment state

My main approach centered around handling scenarios where the Bank client can return a `503 Service Unavailable` and 
how to handle this scenario. So to handle the scenario, where we can have a valid request to our service, which satisfies all the criteria
towards a Payment, but still fail when trying to send the Payment to the bank, I introduced a new field to the Payment 
entity which would capture the `state` of a Payment.

With this approach, if we fail to receive a valid response from the Bank client, we can record this event in our "database"
ensuring we do not lose any audit information about payment transactions that entered our gateway, but could not be fulfilled 
by the Bank API.

This `state` field can have three values: `PENDING`, `COMPLETED` and `FAILED`. When we first receive a valid Payment request
we create the payment in `PENDING` state and store in the DB to make sure we do not lose it, and could even potentially 
recover from a failure of our service. If the payment was sent to the Bank client, and we successfully received a valid response,
we update the Payment with the response from the Bank and move the `state` into `COMPLETED`. If the call fails to the Bank
and the client returns a `5xx` error, we update the Payment's state to `FAILED`, save it into the DB and return an error 
response to the caller.

I decided to return a `502 Bad Gateway` error status, as our service is acting as a gateway between the caller and the 
Bank, and the definition of a `502` fits our situation the best: `"The server was acting as a gateway or proxy and 
received an invalid response from the upstream server."`

I also removed the `REJECTED` enum from the `PaymentStatus` Enum, as it is holding the possible statuses of a Payment and
is linked to it. The task specified that a the `status` field in the Payment response can only have two states: `"Must 
be one of the following values Authorized, Declined`"

Ideally I would separate the logic even more, between validating a Payment request, storing it and logic that actually 
calls the Bank. So the gateway would store the payment, send back a response about success and then send an event (to 
itself or another service) to actually call the Bank and handle any failure or even implement a circuit-breaker for the 
client call.

## Validation
I wanted to keep the validation logic simple, first I wanted to have a separate validation class/method which would have
had a request as a parameter and would have gone through the fields of the request and validated each field against our
criteria, but I went with the annotation approach and use of `spring-boot-starter-validation` to not introduce more code
and follow the advice in the task's description about keeping things simple, maintainable and not over-engineer.

But this also put me on the fence, regarding what error code to use when the validation fails. Because if the request is
not valid and any of the validation fails, then it can be `400 Bad Request`, because the request _is_ bad, and did not 
even reach any business logic of ours. Then again, the description specifically mentions that if invalid information is 
supplied, we should return a `Rejected` response, because no payment could be created and the Bank client was not even
called. I was going back and forth with the thought, that to know if the information is valid or not, it has to be validated
against something. For example, the format of the card number may be correct, but a card with that number might not exist.
Or that the CVV is correct, it is in the request, it is between 3-4 characters and only contains numeric characters, but
the CVV provided is for another card, not the one in the request. But I'm guessing this could be only validated by the bank.

In the end, I decided to go with a `422 Unprocessable Content`. While the definition of a `422` states: `"The request 
was well-formed (i.e., syntactically correct) but could not be processed."`, which might not be a perfect fit the situation 
perfectly, it can be easily changed if needed.

## Models and architecture
I introduced more models to the project and mappers for them, to make sure that certain models/objects stay in their 
corresponding layer e.g.: a request/response object should only be passed between the Controller and Service layer and 
should not go any deeper than that. Also, DB entities should not leave the Repository layer, so that only DTOs are being 
passed around in the business logic. I was thinking of separating the logic even more, and having separate classes for
creating and fetching Payments and then the `PaymentGatewayService` would only map from request to a DTO, and call the 
appropriate class to fulfill the request, and then map back from DTO to response. But given the scope of the task, it felt
like overkill, and even now our service class is quite small and easy to read and maintain.

## Testing
I separated the testing of the creation and fetching of Payment into their own test classes, and made them extend a base
integration test class, where the shared objects and methods can be stored, and this way the Spring context gets initialized
only once when running all the tests. I also tried to cover all the possible scenarios of the endpoints.

I also introduced a GET all Payments endpoint for testing/debugging purposes.