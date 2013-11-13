
set -u

cd

CLASSPATH=NetBeansProjects/vellumgit/build/classes
LIBPATH=NetBeansProjects/lib/vellum
CLASSPATH=$CLASSPATH:NetBeansProjects/vellumgit/build/test/classes
CLASSPATH=$CLASSPATH:$LIBPATH/log4j-1.2.15.jar
CLASSPATH=$CLASSPATH:$LIBPATH/commons-codec-1.7.jar
CLASSPATH=$CLASSPATH:$LIBPATH/slf4j-log4j12-1.5.10.jar
CLASSPATH=$CLASSPATH:$LIBPATH/slf4j-api-1.5.10.jar

export CLASSPATH

bin=jdk7/jre/bin

pass=test1234
alias=test

c3test() {
  storetype=$1
  keystore=tmp/test.$storetype
  rm -f $keystore
  $bin/keytool -keystore $keystore -storetype $storetype -storepass $pass \
    -genkeypair -alias $alias -keypass $pass -dname "CN=$alias"
  $bin/java dualcontrol.KeyStoreBruteForceTimer \
    $2 $3 $keystore $storetype $pass $alias $pass
}

if [ $# -gt 0 ]
then
  command=$1
  shift
  c$#$command $@
else
  grep '^c[0-9]' $0 | sed 's/c\([0-9]\)\([a-z]*\).*/\1 \2/'
fi
