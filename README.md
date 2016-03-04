#The Goldfish
[![Build Status](https://travis-ci.org/dkubiak/goldfish.svg?branch=master)](https://travis-ci.org/dkubiak/goldfish)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.dkubiak/goldfish/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.dkubiak/goldfish)

![](http://dockeri.co/image/dkubiak/goldfish)

If your complexity between your projects is huge, use GoldFish.

The GoldFish shows you how deep is complexity and visualize dependencies between the selected projects.
Relations between projects, appoint in real time, so you can track changes in dependencies.

####How to use
1. Run the server: ```docker run -p 8686:8686 -p 9000:9000 --name=goldfish dkubiak/goldfish```

2. Use a collector plugin 
   * inside your project your project:
    ```xml
                <plugin>
                    <groupId>com.github.dkubiak</groupId>
                    <artifactId>goldfish-collector-maven</artifactId>
                    <version>1.0</version>
                    <configuration>
                        <serverURL>http://localhost:9000</serverURL>
                        <groupIdMask>com.github.</groupIdMask>
                    </configuration>
                </plugin>
    ```
   * command line: ```mvn com.github.dkubiak:goldfish-collector-maven:1.0:run ``` 

* serverURL - http://localhost:9000 is a default value.

* groupIdMask - Artifacts with this groupId, will be collected. [default value all] 

Neo4j browser - http://localhost:8686

####How it work

![alt text](http://i.imgur.com/SeyTuNn.png "")
