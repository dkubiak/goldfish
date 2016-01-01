#The Goldfish
[![Build Status](https://travis-ci.org/dkubiak/goldfish.svg?branch=master)](https://travis-ci.org/dkubiak/goldfish)

If your complexity between your projects is huge, use GoldFish.

The GoldFish shows you how deep is complexity and visualize dependencies between the selected projects.
Relations between projects, appoint in real time, so you can track changes in dependencies.

####How to use
1. Run the server:``` java -jar goldfish-server-1.0-SNAPSHOT.jar```
2. Use a collector plugin 
   * inside your project your project:
    ```xml
                <plugin>
                    <groupId>com.github.dkubiak</groupId>
                    <artifactId>goldfish-collector-maven</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <configuration>
                        <serverURL>http://localhost:8089</serverURL>
                        <groupIdMask>com.github.</groupIdMask>
                    </configuration>
                </plugin>
    ```
   * command line: ```mvn com.github.dkubiak:goldfish-collector-maven:1.0-SNAPSHOT:run ``` 

*serverURL - http://localhost:8089 is a default value.
*groupIdMask - Artifacts with this groupId, will be collected. [default value all] 
