
set -u

cd

CLASSPATH=NetBeansProjects/vellum/build/classes
LIBPATH=NetBeansProjects/lib/vellum
CLASSPATH=$CLASSPATH:NetBeansProjects/vellum/build/test/classes
CLASSPATH=$CLASSPATH:$LIBPATH/log4j-1.2.15.jar
CLASSPATH=$CLASSPATH:$LIBPATH/commons-codec-1.7.jar
CLASSPATH=$CLASSPATH:$LIBPATH/slf4j-log4j12-1.5.10.jar
CLASSPATH=$CLASSPATH:$LIBPATH/slf4j-api-1.5.10.jar

export CLASSPATH

keystore=tmp/test.jks
storetype=JKS
pass=test1234
alias=test

c2test() {
  rm -f $keystore
  keytool -keystore $keystore -storetype $storetype -storepass $pass -alias $alias
  java dualcontrol.KeyStoreBruteForceTimer \
    $1 $2 $keystore $storetype $pass $alias $keypass
}

if [ $# -gt 0 ]
then
  command=$1
  shift
  c$#$command $@
else
  grep '^c[0-9]' $0 | sed 's/c\([0-9]\)\([a-z]*\).*/\1 \2/'
fi
