#Getting started
Tools used and how to get everything running and tested.

##Dependencies:
All this must be installed

* [NodeJS](http://nodejs.org/), needs to be runnable from the command line
    * Check this by running `node --version`
* Install Testacular (through NodeJS)
    * `npm install -g testacular`
* [Maven 3](http://maven.apache.org/), should also be runnable from command line
    * Check by entering `mvn --version` in the command line
    * Maven handles all the Java dependencies (Spring Framework, JUnit etc.)
* [Git](http://git-scm.com/)
    * Must be configured with GitHub
    * The workflow will be like [this](http://nvie.com/posts/a-successful-git-branching-model/)

##Get the code
By grabbing it from Git. Do
`git clone git@github.com:mediastud/Momus.git`
where you want it to be.


##Running
See the IntelliJ page on how to do this in the IDE with Hot Swap for easier/faster coding (recommended!).

Otherwise, go to the root folder of the project (the one with pom.xml) and run
`mvn jetty:run`

For both:
Go to http://localhost:8080/ to see it in action.


##Running tests
###Unit Tests
####Server
Run `mvn test` from the root folder. Or set up a run configuration.

####Client
Run `testacular start testacular.conf.js` from `\src\main\webapp\config` folder.
(Or create a run configuration, see IntelliJ page)

Testacular works by opening a browser and executing JS in it to test the units.
It will listen to changes in the files and rerun every time a relevant file is edited.
Configure it by editing the `testacular.conf.js`, for instance the browser used can be changed and auto-watching of files can be turned on/off.

###End-to-end Tests
Tests that the functionality of everything works as expected. In the browser you can see the web page flashing fast because the testing utility is pressing buttons and stuff.

To run, make sure the server is running. Then navigate to `http://localhost:8080/test/e2e/runner.html` in your browser of choice. That's it.

You can also run it in the same way as the unit tests, by running `testacular start testacular-e2e.conf.js` from `\src\main\webapp\config`.