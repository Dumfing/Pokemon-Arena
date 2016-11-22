//Pokemon.java
//Aaron Li
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Pokemon{
	private int hp, type, resistance, weakness;
	private String name;
	
	private boolean[]debuffs = {false,false};
	private int energy = 50;
	private HashMap<String,Attack> moves = new HashMap<String,Attack>();
	
	public static final int NO_TYPE = 0;
	public static final int EARTH = 1;
	public static final int FIRE = 2;
	public static final int LEAF = 3;
	public static final int WATER = 4;
	public static final int FIGHTING = 5;
	public static final int ELECTRIC = 6;
	public static final String[] displayTypes = {"None","Earth","Fire","Grass","Water","Fighting","Electric"};
	
	public Pokemon(String name, int hp,int type, int resistance, int weakness){
		this.name = name;
		this.hp = hp;
		this.type = type;
		this.resistance = resistance;
		this.weakness = weakness;
	}
	public Pokemon(Pokemon pkmnIn){
		this.name = new String(pkmnIn.name);
		this.hp = pkmnIn.hp;
		this.type = pkmnIn.type;
		this.resistance = pkmnIn.resistance;
		this.weakness = pkmnIn.weakness;
		this.energy = pkmnIn.energy;
		this.moves = new HashMap<String,Attack>(pkmnIn.moves);
	}
	public void stun(){
		debuffs[Attack.STUN_STATUS] = true;
	}
	public void disable(){
		debuffs[Attack.DISABLE_STATUS] = true;
	}
	public String getName(){
		return this.name;
	}
	public void addAttack(String name, int cost, int damage, int special){
		moves.put(name,new Attack(name,cost,damage,special));
	}
	public void recharge(int amount){
		energy = Math.min(50,energy+amount);
	}
	public boolean attack(Pokemon target, Attack attack){
		boolean success = false;
		int damage = target.hp-(debuffs[Attack.DISABLE_STATUS]? attack.getDamage(): attack.getDamage()-10); // damage without disable and with type advantages
		if(this.energy>=attack.getCost()){
			this.energy -= attack.getCost();
			if(this.type == target.weakness){
				target.setHealth(target.hp-damage*2);
			}
			else if(this.type == target.resistance){
				target.setHealth(target.hp-damage/2);
			}
			else{
				target.setHealth(target.hp-damage);
			}
			success = true;
		}
		return success;
	}
	
	public void setHealth(int health){
		this.hp = Math.max(0,health);
	}
	public int getHealth(){
		return this.hp;
	}
	public String[] attacks(){
		String[] sOut = moves.keySet().toArray(new String[moves.keySet().size()]);
		return sOut;
	}
	public Attack getAttack(String attackName){
		return moves.get(attackName);
	}
	public String toString(){
		String sOut = "";
		sOut+=name+"\nHP: "+hp+"\nEnergy: "+energy+"\nType: "+displayTypes[type]+"\nResistance: "+displayTypes[resistance]+"\nWeakness: "+displayTypes[weakness]+"\nStunned: "+debuffs[0]+"\nDisabled: "+debuffs[1]+"\n\n";
		for(String moveName : moves.keySet()){
			sOut+=moves.get(moveName).toString()+"\n\n";
		}
		return sOut;
	}

}