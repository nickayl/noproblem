# NoProblem - HTTP APIs error messages made easy
[![](https://jitpack.io/v/cyclonesword/noproblem.svg)](https://jitpack.io/#cyclonesword/noproblem) <br>
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
implementation 'com.github.cyclonesword.noproblem:no-problem-api:0.8.BETA+'
/* Gson provider or another of your preference */
 implementation 'com.github.cyclonesword.noproblem:gson-provider:0.8.BETA+'
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
	<groupId>com.github.cyclonesword.noproblem</groupId>
	<artifactId>no-problem-api</artifactId>
	<version>[0.8.BETA,)</version>
</dependency>  
  
<!-- Gson provider or another of your preference -->  
<dependency>
	<groupId>com.github.cyclonesword.noproblem</groupId>
	<artifactId>gson-provider</artifactId>
	<version>[0.8.BETA,)</version>
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

### -Adding problem extension members
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
{	"type":"https://www.myapi.com/errors/insufficient-credit.html",
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
### - Deserialize a Json String Problem

``` kotlin
val jsonString = "...";  
val problem = Problem.from(jsonString, provider)
problem.getExtensionValue<CreditInfo>("credit_info")
```
### - Get extension members


``` kotlin
// Returns the CreditInfo instance or null if there's no credit_info extension member
val creditInfo = problem.getExtensionValue<CreditInfo>("credit_info")
```


## - Get Involved!
Please consider to support this library by contributing to its development. 
Email me at dom.aiello90@gmail.com if you have any question .

