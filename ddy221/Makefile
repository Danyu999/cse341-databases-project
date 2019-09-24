all:ddy221.jar

ddy221.jar:Interfaces.class Helper.class CustomerInterface.class OnsiteEmployeeInterface.class AnalyticsEmployeeInterface.class ojdbc7.jar Manifest.txt
	jar cfmv ddy221.jar Manifest.txt Interfaces.class Helper.class CustomerInterface.class OnsiteEmployeeInterface.class AnalyticsEmployeeInterface.class

Interfaces.class:Interfaces.java
	javac Interfaces.java

Helper.class:Helper.java
	javac Helper.java

CustomerInterface.class:CustomerInterface.java
	javac CustomerInterface.java

OnsiteEmployeeInterface.class:OnsiteEmployeeInterface.java
	javac OnsiteEmployeeInterface.java

AnalyticsEmployeeInterface.class:AnalyticsEmployeeInterface.java
	javac AnalyticsEmployeeInterface.java

.PHONY:
clean:
	rm -f Interfaces.class Helper.class CustomerInterface.class OnsiteEmployeeInterface.class AnalyticsEmployeeInterface.class ddy221.jar