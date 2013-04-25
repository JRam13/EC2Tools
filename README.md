EC2Tools
========

AWS EC2 Tools for Instances

Instruction for Part B of Assignment

1 - Create an executable Java jar file of your program (address book)

2 - open terminal

3 - transfer jar file from local to your EC2 Host server (use scp).

4 - type in the terminal:

$ scp -i /keyFilePath.pem JarFilePath ec2Server@ec2:~/.

This command transfer the jar file from your local drive to the host server.

5 - run the jar file from the server.

6 - run: $ java -jar JarFile.jar

7 - Done
