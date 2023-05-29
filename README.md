# hka-infm-letmecook

![Repository Banner](https://i.imgflip.com/74bvex.png)

Repository for the seminar paper on "Monitoring of Applications with Micrometer" by Dennis Zeller. Supervised by Professor Vogelsang at Karlsruhe University of Applied Sciences.
The goal of this repository is to provide a sample application for [Micrometer](https://micrometer.io/) in combination with [Prometheus](https://prometheus.io/).

## Idea
The code examples in the micrometer documentation show possible implementations, but they are often not portable to your own system without modification. For this reason, it is useful to design a sample application that demonstrates the capabilities of micrometer and allows interaction with it. In this way, the functions of the monitoring library can be tested on a real application and help in the selection of suitable metrics.

## Scenario
Users can add their groceries to a virtual fridge and remove expired ones. The application provides an overview of not only the contents of the fridge, but also the value of the groceries it contains and the total value of groceries that have been thrown away. In addition to the virtual fridge, the application allows users to create their own recipes. To do this, the user enters recipes he or she already knows and stores them in the database. After that two operations can be performed on the resulting data set. The user can output a random recipe that can be prepared with at least one grocery from the fridge. Alternatively, the user can output a recipe that uses as much of the groceries in the fridge as possible.

## Required Installation

- Java 17 or higher
- Gradle
- Docker
- Docker-compose

## Getting Started
The following steps are required for a Docker container deployment:
1. Start the docker engine
2. Navigate to the applications folder
3. `./gradlew build`
4. `docker-compose up`
5. To stop the application write `docker-compose down`.

The following steps are required if you want to run the application via sprint boot:
1. Start the docker engine
2. Navigate to the applications folder
3.  `docker-compose up mongodb mongo-express prometheus alertmanager` (mongo-express and alertmanager are ***optional***)
4.  Start the Application via Spring Boot

## Suggestions
When you start the application, first import some recipes and create a fridge. Then you can add some groceries to your fridge and try out the queries.
Predefined data for these queries can be found [here](https://github.com/dnszlr/hka-infm-letmecook/tree/master/data).

## Useful tools (To access these links, the Docker containers must be running)
- [Mongo-Express](http://localhost:8081): Lightweight Web-Based Administrative Tool for MongoDB Database. Can be used to inspect the data in the database.
- [prometheus](http://localhost:9090): The Prometheus web UI. Can be used to run a query on the recorded metrics or view their results. 
- [alertmanager](http://localhost:9093): The web UI of the alertmanager. Can be used to identify outgoing alerts.

## Interaction
As this application has no user interface, this project includes a fully configured [postman collection](https://github.com/dnszlr/hka-infm-letmecook/blob/master/postman/letmecook.postman_collection.json).
You can download postman [here](https://www.postman.com/). After the installation, you will need to import the pre-configured postman collection.
Some paths say "(TODO ID)", this means you have to edit the URI and include the id of an existing fridge.

## Micrometer Metrics
Since it's not easy to spot all the metrics on first sight, they will be linked here.

[Controller](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java): At the end of this file you can find the configuration of *Counter*, *Timer*, *TimeGauge*, *FunctionTimer* and *DistributionSummary*

[Service](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/service/LetmecookService.java): At the end of this file you can find the configuration of *FunctionCounter*, *Gauge*, *Multi-Gauge* and *LongTaskTimer*

The following table shows which API endpoints trigger which micrometers.
| Endpoint | Metric |
| ------------- | ------------- |
| Every Endpoint | Counter   | 
| [deleteGroceryByName](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java#L202) | FunctionCounter 
| [getRandomRecipe](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java#L105)  | Gauge  |
| [postGroceries](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java#L188)   | TimeGauge  |
| [getBestRecipe](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java#L136)  | Multi-Gauge  | 
| [getRandomRecipe](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java#L105)  | Timer | 
| [getBestRecipe](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java#L136)  | Timer.Sample  | 
| [postRecipe](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java#L58)   | FunctionTimer  | 
| [mergeDuplicatedGroceries](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java#L211)  | LongTaskTimer  | 
| [importRecipes](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java#L80)   | DistributionSummary  | 


## Monitoring
All of the application's metrics can be searched in [prometheus](http://localhost:9090) using the ***letmecook*** tag. The other metrics are automatically generated by [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) and are useful aswell.

The random recipe endpoint also has an alert version. This endpoint allows you to trigger an alert that is sent to the Alertmanager because it pushes up the request time of this endpoint. If the average calculation time for this endpoint in the last 5 minutes is greater than 500ms, an alert is created. If you want to receive an alert email, you will need to provide your email details [here](https://github.com/dnszlr/hka-infm-letmecook/blob/master/prometheus/alertmanager/alertmanager.yml). 

More information about the alerts can be found [here](https://github.com/dnszlr/hka-infm-letmecook/blob/master/prometheus/alert.yml).
