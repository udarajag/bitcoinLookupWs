# bitcoinLookupWs
# Sala Play example for Bitcoin data display

[<img src="https://img.shields.io/travis/playframework/play-scala-slick-example.svg"/>](https://travis-ci.org/playframework/play-scala-slick-example)

This project demonstrates how connect to web service using a WSClient, store data in a H2 database and allow users 
to query the data via rest endpoints using [Play](https://www.playframework.com/), [Slick](http://slick.lightbend.com/doc/3.1.1/) and [Play-Slick](https://www.playframework.com/documentation/latest/PlaySlick).

The bitcoin_play_ws was build using [Play](https://www.playframework.com/) for handling rest calls.

The data is stored in a H2 data base.

The following are the WS endpoints.

GET     /                           controllers.BitcoinController.index

POST    /search                     controllers.BitcoinController.searchPrice

GET     /refresh                    controllers.BitcoinController.refresh

GET     /lastweek                   controllers.BitcoinController.lastWeek

GET     /lastmonth                  controllers.BitcoinController.lastMonth

GET     /today                      controllers.BitcoinController.todaysPrice

To run the project clone the repository and run simply executing the command sbt run.

Then you can browse to the web page [localhost](http://localhost:9000/)

This will open a simple web page created for testing the APIs

there will be buttons for each rest call 

for example there will be a date range search to check the average and results accross a date range.
similarly there will be buttons to check latweek, lastmonth and todays data 

the refresh button at the bottom will refresh the H2 database with fresh values queried form the [Web_service](https://www.coinbase.com/api/v2/prices/BTC-USD/historic?period=year)

