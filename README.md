# NoProblem - HTTP APIs error messages made easy
[![](https://jitpack.io/v/cyclonesword/noproblem.svg)](https://jitpack.io/#cyclonesword/noproblem) 
[![](https://badgen.net/badge/java/8+/green)](https://jitpack.io/#cyclonesword/noproblem)
[![](https://badgen.net/badge/kotlin/1.3+/green)](https://jitpack.io/#cyclonesword/noproblem)
[![](https://badgen.net/badge/license/Apache%202.0/blue)](https://github.com/cyclonesword/noproblem/blob/master/README.md)
<br>
Handle your API endpoints errors in  a ****JSON**  Object** using the **[RFC 7807](https://tools.ietf.org/html/rfc7807)** standard format.


## Requirements

* Java 8+ 

OR

* Kotlin 1.3+ (with jvmTarget >= 1.8)


## Installation instructions

#### Gradle
First you have to add the jitpack repository to your global build.gradle file:
``` groovy
allprojects {
    repositories {
         maven { url 'https://jitpack.io' }
    }
}
```


Then, add the dependency to your project-local build.gradle :
``` groovy
implementation 'org.javando.http:no-problem-api:1.0.RC6'
/* Gson provider or another of your preference */
 implementation 'org.javando.http:gson-provider:1.0.RC6'
```
#### Maven
First you have to add the jitpack repository to your pom.xml file:
``` xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Then add the dependency inside the `<dependencies>` tag:
``` xml
<dependency>
	<groupId>org.javando.http</groupId>
	<artifactId>no-problem-api</artifactId>
	<version>1.0.RC6</version>
</dependency>  
  
<!-- Gson provider or another of your preference -->  
<dependency>
	<groupId>org.javando.http</groupId>
	<artifactId>gson-provider</artifactId>
	<version>1.0.RC6</version>
</dependency>
```

## Basic Usage

#### - Obtain an instance of `JsonProvider`:
``` kotlin
// If you have chosen 'noproblem-gson-provider': 
val provider = GsonProvider()  
```

#### - Then create your Problem:
``` kotlin
val problem = Problem.wither(provider)  
    .withType(URI("https://www.myapi.com/errors/insufficient-credit.html"))
    .withInstance(URI("/perform-transaction"))
    .withTitle("Insufficient Credit")
    .withDetails("There's no sufficient credit in the account for the requested transaction")
    .withStatus(HttpStatus.FORBIDDEN)
    .addExtension("account_number", 7699123)
    .build()  
```

#### - Finally get your json string:
``` kotlin
val jsonString = problem.toJson()
 ```
 
 The output will be:
 
 ``` json
 {
	"type":"https://www.myapi.com/errors/insufficient-credit.html",
	"title":"Insufficient Credit",
	"details":"There's no sufficient credit in the account for the requested transaction",
	"status":401,
	"instance":"/perform-transaction",
	"account_number":7699123
  }
 ```
 
## Customizing the JsonProvider

### -Handling dates
If you have date values in your json object, you can customize the default behaviour by setting a custom **date format** and a custom **date identifier** when obtaining the `JsonProvider` instance:
``` kotlin
// If you have chosen 'noproblem-gson-provider': 
val provider = GsonProvider()
	.setDateFormat("dd/MM/yyyy hh:mm:ss")  
	.setDateIdentifier("date")  
```
When deserializing a json string, if there is a property with **'date' as substring**, the provider will try to deserialize it as a `JsonDate` instead of a `JsonString`. If the string is not compatible with the date format, it will be deserialized as a `JsonString`.

The default date identifier is '**date**', and the default date format is **dd/MM/yyyy hh:mm:ss** 

### -Registering a custom extension class 
If you want to add a custom class as an extension member, [as specified in the RFC 7807](https://tools.ietf.org/html/rfc7807#page-6) , you can do as follows:
``` kotlin
// Suppose we have a custom class that we want to be deserielized 
// togheter with the Problm object:
data class CreditInfo(val balance: Float, val currency: String)

// Then by registering it in your provider, it will be serialized with the 
// property name  'credit_info' and deserialized as 
// a CreditInfo class instead of a generic JsonValue instance.
val provider = GsonProvider()
	.registerExtensionClass("credit_info", CreditInfo::class.java) 
```

## Adding additional data to the payload

### - Extension members
Extension members, [as defined in the  RFC 7807](https://tools.ietf.org/html/rfc7807#page-6) , are custom json properties that may be used to carry additional information to the API client: 
``` kotlin
val creditInfo = CreditInfo(34.5f, "EUR")

val p = Problem.wither(provider)  
    .withType(URI("https://www.myapi.com/errors/insufficient-credit.html"))  
    .withInstance(URI("/perform-transaction"))  
    .withTitle("Insufficient Credit")  
    .withDetails("There's no sufficient credit in the account for the requested transaction")  
    .withStatus(HttpStatus.FORBIDDEN)  
    .addExtension("account_number", 7699123)  
    .addExtension("transaction_id", "f23a7600ffd6")  
    .addExtension("transaction_date", Date())  
    .addExtension("credit_info", CreditInfo(34.5f, "EUR"))  
    .build()
```
The output will be:
``` json
{
   "type":"https://www.myapi.com/errors/insufficient-credit.html",
   "title":"Insufficient Credit",
   "details":"There's no sufficient credit in the account for the requested transaction",
   "status":401,
   "instance":"/perform-transaction",
   "account_number":7699123,
   "transaction_id":"f23a7600ffd6",
   "transaction_date":"13/01/2021 05:52:20",
   "credit_info":{
      "balance":34.5,
      "currency":"EUR"
   }
}
```

### Adding exception and stacktrace as extension members

Althout it is not advisable to add the stacktrace of an exception as a member extensions, it
can be helpful when debugging a system. It should be avoided in production since exception messages and stacktraces ***contains information on the implementation's internals and therefore can expose your system to security threats***. ***Use at your own risk.***

As stated in the [RFC 7807](https://tools.ietf.org/html/rfc7807#page-8) document:

> "When defining a new problem type, the information included must be   
> carefully vetted.  Likewise, when actually generating a problem --   
> however it is serialized -- the details given must also be   
> scrutinized.
> 
>    Risks include leaking information that can be exploited to
> compromise    the system, access to the system, or the privacy of
> users of the    system."


``` kotlin
val problemClassic = Problem.create(provider)  
    .title("Authorization Error")  
    .details("You are not authorized to perform write operations. Please contact the server admin at ...")  
    .type(URI("https://www.javando.org/api/errors/authorization-write-error"))  
    .instance(URI("/write-to-database"))  
    .status(HttpStatus.FORBIDDEN)  
    .addExtension(exception) // 'exception' can be any exception type.
    .addExtension(exception.stackTrace, depth = 3, excludePackages = arrayOf("*junit*", "java.lang.*")) 
    .build()
```
In the code example we have limited the stacktrace depth to only 3 elements and we have also excluded the packages that contains `junit` or starts with `java.lang.` 
The `*` can be used as an ant matcher. 
An example output could be:
``` json
{
   "type":"https://www.javando.org/api/errors/authorization-write-error",
   "title":"Authorization Error",
   "details":"You are not authorized to perform write operations. Please contact the server admin at ...",
   "status":403,
   "instance":"/write-to-database",
   "exceptions":[
      {
         "klass":"java.lang.RuntimeException",
         "message":"Write disabled\""
      },
      {
         "klass":"java.io.IOException",
         "message":"stream closed"
      }
   ],
   "stacktrace":[
      {
         "classLoaderName":"app",
         "declaringClass":"org.javando.http.problem.impl.test.GsonProviderTest",
         "methodName":"integrateAll",
         "fileName":"GsonProviderTest.kt",
         "lineNumber":152,
         "format":1
      },
      {
         "moduleName":"java.base",
         "moduleVersion":"11.0.4",
         "declaringClass":"java.util.ArrayList",
         "methodName":"forEach",
         "fileName":"ArrayList.java",
         "lineNumber":1540,
         "format":2
      },
      {
         "moduleName":"java.base",
         "moduleVersion":"11.0.4",
         "declaringClass":"java.util.ArrayList",
         "methodName":"forEach",
         "fileName":"ArrayList.java",
         "lineNumber":1540,
         "format":2
      }
   ]
}
```

### - Get extension members as registered custom class

``` kotlin
// Returns the CreditInfo instance or null if there's no credit_info extension member
// If the corresponding string does not match the requested class, 
// the method internally catches ClassCastException and silently returns null.
val creditInfo = problem.getExtensionValue("credit_info", CreditInfo::class.java)
```

### - Get extension members as a raw JsonObject or JsonArray

``` kotlin
val creditInfo = problem.getExtensionValue("credit_info", JsonObject::class.java) // null if it is not a JsonObject
// readValue will return null if there's no property with the given name or if the class type is wrong
val balance: Float? = creditInfo?.readValue("balance", Float::class.java) 
val currency: String? = creditInfo?.readValue("currency", String::class.java)
```

### - Deserialize a Json String Problem

``` kotlin
val jsonString = "...";  
val problem: Problem = Problem.from(jsonString, provider)
```


## - Get Involved!
Please consider to support this library by contributing to its development. 
Email me at dom.aiello90@gmail.com if you have any question .

