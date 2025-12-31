################################# IMPORTANT ###################################
# Execute this on the same directory level as the /tmp and/target directories #
###############################################################################
#!/bin/sh
BASEDIR=$(dirname $0)
java -jar "$BASEDIR"/target/parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar  $1
#java -jar "./target/parillume-0.0.1-SNAPSHOT-jar-with-dependencies.jar"