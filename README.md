# This application is a RESTful Web Service developed with Java Spring Boot.
## Instructions on how to build and run the program:
1. Clone this repository into a folder in your computer.
2. Open a PowerShell window inside 'Palo-Alto-web-service' folder (click shift + right mouse inside the folder then "Open PowerShell window here")
3. inside the PowerShell run **./mvnw spring-boot:run**
4. There you go, the web service is up and running!! 
## Algorithm Explanation:
First, using the @GetMapping annotation, we ensure that HTTP GET requests to /api/v1/similar and to api/v1/stats are mapped to the similarWords() and stats() functions respectively.
### similarWords() function:
This function reads the words_clean.txt dictionary file into a list of strings, then iterates over the list to check if the word is a permutation of the word 'w' in api/v1/similar?word=w. Finally, the function returns a json object consisting of all words that are similar to 'w' not including 'w'.
The algorithm for checking if a word 'word1' is a permutation of another word 'word2' is as follows: 
We create a constant-spaced count arrray of size 26 (number of lowercase English letters), then we increment the value in the count array in the corresponding positions of characters in 'word1' and decrement for characters in 'word2'. Finally, if all count array values are 0, then the two words are permutation of each other.

### stats() function:
This function 


