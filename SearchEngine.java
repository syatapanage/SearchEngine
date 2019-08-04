import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Allows text searches within a set of documents. Produces a list of the 
 * top 10 documents with the highest frequencies in descending order.
 * 
 * @author Sadhana Yatapanage
 */
public class SearchEngine {

    public static void main(String[] args) {
 
        Scanner input = new Scanner(System.in);
        System.out.print("Please enter the directory name: ");
        String fileAnswer = input.nextLine();
        
        String directory = fileAnswer;
        File folder = new File(directory);
        File[] files = folder.listFiles();

        //Creates inverted index to store words, files and frequencies
        Map<String, HashMap<String, Integer>> index = new HashMap<>();
        
        for (File file : files) {
            if(file.isFile()) {
                try {
                    StringBuilder fileLines = new StringBuilder();
                    
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    
                    String line = br.readLine();
                    
                    while(line != null) {
                        fileLines.append(line + "\n");
                        line = br.readLine();
                    }
                    
                    String stringLines = fileLines.toString();
                    String[] wordArray = stringLines.split("\\s+");
                    
                    for (String word : wordArray) {
                        //If index contains word and file, add one to frequency
                        if (index.containsKey(word)) {
                            //Gets hashmap from index
                            HashMap<String, Integer> wordValue = index.get(word);
                            if(wordValue.containsKey(file.getName())) {
                                int freq = wordValue.get(file.getName());
                                wordValue.replace(file.getName(), freq+1);
                            //If index contains word but not file, add file
                            } else {
                                wordValue.put(file.getName(), 1);
                            }
                        //If index does not contain word, add entry
                        } else {
                            HashMap<String, Integer> newEntry = new HashMap<String, Integer>();
                            newEntry.put(file.getName(), 1);
                            index.put(word,newEntry);
                        }
                    }
                    
                    br.close();
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        System.out.print("Please enter search query keywords: ");
        String answer = input.nextLine();
        String[] inputArray = answer.split("\\s+");
        input.close();
        
        //Creates hashmap to store document and frequencies for query
        Map<String, Integer> queryIndex = new HashMap<>();
        for(int i=0; i< inputArray.length; i++) {
            //Get the hashmap of documents and frequencies from index for query word
            HashMap<String, Integer> inputIndex = index.get(inputArray[i]);
            for (Map.Entry<String, Integer> entry : inputIndex.entrySet()) {
                if(queryIndex.containsKey(entry.getKey())) {
                    int value = queryIndex.get(entry.getKey());
                    queryIndex.replace(entry.getKey(), value + entry.getValue());
                } else {
                    queryIndex.put(entry.getKey(), entry.getValue());
                }
            } 
        }
        
        //Uses comparator to sort the document frequencies
        Set<Entry<String, Integer>> freqSet = queryIndex.entrySet();
        List<Entry<String, Integer>> freqList = new ArrayList<Entry<String, 
                Integer>>(freqSet);
        Collections.sort(freqList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare( Map.Entry<String, Integer> first, 
                    Map.Entry<String, Integer> second) {
                return (second.getValue()).compareTo(first.getValue());
            }
        } );
        
        //Prints the first 10 entries of the list
        for (int i = 0; i < freqList.size(); i++) {
            if(i<10) {
                System.out.println(freqList.get(i).getKey() + " : " + freqList.get(i).getValue());
            }
        }
    }

}
