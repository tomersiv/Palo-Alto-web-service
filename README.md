# This application is a RESTful Web Service developed with Java Spring Boot.
## Instructions on how to build and run the program:
1. Clone this repository into a folder in your computer.
2. Download java jdk 1.8 or later [here](https://www.oracle.com/java/technologies/downloads/).
3. Type **"environment"** in the windows search bar (bottom-left corner). In the following window, choose **Edit the system environment variables**  and click on **Environment Variables**.
4. Click **New** to create a new environment variable.
5. On the **Variable name** type **"JAVA_HOME"**, and on the **Variable value** browse to the installation folder of your java jdk. Click **OK**.   
6. Navigate to the folder where you cloned the repository, then open a PowerShell window inside 'Palo-Alto-web-service' folder (click shift + right mouse inside the folder then "Open PowerShell window here")
7. Inside the PowerShell, run **./mvnw spring-boot:run**
8. There you go, the web service is up and running!! You can now send GET requests through these routes:
   - http://localhost:8000/api/v1/similar
   - http://localhost:8000/api/v1/stats  
    
    
## Algorithm Explanation:
First, by using the @GetMapping annotation, we ensure that HTTP GET requests to /api/v1/similar and to api/v1/stats are mapped to the similarWords() and stats() functions respectively.
### similarWords() function:
This function builds a list that contains all words that are similar to 'w' in /api/v1/similar?word=w not including 'w', and returns a json object of the list. My program supports two different algorithms for building the list of similar words, each with its own advantages depending on the size of the input word and the number of words in the dictionary.  

**Algorithm 1:**  
This algorithm iterates over the words in the dictionary file, and for each word it checks whether it is a permutation of the input string or not.  
The algorithm for checking whether 'word1' is a permutation of 'word2' is as follows: 
A constant-spaced count arrray of size 26 (number of lowercase English letters) is first created; then the value in the count array of the corresponding positions of characters in 'word1' is incremented while that for characters in 'word2' is decremented. Finally, if all count array values are 0, then the two words are considered a permutation of each other.  
  
  
**Some optimizations that were made to decrease the average processing time while iterating over the dictionary file looking for similar words to the input string:**
- Word similarity is checked only if the length of both strings is equal. 
- Only if the input string's chars contains the first char of a word in the list (in O(1), using HashSet), string similarity is checked. 
- The input string's char with the maximal value is calculated and if a word in the list starts with a char that has a greater value than this maximum, then the iteration is stopped. This approach is based on the fact that the words in the dictionary file are sorted lexicographically.
      
- **Time Complexity - O(N⋅K), where K is the length of the input string and N is the number of words in the dictionary file.**  
- **Space Complexity - O(1)**  

**Algorithm 2:**  
This algorithm iterates over all permutations of the input string and checks, for each permutation, if the dictionary contains it using binary search. **Note that we can use binary search on the dictionary file because the strings in that file are sorted lexicographically.**  
  
  
The algorithm for creating all permutations of a given string 'str' uses the backtracking approach:  
- The generatePermutation function considers the first index of the given string.
- If the index is end - 1, i.e. length of the string, it means that the current permutation is completed.
- Run a loop from current index 'start' till end – 1 and do the following:
  - Swap str[j] and str[start].
  - Construct all other possible permutations, from backtrack(start + 1).
  - Backtrack again, i.e. swap(str[j], str[start]).
 
 ![This is an image](https://static.javatpoint.com/programs/images/program-to-find-all-the-permutations-of-a-string.png)  
   
   
 - **Time Complexity - O(K!⋅K⋅logN), where K is the length of the input string and N is the number of words in the dictionary file.**  
 - **Space Complexity - O(K!), where K is the length of the input string. This is because the function will be called recursively and will be stored in call stack for all K! permutations.**  
   
     
**By default, my program uses the first algorithm, however there are some cases that the seccond algorithm will have better performance. Upon checking both algorithms' performance, I noticed that the second algorithm will have better performance when the input string's length is between 1 and 7, and will have really bad performance when the length is above 10.**  
  
### *In order to support the second algorithm instead of the first one, simply comment line 51 and uncomment lines 53, 54 in the Controller class.*
  
  
  
  
### stats() function:
This function returns a json object consisting of three integers: totalWords, totalRequests and avgProcessingTimeNs. 

- **Calculating totalRequests :** Manage a counter that is incremented every time a HTTP Get request to /api/v1/similar is performed.
- **Calculating avgProcessingTimeNs :** We accumulate the duration time of all GET requests to /api/v1/similar that were performed and then divide it by the total amount of GET requests to /api/v1/similar. We calculate the duration time of each GET request to /api/v1/similar by managing two variables that captures the time in the beginning and in the end of the similarWords() function, then we substract the two variables to get the duration time.  
 
