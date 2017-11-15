import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/* CSE 373 Winter 2017
 * @Author Jiaqi Zhang 
 * 
 * TextAssociator represents a collection of associations between words.
 * 
 */
public class TextAssociator {
	private WordInfoSeparateChain[] table;
	private int size;
	
	/* INNER CLASS
	 * Represents a separate chain in implementation of hashing
	 * A WordInfoSeparateChain is a list of WordInfo objects that have all
	 * been hashed to the same index of the TextAssociator
	 */
	private class WordInfoSeparateChain {
		private List<WordInfo> chain;
		
		/* Creates an empty WordInfoSeparateChain without any WordInfo
		 */
		public WordInfoSeparateChain() {
			this.chain = new ArrayList<WordInfo>();
		}
		
		/* Adds a WordInfo object to the SeparateCahin
		 * Returns true if the WordInfo was successfully added, false otherwise
		 */
		public boolean add(WordInfo wi) {
			if (!chain.contains(wi)){
				chain.add(wi);
				return true;
			}
			return false;
			
		}
		
		/* Removes the given WordInfo object from the separate chain
		 * Returns true if the WordInfo was successfully removed, false otherwise
		 */
		public boolean remove(WordInfo wi) {
			if (chain.contains(wi)){
				chain.remove(wi);
				return true;
			}
			return false;
		}
		
		// Returns the size of this separate chain
		public int size() {
			return chain.size();
		}
		
		// Returns the String representation of this separate chain
		public String toString() {
			return chain.toString();
		}
		
		// Returns the list of WordInfo objects in this chain
		public List<WordInfo> getElements() {
			return chain;
		}
	}
	
	
	/* Creates a new TextAssociator without any associations 
	 */
	public TextAssociator() {
		table = new WordInfoSeparateChain[1001]; 
		size = 0;
		
	}
	
	
	/* Adds a word with no associations to the TextAssociator 
	 * Returns False if this word is already contained in your TextAssociator ,
	 * Returns True if this word is successfully added
	 */
	public boolean addNewWord(String word) {
		int index = getIndex(word, table.length);
		//construct a new chain if the index does not contain a chain.
		if (!hasChain(index)){
			table[index] = new WordInfoSeparateChain();
		} 
		WordInfo wi = new WordInfo(word);
		//check if word is already in the chain.
		if (table[index].add(wi)){
			size++;
			expandSize();
			return true;
		} else {			
			return false;
		}
	}
		

	
	/* Adds an association between the given words. Returns true if association correctly added, 
	 * returns false if first parameter does not already exist in the TextAssociator or if 
	 * the association between the two words already exists
	 */
	public boolean addAssociation(String word, String association) {
		int index = getIndex(word, table.length);
		//check if index is empty.
		if (!hasChain(index)){
			return false;
		}
        WordInfo temp = new WordInfo(word);
		//check if chain contains the word.
		for (WordInfo wi: table[index].getElements()){
			if (wi.equals(temp)){
				wi.addAssociation(association);
				return true;
			}
		}
		return false;
	}
	
	
	/* Remove the given word from the TextAssociator, returns false if word 
	 * was not contained, returns true if the word was successfully removed.
	 * Note that only a source word can be removed by this method, not an association.
	 */
	public boolean remove(String word) {
		int index = getIndex(word, table.length);
		//check if index is empty
		if (!hasChain(index)){
			return false;
		}
      WordInfo temp = new WordInfo(word);
		for (WordInfo wi: table[index].getElements()){
			//if word is in chain, remove the WordInfo.
			if (wi.equals(temp)){
				table[index].remove(wi);
				size--;
				shrinkSize();
				return true;
			}
		}
		return false;
	}
	
	
	/* Returns a set of all the words associated with the given String  
	 * Returns null if the given String does not exist in the TextAssociator
	 */
	public Set<String> getAssociations(String word) {
		int index = getIndex(word, table.length);
		//check if the TextAssociator has the given String.
		if(!hasChain(index)){
			return null;
		}
        WordInfo temp = new WordInfo(word);
		for (WordInfo wi: table[index].getElements()){
			//if word is in chain, return a set of its associations.
			if (wi.equals(temp)){
				return wi.getAssociations();
			}
		}
		return null;
	}
	
	
	/* Prints the current associations between words being stored
	 * to System.out
	 */
	public void prettyPrint() {
      System.out.println();
		System.out.println("Current number of elements : " + size);
		System.out.println("Current table size: " + table.length);
		
		//Walk through every possible index in the table
		for (int i = 0; i < table.length; i++) {
			if (table[i] != null) {
				WordInfoSeparateChain bucket = table[i];
				
				//For each separate chain, grab each individual WordInfo
				for (WordInfo curr : bucket.getElements()) {
					System.out.println("\tin table index, " + i + ": " + curr);
				}
			}
		}
		System.out.println();
	}
	
   
   //Return all the words stored in the table as a set.
   public Set<String> getWords() {
      WordInfo temp = new WordInfo("");
      Set<String> wordList = temp.getAssociations();
      for (int index = 0; index < table.length; index ++){
         if (hasChain(index)){
            for(WordInfo wi: table[index].getElements()){
               wordList.add(wi.getWord());
            }
         }
      }
      return wordList;
   }
   
	/* Check if there is a chain in the table at the given index.
	 * Returns true if there is existing chain.
	 * Returns false if it is empty.
	 */
	private boolean hasChain(int index) {
		return table[index] != null;
	}
	
	/* Changes the size of the table by creating new table and copying elements.
	 * If load factor is larger than 1, copy elements to a new table with double size.
	 */
	private void expandSize() {
		double loadFactor = size / table.length;
		if (loadFactor >= 1.0) {
			replaceTable(table.length * 2);
		} 
   }
   
   /* Changes the size of the table by creating new table and copying elements.
	 * If load factor is smaller than 0.25, copy elements to a new table with half size.
	 */
   private void shrinkSize() {
      double loadFactor = size / table.length;
      if (loadFactor <= 0.25){
         replaceTable(table.length / 2);
      }
   }  
	
	
	/* Copy elements in the old table to a new table with given size.
	 * Replace the old table with the resized table.
	 */
	private void replaceTable (int length){
		WordInfoSeparateChain[] newTable = new WordInfoSeparateChain[length];
		for (int i = 0; i < table.length; i++){
			if (table[i] != null){
				for (WordInfo temp : table[i].getElements()) {
					int newIndex = getIndex(temp.getWord(), newTable.length);
					if (newTable[newIndex] == null) {
						newTable[newIndex] = new WordInfoSeparateChain();
					} 
					newTable[newIndex].add(temp);
				}
			}
		}
		table = newTable;
	}
	
	/* Find out the index for the word in a HashSet of given size.
	 * Returns the index.
	 */
	private int getIndex(String word, int size){
		return Math.abs(word.hashCode()) % size; 
	}
}
