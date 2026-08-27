# Maven & Gradle — Exam Preparation Notes

## 1. Maven

### What is Maven?

**Maven is a build automation and dependency management tool primarily used for Java projects.**

Maven helps automate:

* Compiling source code
* Compiling test code
* Running test cases
* Managing dependencies
* Packaging applications into JAR/WAR files
* Installing artifacts into a local repository
* Deploying artifacts to a remote repository
* Generating project documentation

### Simple Flow

```text
Java Source Code
       ↓
     Compile
       ↓
      Test
       ↓
    Package
       ↓
   JAR / WAR
       ↓
Install / Deploy
```

### Interview Answer

> Maven is a Java build automation and dependency management tool. It uses a declarative configuration file called `pom.xml` to manage dependencies, plugins, project information, and build configuration. Maven follows predefined build lifecycles such as Clean, Default, and Site.

---

# 2. Maven Project Creation

## Maven Archetype

An **archetype** is a predefined project template used to generate the basic structure of a Maven project.

Instead of manually creating folders, Maven can generate the project structure automatically.

### Command

```bash
mvn archetype:generate \
-DgroupId=com.kali \
-DartifactId=my-app \
-DarchetypeArtifactId=maven-archetype-quickstart \
-Dversion=1.0-SNAPSHOT \
-DinteractiveMode=false
```

### Important Parameters

| Parameter                 | Meaning                              |
| ------------------------- | ------------------------------------ |
| `mvn`                     | Maven command                        |
| `archetype:generate`      | Generate a project from an archetype |
| `-DgroupId=com.kali`      | Identifies the organization/group    |
| `-DartifactId=my-app`     | Name of the project/artifact         |
| `-Dversion=1.0-SNAPSHOT`  | Project version                      |
| `-DarchetypeArtifactId`   | Template to use                      |
| `-DinteractiveMode=false` | Don't ask questions interactively    |

### Common Archetypes

```text
maven-archetype-quickstart
        ↓
Basic Java application

maven-archetype-webapp
        ↓
Basic Java web application
```

### Memory Trick

```text
GroupId     → Who owns it?
ArtifactId  → What is it called?
Version     → Which version?
Archetype   → Which template?
```

---

# 3. Maven Project Structure

A typical Maven project looks like:

```text
my-app/
│
├── pom.xml
│
└── src/
    ├── main/
    │   ├── java/
    │   └── resources/
    │
    └── test/
        ├── java/
        └── resources/
```

### Important Directories

| Directory            | Purpose                             |
| -------------------- | ----------------------------------- |
| `src/main/java`      | Production Java source code         |
| `src/main/resources` | Application resources/configuration |
| `src/test/java`      | Test source code                    |
| `src/test/resources` | Test resources                      |
| `target/`            | Generated build output              |
| `pom.xml`            | Maven project configuration         |

---

# 4. POM.xml

## POM = Project Object Model

`pom.xml` is the main configuration file of a Maven project.

It contains information such as:

* Project information
* Dependencies
* Plugins
* Build configuration
* Packaging
* Properties
* Repositories
* Profiles

### Basic Example

```xml
<project>

    <groupId>com.kali</groupId>

    <artifactId>my-app</artifactId>

    <version>1.0-SNAPSHOT</version>

    <packaging>jar</packaging>

    <dependencies>
        ...
    </dependencies>

    <build>
        ...
    </build>

</project>
```

### Important

```text
pom.xml
   ↓
Maven project configuration
```

---

# 5. GroupId, ArtifactId and Version

These values identify a Maven artifact.

```text
groupId:     com.kali
artifactId:  my-app
version:     1.0-SNAPSHOT
```

Together:

```text
com.kali:my-app:1.0-SNAPSHOT
```

These are called the **Maven coordinates**.

### Memory Trick

```text
GroupId     → Organization
ArtifactId  → Application/Library name
Version     → Version
```

---

# 6. Maven Dependency Management

A dependency is an external library required by the application.

Examples:

```text
Spring Boot
JUnit
Jackson
Lombok
Mockito
```

