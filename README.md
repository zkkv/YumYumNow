# YumYumNow Delivery Microservice - Team 11B

Welcome to the repository for the Delivery Microservice for Team 11B's YumYumNow project.
Below you will find useful information about how to run the project, as well as a link to the report for the project.

## Table of Contents

* [Wiki](https://gitlab.ewi.tudelft.nl/cse2115/2023-2024/group-11/team-11b/-/wikis/home)
* [Report](https://gitlab.ewi.tudelft.nl/cse2115/2023-2024/group-11/team-11b/-/wikis/final-report)
* [API Documentation](delivery-openapi-spec.yaml)
* [Contributors](#contributors)
* [Mocked Services](#mocked-services)
* [Running the project](#running-the-project)
* [Running the tests](#running-the-tests)
* [Run instructions for the TA](#run-instructions-for-the-ta)

## Contributors

| Name               | GitLab Username                                           |
|--------------------|-----------------------------------------------------------|
| Alexandra Nicola   | [@amnicola](https://gitlab.ewi.tudelft.nl/amnicola)       |
| Eve Smura          | [@esmura](https://gitlab.ewi.tudelft.nl/esmura)           |
| Horia Radu         | [@horiaradu](https://gitlab.ewi.tudelft.nl/horiaradu)     |
| Kirill Zhankov     | [@kzhankov](https://gitlab.ewi.tudelft.nl/kzhankov)       |
| Rafayel Gardishyan | [@rgardishyan](https://gitlab.ewi.tudelft.nl/rgardishyan) |
| Zhuoyue Ge         | [@zhuoyuege](https://gitlab.ewi.tudelft.nl/zhuoyuege)     |

## Mocked Services

The code on the `main` branch has the other two microservices mocked with WireMock Cloud. If you want to run the project
with the actual microservices, please use the `integration` branch.

## Running the project
To run the project, you need to have Java 15 installed on your machine. You can check your Java version by running the
following command in your terminal:

```shell
java -version
```

If you don't have Java 15 installed, you can download it from [here](https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html).

After you have Java 15 installed, you can run the project by running the following command in the root directory of the
project:

```shell
./gradlew bootRun
```

This will start the server on port 8082. You can then send requests to the server on `localhost:8082`.
You can find the API documentation [here](delivery-openapi-spec.yaml).

Alternatively, if you are using IntelliJ, you can run the project by opening the project in IntelliJ and adding a new
configuration. You can do this by clicking on the dropdown next to the green play button in the top right corner of the
IDE and selecting "Edit Configurations...". Then, click on the plus sign in the top left corner of the window and select
"Application". In the "Main class" field, enter `nl.tudelft.sem.yumyumnow.delivery.Application`.

## Running the tests

To run the tests, you can run the following command in the root directory of the project:

```shell
./gradlew test
```

Alternatively, if you want to run reports on the code (like coverage, mutation testing, etc.), you can run the following
commands:

```shell
./gradlew jacocoTestReport
./gradlew pitest
```

## Run instructions for the TA

Dear TA,

In the repository, you can find
a [postman file](https://gitlab.ewi.tudelft.nl/cse2115/2023-2024/group-11/team-11b/-/blob/main/YumYumNow%20Delivery.postman_collection.json),
which contains all the endpoints implemented, already set up and ready to be tested. You can import this file in
postman. There are however a couple of things to keep in mind when running the requests:

* The version on the main branch is integrated with _Wiremock Cloud_. This means that all the requests made to other
  microservices are sent to our mocked server which will return responses based on the API spec of the other groups. If
  you want to check integration with the real servers, you need to change
  the [`application.properties`](https://gitlab.ewi.tudelft.nl/cse2115/2023-2024/group-11/team-11b/-/blob/main/yumyumnow-delivery-microservice/src/main/resources/application.properties)
  file to work with localhost. This is simply done by (un)commenting the correct lines.
* Some endpoints need to be run before the others, otherwise they will return an error response. For example, the
  endpoint to change delivery time depends on a preparation time to be set. The endpoint setting the latter one should be
  run first to ensure correct order of events. The requests are numbered and put in order for your convenience.
* As the database doesn't autocorrect after a server is stopped (read: it's non-persistent), the application will
  generate a sample delivery with a fixed ID every time you run the server and save it to the database. This ID is also
  set as a variable in postman, such that you don't need to change it for every request. If you wish to test the
  endpoints with a different delivery (which you can generate by running the "_Create a delivery"_ endpoint), you can
  change the variable in the upper level file in postman, under the variable tab (you'll see it in the tree structure
  when opened in postman).
* The mocked server returns users and orders with a specific, pre-set ID. If you change those values in request bodies,
  you will probably encounter errors, as the application is very resilient and checks for validity of objects for many
  requests. When integrating with the real services, you should keep in mind that their ID's are probably different, so
  you'll need to change the values.
* **Last, but not least: if you just want to check if the endpoints work, you can just run all the requests in the
  postman setup, without changing anything**

We hope to have informed you enough about how to run and check our application. If you have any questions, feel free to
reach out to us, and we'll try to help.


