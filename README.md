## Taboleiro.

Taboleiro is a school management and communication web application. 

This was an university project, any feedback will be appreciated and I will improve the project gradually.

### Requirements

To add flexibility during developement the application is packed with Docker.

If you don't want to run with Docker you will need to install: MySQL 5.7 or later, Apache Maven 3 and Java SE Runtime Enviroment 8.

### Usage & installation

If you have Docker installed you can run the application and migrate the database with the following commands:

Compile and run the application:
```
make run
```

Import the database:
```
make migratedb
```

Launch tests
```
make test
```

**The example users are:**

Teacher:
```
login name: hemingway
password: viejo
```

Family:
```
login name: picasso
password: guernica
```

Admin:
```
login name: newton
password: gravitacion
```
### Demo

Demo video: [demo_taboleiro](https://vimeo.com/153252314)

### License

Taboleiro is under [GNU GENERAL PUBLIC LICENSE](http://www.gnu.org/licenses/gpl-3.0.en.html).