Instead of manually downloading JAR files, Maven can download dependencies automatically.

### Example

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>...</version>
</dependency>
```

### Dependency Flow

```text
pom.xml
   ↓
Maven checks dependency
   ↓
Local Repository
   ↓
If not available
   ↓
Remote/Central Repository
   ↓
Download dependency
   ↓
Store in Local Repository
```

---

# 7. Maven Repositories

A Maven repository is a location where Maven artifacts are stored.

There are three important repository concepts:

```text
              Maven
                │
       ┌────────┼────────┐
       ↓        ↓        ↓
    Local    Central   Remote
```

## 7.1 Local Repository

Default location:

```text
~/.m2/repository
```

It stores:

* Downloaded dependencies
* Plugins
* Locally installed artifacts

### Important

```bash
mvn install
```

installs the generated artifact into the local Maven repository.

---

## 7.2 Central Repository

**Maven Central** is a public repository containing a large number of commonly used Java libraries.

Maven can download dependencies from Central.

---

## 7.3 Remote Repository

A remote repository is an external repository used by a project or organization.

Common repository management tools include:

* Nexus Repository
* JFrog Artifactory

Organizations can use private repositories to store internal libraries and approved dependencies.

---

# 8. SNAPSHOT vs RELEASE

## SNAPSHOT

A `SNAPSHOT` version represents a development version.

Example:

```text
1.0-SNAPSHOT
```

It generally means:

```text
Still under development
May change
Not a final release
```

---

## RELEASE

A release version represents a stable version.

Examples:

```text
1.0
2.0.0
2.1.5
```

### Interview Question

**Q: What is the difference between SNAPSHOT and RELEASE?**

### Answer

> A SNAPSHOT version represents an ongoing development version that may change, while a RELEASE version represents a stable version intended for consumption.

### Memory Trick

```text
SNAPSHOT → Development
RELEASE  → Stable
```

---

# 9. Maven Lifecycles

Maven has three built-in lifecycles:

```text
1. Clean
2. Default
3. Site
```

---

## 9.1 Clean Lifecycle

Used to clean previous build output.

Command:

```bash
mvn clean
```

It removes the:

```text
target/
```

directory.

---

## 9.2 Default Lifecycle

The Default lifecycle handles the main build process.

Important phases:

```text
validate
   ↓
compile
   ↓
test
   ↓
package
   ↓
verify
   ↓
install
   ↓
deploy
```

### Important Concept

When you execute a later lifecycle phase, Maven executes the earlier phases automatically.

For example:

```bash
mvn package
```

will execute approximately:

```text
validate
   ↓
compile
   ↓
test
   ↓
package
```

You don't normally need to execute all phases separately.

---

## 9.3 Site Lifecycle

The Site lifecycle is used for generating project documentation and reports.

Typical command:

```bash
mvn site
```

---

# 10. Important Maven Commands

## Compile

```bash
mvn compile
```

Compiles the main source code.

---

## Compile Test Code

```bash
mvn test-compile
```

Compiles test source code.

---

## Run Tests

```bash
mvn test
```

Compiles the required code and executes tests.

---

## Package

```bash
mvn package
```

Builds and packages the application.

Possible outputs:

```text
JAR
WAR
```

Example:

```text
target/my-app-1.0.jar
```

---

## Verify

```bash
mvn verify
```

Performs verification checks on the project/package.

---

## Install

```bash
mvn install
```

Builds the project and installs the generated artifact into:

```text
~/.m2/repository
```

### VERY IMPORTANT

```bash
mvn install
```

is **NOT the same as**:

```bash
npm install
```

In Maven:

```text
mvn install
     ↓
Build project
     ↓
Create artifact
     ↓
Install artifact into local Maven repository
```

---

## Deploy

```bash
mvn deploy
```

Publishes the artifact to a configured remote repository.

---

## Clean + Package

```bash
mvn clean package
```

Flow:

```text
Remove target/
      ↓
Compile
      ↓
Run tests
      ↓
Package
      ↓
