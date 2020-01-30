# kalah-api

[![Build Status](https://travis-ci.com/damaya0226/kalah-api.svg?branch=master)](https://travis-ci.com/damaya0226/kalah-api)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kalah-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=kalah-api)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=kalah-api&metric=coverage)](https://sonarcloud.io/dashboard?id=kalah-api) 
  
**[The API](#heading--1)**
  * [Create game](#heading--1-1)
  * [Make a move](#heading--1-2)

**[Components](#Components)**

**[DevOps](#DevOps)**

**[How to run it](#heading-run-it)**
  * [With Maven](#heading-run-it-1)
  * [With Docker](#heading-run-it-2)
  
**[Future Work](#FutureWork)**
    
## The API

<div id="heading--1-1"/>

### Create Game
```
POST
http://<host>:<port>/games
```
This endpoint creates a new Kalah game.

#### Response Body

```json
{
    "id": "34e35ab6-26bd-4258-affe-6564b1c6d529",
    "url": "http://localhost:8080/games/34e35ab6-26bd-4258-affe-6564b1c6d529"
}
```

#### Response Codes
| HTTP Code             | Explanation             |
| -------------         |:-----------------------------------------------:|
| 201 (Created)         | The game was created |


<div id="heading--1-2"/>

### Make a move
```
PUT
http://<host>:<port>/games/<gameId>/pits/<pitId>
```
This endpoint make a move on the kalah board.

#### Response Body

```json
{
    "id": "34e35ab6-26bd-4258-affe-6564b1c6d529",
    "url": "http://localhost:8080/games/34e35ab6-26bd-4258-affe-6564b1c6d529",
    "status": {
        "1": "6",
        "2": "6",
        "3": "6",
        "4": "6",
        "5": "6",
        "6": "6",
        "7": "0",
        "8": "6",
        "9": "6",
        "10": "6",
        "11": "6",
        "12": "6",
        "13": "6",
        "14": "0"
    }
}
```

#### Responses
| HTTP Code                 | Explanation             |
| -------------             |:-----------------------------------------------:|
| 200 (OK)                  | Success move |
| 400 (Bad request)         | Invalid move: Trying to move adversary stones, trying to move home stones, Current turn is on the other player      |
| 401 (Conflict)            | Game is already finished      |
| 404 (Not found)           | Game not found      |

## Components
 * Core: Business logic.
 * Datastore: In memory data storage implementation.
 * Rest API: Exposes Rest API Endpoints.
 
## DevOps
DevOps is managed using travis-ci. Travis executes test, sonar analysis, publish the results to sonar cloud and then it builds a docker image and push it to the public docker hub repo.

<div id="heading-run-it"/>

## How to run it

<div id="heading-run-it-1"/>

### With Maven

```
./mvnw -pl rest-api -am spring-boot:run
```

<div id="heading-run-it-2"/>

### With Docker

Here you will find the current docker image: https://hub.docker.com/r/damaya0226/kalah-api
```
docker pull damaya0226/kalah-api
docker run -p 8080:8080 damaya0226/kalah-api
```


## FutureWork
 * Add Swagger.
 * Continuous Delivery. (Right now I am delivering the solution as a docker image)
 * Shared in memory DB to be able to scale the solution