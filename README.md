# dos-wrapper
This application primarily acts as a proxy between a DoS client and the DoS system itself.
It only takes additional processing when handling a Check Capacity Summary Response, 
the SOAP response to a DoS SOAP request of CheckCapacitySummary.

When processing CheckCapacitySummaryResponse, the wrapper uses the service id of each 
ServiceCareSummaryDestination element in the response and calls the Capacity Service, requesting
Capacity Information for that specific Service Id.

If Capacity Information is returned then this information is formatted into plan text 
(albeit with carriage returns) and PREFIXED to the response's notes field.


This is a Spring Boot application, when running a Maven build, it also creates a zip file that includes
the Spring Boot application and a Docker file, which is a specific set of instructions describing the 
Docker image to be built.

## Getting started
First, download the code from GitHub. This can be done using the desktop git tool, an IDE which supports git or by downloading the code as a zip file which you can then extract.

Next, install the dev tools and dependencies....

##Installation of Development Tools and Dependencies
Install Git for Windows:
Install official git release: https://git-scm.com/download/win

Or install GitHub Desktop which also includes a GUI interface for git management: https://desktop.github.com/

###Install Java Development Kit 8:
http://www.oracle.com/technetwork/java/javase/downloads/

###Install Maven 3:
https://maven.apache.org/download.cgi

###Environment Variables
Ensure that the system environment variables for Java and Maven are set correctly, as described below...

M2_HOME should point to the install directory of your local Maven install folder, e.g.
M2_HOME C:\Maven\apache-maven-3.3.9

JAVA_HOME should point to the install directory of your local Java JDK install folder, e.g.

JAVA_HOME C:\Program Files\Java\jdk1.8.0_121

PATH should contain the bin directory of both M2_HOME and JAVA_HOME, e.g.

```
...;%JAVA_HOME%\bin;%M2_HOME%\bin;...
```

## Dependencies
Before building this module, the following modules must have been downloaded and built, using "mvn install"
to add them into your local Maven Repository.

1) a2si-capacity-information
2) a2si-capacity-service-client

## Use maven to build the project:

cd {projectRoot}
mvn clean install

the Maven "install" goal stores the built artifact into the local Maven repository, 
making it accessible to other projects using this repository.

The application is going to be deployed in AWS using Elastic Beanstalk, using Docker as a container. Elastic Beanstalk
allows Spring Boot applications to be packaged along with a DockerFile in a single zip file. This zip file is all
that is required to deploy into AWS Elastic Beanstalk. Environment variables may be required to define the 
Spring Profiles Active variable.

## Running the Application
The Dos Wrapper can connect to different DoS systems and different capacity services. This creates a 
large number of possible combinations of configuration, particularly when including that the 
capacity service might be running locally or in AWS, and might be using a Capacity Service that is 
remotely deployed or use a stubbed client that returns canned data, removing the need for a capacity
service to be running.
The configuration is set at run time using Spring Profiles. The current profiles are:

application.yml - default profile file, defines the standard port used (7030) and the 
default SOAP API URL suffix.

+ application-aws-dos-prod-cpsc-rest.yml - Deployed in AWS, Accesses Production DoS, Accesses Real Capacity Service
+ application-aws-dos-prod-cpsc-stub.yml - Deployed in AWS, Accesses Production DoS, Uses Stubbed Capacity Service Client
+ application-aws-dos-stub-cpsc-rest.yml - Deployed in AWS, Uses DoS Stub Client, Accesses Real Capacity Service
+ application-aws-dos-stub-cpsc-stub.yml - Deployed in AWS, Uses DoS Stub Client, Uses Stubbed Capacity Service Client
+ application-aws-dos-uat-cpsc-rest.yml - Deployed in AWS, Accesses UAT DoS, Accesses Real Capacity Service
+ application-aws-dos-uat-cpsc-stub.yml - Deployed in AWS, Accesses UAT DoS, Uses Stubbed Capacity Service Client
+ application-local-dos-stub-cpsc-rest.yml - Deployed in Locally, Uses DoS Stub Client, Accesses Real Capacity Service
+ application-local-dos-stub-cpsc-stub.yml - Deployed in Locally, Uses DoS Stub Client, Uses Stubbed Capacity Service Client
+ application-local-dos-uat-cpsc-rest.yml - Deployed in Locally, Accesses UAT DoS, Accesses Real Capacity Service
+ application-local-dos-uat-cpsc-stub.yml - Deployed in Locally, Accesses UAT DoS, Uses Stubbed Capacity Service Client
  

## Maintaining the Application
The main focus of the DoS Wrapper must always be to remain compatible with the DoS system, it should always be possible
to point a DoS Client to the DoS Wrapper without any change other than the URL the DoS Client is using.
It is therefore important to possess a wsdl file that accurately reflects the DoS API itself. The build process for
the DoS Wrapper uses WSDL to generate client code

```
Note that the existing DoS System is NOT fully SOAP compliant because errors (faults on SOAP parlance) 
do not follow SOAP standards. The DoS Wrapper however is SOAP compliant so when DoS throws an error, 
an interceptor is used to tweak the SOAP Fault so that no invalid values are used that would cause the 
DoS Wrapper to fail when validating the DoS response
```  

## Application Configuration
Following a best practice approach that comes from Spring, configuration files hold data specific to 
environments whilst wiring of class dependencies is done via Java Configuration classes. These classes
use the configuration files to set simple properties and are created when the application starts.

## Using the Application
When running the application, two environment variables must be provided on the application startup 
to define the Capacity Service API Client's username and password so calls to the Capacity Service
are correctly authenticated.

Also, each call expects http headers defining the api username and password to use. Again, HTTP headers are used 
and the header names are:
```
capacity.service.api.username
capacity.service.api.password
```
They are required for all calls.
The application configuration files have the usernames and password to use, and the values can be set on 
a per profile basis.

The API endpoint depends on the deployment location but after the host address and port (7020), a URL of ```/capacity``` 
is used for all requests.

The Git repository has several collections of calls for use using Postman (https://www.getpostman.com), each collection 
is targeted to one environment and profile, e.g. Local, Local Docker machine, AWS etc. 
The maintenance of these collections as the APIs change or environments change is highly recommended, using the API 
with Postman is one of the best ways to learn about and test the Service.
