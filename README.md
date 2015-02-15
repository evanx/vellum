
See the wiki homepage: https://github.com/evanx/vellum/wiki

At some stage, commmon utilities were copied to <a href="https://github.com/evanx/vellumcore">github.com/evanx/vellumcore</a>, as a dependency for other projects.

Also <a href="https://github.com/evanx/dualcontrol">dualcontrol</a> code was copied to <a href="https://github.com/evanx/dualcontrol">github.com/evanx/dualcontrol</a>. However it is possible that more recent changes to that code were made here, for the purposes of the <a href="https://github.com/evanx/vellum/wiki/DualControl">Dual Control</a> article.

On that subject, also see <a href="https://github.com/evanx/keyserver">github.com/evanx/keyserver</a> - a Node.js re-implementation of the Java <a href="https://github.com/evanx/dualcontrol">dualcontrol</a> package. It provides a secure "vault" server with client-authenticated HTTPS access. It uses Redis to store encrypted data, and the encryption keys. Encryption keys are protected by split-knowledge passwords, hashed with PDKDF2, and encrypted using AES.
