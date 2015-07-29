package com.spartanlaboratories.engine.structure;

public class Constants {
	public static final String versionString = "SLE v. A2.0.0 Pre-Release remake";
	//STATS
	public static final int numberOfPowerUps = 6;
	public static final int statsSize = 50;
	public static int numConstantStats;
	public static final int level = 1;
	public static final int experience = 2;
	public static final int health = 3;
	public static final int mana = 4;
	public static final int gold = 11;
	public static final int goldGiven = 12;
	public static final int maxHealth = 14;
	public static final int maxMana = 15;
	public static final int damage = 16;
	public static final int attackRange = 17;
	public static final int visibilityRange = 18;
	public static final int baseAttackTime = 19;
	public static final int baseAttackSpeed = 20;
	public static final int baseAnimationTime = 21;
	public static final int attackSpeed = 22;
	public static final int attackCD = 23;
	public static final int animationCD = 24;
	public static final int retractionCD = 25;
	public static final int startingDamage = 26;
	public static final int baseDamage = 27;
	public static final int manaRegen = 28;
	public static final int healthRegen = 29;
	public static final int experienceGiven = 30;
	public static final int bonusDamage = 31;
	public static final int armor = 32;
	public static final int baseArmor = 33;
	public static final int bonusArmor = 35;
	public static final int baseHealth = 120;
	public static final int reliableGold = 38;
	public static final int unreliableGold = 39;
	public static final int abilityPoints = 40;
	public static final int cleave = 41;
	public static final int lifesteal = 42;
	public static final int manaBurn = 43;
	public static final int manaBurnPercentDamage = 44;
	public static final int autoAttackMoveSlow = 45;
	public static final int autoAttackAttackSlow = 46;
	public static final int autoAttackSlow = 47;
	public static final int desolation = 48;
	public static final int evasion = 49;
	//RGB
	public static final int red = 0;
	public static final int green = 1;
	public static final int blue= 2;
	//Alive Permissions
	public static final int numberOfPermissions = 4;
	public static final int movementAllowed = 0;
	public static final int spellCastAllowed = 1;
	public static final int autoAttackAllowed = 2;
	public static final int channelingAllowed = 3;
	
	//the amount by which actors appear smaller on the minimap
	public static final double mapMultiplicationFactor = 9;
	public static final double mapToScreenSizeProportion = 0.1;
	public static int convertString(String string){
		switch(string){
		case "health": return health;
		case "mana": return mana;
		case "gold": return gold;
		default: 
			System.out.println("The string: '" + string + "' is not accounted for in the function convertString()");
			return -1;
		}
	}
}
