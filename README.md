# broadlink-java-api [![Build Status](https://travis-ci.org/mob41/broadlink-java-api.svg?branch=master)](https://travis-ci.org/mob41/broadlink-java-api)
A clean Java API to Broadlink devices!

This project is still in very early development stage! Many things not implemented though:

- [x] Broadlink devices discovery
- [ ] Specific broadlink device "creation"
- [ ] Authentication and encryption things
- [ ] Send data/learn code

Thanks [mjg59](https://github.com/mjg59) in developing the [python-broadlink](https://github.com/mjg59/python-broadlink) library! I really appreciate mjg59 for spending time in reverse engineering the protocol!

I would like to integrate in my own home system (private, [SakuraHome](https://github.com/mob41/Sakura)) so I suggested to start the work of the API in Java.

## Ways to add into your project

1. (Eclipse) Clone the project via ```git clone https://github.com/mob41/broadlink-java-api.git``` or via the [download ZIP](https://github.com/mob41/broadlink-java-api/archive/master.zip) and extract the ZIP to a folder.
   
   And add the project into your Eclipse IDE by right clicking the ```Package Explorer```, and,
   ```Import...``` -> ```Maven``` -> ```Existing Maven Projects```
   
   Select the folder you cloned/downloaded broadlink-java-api to. And select the ```pom.xml``` inside.
   
   The project should be added. And the following you have to do is add the following dependency to your ```pom.xml```:
   
   ```xml
    <dependency>
        <groupId>com.github.mob41.blapi</groupId>
        <artifactId>broadlink-java-api</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    ```
    

2. ~~Maven dependency: Add the following to your ```pom.xml``` under ```<dependencies>```~~ The project hasn't published to Maven central, yet.

    ```xml
    <dependency>
        <groupId>com.github.mob41.blapi</groupId>
        <artifactId>broadlink-java-api</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    ```
    

## Tutorial

Discovering devices in the local network:

```java
BLDevice[] devs = BLDevice.discoverDevices(); //Default with 10000 ms (10 sec) timeout, search for multiple devices

//BLDevice[] devs = BLDevice.discoverDevices(0); //No timeout will block the thread and search for one device only
//BLDevice[] devs = BLDevice.discoverDevices(5000); //With 5000 ms (5 sec) timeout

//The BLDevice[] array stores the found devices in the local network
```

Well, that's it for now. Still lots of things to be implemented

## License

[tl;dr](https://tldrlegal.com/license/mit-license) This project is licensed under the MIT License.