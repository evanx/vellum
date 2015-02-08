cd
mkdir -p ~/scripts
cd ~/scripts && 
  [ ! -f edit-secret.sh ] &&
  curl -s -O https://raw.githubusercontent.com/evanx/vellum/master/src/scripts/edit-secret.sh &&
  chmod +x edit-secret.sh &&
  cat edit-secret.sh &&
  ls -l edit-secret.sh 
