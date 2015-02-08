#!/bin/bash

# Source https://github.com/evanxvellum by @evanxsummers

if ! echo $BASH | grep -q '/bin/bash' 
then
  echo "Please invoke this using bash"
  exit 1
fi

if ! which openssl 
then
  echo "Please install openssl"
  exit 1
fi

set -u 

cd

dir=.secret
enc=info.enc
txt=info.txt

if [ ! -d $dir ]
then
  echo "Creating $dir in $HOME"
  mkdir $dir
fi

cd $dir 

c0rm() {
  if [ -f $txt ]
  then
    if [ `stat -c %s $txt` -gt 0 ]
    then
      echo "Removing $txt"
    fi
    rm -f $txt
  fi
  pwd
  ls -l 
  cat $enc
}

trap c0rm EXIT

c0passwd() {
  echo -n "Password: "
  read -s passwd
  echo
}

c0enc() {
  openssl enc -aes-256-cbc -a -salt -in $txt -out $enc -k "$passwd"
}

c0dec() {
  openssl enc -d -aes-256-cbc -a -in $enc -out $txt -k "$passwd"
}

c0cat() {
  c0dec
  cat $txt
}

c0edit() {
  if [ -f $enc ] 
  then
    if ! c0dec 
    then
      echo "Decryption failed: incorrect password?"
      exit 1
    fi
  fi
  echo "Invoking $EDITOR $txt"
  "${EDITOR:-vi}" $txt
  c0enc
}

c0delete() {
  if [ -f $enc ]
  then
    cat $enc
    echo "Deleting $enc"
    rm -f $enc
  fi
}

if [ $# -gt 0 ]
then
  command=$1
  shift
  if [ $command != 'delete' ] 
  then
    c0passwd
  fi
  c$#$command $@
else
  c0passwd
  c0edit
fi
