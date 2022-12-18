# reproducing micronaut application not exiting

in spring, if you want to programatically gracefully exit, you can call [ConfigurableApplicationContext#close][springclose], and it will exit the application.

I can't figure out how to do the same in micronaut. I tried using the [ApplicationContext][mnac]'s [close][mnclose] method.

[springclose]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ConfigurableApplicationContext.html#close()

[mnac]: https://docs.micronaut.io/3.7.4/api/io/micronaut/context/ApplicationContext.html#stop--
[mnclose]: https://docs.micronaut.io/3.7.4/api/io/micronaut/context/LifeCycle.html#close--

See the file at [src/main/java/Reproducer.java](src/main/java/Reproducer.java) to see a demonstration of the issue.

## usage

the way to "run" this repository is to execute the default task on the root project, aka:

```shell
./gradlew
```
