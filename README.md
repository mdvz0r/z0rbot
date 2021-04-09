# z0rbot
**unsupported ancient game statistics and match organiser bot**

Server collects quake2, quake3 and halflife player data and stores it in a database.

Client connects to IRC and is able to provide player statistics, find active players and organise matches.

Web environment; not published.

# install guide - Ubuntu 20.04

1. install openjdk 8
```
sudo apt-get update
sudo apt-get install openjdk-8-jdk
```
2. install mysql: [Digital ocean guide](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-20-04)
3. Create database
```
CREATE DATABASE z0rbot;
GRANT ALL PRIVILEGES ON z0rbot.* TO '[user]'@'localhost';
```
4. Build database from dump
```
mysql -u [user] -p z0rbot < db.sql
```
5. Add following line to ~/.bashrc
```
export CLASSPATH=$CLASSPATH:[z0rbot location here]/z0rbot/jars/mysql-connector-java-3.0.9-stable-bin.jar
```
6. Compile
```
cd server
javac Main.java
```
7. Run
```
java Main
```