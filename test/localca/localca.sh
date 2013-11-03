
set -u 

cd

lscript=`basename $0 .sh`
cat $0 | grep ^command > tmp/$lscript.txt

cd tmp

pass=test1234

export CLASSPATH=/home/evans/NetBeansProjects/vellum/build/test/classes:/home/evans/NetBeansProjects/vellum/build/classes:/home/evans/NetBeansProjects/vellum/dist/lib/log4j-1.2.15.jar:/home/evans/NetBeansProjects/vellum/dist/lib/slf4j-log4j12-1.5.10.jar:/home/evans/tmp/netbeans-7.3.1/platform/modules/ext/junit-4.10.jar

keytool=~/jdk7/jre/bin/keytool

command0_clean() {
  rm -f *.jks
}

command1_initks() {
  alias=$1
  $keytool -keystore $alias.jks -storepass $pass -keypass $pass -alias $alias \
    -genkeypair -keyalg rsa -keysize 2048 -validity 999 -dname "CN=$alias" \
    -ext BC:critical=ca:false,pathlen:0 -ext KU:critical=digitalSignature
  $keytool -keystore $alias.jks -storepass $pass -alias $alias \
    -exportcert -rfc -file $alias.pem
  $keytool -keystore $alias.jks -storepass $pass -alias $alias \
    -certreq -file $alias.csr
  openssl x509 -text -in $alias.pem | grep "CN=\|CA:"
  openssl x509 -text -in $alias.pem | grep "X509v3" -A1
}

command1_initca() {
  alias=$1
  $keytool -keystore $alias.jks -storepass $pass -keypass $pass -alias $alias \
    -genkeypair -keyalg rsa -keysize 2048 -validity 999 -dname "CN=$alias" \
    -ext BC:critial=CA:false,pathlen:0 -ext KU:critical=decipherOnly
    # -ext BC:critial=ca:true,pathlen:0 -ext KU:critical=keyCertSign,cRLSign
  $keytool -keystore $alias.jks -storepass $pass -alias $alias \
    -exportcert -rfc -file $alias.pem
  openssl x509 -text -in $alias.pem | grep "CN=\|CA:"
  openssl x509 -text -in $alias.pem | grep "X509v3" -A1
}

command0_initks() {
  command1_initca ca
  command1_initks server
  command1_initks client
  cp client.jks client.server.jks
}

command0_trust() {
  $keytool -keystore server.trust.jks -storepass $pass -importcert -noprompt \
    -alias client -file client.pem
  $keytool -keystore client.trust.jks -storepass $pass -importcert -noprompt \
    -alias server -file server.pem
}

command_connect() {
  echo command_connect $@
  for ks in $@
  do
    echo "$ks" 
    $keytool -keystore $ks -storepass $pass -list | grep Entry
  done
  java localca.LocalCaMain $@ $pass
}

command0_connect() {
  #command_connect server.jks server.jks server.jks server.jks
  #command_connect ca.jks server.trust.jks client.jks ca.jks
  command_connect server.jks server.trust.jks client.jks client.trust.jks
}

command0_xconnect() {
  command_connect server.jks server.trust.jks client.server.jks client.trust.jks
}

command0_xsign() {
  $keytool -keystore server.jks -storepass $pass -keypass $pass -alias server \
    -gencert -infile client.csr -rfc -outfile client.server.pem \
    -validity 999 -dname "CN=client" && echo INFO server cert can sign client cert
  $keytool -keystore client.jks -storepass $pass -keypass $pass -alias client \
    -gencert -infile server.csr -rfc -outfile server.client.pem \
    -validity 999 -dname "CN=server" && echo INFO client cert can sign server cert
  $keytool -keystore client.server.jks -alias ca -file ca.pem \
    -storepass $pass -importcert -noprompt
  $keytool -keystore client.server.jks -alias server -file server.signed.pem \
    -storepass $pass -importcert -noprompt 
  $keytool -keystore client.server.jks -alias client -file client.server.pem \
    -storepass $pass -importcert -noprompt
}

command0_xtrust() {
  $keytool -keystore server.trust.jks -alias client.server -file client.server.pem \
     -storepass $pass -importcert -noprompt    
  echo "client.server.trust.jks"
  $keytool -keystore client.server.trust.jks -alias server -file server.signed.pem \
    -storepass $pass -importcert -noprompt
  $keytool -keystore server.trust.jks -alias client -file client.signed.pem \
    -storepass $pass -importcert -noprompt
}

command0_sign() {
  $keytool -keystore ca.jks -storepass $pass -keypass $pass -alias ca \
    -gencert -infile client.csr -rfc -outfile client.signed.pem \
    -validity 999 -dname "CN=client" \
    -ext BasicConstraints:critical=ca:false,pathlen:0 \
    -ext KeyUsage:critical=digitalSignature \
    -ext ExtendedKeyUsage:critical=clientAuth
  $keytool -keystore ca.jks -storepass $pass -keypass $pass -alias ca \
    -gencert -infile server.csr -rfc -outfile server.signed.pem \
    -validity 999 -dname "CN=server" \
    -ext BasicConstraints:critical=ca:false,pathlen:0 \
    -ext KeyUsage:critical=keyEncipherment \
    -ext ExtendedKeyUsage:critical=serverAuth
  openssl x509 -text -in client.signed.pem | grep "CN=\|CA:"
  openssl x509 -text -in client.signed.pem | grep "X509v3" -A1
  $keytool -keystore client.jks -storepass $pass -importcert -noprompt \
    -alias ca -file ca.pem 
  $keytool -keystore client.jks -storepass $pass -importcert -noprompt \
    -alias client -file client.signed.pem
  $keytool -keystore server.jks -storepass $pass -importcert -noprompt \
    -alias ca -file ca.pem 
  $keytool -keystore server.jks -storepass $pass -importcert -noprompt \
    -alias server -file server.signed.pem
  $keytool -keystore server.trust.jks -storepass $pass -importcert -noprompt \
    -alias ca -file ca.pem
  $keytool -keystore client.trust.jks -storepass $pass -importcert -noprompt \
    -alias ca -file ca.pem
  $keytool -keystore server.jks -storepass $pass -exportcert -rfc -alias server |
    openssl x509 -text | grep 'CN='
  $keytool -keystore client.jks -storepass $pass -exportcert -rfc -alias client |
    openssl x509 -text | grep 'CN='
}

command0_test() {
  command0_clean
  command0_initks
  command0_sign
  command0_connect
  command0_xsign
  command0_xconnect
}


if [ $# -gt 0 ]
then
  command=$1
  shift
  command$#_$command $@
else 
  cat $lscript.txt
fi



