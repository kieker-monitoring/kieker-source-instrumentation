# Kieker Source Instrumentation

*This is temporary fork, which is needed to deploy the artifacts to maven central. As soon as regular Kieker builds are possible, this repository will be discontinued.*

To measure the performance of method executions, measurement probes need to be inserted to the measured method. This projects provides instrumentation by changing the source code of the project. Alternatives, e.g. instrumentation via AspectJ, create more overhead and might therefore hinder identification of performance changes.

## Usage

To instrument the project `$PROJECT` using source instrumentation, build this project (`mvn clean package`) and then call `java -jar target/kieker-source-instrumentation-1.15.1-SNAPSHOT.jar --folder=$PROJECT`. To execute the project with instrumentation, add Kieker to the build path of your project, e.g. using

```
<dependency>
	<groupId>net.kieker-monitoring</groupId>
	<artifactId>kieker</artifactId>
	<version>1.15</version>
</dependency>
```
in maven. Afterwards, you can build and run your instrumented project without AspectJ.

You might also specify the following options:
- `--extractMethod`: Extract original method to own method (might improve performance)
- `--aggregate`: Whether to aggregate invocations
- `--aggregationCount`: How many method invocations to aggregate (default: 1000)
- `--includedPatterns`: Semicolon-seperated list of patterns of methods to include (if empty, just *)
- `--excludedPatterns`: Semicolon-seperated list of patterns of methods to exclude


## Versioning

The major version of kieker-source-instrumentation equals the major version of Kieker, i.e. versions built to instrument with Kieker 1.15 will be named 1.15.x (or 1.15.x-SNAPSHOT).
