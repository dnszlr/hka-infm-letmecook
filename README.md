# hka-infm-letmecook

![Repository Banner](https://i.imgflip.com/74bvex.png)

Repository for the seminar paper on "Monitoring of Applications with Micrometer" supervised by Professor Vogelsang at Karlsruhe University of Applied Sciences.
The goal of this repository is to provide a sample application for Micrometer in combination with Prometheus.

## Idea
Code examples in the micrometer documentation show possible implementations, but these are often not transferable to one's own system without adaptation. For this reason, it is useful to design a sample application that demonstrates the capabilities of micrometer and allows to interact with it. In this way, the functions of the monitoring library can be tested on a real application, and assistance can be given in the selection of suitable metrics.

## Scenario
Users can add their groceries to a virtual fridge and remove expired groceries. The application provides an overview of not only the contents of the fridge, but also the value of the groceries it contains and the total amount of groceries that have been thrown away. In addition to the virtual fridge, the application allows users to create their own recipes. To do this, the user enters recipes he or she already knows into the application. The user has maximum freedom in the selection of possible recipes. Two operations can be performed on the resulting data set. The user can output a random recipe that can be prepared with at least one food from the fridge. Alternatively, the user can output a recipe that uses as much of the food in the fridge as possible.
## Required Installation

- Java 17 or higher
- Gradle
- Docker
- Docker-compose

## Getting Started
The following steps are required for a Docker container deployment:
1. Navigate to the applications folder
2. `./gradlew build`
3. `docker-compose up`
4. To stop the application write `docker-compose down`.

The following steps are required if you want to run the application via sprint boot:
1. docker-compose start mongodb
2. docker-compose start mongo-express (Optional)
3. docker-compose start prometheus
4. docker-compose start alertmanager (Optional)
5. Start the Application via Spring Boot

## Suggestions
When you start the application, first import some recipes and create a fridge. You can then add some ingredients to your fridge and try out some of the queries.
Predefined data for these queries can be found [here](https://github.com/dnszlr/hka-infm-letmecook/tree/master/data).

## Useful tools (To access these links, the Docker containers must be running)
- [Mongo-Express](http://localhost:8081): Lightweight Web-Based Administrative Tool for MongoDB Database. Can be used to inspect the data in the database.
- [prometheus](http://localhost:9090): The Prometheus web UI. Can be used to query the recorded metrics. 
- [alertmanager](http://localhost:9093): The web UI of the alertmanager. Can be used to identify outgoing alerts.

## Interaction
As this application has no user interface, this project includes a fully configured [postman collection](https://github.com/dnszlr/hka-infm-letmecook/blob/master/postman/letmecook.postman_collection.json).
You can download postman [here](https://www.postman.com/). After installation, you will need to import the pre-configured postman collection.
Some paths say "(TODO ID)", this means you have to edit the URI and include the id of an existing object.

## Micrometer Metrics
Since it's not easy to spot all the metrics on first sight, they will be linked here.
[Controller](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/controller/LetmecookController.java): At the end of this file you can find the configuration of *Counter*, *Timer*, *TimeGauge*, *FunctionTimer* and *DistributionSummary*
[Service](https://github.com/dnszlr/hka-infm-letmecook/blob/master/src/main/java/com/zeller/letmecook/service/LetmecookService.java): At the end of this file you can find the configuration of *FunctionCounter*, *Gauge*, *Multi-Gauge* and *LongTaskTimer*
