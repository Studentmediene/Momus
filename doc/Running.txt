INSTRUCTIONS
=========================

##Dependencies:
 * NodeJS need to be installed, and runnable from the command line
  * Check this by running 'node --version'
 * Install Testacular
  * 'npm install -g testacular'



##To run client:
At the moment, we just use nodeJS to run a simple server serving the files
 * Open command line and go to the *client* folder
 * Run 'node .\scripts\web-server.js'


##To run unit tests:
Run 'test.bat' (Windows) or 'test.sh' (Linux) in the client\scripts folder.
A browser window will open, just leave it in the background.
Whenever a file is edited, Testacular will notice and rerun the tests.
Results are displayed in the command line.

##To run end-to-end tests:
 * Option 1:
  Run 'e2e-test.bat' (Windows) or 'e2e-test.sh' (Linux) in the client\scripts folder.
  A browser window will appear, test the application and then close.
  Results are displayed in the command line.
 * Option 2 (preferred):
  Open a tab in your browser, and go to /test/e2e/runner.html instead of /app
  You will see the app being tested, and can see the results with GUI


Test-configs are in client/config

