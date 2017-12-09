# CIS656
labs for CIS 656

Lab 4
navigate to the lib directory, or copy all the jars to whatever location you are running from:

//Master
<span>java -cp ChatClient.jar;log4j.jar;openchord_1.0.4.jar client.ChordClient -master master localhost</span>
//Alice
java -cp ChatClient.jar;log4j.jar;openchord_1.0.4.jar client.ChordClient Alice localhost
//Bob
java -cp ChatClient.jar;log4j.jar;openchord_1.0.4.jar client.ChordClient Bob localhost
 
