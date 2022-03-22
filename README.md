# This application is a RESTful Web Service developed with Java Spring Boot.
## Instructions on how to build and run the program:
1. Clone this repository into a folder in your computer.
2. Download java jdk 1.8 or a later version [here](https://www.oracle.com/java/technologies/downloads/).
3. Type **"environment"** in the windows search bar (bottom-left corner), and in the following window choose **Edit the system environment variables**, and click on **Environment Variables**.
4. Click **New** to create a new environment variable.
5. On the **Variable name** type **"JAVA_HOME"**, and on the **Variable value** browse to the installation folder of your java jdk. Click **OK**.   
6. Navigate to the folder where you cloned the repository, then open a PowerShell window inside 'Palo-Alto-web-service' folder (click shift + right mouse inside the folder then "Open PowerShell window here")
7. In the PowerShell, run **./mvnw spring-boot:run** and wait for the download to complete.
8. The web service is up and running!! You can now send GET requests through these routes:
   - http://localhost:8000/api/v1/similar
   - http://localhost:8000/api/v1/stats  
    
    
## Algorithm Explanation:
By using the @GetMapping annotation, we ensure that HTTP GET requests to /api/v1/similar and to api/v1/stats are mapped to 'similarWords' and 'stats' functions, respectively.
### The 'similarWords' function:
This function builds a list of all words that are similar (a permutation) to a given word 'w', but not including 'w' in /api/v1/similar?word=w, and returns a json object of this list.   
### The 'stats' function:
This function returns a json object consisting of three integers: totalWords, totalRequests and avgProcessingTimeNs. 

- **Calculating totalRequests:** Manages a counter that is incremented every time a HTTP Get request to /api/v1/similar is performed.
- **Calculating avgProcessingTimeNs:** The duration time of all GET requests to /api/v1/similar are first accumulated and then divided by the total amount of GET requests. The duration time of each GET request to /api/v1/similar is calculated by managing two variables that capture the time, in the beginning and in the end of the 'similarWords' function; the difference between these variables is the duration time. 

### Main algorithm for creating the list of similar words: 
1. Create a HashMap that maps a string to a list of words from the dictionary, in the following way:
     "8b" -> [list, of, all, words, in, length, 8, that, start, with, 'b']. 
3. Create a HashSet of all words' lengths in the dictionary.
4. If the input word is an empty string, or the HashSet doesn't contain the input word's length, then there are no similar words to the input word in the dictionary (because the dictionary has no empty strings, and a permutation of the input word must has the same length). 
5. Iterate over all input word's unique chars and, for each char, get the list of all words that start with that char and has the same length as the input word using the HashMap. For each such list, call the 'filterSimilarWords' function.  
6. if the input word is included in our final list of similar words, remove it.
### The 'filterSimilarWords' function:    
This function iterates over the words in a given list and, for each word, checks whether or not it is a permutation of the input string.  

**The algorithm for checking if 'word1' is a permutation of 'word2' is as follows:**   
A constant-spaced count array of size 26 (number of lowercase English letters) is first created; then the value in the count array of the corresponding positions of characters in 'word1' is incremented while that of characters in 'word2' is decremented. Finally, if all count array values are 0, then the two words are considered a permutation of each other.
  
### Time and space complexity of the main algorithm:  
* Time Complexity - O(N⋅K), where K is the length of the input string and N is the number of words in the dictionary file.
* Space Complexity - O(N), where N is the number of words in the dictionary file.
