//Party.java
//Aaron Li
//Takes care of the player's and computer's Pokemon, picking pokemon, dealing with the active pokemon, healing, recharging
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
public class Party{
	private ArrayList<Pokemon> party = new ArrayList<Pokemon>(); // Pokemon that the owner of this party owns
	private int active = -1; // current pokemon that is fighting
	private String ownerName; // the name of the owner (player, bot)
	public Party(Party partyIn){ // if you wish to clone a party
		this.ownerName = partyIn.ownerName;
		this.party = partyIn.party;
	}
	
	public Party(int active, String name){ // Create a party with an already assigned active index
		this.active = active;
		this.ownerName = name;
	}
	public String getOwner(){ // gets the name of the owner
		return ownerName;
	}
	public Party setOwner(String name){
		this.ownerName = name;
		return this;
	}
	public Party addPokemon(Pokemon pkmnIn){ // add a pokemon to the party
		party.add(pkmnIn);
		return this;
	}
	public int size(){ // get the amount of pokemon in the party
		return party.size();
	}
	public Pokemon currentPokemon(){ // get the pokemon that is currently fighting in the party
		return party.get(active);
	}
	public Party setActive(int reqActive){ // change the active pokemon
		active = reqActive;
		return this;
	}
	public boolean contains(Pokemon pkmnIn){ // check whether a pokemon is in the party or not
		return party.contains(pkmnIn);
	}
	public Party restAll(){ // give energy to all pokemon that haven't attacked
		for(Pokemon pkmn:party){
			if(!pkmn.hasAttacked()){ // if the pokemon has attacked it will recharge
				pkmn.recharge(10);
			}
			else{
				pkmn.setAttacked(false); // if the pokemon attacked in the round before it will be able to recharge next round (unless it attacks again)
			}
		}
		return this;
	}
	public Party healAll(){ // heal all pokemon in the party by 20 health
		for(Pokemon pkmn:party){
			if(pkmn.getHealth()>0){ // the pokemon needs to be alive to heal
				pkmn.heal(20);
			}
		}
		return this;
	}
	public static Party pickParty(Pokedex pokedex){ // used for picking the contents of a party
		int picked; // number of the pokemon the user picked
		int partySize = 6;
		String uIn; // user input
		String[] pokemonPickedWords = {"","second ","third ","fourth ","fifth ","sixth ","sevent ","eighth ","ninth "}; // Used for making the prompt read more easily. Probably won't use all but expansion is there
		Party userParty = new Party(-1,"User"); // create a new party with an index of -1
		ArrayList<String> pickablePokemon = pokedex.pokemonNames();  // the names of all pokemon in the pokedex
		ArrayList<Integer> pokemonNumbers = new ArrayList<Integer>(); // the indexes of all pokemon (allows you to pick a pokemon correctly when the already picked ones are removed)
		for(int i = 0; i < pickablePokemon.size(); i++){ // add the indexes to the pokemonnumbers arraylist
			pokemonNumbers.add(new Integer(i));
		}
		
		int numPicked = 0; // amount of pokemon you've picked
		while(numPicked < partySize){
			System.out.println("Please pick a "+pokemonPickedWords[numPicked]+"pokemon:\n"); // print the prompt
			System.out.println((PkmnArena.options[PkmnArena.POKEMON_DETAILS]?"s. simple\n":"d. Details\n")+"p. Pick for me");
			if(numPicked>0){
				System.out.println("f. Finished");
			}
			if(PkmnArena.options[PkmnArena.POKEMON_DETAILS]){ // print the details or just the name of each pokemon
				for(int i = 0;i<pickablePokemon.size();i++){ // print all the pickable pokemon
					System.out.printf(PkmnTools.ANSI_CYAN+"%3d"+PkmnTools.ANSI_RESET+" %s\n",i+1,pokedex.getPokemon(pokemonNumbers.get(i))); // print detailed version
				}
			}
			else{
				for(int i = 0;i<pickablePokemon.size(); i++){
					if(i%4 == 0){ // new line every fourth pokemon
						System.out.println();
					}
					System.out.printf(PkmnTools.ANSI_CYAN+"%3d"+PkmnTools.ANSI_RESET +" %-13s ",i+1,pickablePokemon.get(i)); // Print simple version in a compact grid
				}
				System.out.println();
			}
			System.out.println("Enter a number: ");
			uIn = PkmnArena.kb.nextLine();
			if(uIn.equals("d")){ // if the user wants the detailed view
				PkmnArena.options[PkmnArena.POKEMON_DETAILS] = true;
			}
			else if(uIn.equals("s")){ // if the user wants the simple view
				PkmnArena.options[PkmnArena.POKEMON_DETAILS] = false;
			}
			else if(uIn.equals("p")){
				Party pOut = computerParty(pokedex, userParty, userParty);
				pOut.setOwner("User");
				return pOut;
			}
			else if(uIn.equals("f") && numPicked>0){
				return userParty;
			}
			else if(uIn.replaceAll("[0-9]+","").equals("") && !uIn.equals("")){ // if the user's input only contains numbers (removing all numbers results in nothing)
				picked = Integer.parseInt(uIn); // turn the input into an integer that cna be used to get pokemon
				if (0<picked && picked < pickablePokemon.size()+1){ // if the selected pokemon is within the range of selectable pokemon
					Pokemon selectedPokemon = pokedex.getPokemon(pokemonNumbers.get(picked-1));
					System.out.printf("You picked "+PkmnTools.ANSI_CYAN+"%s"+PkmnTools.ANSI_RESET+"!\n",selectedPokemon.getName());
					System.out.printf("Health: %s"+PkmnTools.ANSI_GREEN+" %d/%d"+PkmnTools.ANSI_RESET+"\nType: %s\nWeakness: %s\nResistance: %s\nMoves:\n",PkmnTools.makeBar(selectedPokemon.getHealth(),selectedPokemon.getMaxHealth()),selectedPokemon.getHealth(),selectedPokemon.getMaxHealth(),selectedPokemon.getType(), selectedPokemon.getWeakness(), selectedPokemon.getResistance());
					for(int i = 0; i<selectedPokemon.getMoves().size(); i++){
							System.out.printf(PkmnTools.ANSI_GREEN+"\t%3d."+PkmnTools.ANSI_RESET+" %s\n",i+1 ,selectedPokemon.getAttack(i));
					}
					System.out.println("\nConfirm? ("+PkmnTools.ANSI_GREEN+"Y"+PkmnTools.ANSI_WHITE+"/"+PkmnTools.ANSI_RED+"N"+PkmnTools.ANSI_RESET+")");
					uIn = PkmnArena.kb.nextLine();
					if(uIn.toLowerCase().equals("y")) {
						userParty.addPokemon(pokedex.getPokemon(pokemonNumbers.get(picked - 1))); // add the pokemon to the output party
						pickablePokemon.remove(picked - 1); // remove the name of the pokemon
						pokemonNumbers.remove(picked - 1); // remove the index of the pokemon
						numPicked++;
					}
				}
				else{
					System.out.println("Invalid Number");
				}
			}
		}
		return userParty;
	}
	
