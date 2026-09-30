/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items.weapon;

import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.Berserk;
import pd.actors.buffs.MagicImmune;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.AscendedForm;
import pd.actors.hero.abilities.duelist.ElementalStrike;
import pd.actors.hero.abilities.rogue.ShadowClone;
import pd.actors.hero.spells.BodyForm;
import pd.actors.hero.spells.HolyWeapon;
import pd.actors.hero.spells.Smite;
import pd.actors.mobs.npcs.MirrorImage;
import pd.items.Generator;
import pd.items.Item;
import pd.items.KindOfWeapon;
import pd.items.bags.Bag;
import pd.items.rings.RingOfAccuracy;
import pd.items.rings.RingOfArcana;
import pd.items.rings.RingOfForce;
import pd.items.rings.RingOfFuror;
import pd.items.trinkets.ParchmentScrap;
import pd.items.trinkets.ShardOfOblivion;
import pd.items.weapon.curses.Annoying;
import pd.items.weapon.curses.Dazzling;
import pd.items.weapon.curses.Displacing;
import pd.items.weapon.curses.Explosive;
import pd.items.weapon.curses.Friendly;
import pd.items.weapon.curses.Polarized;
import pd.items.weapon.curses.Pressurized;
import pd.items.weapon.curses.Sacrificial;
import pd.items.weapon.curses.Wayward;
import pd.items.weapon.curses.Wondrous;
import pd.items.weapon.enchantments.Blazing;
import pd.items.weapon.enchantments.Blocking;
import pd.items.weapon.enchantments.Blooming;
import pd.items.weapon.enchantments.Chilling;
import pd.items.weapon.enchantments.Corrupting;
import pd.items.weapon.enchantments.Crystal;
import pd.items.weapon.enchantments.Elastic;
import pd.items.weapon.enchantments.Eldritch;
import pd.items.weapon.enchantments.EnchantmentDark2;
import pd.items.weapon.enchantments.EnchantmentDark;
import pd.items.weapon.enchantments.EnchantmentEarth2;
import pd.items.weapon.enchantments.EnchantmentEarth;
import pd.items.weapon.enchantments.EnchantmentEnergy2;
import pd.items.weapon.enchantments.EnchantmentEnergy;
import pd.items.weapon.enchantments.EnchantmentFire2;
import pd.items.weapon.enchantments.EnchantmentFire;
import pd.items.weapon.enchantments.EnchantmentIce2;
import pd.items.weapon.enchantments.EnchantmentIce;
import pd.items.weapon.enchantments.EnchantmentLight2;
import pd.items.weapon.enchantments.EnchantmentLight;
import pd.items.weapon.enchantments.EnchantmentShock2;
import pd.items.weapon.enchantments.EnchantmentShock;
import pd.items.weapon.enchantments.Grim;
import pd.items.weapon.enchantments.Kinetic;
import pd.items.weapon.enchantments.Lucky;
import pd.items.weapon.enchantments.Projecting;
import pd.items.weapon.enchantments.Shocking;
import pd.items.weapon.enchantments.Unstable;
import pd.items.weapon.enchantments.Vampiric;
import pd.items.weapon.enchantments.Venomous;
import pd.items.weapon.enchantments.Vorpal;
import pd.items.weapon.melee.MeleeWeapon;
import pd.items.weapon.melee.RunicBlade;
import pd.items.weapon.melee.Scimitar;
import pd.items.weapon.missiles.MissileWeapon;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

abstract public class Weapon extends KindOfWeapon {

	public float    ACC = 1f;	// Accuracy modifier
	public float	DLY	= 1f;	// Speed modifier
	public int      RCH = 1;    // Reach modifier (only applies to melee hits)

	public enum Augment {
		SPEED   (0.7f, 2/3f),
		DAMAGE  (1.5f, 5/3f),
		NONE	(1.0f, 1f);

		private float damageFactor;
		private float delayFactor;

		Augment(float dmg, float dly){
			damageFactor = dmg;
			delayFactor = dly;
		}

		public int damageFactor(int dmg){
			return Math.round(dmg * damageFactor);
		}

		public float damageFactor(float dmg){
			return dmg * damageFactor;
		}

		public float delayFactor(float dly){
			return dly * delayFactor;
		}
	}
	
	public Augment augment = Augment.NONE;

	protected int usesToID(){
		return 20;
	}
	protected float usesLeftToID = usesToID();
	protected float availableUsesToID = usesToID()/2f;
	
	public Enchantment enchantment;
	public boolean enchantHardened = false;
	public boolean curseInfusionBonus = false;
	public boolean masteryPotionBonus = false;
	private int spsDurability = 100;
	
