# Load Variables Into JMeter

TThis sampler allows to load in jMeter variables directly from the sampler or from a file. The variables has to be defined as a json and are loaded when the sampler is executed. This allow to reuse modules using the same variable names just changing the values right before executing they. Also you could use different datasets in different files and launch the same agenda with the chosen one.

## Getting Started


### Prerequisites

This sampler works with jMeter Ver 4+

```
Give examples
```

### Installing

- Download the project
- Compile with maven
- Copy the result jar in lib/ext directory of jMeter


## Use

Write the json in the sampler (json to load) or type the path of the file:

![picture](jMeterLoadVariables/resources/img/abc.png)

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
