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
5. Copy config.properties.example as config.properties and add your database config there
6. Add following line to ~/.bashrc
```
export CLASSPATH=$CLASSPATH:[z0rbot location here]/z0rbot/jars/mysql-connector-java-3.0.9-stable-bin.jar
```
7. Compile
```
cd server
javac Main.java
```
8. Run
```
java Main
```

# Error handling

- Error
```
Connection failed: java.sql.SQLException: Server connection failure during transaction. Due to underlying exception: 'java.sql.SQLException: Data source rejected establishment of connection,  message from server: "Client does not support authentication protocol requested by server; consider upgrading MySQL client"'.
```
- Solution
```
mysql> ALTER USER '[user]'@'localhost' IDENTIFIED WITH mysql_native_password BY 'your_new_password';
mysql> FLUSH PRIVILEGES;
mysql> quit
```