This Java project is available through github.com:

    username: tom@parillume.com
    password: -_%(]

Sections in this README.md:
- HTTPS SECURITY
- COMMAND-LINE PROGRAMS
- WEB APPLICATION IN A BROWSER

=========== HTTPS SECURITY =====================================================
The src/main/resources/parillume-keystore.jks was generated on 10/28/2024 on the command line:

$ keytool -genkey -alias your-alias -keyalg RSA -keysize 2048 -keystore parillume-keystore.jks -validity 7300
Enter keystore password:  LSFAbundance
Re-enter new password: LSFAbundance
What is your first and last name?
  [Unknown]:  Thomas Margolis
What is the name of your organizational unit?
  [Unknown]:  Parillume
What is the name of your organization?
  [Unknown]:  Parillume
What is the name of your City or Locality?
  [Unknown]:  Boulder
What is the name of your State or Province?
  [Unknown]:  Colorado
What is the two-letter country code for this unit?
  [Unknown]:  US
Is CN=Thomas Margolis, OU=Parillume, O=Parillume, L=Boulder, ST=Colorado, C=US correct?
  [no]:  yes

Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 7,300 days
        for: CN=Thomas Margolis, OU=Parillume, O=Parillume, L=Boulder, ST=Colorado, C=US

=========== COMMAND-LINE PROGRAMS ==============================================
****** BEFORE USING COMMAND-LINE PROGRAMS ******
    In Google Drive, download the files found in Parillume Software > SAIGE > SAIGE Program Resources
    NOTE: If files are not available, ask Tom to "give me a jar file and the program templates"

    Create a base directory on your machine - e.g. Documents/Parillume/SomeDir
    Put parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar in a [base]/target directory - e.g. Documents/Parillume/SomeDir/target.

****** SHEET-CREATION PROGRAM ******	
    ****** PREPARATION ******
    Create your employee spreadsheets based on WorksheetTemplate.xlsx
    Put these spreadsheets in a [base]/tmp directory - e.g. Documents/Parillume/SomeDir/tmp.
    If you are making team charts, put the client logo (named "client_logo.png") in this [base]/tmp directory.
	
    ****** EXECUTING SHEET-CREATION PROGRAM ******
    Open a terminal window (or a "command" window) and type:  cd [path/to/base directory] - e.g. cd Documents/Parillume/SomeDir

    To create one-sheets:
        Type:  java -jar target/parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar
        Hit Enter

    To create multi-sheets:
        Type:  java -jar target/parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar process=multisheets
        Hit Enter

    To create team charts with NO team name:
        Type:  java -jar target/parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar process=teamcharts
        Hit Enter
    To create team charts with a team name:
        Type:  java -jar target/parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar process=teamcharts teamname='Some Team Name'
        Hit Enter

    ******* AFTER USING SHEET-CREATION PROGRAM ******
    1.) Look in the [base]/tmp/ directory; there may be an errors.txt file to pay attention to.
    2.) Inspect the generated PDFs in the [base]/tmp/ directory: Look for names that are too long, logo position, text overflow...

****** SURVEY BAR GRAPH CREATION PROGRAM ******
    ****** PREPARATION ******
    The template_survey_bargraph.xlsx template is intended to generate bar graphs that can be inserted into a survey PowerPoint. 
    
    Copy this template into your [base]/tmp directory - e.g. Documents/Parillume/SomeDir/tmp
    Adjust the row values in this copied template to match the survey results.

    ****** EXECUTING BAR GRAPH CREATION PROGRAM ******
    Open a terminal window (or a "command" window) and type:  cd [path/to/base directory] - e.g. cd Documents/Parillume/SomeDir

    Type:  java -cp target/parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.parillume.print.bargraph.BarGraphController 
    Hit Enter

    ******* AFTER USING BAR GRAPH CREATION PROGRAM ******
    1.) Look in the [base]/tmp/ directory; there may be an errors.txt file to pay attention to.
    2.) Drag the generated bar graph images into your survey PowerPoint

=========== WEB APPLICATION IN A BROWSER =======================================
Download the project from github.com onto your machine; this will create a saige/ directory.
    - On your machine, on a command line, execute: https://github.com/tomatparillume/saige.git
    - This will download the github project into a saige/ directory

The application must have access to a MySQL DB; see the spring.datasource.* parameters in saige/src/main/resources/application.properties file.

To start the application: 
    In a terminal (command window), inside the saige/ directory, type:  mvn spring-boot:run
        ('mvn' runs a maven command; if you don't have maven installed, get help to install it!)

    OR

    In a Java IDE (e.g. NetBeans), run the ParillumeApplication.java class

To view the UI in a browser:
    Browse to https://localhost:8443/
    In the resulting login screen, enter info@parillume.com / _ (Lisa's standard password)
        * NOTE that these credentials depend on database entries, and may change
        * You can also add these credentials to the database by calling the /seedParillume endpoint 

To call endpoints:
    The URL for the browser UI, and for all endpoints, is https://localhost:8443 + /[endpoint]
    Download Postman (https://www.postman.com/downloads); import the docs/Parillume.postman_collection.json into Postman; run the endpoints