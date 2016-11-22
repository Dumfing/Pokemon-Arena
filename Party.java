//Party.java
//Aaron Li
//Takes care of the player's and computer's Pokemon
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
public class Party{
	private ArrayList<Pokemon> party = new ArrayList<Pokemon>();
	private int active = 0;
	private static Scanner kb = new Scanner(System.in);
	public void addPokemon(Pokemon pkmnIn){
		party.add(pkmnIn);
	}
	public Pokemon currentPokemon(){
		return party.get(active);
	}
	public void setActive(int reqActive){
		active = reqActive;
	}
	public boolean contains(Pokemon pkmnIn){
		return party.contains(pkmnIn);
	}

	public static Party pickParty(Pokedex pokedex){
		int picked;
		Party userParty = new Party();
		ArrayList<String> pickablePokemon = pokedex.pokemonNames();
		int numPicked = 0;
		while(numPicked < 4){
			System.out.println("Please pick a pokemon:\n");
			for(int i = 0;i<pickablePokemon.size(); i++){
				if(i%4 == 0){
					System.out.println();
				}
				System.out.printf("%2d. %10s ",i+1,pickablePokemon.get(i));
			}
			System.out.println();
			picked = Integer.parseInt(kb.nextLine());
			userParty.addPokemon(pokedex.getPokemon(pickablePokemon.get(picked-1)));
			pickablePokemon.remove(picked-1);
			numPicked++;
		}
		return userParty;
	}
	
	public static Party computerParty(Pokedex pokedex, Party userPokemon){
		Party computerParty = new Party();
		ArrayList<String> remainingPokemon = new ArrayList<String>(pokedex.pokemonNames());
		remainingPokemon.removeAll(userPokemon.partyNames());
		for(String pkmn : remainingPokemon){
			computerParty.addPokemon(pokedex.getPokemon(pkmn));
		}
		return computerParty;
	}
	
	public ArrayList<String> partyNames(){
		ArrayList<String> namesOut = new ArrayList<String>();
		for(Pokemon pkmn : party){
			namesOut.add(pkmn.getName());
		}
		return namesOut;
	}
	
	
	public String toString(){
		String sOut = "";
		for(int i = 0; i<party.size();i++){
			sOut+=party.get(i)+"\n";
		}
		return sOut;
	}
	public void pickActive(){
		int uIn = 0;
		System.out.println("Pick a starting pokemon.");
		ArrayList<String> pokeNames= partyNames();
		for(int i = 0;i<pokeNames.size();i++){
			System.out.printf("%2d. %-10s\n",i+1,pokeNames.get(i));
		}
		uIn = Integer.parseInt(kb.nextLine());
		if(uIn>0 && uIn<5){
			setActive(uIn-1);
			System.out.printf("%s, I choose you!\n",pokeNames.get(uIn-1));
		}
		else{
			//Action if input is not linked to option
		}
	}
}