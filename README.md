Maven plugin for PerfCake
=========================
Maven plugin which allows to run [PerfCake](http://www.perfcake.org) scenarion within Maven build. This makes perfomance test 
automation more easy and encourage you to run perfomance tests on a regular basis, e.g. in within your favourite CI server. 
Runnning perfomance test on a regular basis allows you to spot perfomance drops very soon and thus make much more easy to 
identify, which commit has caused perfomance regression.

Configuration
---
Currently plugin has only one goal `scenario-run`, which runs specified PerfCake scenario. By default which goal is executed
in `integration-test` phase (i.e. assumes, that you deploy/start your application in `pre-integration-test` phase and 
hut it down in `post-integration-test` phase). The only mandatory parameter is `<scenario>`, which specifies the name of 
scenario to be run. Optionally, you can also specify `scenarios-dir`, `messages-dir` and `plugins-dir`, which speficy paths to
directory containing scenarios, messages and plugins, respectivelly. If you don't setup these parameters, pluing assumes,
that appropriate directories (`scenarios`, `messages`, `plugins`) are in `src/test/resources/perfcake`. If any of them doesn't
exists, plugin will `src/test/resources` as a fallback value for missing parameter.

Example configuration
---

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.perfcake.maven</groupId>
        <artifactId>perfcake-maven-plugin</artifactId>
        <version>0.1-SNAPSHOT</version>
        <configuration>
          <scenario>my_perfcake_scenario</scenario>
        </configuration>
        <executions>
          <execution>
            <id>perfcake-scenario-run</id>
            <goals>
              <goal>scenario-run</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```
