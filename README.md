helpers
=======

This Kotlin library provides helpful classes and functions for use with my cloud projects.

Usage
-----

Include this in your POM:

```xml
<dependency>
  <groupId>com.github.brymck</groupId>
  <artifactId>helpers</artifactId>
  <version>0.0.2-SNAPSHOT</version>
</dependency>
```

And use it as so in Kotlin:

```kotlin
val overrides = mapOf("FOO" to "bar")
withEnvironmentOverrides(overrides) {
  val foo = System.getenv("FOO");
  println(foo)  // prints "bar"
}
```
