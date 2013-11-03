

set -u

cd

dualcontrol="com.innoforge.bizswitch.security.dualcontrol"

PATH=$PATH:/usr/java/default/bin

CLASSPATH=lib/bizswitch.jar:lib/log4j-1.2.15.jar:tmp/dualcontrol/test/classes/

pass=test1234

command2_javaks() {
  keystore=tmp/$1.jks
  keystore=tmp/$1.trust.jks
  shift
  java -cp $CLASSPATH \
    -Ddualcontrol.ssl.keyStore=$keystore \
    -Ddualcontrol.ssl.keyStorePassword=$pass \
    -Ddualcontrol.ssl.keyPassword=$pass \
    -Ddualcontrol.ssl.trustStore=$truststore \
    -Ddualcontrol.ssl.trustStorePassword=$pass \
    -Ddualcontrol.verifyPassphrase=false \
    $2
  exitCode=$?
  if [ $exitCode -ne 0 ]
  then
    echo WARN javaks $keystore exitCode $exitCode $@
    exit 1
  fi
}

command1_console() {
  command2_javaks $1 $dualcontrol.DualControlConsole
}

command1_initkeystore() {
  dname="CN=$1, OU=test"
  keystore=tmp/$1.jks
  truststore=tmp/$1.trust.jks
  servercert=tmp/server.pem
  clientcert=tmp/$1.pem
  rm -f $keystore
  rm -f $truststore
  keytool -keystore $keystore -storepass "$pass" -alias $1 -keypass "$pass" -genkeypair -dname "$dname"
  keytool -keystore $keystore -storepass "$pass" -alias $1 -exportcert -rfc > $clientcert
  keytool -keystore $truststore -storepass "$pass" -alias "dualcontrol" -importcert -noprompt -file $servercert
  keytool -keystore $truststore -storepass "$pass" -list | grep Entry
}

if [ $# -gt 0 ]
then
  command=$1
  shift
  command$#_$command $@
else
  grep ^command $0 | sed 's/command\([0-9]\)_\([a-z]*\).*/\1 \2/'
fi
