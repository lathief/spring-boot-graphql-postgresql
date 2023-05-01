# Spring Boot + GraphQL + PostgreSQL example

GraphQL is a query language for your API, and a server-side runtime for executing queries using a type system you define for your data. GraphQL isn't tied to any specific database or storage engine and is instead backed by your existing code and data.

A GraphQL service is created by defining types and fields on those types, then providing functions for each field on each type.

Official documentation: [GraphQL](https://graphql.org/)

Official Spring Boot documentation: [Spring Boot GraphQL](https://spring.io/projects/spring-graphql)

## Requirements

1. Java - 11.x
2. Maven
3. JPA
4. PostgreSQL

## Usage
**1. Clone the application**

```bash
git clone https://github.com/lathief/spring-boot-graphql-postgresql.git
```

**2. Build and run the app using maven**

```bash
cd spring-boot-graphql-postgresql
mvn package
java -jar target/graphql-demo-0.0.1-SNAPSHOT.jar
```

Alternatively, you can run the app directly without packaging it like so -

```bash
mvn spring-boot:run
```
Alternatively, if you have IDE head out to [http://localhost:8080/graphql](http://localhost:8080/graphql)

### Query
- `get all book`
```
query {
    books{
        title
        description
        genres{
            name
        }
    }
}
```
- `get book by id`
```
query {
    book(id:1){
        title
        description
        genres{
            name
        }
    }
}
```
### Mutation
- `insert new book`
```
mutation{
    newBook(book:{
        title:"Title Book"
        description:"Description Book"
        isbn:"0001"
        authorId:1
        publisherId:2
    }) {
        title
        description
        isbn
    }
}
```
- `add genre to book by id`
```
mutation{
    addGenreToBook(
        bookid:1,
        genres:[{
            name:"Fiction"
        }]){
        title
        description
        genres{
            name
        }
    }
}
```