Create JAR/WAR
```

---

# 11. Maven Goals vs Lifecycle Phases

This is an important interview topic.

## Lifecycle Phase

Examples:

```text
compile
test
package
install
deploy
```

A lifecycle phase represents a stage in the Maven build process.

---

## Plugin Goal

A goal is a specific task provided by a Maven plugin.

Example:

```bash
mvn compiler:compile
```

Here:

```text
compiler → Maven plugin
compile  → Plugin goal
```

Another example:

```bash
mvn spring-boot:run
```

Here:

```text
spring-boot → Plugin
run         → Goal
```

### Easy Difference

```text
Lifecycle Phase
      ↓
High-level build stage

Plugin Goal
      ↓
Specific task provided by a plugin
```

### Interview Answer

> Maven lifecycle phases represent stages of the build process, while plugin goals are specific operations provided by Maven plugins. Lifecycle phases are executed through plugins.

---

# 12. Maven Plugins

Maven uses plugins to perform many build operations.

Examples:

```text
Maven Compiler Plugin
Maven Surefire Plugin
Maven JAR Plugin
Maven WAR Plugin
Spring Boot Maven Plugin
```

### Example

```bash
mvn compiler:compile
```

The compiler plugin performs compilation.

### Important Interview Point

> Maven provides lifecycle phases, while plugins provide goals that perform the actual build operations.

---

# 13. JAR vs WAR

## JAR

**JAR = Java ARchive**

Commonly used for:

* Java libraries
* Standalone Java applications
* Spring Boot applications

Example:

```text
my-app.jar
```

---

## WAR

**WAR = Web Application Archive**

Traditionally used for Java web applications deployed to a servlet container/application server.

Example:

```text
my-app.war
```

### Comparison

| JAR                                       | WAR                                     |
| ----------------------------------------- | --------------------------------------- |
| Java Archive                              | Web Application Archive                 |
| `.jar`                                    | `.war`                                  |
| Common for Java libraries/apps            | Traditional Java web applications       |
| Spring Boot commonly uses executable JARs | Common with external servlet containers |

---

# 14. Running a JAR

After:

```bash
mvn package
```

you may get:

```text
target/my-app.jar
```

You can run it using:

```bash
java -jar target/my-app.jar
```

For `java -jar` to work as an executable application, the JAR manifest needs to identify the application's entry point.

Example:

```text
Main-Class: com.kali.Main
```

The class should contain:

```java
public static void main(String[] args) {
    ...
}
```

### Important Interview Point

> `java -jar` requires the JAR manifest to identify the `Main-Class` entry point.

In Spring Boot projects, the Spring Boot Maven Plugin commonly configures the executable JAR appropriately.

---

# 15. Effective POM

The **effective POM** is the final configuration Maven uses after combining applicable configurations from:

* Project `pom.xml`
* Parent POM
* Super POM
* Inherited configuration
* Profiles
* Other applicable Maven settings

Command:

```bash
mvn help:effective-pom
```

### Important Correction

Do not think:

```text
effective-pom = default file maintained for the whole project
```

Instead:

```text
pom.xml
   +
Parent POM
   +
Super POM
   +
Profiles
   +
Inherited configuration
   ↓
Effective POM
```

---

# 16. Gradle

## What is Gradle?

**Gradle is a build automation tool that supports multiple programming languages and provides a flexible, task-based build system.**

Gradle can be used for:

* Compiling source code
* Running tests
* Dependency management
* Packaging applications
* Publishing artifacts
* Custom build automation

Gradle is widely used in Java, Kotlin, Android and other ecosystems.

---

# 17. Maven vs Gradle Configuration

### Maven

```text
pom.xml
```

Maven primarily uses XML configuration.

### Gradle

```text
build.gradle
```

or:

```text
build.gradle.kts
```

Gradle supports:

```text
Groovy DSL → build.gradle
Kotlin DSL → build.gradle.kts
```

### Comparison

| Maven                  | Gradle                     |
| ---------------------- | -------------------------- |
| `pom.xml`              | `build.gradle`             |
| XML                    | Groovy/Kotlin DSL          |
| Lifecycle-oriented     | Task-oriented              |
| More convention-driven | More flexible/customizable |

---

# 18. Gradle Wrapper

One of the major advantages of Gradle is the **Gradle Wrapper**.

Typical files:

```text
gradlew
gradlew.bat
gradle/
    wrapper/