	@Override
	public int proc( Char attacker, Char defender, int damage ) {

		if (attacker.buff(MagicImmune.class) == null) {
			Enchantment trinityEnchant = null;
			//only when it's the hero or a char that uses the hero's weapon
			if (Dungeon.hero.buff(BodyForm.BodyFormBuff.class) != null && this instanceof MeleeWeapon
					&& (attacker == Dungeon.hero || attacker instanceof MirrorImage || attacker instanceof ShadowClone.ShadowAlly)){
				trinityEnchant = Dungeon.hero.buff(BodyForm.BodyFormBuff.class).enchant();
				if (enchantment != null && trinityEnchant != null && trinityEnchant.getClass() == enchantment.getClass()){
					trinityEnchant = null;
				}
			}

			if (attacker instanceof Hero && isEquipped((Hero) attacker)
					&& attacker.buff(HolyWeapon.HolyWepBuff.class) != null){
				if (enchantment != null &&
						(((Hero) attacker).subClass == HeroSubClass.PALADIN || hasCurseEnchant())){
					damage = enchantment.proc(this, attacker, defender, damage);
				}
				if (defender.isAlive() && trinityEnchant != null){
					damage = trinityEnchant.proc(this, attacker, defender, damage);
				}
				if (defender.isAlive()) {
					int dmg = ((Hero) attacker).subClass == HeroSubClass.PALADIN ? 6 : 2;
					defender.damage(Math.round(dmg * Enchantment.genericProcChanceMultiplier(attacker)), HolyWeapon.INSTANCE);
				}

			} else {
				if (enchantment != null) {
					damage = enchantment.proc(this, attacker, defender, damage);
				}

				if (defender.isAlive() && trinityEnchant != null){
					damage = trinityEnchant.proc(this, attacker, defender, damage);
				}
			}

			if (attacker instanceof Hero && isEquipped((Hero) attacker) &&
					attacker.buff(Smite.SmiteTracker.class) != null && defender.isAlive()){
				defender.damage(Smite.bonusDmg((Hero) attacker, defender), Smite.INSTANCE);
			}
		}

		//do not progress toward ID in the specific case of a missile weapon with no parent using
		// up it's last shot, as in this case there's nothing left to ID anyway
		if (this instanceof MissileWeapon
				&& ((MissileWeapon) this).durabilityLeft() <= ((MissileWeapon) this).durabilityPerUse()
				&& ((MissileWeapon) this).parent == null){
			return damage;
		}
		
		if (!levelKnown && attacker == Dungeon.hero) {
			float uses = Math.min( availableUsesToID, Talent.itemIDSpeedFactor(Dungeon.hero, this) );
			availableUsesToID -= uses;
			usesLeftToID -= uses;
			if (usesLeftToID <= 0) {
				if (ShardOfOblivion.passiveIDDisabled()){
					if (usesLeftToID > -1){
						GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), name());
					}
					setIDReady();
				} else {
					identify();
					GLog.p(Messages.get(Weapon.class, "identify"));
					Badges.validateItemLevelAquired(this);
				}
			}
		}

		applySpsAbrasion(attacker);

		return damage;
	}

	public boolean usesSpsAbrasion() {
		Class<?>[] legacyWeapons = Generator.Category.WEAPON.classes;
		for (int i = 0; i < Math.min(30, legacyWeapons.length); i++) {
			if (getClass() == legacyWeapons[i]) return true;
		}
		return false;
	}

	private void applySpsAbrasion(Char attacker) {
		if (!Dungeon.isChallenged(Challenges.ABRASION) || attacker != Dungeon.hero
				|| !usesSpsAbrasion() || spsDurability <= 0) return;
		spsDurability--;
		if (spsDurability == 10) {
			GLog.n(Messages.get(KindOfWeapon.class, "almost_destory"));
		} else if (spsDurability == 0) {
			if (Dungeon.hero.belongings.weapon == this || Dungeon.hero.belongings.secondWep == this) {
				forceUnequipWithoutTime(Dungeon.hero);
			}
			GLog.n(Messages.get(KindOfWeapon.class, "destory"));
		}
	}

	public int spsDurability() {
		return spsDurability;
	}

	protected final void upgradeAbrasionDurability() {
		spsDurability += 10;
	}
	
	public void onHeroGainExp( float levelPercent, Hero hero ){
		levelPercent *= Talent.itemIDSpeedFactor(hero, this);
		if (!levelKnown && (isEquipped(hero) || this instanceof MissileWeapon)
				&& availableUsesToID <= usesToID()/2f) {
			//gains enough uses to ID over 0.5 levels
			availableUsesToID = Math.min(usesToID()/2f, availableUsesToID + levelPercent * usesToID());
		}
	}
	
	private static final String USES_LEFT_TO_ID = "uses_left_to_id";
	private static final String AVAILABLE_USES  = "available_uses";
	private static final String ENCHANTMENT	    = "enchantment";
	private static final String ENCHANT_HARDENED = "enchant_hardened";
	private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
	private static final String MASTERY_POTION_BONUS = "mastery_potion_bonus";
	private static final String AUGMENT	        = "augment";
	private static final String SPS_DURABILITY = "sps_durability";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( USES_LEFT_TO_ID, usesLeftToID );
		bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( ENCHANTMENT, enchantment );
		bundle.put( ENCHANT_HARDENED, enchantHardened );
		bundle.put( CURSE_INFUSION_BONUS, curseInfusionBonus );
		bundle.put( MASTERY_POTION_BONUS, masteryPotionBonus );
		bundle.put( AUGMENT, augment );
		bundle.put( SPS_DURABILITY, spsDurability );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		usesLeftToID = bundle.getFloat( USES_LEFT_TO_ID );
		availableUsesToID = bundle.getFloat( AVAILABLE_USES );
		enchantment = (Enchantment)bundle.get( ENCHANTMENT );
		enchantHardened = bundle.getBoolean( ENCHANT_HARDENED );
		curseInfusionBonus = bundle.getBoolean( CURSE_INFUSION_BONUS );
		masteryPotionBonus = bundle.getBoolean( MASTERY_POTION_BONUS );
		spsDurability = bundle.contains(SPS_DURABILITY)
				? bundle.getInt(SPS_DURABILITY)
				: 100 + 10 * Math.max(0, trueLevel());

		augment = bundle.getEnum(AUGMENT, Augment.class);
	}
	
	@Override
	public void reset() {
		super.reset();
		usesLeftToID = usesToID();
		availableUsesToID = usesToID()/2f;
	}

	@Override
	public boolean collect(Bag container) {
		if(super.collect(container)){
			if (Dungeon.hero != null && Dungeon.hero.isAlive() && isIdentified() && enchantment != null){
				Catalog.setSeen(enchantment.getClass());
				Statistics.itemTypesDiscovered.add(enchantment.getClass());
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Item identify(boolean byHero) {
		if (enchantment != null && byHero && Dungeon.hero != null && Dungeon.hero.isAlive()){
			Catalog.setSeen(enchantment.getClass());
			Statistics.itemTypesDiscovered.add(enchantment.getClass());
		}
		return super.identify(byHero);
	}

	public void setIDReady(){
		usesLeftToID = -1;
	}

	public boolean readyToIdentify(){
		return !isIdentified() && usesLeftToID <= 0;
	}
	
	@Override
	public float accuracyFactor(Char owner, Char target) {
		
		int encumbrance = 0;
		
		if( owner instanceof Hero ){
			encumbrance = STRReq() - ((Hero)owner).STR();
		}

		float ACC = this.ACC;

		if (owner.buff(Wayward.WaywardBuff.class) != null && enchantment instanceof Wayward){
			ACC /= 5;
		}

		return encumbrance > 0 ? (float)(ACC / Math.pow( 1.5, encumbrance )) : ACC;
	}
	
	@Override
	public float delayFactor( Char owner ) {
		return baseDelay(owner) * (1f/speedMultiplier(owner));
	}

	protected float baseDelay( Char owner ){
		float delay = augment.delayFactor(this.DLY);
		if (owner instanceof Hero) {
			int encumbrance = STRReq() - ((Hero)owner).STR();
			if (encumbrance > 0){
				delay *= Math.pow( 1.2, encumbrance );
			}
		}

		return delay;
	}

	protected float speedMultiplier(Char owner ){
		float multi = RingOfFuror.attackSpeedMultiplier(owner);

		if (owner.buff(Scimitar.SwordDance.class) != null){
			multi += 0.6f;
		}

		return multi;
	}

	@Override
	public int reachFactor(Char owner) {
		return reachFactor(owner, RCH);
	}

	protected int reachFactor(Char owner, int baseReach) {
		int reach = baseReach + (owner instanceof Hero ? RingOfAccuracy.reachBonus(owner) : 0);
		if (owner instanceof Hero && RingOfForce.fightingUnarmed((Hero) owner)){
			reach = 1; //brawlers stance benefits from enchantments, but not innate reach
			if (!RingOfForce.unarmedGetsWeaponEnchantment((Hero) owner)){
				return reach;
			}
		}
		if (owner instanceof Hero && owner.buff(AscendedForm.AscendBuff.class) != null){
			reach += 2;
		}
		if (hasEnchant(Projecting.class, owner)){
			return reach + Math.round(Enchantment.genericProcChanceMultiplier(owner));
		} else {
			return reach;
		}
	}

	public int STRReq(){
		return STRReq(level());
	}

	public abstract int STRReq(int lvl);

	protected static int STRReq(int tier, int lvl){
		lvl = Math.max(0, lvl);

		//strength req decreases at +1,+3,+6,+10,etc.
		return (8 + tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}

	@Override
	public int level() {
		int level = super.level();
		if (curseInfusionBonus) level += 1 + level/6;
		return level;
	}
	
	@Override
	public Item upgrade() {
		return upgrade(false);
	}
	
	public Item upgrade(boolean enchant ) {

		if (enchant){
			if (enchantment == null){
				enchant(Enchantment.random());
			}
		} else if (enchantment != null) {
			//chance to lose harden buff is 10/20/40/80/100% when upgrading from +6/7/8/9/10
			if (enchantHardened){
				if (level() >= 6 && Random.Float(10) < Math.pow(2, level()-6)){
					enchantHardened = false;
				}

			//chance to remove curse is a static 33%
			} else if (hasCurseEnchant()) {
				if (Random.Int(3) == 0) enchant(null);

			//otherwise chance to lose enchant is 10/20/40/80/100% when upgrading from +4/5/6/7/8
			} else if (level() >= 4 && Random.Float(10) < Math.pow(2, level()-4)){
				enchant(null);
			}

			//if we still have a crystal enchant, repair it (just like thrown weapon repair)
			if (enchantment instanceof Crystal){
				((Crystal) enchantment).repair(this, false, 100);
			}
		}
		
		cursed = false;
		if (usesSpsAbrasion()) upgradeAbrasionDurability();

		return super.upgrade();
	}
	
	@Override
	public String name() {
		if (isEquipped(Dungeon.hero) && !hasCurseEnchant() && Dungeon.hero.buff(HolyWeapon.HolyWepBuff.class) != null
			&& (Dungeon.hero.subClass != HeroSubClass.PALADIN || enchantment == null)){
				return Messages.get(HolyWeapon.class, "ench_name", super.name());
			} else {
				return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.name(super.name()) : super.name();

		}
	}
	
	@Override
	public Item random() {
		//+0: 75% (3/4)
		//+1: 20% (4/20)
		//+2: 5%  (1/20)
		int n = 0;
		if (Random.Int(4) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}
		level(n);

		//we use a separate RNG here so that variance due to things like parchment scrap
		//does not affect levelgen
		Random.pushGenerator(Random.Long());

			//30% chance to be cursed
			//10% chance to be enchanted
			float effectRoll = Random.Float();
			if (effectRoll < 0.3f * ParchmentScrap.curseChanceMultiplier()) {
				enchant(Enchantment.randomCurse());
				cursed = true;
			} else if (effectRoll >= 1f - (0.1f * ParchmentScrap.enchantChanceMultiplier())){
				enchant();
			}

		Random.popGenerator();

		return this;
	}
	
	public Weapon enchant( Enchantment ench ) {
		if (ench == null || !ench.curse()) curseInfusionBonus = false;
		enchantment = ench;
		updateQuickslot();
		if (ench != null && isIdentified() && Dungeon.hero != null
				&& Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(this)){
			Catalog.setSeen(ench.getClass());
			Statistics.itemTypesDiscovered.add(ench.getClass());
		}
		return this;
	}

	public Weapon enchant() {

		Class<? extends Enchantment> oldEnchantment = enchantment != null ? enchantment.getClass() : null;
		Enchantment ench = Enchantment.random( oldEnchantment );

		return enchant( ench );
	}

	public boolean hasEnchant(Class<?extends Enchantment> type, Char owner) {
		if (owner.buff(MagicImmune.class) != null) {
			return false;
		} else if (enchantment != null
				&& !enchantment.curse()
				&& owner instanceof Hero
				&& isEquipped((Hero) owner)
				&& owner.buff(HolyWeapon.HolyWepBuff.class) != null
				&& ((Hero) owner).subClass != HeroSubClass.PALADIN) {
			return false;
		} else if (owner.buff(BodyForm.BodyFormBuff.class) != null
				&& owner.buff(BodyForm.BodyFormBuff.class).enchant() != null
				&& owner.buff(BodyForm.BodyFormBuff.class).enchant().getClass().equals(type)){
			return true;
		} else if (enchantment != null) {
			return enchantment.getClass() == type;
		} else {
			return false;
		}
	}
	
	//these are not used to process specific enchant effects, so magic immune doesn't affect them
	public boolean hasGoodEnchant(){
		return enchantment != null && !enchantment.curse();
	}

	public boolean hasCurseEnchant(){
		return enchantment != null && enchantment.curse();
	}

	private static ItemSprite.Glowing HOLY = new ItemSprite.Glowing( 0xFFFF00 );

	@Override
	public ItemSprite.Glowing glowing() {
		if (isEquipped(Dungeon.hero) && !hasCurseEnchant() && Dungeon.hero.buff(HolyWeapon.HolyWepBuff.class) != null
				&& (Dungeon.hero.subClass != HeroSubClass.PALADIN || enchantment == null)){
			return HOLY;
		} else {
			return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.glowing() : null;
		}
	}

	public static abstract class Enchantment implements Bundlable {

		public static final Class<?>[] common = new Class<?>[]{
				EnchantmentFire.class, EnchantmentEarth.class, EnchantmentDark.class,
				EnchantmentEnergy.class, EnchantmentIce.class
		};

		public static final Class<?>[] uncommon = new Class<?>[]{
				EnchantmentShock.class, EnchantmentLight.class, EnchantmentFire2.class,
				EnchantmentEarth2.class, EnchantmentDark2.class
		};

		public static final Class<?>[] rare = new Class<?>[]{
				EnchantmentEnergy2.class, EnchantmentIce2.class,
				EnchantmentShock2.class, EnchantmentLight2.class
		};

		public static final float[] typeChances = new float[]{
				5, 5, 4 //group weights preserve equal 1/14 odds per legacy enchantment
		};

		public static final Class<?>[] curses = new Class<?>[]{
				Annoying.class, Displacing.class, Dazzling.class, Explosive.class, Friendly.class,
				Polarized.class, Pressurized.class, Sacrificial.class, Wayward.class, Wondrous.class
		};
			
		public abstract int proc( Weapon weapon, Char attacker, Char defender, int damage );

		protected float procChanceMultiplier( Char attacker ){
			return genericProcChanceMultiplier( attacker );
		}

		public static float genericProcChanceMultiplier( Char attacker ){
			float multi = RingOfArcana.enchantPowerMultiplier(attacker);
			Berserk rage = attacker.buff(Berserk.class);
			if (rage != null) {
				multi = rage.enchantFactor(multi);
			}

			if (attacker.buff(RunicBlade.RunicSlashTracker.class) != null){
				multi += attacker.buff(RunicBlade.RunicSlashTracker.class).boost;
				attacker.buff(RunicBlade.RunicSlashTracker.class).detach();
			}

			if (attacker.buff(Smite.SmiteTracker.class) != null){
				multi += 3f;
			}

			if (attacker.buff(ElementalStrike.DirectedPowerTracker.class) != null){
				multi += attacker.buff(ElementalStrike.DirectedPowerTracker.class).enchBoost;
				attacker.buff(ElementalStrike.DirectedPowerTracker.class).detach();
			}

			if (attacker.buff(Talent.SpiritBladesTracker.class) != null
					&& ((Hero)attacker).pointsInTalent(Talent.SPIRIT_BLADES) == 4){
				multi += 0.1f;
			}
			if (attacker.buff(Talent.StrikingWaveTracker.class) != null
					&& ((Hero)attacker).pointsInTalent(Talent.STRIKING_WAVE) == 4){
				multi += 0.2f;
			}

			return multi;
		}

		public String name() {
			if (!curse())
				return name( Messages.get(this, "enchant"));
			else
				return name( Messages.get(Item.class, "curse"));
		}

		public String name( String weaponName ) {
			return Messages.get(this, "name", weaponName);
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		public boolean curse() {
			return false;
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
		}
		
		public abstract ItemSprite.Glowing glowing();
		
		@SuppressWarnings("unchecked")
		public static Enchantment random( Class<? extends Enchantment> ... toIgnore ) {
			switch(Random.chances(typeChances)){
				case 0: default:
					return randomCommon( toIgnore );
				case 1:
					return randomUncommon( toIgnore );
				case 2:
					return randomRare( toIgnore );
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomCommon( Class<? extends Enchantment> ... toIgnore ) {
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(common));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomUncommon( Class<? extends Enchantment> ... toIgnore ) {
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(uncommon));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomRare( Class<? extends Enchantment> ... toIgnore ) {
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(rare));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}

		@SuppressWarnings("unchecked")
		public static Enchantment randomCurse( Class<? extends Enchantment> ... toIgnore ){
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(curses));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}
		
	}
}
