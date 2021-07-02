FROM maven:3.6.3-openjdk-8-slim

ADD . ./acme-ui-gcustomer-contract-test

WORKDIR /acme-ui-gcustomer-contract-test

RUN mvn clean install

RUN mvn pact:publish