```

The wrapper allows the project to use a specific Gradle version without requiring the user to manually install that Gradle version globally.

### Commands

Linux/macOS:

```bash
./gradlew build
```

Windows:

```bash
gradlew.bat build
```

### Important Correction

It is better to say:

> The Gradle Wrapper downloads and uses the required Gradle version for the project. Therefore, developers do not need to manually install Gradle globally.

It is not simply that Gradle "doesn't need to be installed anywhere."

---

# 19. Gradle Build

Typical command:

```bash
gradle build
```

With the wrapper:

```bash
./gradlew build
```

Typical flow:

```text
Compile
   ↓
Test
   ↓
Package
   ↓
Build
```

Build output is generally stored under:

```text
build/
```

---

# 20. Important Gradle Commands

## Compile Java

```bash
gradle compileJava
```

or:

```bash
./gradlew compileJava
```

---

## Compile Test Code

```bash
gradle compileTestJava
```

---

## Run Tests

```bash
gradle test
```

---

## Create JAR

```bash
gradle jar
```

---

## Full Build

```bash
gradle build
```

A normal Java Gradle build includes compilation, testing and packaging tasks as applicable.

---

## Clean

```bash
gradle clean
```

Removes the Gradle build output.

---

# 21. Maven vs Gradle Commands

| Purpose                  | Maven                 | Gradle                       |
| ------------------------ | --------------------- | ---------------------------- |
| Compile main Java code   | `mvn compile`         | `gradle compileJava`         |
| Compile test code        | `mvn test-compile`    | `gradle compileTestJava`     |
| Run tests                | `mvn test`            | `gradle test`                |
| Package JAR              | `mvn package`         | `gradle jar`                 |
| Full build               | `mvn package`         | `gradle build`               |
| Clean                    | `mvn clean`           | `gradle clean`               |
| Install artifact locally | `mvn install`         | `gradle publishToMavenLocal` |
| Run Spring Boot app      | `mvn spring-boot:run` | `gradle bootRun`             |

### Important

The commands are not exactly equivalent in every project because Maven and Gradle have different build models and plugins/tasks.

---

# 22. Node.js vs Maven vs Gradle

| Purpose                  | Node.js / npm    | Maven                       | Gradle                       |
| ------------------------ | ---------------- | --------------------------- | ---------------------------- |
| Dependency management    | `npm install`    | Maven dependency management | Gradle dependency management |
| Build project            | `npm run build`  | `mvn package`               | `gradle build`               |
| Run tests                | `npm test`       | `mvn test`                  | `gradle test`                |
| Run Spring Boot app      | `npm start`      | `mvn spring-boot:run`       | `gradle bootRun`             |
| Clean build files        | Project-specific | `mvn clean`                 | `gradle clean`               |
| Build + test + package   | `npm run build`  | `mvn package`               | `gradle build`               |
| Install artifact locally | N/A equivalent   | `mvn install`               | `publishToMavenLocal`        |

### Important Difference

Do not compare:

```text
npm install
```

directly with:

```text
mvn install
```

because they mean different things.

```text
npm install
    ↓
Install Node dependencies

mvn install
    ↓
Build Maven project
    ↓
Install generated artifact
    ↓
