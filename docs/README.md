This is the README file for the SAIGE Java project, available through github.com:

    username: tom@parillume.com
    password: -_%(]

Sections in this README.md:
- PREPARATION
- COMMAND-LINE PROGRAMS
- WEB APPLICATION IN A BROWSER
- HTTPS SECURITY

=========== PREPARATION ========================================================

    (1)
    You must have Java installed on your machine.

    (2)
    You will need parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar.
    If you don't know how to generate this *.jar file using Java: 
        - In Google Drive > Parillume Software > SAIGE > SAIGE Program Resources, download the *.jar.

    Create a base directory on your machine - e.g. Documents/Parillume/SomeDir. We'll call this the [base] directory.
    Put the *.jar file in a [base]/target directory - e.g. Documents/Parillume/SomeDir/target.

=========== COMMAND-LINE PROGRAMS ==============================================

****** SHEET-CREATION PROGRAM ******
	
    ** PREPARATION **

    Create your employee spreadsheets by copying and editing WorksheetTemplate.xlsx (found in src/main/resources)
    Put these spreadsheets in a [base]/tmp directory - e.g. Documents/Parillume/SomeDir/tmp.
    If you are making team charts, put the client logo (and name it "client_logo.png") in this [base]/tmp directory.
	
    ** EXECUTING SHEET-CREATION PROGRAM **

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

    ** AFTER USING SHEET-CREATION PROGRAM **

    1.) Look in the [base]/tmp/ directory; there may be an errors.txt file to pay attention to.
    2.) Inspect the generated PDFs in the [base]/tmp/ directory: Look for names that are too long, logo position, text overflow...

****** PDF-to-PNG PROGRAM ******

To convert one-sheets to PNGs, so you can insert them in PowerPoints:

        Put the PNGs that you want to convert in the [base]/tmp/ directory.

	Open a terminal window (or a "command" window) and type:  cd [path/to/base directory] - e.g. cd Documents/Parillume/SomeDir

        Type:  java -cp target/parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.parillume.controller.ConversionController PDF PNG
        Hit Enter

    *** AFTER USING PDF-to-PNG PROGRAM **

    1.) Look in the [base]/tmp/ directory; there may be an errors.txt file to pay attention to.
    2.) Drag the generated PNG files into your PowerPoint.

****** SURVEY BAR GRAPH CREATION PROGRAM ******

    ** PREPARATION **

    The template_survey_bargraph.xlsx template (found in src/main/resources) is intended to generate bar graphs that can be inserted into a survey PowerPoint. 
    
    Copy this template into your [base]/tmp directory - e.g. Documents/Parillume/SomeDir/tmp
    Adjust the row values in this copied template to match the survey results.

    ** EXECUTING BAR GRAPH CREATION PROGRAM **

    Open a terminal window (or a "command" window) and type:  cd [path/to/base directory] - e.g. cd Documents/Parillume/SomeDir

    Type:  java -cp target/parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.parillume.controller.BarGraphController 
    Hit Enter

    *** AFTER USING BAR GRAPH CREATION PROGRAM **

    1.) Look in the [base]/tmp/ directory; there may be an errors.txt file to pay attention to.
    2.) Drag the generated bar graph images into your survey PowerPoint.

=========== WEB APPLICATION IN A BROWSER =======================================

The application must have access to a MySQL DB; see the spring.datasource.* parameters in saige/src/main/resources/application.properties file.

To start the application: 
    (1) Install Maven
    (2) On a command line, inside the saige/ directory, type:  mvn spring-boot:run
        
    OR

    In a Java IDE (e.g. NetBeans), run the ParillumeApplication.java class

To view the UI in a browser:
    Browse to https://localhost:8443/
    In the resulting login screen, enter info@parillume.com / _ (Lisa's standard password)
        * NOTE that these credentials depend on database entries, and may change
        * You can add these credentials to the database by calling the /seedParillume endpoint 

To call endpoints:
    The URL for the browser UI, and for all endpoints, is https://localhost:8443 + /[endpoint]
    Download Postman (https://www.postman.com/downloads); import the docs/Parillume.postman_collection.json into Postman; run the endpoints

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
