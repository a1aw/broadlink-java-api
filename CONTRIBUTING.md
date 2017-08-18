# Contributing
Multiple important things to know before contributing to the repository.

## Reporting Issues

As the library is still in heavy development stage, most things are not supposed to be stable. We are welcome to receive problems and bugs reports from users.

To report a bug, a proper log recording files are required. Please kindly create a file called ```log4j.properties``` next to your application/development environment:

```java
log4j.rootLogger=DEBUG, STDOUT
log4j.logger.deng=DEBUG
log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
log4j.appender.STDOUT.layout=org.apache.log4j.PatternLayout
log4j.appender.STDOUT.layout.ConversionPattern=%5p [%t] (%F:%L) - %m%n
```

This file will allow the library to print debug logs to console.

And try to reproduce the problem again. After that, copy the logs to a text file and attach/upload to the issue page.

## Code contributing

Code contributing to the Java Broadlink library is appreciated. However, please follow the following coding style, so as to improve code readability.

### We use [K&R (1TBS (OTBS))](https://en.wikipedia.org/wiki/Indent_style#K.26R) coding style.

  ```java
  if (condition){
      //do stuff
  } else {
      //do else stuff
  }
  
  for (int i = 0; i < 10; i++){
      //do stuff
  }
  
  while(true){
  	//do loop stuff
  }
  ```

### Use spaces instead of tabs for indentation. (1 tab for 4 spaces) [[Eclipse IDE]](https://stackoverflow.com/questions/407929/how-do-i-change-eclipse-to-use-spaces-instead-of-tabs)

### No EOF allowed in the last line of the source code. Please add a new line (```\r\n```) instead.

### Comment and describe usage on every bitwise operation

  ```java
  int test = 15; //0000 1111
  
  test |= 15 << 4; //shift 15 (00001111) left 4 position and OR "test"
  ```

### JavaDoc/comment for every method, including constructor, for better JavaDoc API documentation generating

  JavaDoc comment starts with ```/**```, appends ```*``` down on each single line and ends with ```*/```. For example:
  
  ```java
  /**
   * This is the base class of all Broadlink devices (e.g. SP1, RMPro)
   * 
   * @author Anthony
   *
   */
   public BLDevice(){
       //do stuff
   }
   ```
   
### Comment style

  Don't be lazy :smile: Eclipse IDE loves automatically helps you to open a comment box like this:
  
  ```java
  /*
   * This is a dirty comment
   * 
   */
  ```
  
  We don't allow to use this kind of commenting. It is only allowed to use to comment outside of any method to keep clean. Instead, you could use this in methods:
  
  ```java
  /*
  System.out.println("I just wanted to comment this code snippet.");
  System.out.println("It is much cleaner, right?");
  */
  ```
  
  Or even using the simple ```//```:
  
  ```java
  //System.exit(0);
  ```


  