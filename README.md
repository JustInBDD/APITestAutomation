# APITestAutomation

### FeatureFiles Location 
src/test/java/org/auto/test/features/*.feature



### StepDefs Location 
src/test/java/org/auto/test/step_defs/



### Maven Build file
pom.xml



### SmokeSuiteRunner File
src/test/java/org/auto/test/SmokeSuiteRunner.java



### Note
Please note the values set for attributes glue and feature in cucumberoptions of the runnerclass are on the assumption that maven compile and build will be executed before executing the runner class.

If not please change the paths in runner class accordingly.



### Delay in response time
As the response time of the api was exceeding 200ms very often , I have configured it to 300ms for development purposes.

You can configure it parameters.json file.



### Auth Token
Auth token for POST/PATCH/DELETE actions is placed in parameters.json file.

