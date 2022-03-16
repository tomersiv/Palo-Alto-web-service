# This application is a RESTful Web Service developed with Java Spring Boot.
## Instructions on how to build and run the program:
1. Clone this repository into a folder in your computer.
2. Open a PowerShell window inside 'Palo-Alto-web-service' folder (click shift + right mouse inside the folder then "Open PowerShell window here")
3. inside the PowerShell run **./mvnw spring-boot:run**
4. There you go, the web service is up and running!! 
## Algorithm Explanation:
First, using the @GetMapping annotation, we ensure that HTTP GET requests to /api/v1/similar and to api/v1/stats are mapped to the similarWords() and stats() functions respectively.
### similarWords() function:
This function builds a list that contains all words that are similar to 'w' in /api/v1/similar?word=w not including 'w', and returns a json object of the list. My program supports two different algorithms for building the list of similar words, each with its own advantages depending on the size of the input word and the number of words in the dictionary.  

**Algorithm 1:**  
This algorithm iterates over the words in the dictionary file, and for each word, checks if this word is a permutation of the input string.  
The algorithm for checking if a word 'word1' is a permutation of another word 'word2' is as follows: 
We create a constant-spaced count arrray of size 26 (number of lowercase English letters), then we increment the value in the count array in the corresponding positions of characters in 'word1' and decrement for characters in 'word2'. Finally, if all count array values are 0, then the two words are permutation of each other.  
  
  
**Some optimizations that I made to decrease the average processing time while iterating over the dictionary file looking for a similar word to the input string:**
- Compared the length of both strings, and only if they are equal, continued to check if strings are similar (because similar strings must be with the same length).
- Checked if the input string's chars contain the first char of a word in the list (using HashSet), and only if they do, continued to check if strings are similar.
- Calculated the input string's char with the maximum value and checked if a word in the list starts with a char that has a greater value than this maximum, if it does, we can stop the iteration and break from the whole loop because the words in the dictionary file are sorted lexicographically and we can use it to our advantage.
      
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


### stats() function:
This function returns a json object consisting of three integers: totalWords, totalRequests and avgProcessingTimeNs. 

- **Calculating totalRequests :** Manage a counter that is incremented every time a HTTP Get request to /api/v1/similar is performed.
- **Calculating avgProcessingTimeNs :** We accumulate the duration time of all GET requests to /api/v1/similar that were performed and then divide it by the total amount of GET requests to /api/v1/similar. We calculate the duration time of each GET request to /api/v1/similar by managing two variables that captures the time in the beginning and in the end of the similarWords() function, then we substract the two variables to get the duration time.  
 
