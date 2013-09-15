#Architecture
Notes about the system, could be useful to read

##Overall
There's a self-contained webapp written in AngularJS that speaks to the server through AJAX, sending JSON to the REST API.


##Client
All the files for the client resides in `src/main/webapp`.
`app` is the folder containing the app.
`config` is a folder containing configs for running tests.
`test` is a folder containing the tests.
`WEB-INF` is used by the server.

All the webapp-files are served directly by the server without intervention. E.g. it's not like serving a PHP file where one dynamically change the content. These files are completely static. All the dynamic parts (editing and saving stuff) is done through AJAX calls.

###Layout
Controllers, services etc. has their own folders. To add a new, it should be included in `index.html` in the correct spot.
In `js/app.js` the app is started and dependencies and routes set.

###Wysihtml5
Is the rich text editor being used. Chosen because it has good support for specifying exactly which tags should be allowed, and we only need a selected few. Other rich editors contain options that we won't support.

##Server

###Datasource
By default it uses HSQLDB, which is an in-memory-db and needs no installation (maven fetches it as a dependency). HSQLDB will create a folder named `database` that it saves everything into. Deleting this folder will clear the DB.
However, mysql can also be used by un-commenting the lines in momus.properties. Using mysql allows you to browse the data while the server is running.

####Fill inn dummy data
Go to `/api/dev/create` to create some dummy users to be able to log in.

###Dev login
Go to `/api/dev/login/{id}` to hack yourself in as the user of your choice.

###Configuration
Location and description of various configuration files. Except for momus.properties, these files should be identical on both dev and production.
Most of the files mentioned here should have useful comments in them.

###pom.xml
Contains all the dependencies for the project, along with how it should be run.

Files in `src/main/webapp/WEB-INF`:

####momus.properties
Various values that can be used inside the different xml-files or be assigned to java variables.
All values that can/should be different between production and development, between different developers etc. should be put here so they are easy to locate and change. To ignore local changes to this file, run `git update-index --assume-unchanged src/main/webapp/WEB-INF/momus.properties` To redo use the flag `--no-assume-unchanged`.
Or possibly use `--skip-worktree` instead.

####web.xml
Definition of our server, creates the "momusapi"-servlet that will listen for requests.

####applicationContext.xml
Configuration of most of the back-end.

####momusapi-servlet.xml
Special rules for this servlet. Since we only have one servlet these rules may as well be stated in the app context.

Files in `src/main/resources`:

####logback.xml
Configuration for our Logger.

####META-INF/persistence.xml
Configuration for persistence, e.g. how entities should be saved to the database.

###Code
Our Java code is in the package `no.dusken.momus`

In `model` we have classes annotated with `@Entity`, which means that objects of that class should be stored in the DB. Classes are also related to each other, for instance `@ManyToOne`. Also specify if the relation is lazy or not. Lazy means it's only retrieved from the DB when needed. Eager means it's fetched right away. See the code for examples.

In `service.repository` repositories for the different models/entities are specified. Just by defining an interface Spring will do most of the work for us on how to store these objects, so we normally don't need to write any SQL or similar.
Empty interfaces will get a few standard methods, but we can also add our own. For instance, in `ArticleRevisionRepository` we define some additional methods on how to to retrieve `ArticleRevisions`. Because of the way it's written, Spring can automagically determine how it should fetch the objects, so we don't need to write any SQL even for our special needs!
To use these classes, just ask for them anywhere Spring-managed with the `@Autowired` annotation, see the controllers for examples.

In `controller` we have our controllers. That is, the URLs a user can access and how we should respond to them. A class is annotated with `@RequestMapping(...)` which specifies which url the class should be used for. Can specify additional mappings for each method, and also specify the type of the request (GET, PUT, POST, DELETE).
Methods that a user should be able to call should have the `@ResponseBody` annotation. This tells Spring that the method is used to return stuff to the user.

In `mapper` is our `HibernateAwareObjectMapper`. There shouldn't be any need to change stuff there.
But the mapper is an important part of our system. When returning normal Java objects (entities) from our controller-methods with `@ResponseBody`, Jackson (the mapper) will convert those to JSON!

In `exceptions` there are two classes. First is our `ExceptionHandler` that takes care of every exception in our system.
The second class is our custom exception. By throwing this anywhere we can specify the return code (401, 404, 500 etc.) and message to the user.

###Security/authentication
Smmdb is a new project in Studentmediene that should provide single-logon for all our systems. So to login to our system, the user will login at Smmdb (or already be logged in there) and get a "token" that we can use to verify if the user is really logged in and who they claim to be.
If the token is valid, we log in the user and assign  different roles to it (all the groups it belongs to).
Then we can inject the `UserLoginService` to get various information about the logged in user.
We can annotate methods with `@PreAuthorize` to restrict access for different users. See `DevController` for an example.

