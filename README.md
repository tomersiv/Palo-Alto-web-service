# This application is a RESTful Web Service developed with Java Spring Boot.
## Instructions on how to build and run the program:
1. Clone this repository into a folder in your computer.
2. Open a PowerShell window inside 'Palo-Alto-web-service' folder (click shift + right mouse inside the folder then "Open PowerShell window here")
3. inside the PowerShell run **./mvnw spring-boot:run**
4. There you go, the web service is up and running!! 

## Algorithm Explanation:
First, using the @GetMapping annotation, we ensure that HTTP GET requests to /api/v1/similar and to api/v1/stats are mapped to the similarWords() and stats() functions respectively.
### similarWords() function:
This function reads the words_clean.txt dictionary file into a list of strings, then iterates over the list to look for a word that is a permutation of 'w' in api/v1/similar?word=w.