Local Maven repository
```

---

# 23. Maven vs Gradle — Key Differences

| Feature                | Maven                    | Gradle                                      |
| ---------------------- | ------------------------ | ------------------------------------------- |
| Build model            | Lifecycle-based          | Task-based                                  |
| Configuration          | XML                      | Groovy/Kotlin DSL                           |
| Main config            | `pom.xml`                | `build.gradle` / `build.gradle.kts`         |
| Flexibility            | More convention-driven   | Highly flexible                             |
| Build performance      | Good                     | Often faster for complex/incremental builds |
| Incremental builds     | More limited             | Strong support                              |
| Build cache            | Available                | Strong build-cache support                  |
| Learning curve         | Easier initially         | More flexible but more concepts             |
| Dependency management  | Yes                      | Yes                                         |
| Multi-language support | Primarily Java ecosystem | Supports multiple ecosystems                |
| Wrapper                | Maven Wrapper available  | Gradle Wrapper is widely used               |

---

# 24. Maven vs Gradle — Interview Answer

### Question:

**Which is better, Maven or Gradle?**

### Best Answer

> Neither is universally better. Maven is mature, convention-driven, and widely adopted in enterprise Java projects. Gradle provides a more flexible task-based build system, supports Groovy and Kotlin DSLs, and offers strong incremental build and caching capabilities. The choice depends on project requirements, team familiarity, build complexity, and the existing ecosystem.

### Avoid Saying:

> Gradle is always better than Maven.

Instead say:

```text
Maven
→ Standardized
→ Mature
→ Convention-driven
→ Very common in enterprise Java

Gradle
→ Flexible
→ Task-based
→ Customizable
→ Strong build performance features
```

---

# 25. Is Maven Only for Java?

A common interview statement is:

> Maven supports only Java.

This is **too strict**.

A better statement is:

> Maven is primarily associated with the Java ecosystem and has strong Java support, although Maven can be extended through plugins and can participate in builds involving other technologies.

Gradle has broader multi-language support and is commonly used beyond Java.

### Exam Memory

```text
Maven → Strongly associated with Java
Gradle → Multi-language + highly flexible
```

---

# 26. Maven Build Flow

```text
                 Maven
                   │
                   ↓
                pom.xml
                   │
          ┌────────┴────────┐
          ↓                 ↓
    Dependencies         Plugins
          │                 │
          └────────┬────────┘
                   ↓
               Lifecycle
                   │
                   ↓
              Validate
                   ↓
               Compile
                   ↓
                 Test
                   ↓
               Package
                   ↓
                Verify
                   ↓
                Install
                   ↓
                Deploy
```

---

# 27. Gradle Build Flow

```text
              Gradle
                 │
                 ↓
        build.gradle(.kts)
                 │
        ┌────────┴────────┐
        ↓                 ↓
  Dependencies          Tasks
        │                 │
        └────────┬────────┘
                 ↓
              Build
                 │
        ┌────────┼────────┐
        ↓        ↓        ↓
     Compile    Test    Package
                 │
                 ↓
              build/
```

---

# 28. Maven Important Terms — Quick Revision

| Term               | Meaning                                  |
| ------------------ | ---------------------------------------- |
| Maven              | Build automation + dependency management |
| POM                | Project Object Model                     |
| `pom.xml`          | Maven project configuration              |
| Archetype          | Project template                         |
| GroupId            | Organization/group identifier            |
| ArtifactId         | Project/artifact identifier              |
| Version            | Artifact version                         |
| Dependency         | External library required by project     |
| Plugin             | Provides specific build functionality    |
| Goal               | Specific task provided by a plugin       |
| Lifecycle          | Ordered build process                    |
| Repository         | Storage location for artifacts           |
| Local Repository   | Local artifact storage                   |
| Central Repository | Public Maven repository                  |
| Remote Repository  | External/private repository              |
| JAR                | Java Archive                             |
| WAR                | Web Application Archive                  |
| SNAPSHOT           | Development version                      |
| RELEASE            | Stable version                           |
| Effective POM      | Final effective Maven configuration      |
| `target/`          | Maven build output                       |

---

# 29. Gradle Important Terms — Quick Revision

| Term               | Meaning                                               |
| ------------------ | ----------------------------------------------------- |
| Gradle             | Build automation tool                                 |
| `build.gradle`     | Gradle Groovy DSL configuration                       |
| `build.gradle.kts` | Gradle Kotlin DSL configuration                       |
| Task               | Unit of work performed by Gradle                      |
| Plugin             | Adds functionality to Gradle                          |
| Dependency         | External library required by project                  |
| Gradle Wrapper     | Runs the project's required Gradle version            |
| `gradlew`          | Gradle Wrapper for Linux/macOS                        |
| `gradlew.bat`      | Gradle Wrapper for Windows                            |
| `build/`           | Gradle build output                                   |
| Build Cache        | Reuses previous build results where possible          |
| Incremental Build  | Executes only tasks whose inputs/outputs require work |

---

# 30. Most Important Interview Questions

## Q1. What is Maven?

> Maven is a build automation and dependency management tool primarily used in Java projects. It uses `pom.xml` to define project configuration, dependencies, plugins and build settings.

---

## Q2. What is POM?

> POM stands for Project Object Model. `pom.xml` is Maven's main configuration file containing project metadata, dependencies, plugins, build configuration and other settings.

---

## Q3. What is Maven Archetype?

> An archetype is a predefined project template used to generate the initial structure of a Maven project.

---

## Q4. What is the Maven lifecycle?

> Maven provides predefined lifecycles such as Clean, Default and Site. The Default lifecycle contains phases such as validate, compile, test, package, verify, install and deploy.

---

## Q5. What happens when you run `mvn package`?

```text
mvn package
    ↓