	public static Party computerParty(Pokedex pokedex, Party userPokemon, Party initial){
		//Party computerParty = new Party(0,""); //create a party with the default index of 0
		int computerPartySize = 6-initial.size();
		int nextPokemon; // the pokemon that will be added to the party
		ArrayList<Pokemon> remainingPokemon = pokedex.allPokemon(); //get all the pokemon from the pokedex
		remainingPokemon.removeAll(userPokemon.allMembers()); // remove the pokemon that the user already picked
		remainingPokemon.removeAll(initial.allMembers());
		for(int i = 0; i< computerPartySize; i++){ // pick the amount of pokemon defined in computerPartySize
			nextPokemon = PkmnArena.rand.nextInt(remainingPokemon.size()); // pick a random pokemon from remainingPokemon
			initial.addPokemon(remainingPokemon.get(nextPokemon)); // add the pokemon to the computerParty
			remainingPokemon.remove(nextPokemon); //remove the pokemon from the selectable pokemon
		}
		return initial;
	}
	
	public ArrayList<String> partyNames(){ // get the names of the pokemon from the party
		ArrayList<String> namesOut = new ArrayList<String>();
		for(Pokemon pkmn : party){ // goes through the party and adds the names of the pokemon in the party to the arraylist
			namesOut.add(pkmn.getName());
		}
		return namesOut;
	}
	public ArrayList<Pokemon> allMembers(){ // Get a copy of all the Pokemon in the party
		return new ArrayList<Pokemon>(party);
	}
	public String toString(){
		return toString(false);
	}
	public String toString(boolean detail){ // turn the party into a string
		String sOut = "";
		for(int i = 0; i<party.size();i++){
			sOut+=detail?party.get(i):party.get(i).getName()+"\n"; // print it with or without details based on detail
		}
		return sOut;
	}
	public int numAlive(){ // returns the amount of pokemon with hp > 0
		int alive = 0;
		for(Pokemon pkmn : this.party){ // goes through the party increasing alive by 1 with each pokemon that has more than 0 health
			if(pkmn.getHealth()>0){
				alive++;
			}
		}
		return alive;
	}
	public boolean pickActive(){
		return pickActive(true);
	}
	public boolean pickActive(boolean allowBack){ // deals with showing the user which pokemon they can to switch to and allows them to select one
		String uIn;
		boolean pickedNewPokemon = false; // false if the user decided to enter 0 to return
		ArrayList<Integer> livingPokemon =  new ArrayList<Integer>(); //the pokemon the user can pick that have more than 0 health
		for(int i = 0;i<party.size();i++){
			if(party.get(i).getHealth()>0 && active != i){
				livingPokemon.add(new Integer(i)); // add the index of the pokemon to the arraylist of pokemon the user can pick
			}
		}
		while(true){
			if(allowBack){
				System.out.println("0. Back");
			}
			System.out.println(PkmnArena.options[PkmnArena.POKEMON_DETAILS]?"s. Simple":"d. Details"); // add option for switching pokemon details on and off
			for(int i = 0; i<livingPokemon.size(); i++){
				System.out.printf("%d. %s\n",i+1,PkmnArena.options[PkmnArena.POKEMON_DETAILS]?party.get(livingPokemon.get(i)):party.get(livingPokemon.get(i)).getName()); // print the pokemon name or the pokemon details based on the options defined in pokemon arena
			}
			uIn = PkmnArena.kb.nextLine();
			if(uIn.equals("s") && PkmnArena.options[PkmnArena.POKEMON_DETAILS]){ // if the user is trying to turn off detailed pokemon info
				PkmnArena.options[PkmnArena.POKEMON_DETAILS] = false;
			}
			else if(uIn.equals("d") && !PkmnArena.options[PkmnArena.POKEMON_DETAILS]){ // if the user is trying to turn on detailed pokemon info
				PkmnArena.options[PkmnArena.POKEMON_DETAILS] = true;
			}
			else if(uIn.replaceAll("[0-9]+","").equals("") && !uIn.equals("")){
				if(Integer.parseInt(uIn)>0 && Integer.parseInt(uIn)<livingPokemon.size()+1) { // check if the pokemon the user is trying to select is a selectable pokemon and that the input contains only numbers
					setActive(livingPokemon.get(Integer.parseInt(uIn) - 1)); // set the user's selection to the active pokemon
					System.out.printf("%s, I choose you!\n", this.currentPokemon().getName());
					pickedNewPokemon = true;
					break;
				}
				else if(Integer.parseInt(uIn) == 0 && allowBack){ // if the user wants to go back
					break;
				}
			}
			else{
				//Action if input is not linked to option
			}
		}
		return pickedNewPokemon;
	}
	public int getActiveIndex(){ // get the index of the active pokemon
		return active;
	}
}