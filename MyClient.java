import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/* CSE 373 Winter 2016
 * @Author: Jiaqi Zhang
 * 
 * MyClient takes a text file of synonyms for words. 
 * It then chooses a word from the file, tell users the first 
 * letter of the word and ask user to guess the word.
 * It gives synonyms as clues once at a time.
 * If user cannot guess the word before synonyms run out, user lose.
 * Otherwise, user win.
 * 
 * This Client program is dependent on TextAssociator
 */
public class MyClient {
	
	//Path to desired thesaurus file to read
	public final static String MY_FILE = "wordlist.txt";
	
	public static void main(String[] args) throws IOException {
		File file = new File(MY_FILE);
		
		//Create new empty TextAssociator
		TextAssociator sc = new TextAssociator();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String text = null;
      //Put words and associations in TextAssociator
		while ((text = reader.readLine()) != null) {
			String[] words = text.split(",");
			String currWord = words[0].trim();
			sc.addNewWord(currWord);
			
			for (int i = 1; i < words.length; i++) {
				sc.addAssociation(currWord, words[i].trim());
			}
		}
      
      //Start Game
      boolean playOn = true;
      Scanner scan = new Scanner(System.in);
      while (playOn){
         //Get a list of all words and randomly choose one.
         Set<String> words = sc.getWords();
         if (words.size() == 0){
            System.out.println("No more words available. Game ends.");
            break;
         }
         int index = (int) (Math.random() * (words.size() - 1));
         System.out.println("Welcome to the word guessing game. Please guess the word based on the clues given.");
         
         int temp = 0;
         WordInfo target = new WordInfo(words.iterator().next());
         for(String s: words){
            if(temp == index){
               target = new WordInfo(s);
               break;
            }
            temp++;
         }
         
         String word = target.getWord();//The target word.
         char Init = word.charAt(0); //First char o word.
         Set<String> assos = sc.getAssociations(word);
         int len = assos.size();
         System.out.println("The word starts with \'" + Init + "\', you have " + len + " chances.");
         int attempt = 1;
         for (String clue: assos) {
            System.out.println("The clue is: \'" + clue + "\', please guess.");
            String guess = scan.next();
            if (guess.equalsIgnoreCase(word)){
               System.out.println("Correct! You got it in " + attempt + " guesses!");
               //Word guessed, delete this word in case of redundancy.
               sc.remove(word);
               break;
            } else {
               if(len == attempt){
                  System.out.println("Wrong, You lost. The answer is \'" + word + "\'.");
                  break;
               }
               System.out.println("Wrong, please guess again. You have " + (len - attempt) + " chances left.");
               attempt++;
            }
         }
 
         System.out.println("Do you want to play again? (1-yes, 0-no)");
         int playAgain = scan.nextInt();
         if(playAgain == 0) {
            playOn = false;
         }
      }
   }
}