validate
    ↓
compile
    ↓
test
    ↓
package
    ↓
JAR/WAR
```

> Maven executes the required lifecycle phases up to the package phase and produces the configured artifact.

---

## Q6. What happens when you run `mvn install`?

> Maven builds the project through the required lifecycle phases and installs the generated artifact into the local Maven repository, usually `~/.m2/repository`.

---

## Q7. Difference between `mvn package` and `mvn install`?

```text
mvn package
    ↓
Creates JAR/WAR
    ↓
target/

mvn install
    ↓
Builds and packages
    ↓
Installs artifact
    ↓
~/.m2/repository
```

---

## Q8. What is the difference between Maven Central and Local Repository?

> Maven Central is a public repository containing many Java artifacts. The local repository is stored on the developer's machine and caches downloaded dependencies and locally installed artifacts.

---

## Q9. What is SNAPSHOT?

> SNAPSHOT indicates a development version that may change before a final release.

Example:

```text
1.0-SNAPSHOT
```

---

## Q10. What is a Maven plugin?

> A Maven plugin provides goals that perform specific operations such as compiling code, running tests, creating JAR/WAR files, or running Spring Boot applications.

---

## Q11. What is the difference between a lifecycle phase and a goal?

```text
Phase
 ↓
High-level lifecycle stage

Goal
 ↓
Specific plugin operation
```

Example:

```bash
mvn package
```

`package` is a lifecycle phase.

```bash
mvn compiler:compile
```

`compile` is a plugin goal.

---

## Q12. What is Gradle?

> Gradle is a flexible build automation tool that uses a task-based build system and supports Groovy and Kotlin DSLs. It is commonly used for Java, Kotlin, Android and other ecosystems.

---

## Q13. What is Gradle Wrapper?

> Gradle Wrapper allows a project to use a specific Gradle version without requiring developers to manually install that version globally.

---

## Q14. Maven vs Gradle?

> Maven is convention-driven and lifecycle-based, while Gradle is task-based and highly customizable. Maven uses XML configuration through `pom.xml`, whereas Gradle uses Groovy or Kotlin DSL. Gradle also provides strong incremental build and caching capabilities.

---

# 31. Common Exam Traps

### Trap 1

```text
mvn install = npm install
```

**Wrong.**

Correct:

```text
npm install
→ Install Node dependencies

mvn install
→ Build + install Maven artifact locally
```

---

### Trap 2

```text
Maven lifecycle = Maven goal
```

**Wrong.**

```text
Lifecycle Phase → Build stage
Goal            → Specific plugin task
```

---

### Trap 3

```text
pom.xml = Effective POM
```

**Wrong.**

```text
pom.xml
   ↓
Project configuration

Effective POM
   ↓
