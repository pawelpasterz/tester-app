## Simple Tester App
### How to build and run app
* clone repo
* go to `/applause` directory
* run `mvn install`
* when build is finished run `docker-compose up`

### How to use
App can be reached on `localhost:8082` url.
There are 2 ways of fetching data
1) via url
2) via sending JSON in body
 
##### 1) URL
Put `http://localhost:8082/testers?countries=[country]&devices=[device1]&devices=[device2]`

Multiple variables countries & devices are supported

##### 2) JSON
Use ex Postman and send GET request with JSON in body -- `/testers/json`

```
{
    "devices": ["iPhone 3", "iPhone 4s"],
    "countries": ["GB"]
}
``` 
----
Possible values for:
* countries = `GB`, `JP`, `US`, `ALL`
* devices = `iPhone 3`, `iPhone 4`, `iPhone 4S`, `iPhone 5`, `Galaxy S3`, `Galaxy S4`, `Galaxy S4`, `Nexus 4`, `Droid Razor`, `Droid DNA`, `HTC One`, `ALL`

For both, `ALL` neglects other choices and returns all devices/countries.
