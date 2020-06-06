# Parkserv API

This API has one end point that will return a list of car parks with their distance from the reference position.  
Sample query: 
```
http://localhost:8080/parkserv/v1_0/carparks?latitude=44.888008&longitude=-0.572154
```

## Documentation of the API

The API is documented respecting the openAPI 3.0 standard. It is included in the apidoc.yml file.

## Building and running the project

Platform requirements:
* maven
* java 1.8

To build the project, you need to place yourself into the project root directory then run:
```
mvn install
```
To run it
```
## on Windows
java -jar server/target/server-0.0.1-SNAPSHOT.jar

## on UNIX, place yourself in the directory where the jar is located then
server-0.0.1-SNAPSHOT.jar
```
Of course the project can also be run under your favorite IDE
## Project organization
Because it is requested to be able to interface with different external APIs, this application will transform the data into
a unified format that will be exposed to mobile applications whatever is the external API. To satisfy this need and make 
it easy to interface with different cities, the project is organized around three main components:
* server: The server part which is a springboot application exposing the service
* parkserv-api: This is the contract it contains the model of our rest API and an interface to be implemented in order 
to interface with a new external city API
* lacub-api: The external module that will manage data from the "lacub" external API
 
### server module
For simplicity in the scope of this test, I have chosen to package it has a self contained executable jar that embeds the 
three modules mentioned above. Of course if it were to be packaged for a production environment, I would have externalized
the configuration file and the lacub-api to:
* make common and independent the server jar.
* be able to modiy the conf without needing to deliver again the full package

This module has been made using springboot version 2.2.4. For the HTTP server and client part I have chosen to use 
webflux reactive stack with Netty instead of servlet API. This stack is able to handle concurrency with a limited number 
of threads due to the non blocking model.

##### Cache usage
Because I wanted to guarantee the best possible performances, I have used an "in memory" cache "caffeine" that is 
integrated with the spring-cache module. It is backed by a concurrent hash map and can be configured to evict cached 
data on a time basis. In our case, I have considered that the car park data is not moving a lot, so we are not constrained
to provide very fresh data. Actually, only the number of free places is supposed to change between two calls, so I have
considered that the data could be kept in the cache during one minute (this delay is configurable in the 
application.properties file). This has the advantage to reduce the number of calls made to the external API to at 
most 1440 calls per day, which is a fair usage for the external API.
Moreover, from a performance perspective we save: 
* Network time by avoiding data transfer from the external API
* Processing time because the data is parsed and transformed to into our model only once, then the result of this
transformation is kept in the cache.

##### Security
Because our API does not expose any sensible data, and is readonly. I have considered that it was not necessary to 
secure it. If we wish to use security the spring security module can be easily added and it supports usage of Oauth2

##### Configuration

The configuration is stored in the application.properties file:
```
spring.cache.cache-names=${retriever.service.cache.name}
spring.cache.caffeine.spec=expireAfterWrite=60s
spring.cache.type=CAFFEINE

retriever.service.identifier=LACUB
retriever.service.base.url=http://data.lacub.fr/wfs?key=9Y2RU3FTE8&SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=ST_PARK_P&SRSNAME=EPSG:4326
retriever.service.cache.name=${retriever.service.identifier}

logging.level.=INFO
```

### parkserv API module
This is a very simple module to be embedded in the external API module.
It contains a n interface **CarParkRetrieverServiceConfigurer** to be implemented in order to configure the web client
that will query the external API. In the implementing class we will find three elements:
* The target type: this is the type of object returned by the external API
* The default http header: because the content type header is sometimes not correctly supported by spring webclient, we 
could need to apply a filtering function to override the content type. (example: the lacub API return the Content-Type 
*text/xml; subtype=gml/3.1.1; charset=iso-8859-1* which causes an exception when parsed by the webclient) 
* the mapper function: this is the function responsible for translating the data received from the external API into
our own representation. It implements the functional interface Function<Object, CarPark>

### Lacub API
This is the component responsible for handling the data received from the external API. To implement it, I have used 
JAXB2 xjc to process the XML schemas from the lacub API and generate objects that will result from XML parsing of the
http call.  

## Enhancements to be done

The following enhancements could be done before running on production.
* Increase the unit test coverage. The Lacub API module ias 100% covered but unit tests should be done also on the
server module
* Change packaging: as mentioned above the lacub API jar and the application.properties should be store outside the 
server jar file
* Spring Actuator could be used to provide monitoring capabilities.      