Final configuration after inheritance,
defaults, profiles, etc.
```

---

### Trap 4

```text
mvn package
```

only packages the application.

**Incomplete.**

It also executes the preceding required lifecycle phases.

```text
validate
compile
test
package
```

---

### Trap 5

```text
mvn install
```

means installing dependencies.

**Wrong.**

It installs the project's generated artifact into:

```text
~/.m2/repository
```

---

### Trap 6

```text
Gradle must always be installed globally.
```

**Wrong.**

Gradle Wrapper allows the project to download/use the required Gradle version.

```bash
./gradlew build
```

or Windows:

```bash
gradlew.bat build
```

---

### Trap 7

```text
java -jar app.jar
```

always works after packaging.

**Not necessarily.**

The JAR must be configured as an executable JAR with an appropriate entry point, such as:

```text
Main-Class
```

---

# 32. Final Exam Cheat Sheet

```text
MAVEN
│
├── Build Automation
├── Dependency Management
├── Mainly Java ecosystem
│
├── Configuration
│   └── pom.xml
│
├── Archetype
│   └── Project template
│
├── Coordinates
│   ├── groupId
│   ├── artifactId
│   └── version
│
├── Lifecycle
│   ├── Clean
│   ├── Default
│   └── Site
│
├── Default phases
│   ├── validate
│   ├── compile
│   ├── test
│   ├── package
│   ├── verify
│   ├── install
│   └── deploy
│
├── Repository
│   ├── Local
│   ├── Central
│   └── Remote
│
├── Packaging
│   ├── JAR
│   └── WAR
│
└── Important commands
    ├── mvn clean
    ├── mvn compile
    ├── mvn test
    ├── mvn package
    ├── mvn verify
    ├── mvn install
    └── mvn deploy
```

```text
GRADLE
│
├── Build Automation
├── Dependency Management
├── Multi-language support
│
├── Configuration
│   ├── build.gradle
│   └── build.gradle.kts
│
├── Build System
│   └── Task-based
│
├── Gradle Wrapper
│   ├── gradlew
│   └── gradlew.bat
│
├── Output
│   └── build/
│
└── Important commands
    ├── gradle clean
    ├── gradle compileJava
    ├── gradle test
    ├── gradle jar
    └── gradle build
```

---

# 33. One-Minute Revision

Before an interview/exam, remember these:

```text
Maven
  ↓
Java Build + Dependency Management

pom.xml
  ↓
Maven Configuration

Archetype
  ↓
Project Template

groupId
  ↓
Organization

artifactId
  ↓
Project/Artifact Name

version
  ↓
Artifact Version

SNAPSHOT
  ↓
Development

RELEASE
  ↓
Stable

package
  ↓
Create JAR/WAR

install
  ↓
Install Artifact → ~/.m2/repository

deploy
  ↓
Publish Artifact → Remote Repository

target/
  ↓
Maven Build Output

build/
  ↓
Gradle Build Output

Maven
  ↓
Lifecycle + XML + Convention

Gradle
  ↓
Tasks + Groovy/Kotlin + Flexibility

Gradle Wrapper
  ↓
Use Project-Specified Gradle Version
```

---

# 34. Best Interview Comparison

| Topic         | Maven                         | Gradle                                   |
| ------------- | ----------------------------- | ---------------------------------------- |
| Main purpose  | Build + dependency management | Build + dependency management            |
| Ecosystem     | Strong Java ecosystem         | Multi-language                           |
| Configuration | `pom.xml`                     | `build.gradle` / `build.gradle.kts`      |
| Language      | XML                           | Groovy/Kotlin                            |
| Build model   | Lifecycle-based               | Task-based                               |
| Flexibility   | More convention-driven        | Highly customizable                      |
| Performance   | Good                          | Strong incremental/caching capabilities  |
| Learning      | Easier initially              | More concepts but more flexibility       |
| Wrapper       | Available                     | Widely used                              |
| Common use    | Enterprise Java               | Java/Kotlin/Android and other ecosystems |

### Final Interview Statement

> Maven and Gradle are both build automation and dependency management tools. Maven is mature, convention-driven, and widely used in enterprise Java projects, while Gradle provides a flexible task-based build system with Groovy or Kotlin DSLs, incremental builds, and caching. The choice depends on the project's requirements and existing ecosystem rather than one being universally better.
