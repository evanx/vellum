

set -u 

cd 

port=4444
keystore=tmp/dualtest.jceks
storepass=test1234
salt=abcdef22

encrypt() {
  openssl enc -des-cbc -k "$storepass" -a -S $salt
}

decrypt() {
  openssl enc -d -des-cbc -k "$storepass" -a -S $salt
}

sendpass2() {
  sleep 1 
  echo "testpassword1" | encrypt | nc localhost $port
  sleep 1
  echo "testpassword2" | encrypt | nc localhost $port
}

getpass() {
  nc -l localhost $port | decrypt
}

command0_default() {
  echo "encryption ok" | encrypt | decrypt 
  sendpass2 & pass=`getpass "password 1?"`-`getpass "password 2?"`
  echo "getpass $pass"
  rm -f $keystore
  keytool -keystore $keystore -storepass $storepass -storetype JCEKS \
    -alias dek2013 -keypass "$pass" \
    -genseckey -keyalg DESede
  keytool -keystore $keystore -storepass $storepass -storetype JCEKS -list | grep Entry
}

if [ $# -gt 0 ]
then 
  command=$1
  shift
  command$#_$command $@
else
  command0_default
fi
