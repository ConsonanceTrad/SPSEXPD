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

package pd.items.artifacts;

import pd.atlas.items.GroundFunctionalFallingDict;
import pd.atlas.items.EquipmentJewelleryArtifactDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.CorrosiveGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.AllyBuff;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.MagicImmune;
import pd.actors.buffs.Regeneration;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.hero.spells.Stasis;
import pd.actors.mobs.RedWraith;
import pd.actors.mobs.Wraith;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.actors.mobs.npcs.Ghost;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.PET;
import pd.effects.CellEmitter;
import pd.effects.FloatingText;
import pd.effects.Speck;
import pd.effects.particles.ElmoParticle;
import pd.effects.particles.ShaftParticle;
import pd.items.Item;
import pd.items.armor.Armor;
import pd.items.bags.Bag;
import pd.items.rings.RingOfEnergy;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.items.scrolls.ScrollOfRetribution;
import pd.items.weapon.Weapon;
import pd.items.weapon.melee.MeleeWeapon;
import pd.items.weapon.melee.special.WraithBreath;
import pd.journal.Catalog;
import pd.levels.VaultLevel;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.AlchemyScene;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.sprites.GhostSprite;
import pd.sprites.ItemSprite;
import pd.sprites.NewGhostSprite;
import pd.ui.BossHealthBar;
import pd.ui.ItemButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.utils.GLog;
import pd.windows.IconTitle;
import pd.windows.WndBag;
import pd.windows.WndInfoItem;
import pd.windows.WndQuest;
import pd.windows.WndUseItem;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class DriedRose extends Artifact {

	{
		image = EquipmentJewelleryArtifactDict.ARTIFACT_ROSE1;

		levelCap = 10;

		charge = 200;
		chargeCap = 200;

		defaultAction = AC_SUMMON;
	}

	protected static boolean talkedTo = false;
	protected static boolean firstSummon = false;
	
	private GhostHero ghost = null;
	private int ghostID = 0;
	
	private MeleeWeapon weapon = null;
	private Armor armor = null;

	public int droppedPetals = 0;

	public static final String AC_SUMMON = "SUMMON";
	public static final String AC_DIRECT = "DIRECT";
	public static final String AC_OUTFIT = "OUTFIT";
	public static final String AC_SOULBLESS = "SOULBLESS";

	public DriedRose() {
		talkedTo = firstSummon = false;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (!Ghost.Quest.completed()){
			actions.remove(AC_EQUIP);
			return actions;
		}
		if (isEquipped( hero )
				&& charge == chargeCap
				&& !cursed) {
			actions.add(AC_SUMMON);
		}
		if (isIdentified() && !cursed){
			actions.add(AC_OUTFIT);
		}
		if (level() > 0 && !isEquipped(hero)) {
			actions.add(AC_SOULBLESS);
		}
		
		return actions;
	}

	@Override
	public String defaultAction() {
		return AC_SUMMON;
	}

	@Override
	public void execute( Hero hero, String action ) {
		if (!AC_SUMMON.equals(action) && !AC_DIRECT.equals(action)
				&& !AC_OUTFIT.equals(action) && !AC_SOULBLESS.equals(action)) {
			super.execute(hero, action);
			return;
		}

		if (action.equals(AC_SUMMON)) {

			if (!Ghost.Quest.completed())   GameScene.show(new WndUseItem(null, this));
			else if (!isEquipped( hero ))   GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (charge != chargeCap)   GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                GLog.i( Messages.get(this, "cursed") );
			else {
				ArrayList<Integer> spawnPoints = new ArrayList<>();
				for (int i = 0; Dungeon.level != null && i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = hero.pos + PathFinder.NEIGHBOURS8[i];
					if (Dungeon.level.insideMap(p) && Actor.findChar(p) == null
							&& (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
						spawnPoints.add(p);
					}
				}

				if (spawnPoints.size() > 0) {
					ghost = hero.subClass == HeroSubClass.LEADER
							? new SuperGhostHero(this) : new GhostHero(this);
					ghostID = ghost.id();
					ghost.pos = Random.element(spawnPoints);

					GameScene.add(ghost, 1f);
					Dungeon.level.occupyCell(ghost);
					
					if (ghost.sprite != null) {
						CellEmitter.get(ghost.pos).start( ShaftParticle.FACTORY, 0.3f, 4 );
						CellEmitter.get(ghost.pos).start( Speck.factory(Speck.LIGHT), 0.2f, 3 );
					}

					hero.spend(1f);
					hero.busy();
					if (hero.sprite != null) hero.sprite.operate(hero.pos);
					charge = 0;
					partialCharge = 0;
					updateQuickslot();

				} else
					GLog.i( Messages.get(this, "no_space") );
			}

		} else if (action.equals(AC_DIRECT)){
			if (ghost == null && ghostID != 0){
				findGhost();
			}
			if (ghost != null && ghost != Stasis.getStasisAlly()){
				GameScene.selectCell(ghostDirector);
			}
			
		} else if (action.equals(AC_OUTFIT)){
			GameScene.show( new WndGhostHero(this) );
		} else if (action.equals(AC_SOULBLESS) && level() > 0 && !isEquipped(hero)) {
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
			hero.petLevel += level()/2;
			LegacyPet pet = LegacyPet.active();
			if (pet != null) pet.updateStats(false);
			GLog.p(Messages.get(PET.class, "levelup"));
			hero.spendAndNext(1f);
			detach(hero.belongings.backpack);
		}
	}

	private void findGhost(){
		Actor a = Actor.findById(ghostID);
		if (a != null){
			ghost = (GhostHero)a;
		} else {
			if (Stasis.getStasisAlly() instanceof GhostHero){
				ghost = (GhostHero) Stasis.getStasisAlly();
				ghostID = ghost.id();
			} else {
				ghostID = 0;
			}
		}
	}
	
	public int ghostStrength(){
		return 30;
	}

	@Override
	public String desc() {
		if (!Ghost.Quest.completed() && !isIdentified()){
			return Messages.get(this, "desc_no_quest");
		}
		
		String desc = super.desc();

		if (isEquipped( Dungeon.hero )){
			if (!cursed){

				if (level() < levelCap)
					desc+= "\n\n" + Messages.get(this, "desc_hint");

			} else {
				desc += "\n\n" + Messages.get(this, "desc_cursed");
			}
		}

		return desc;
	}

	@Override
	public String status() {
		return super.status();
	}
	
	@Override
	protected ArtifactBuff passiveBuff() {
		return new roseRecharge();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		// SPS-PD 0.9.8 charges this artifact only through roseRecharge.
	}
	
	@Override
	public Item upgrade() {
		if (level() >= 9)
			image = EquipmentJewelleryArtifactDict.ARTIFACT_ROSE3;
		else if (level() >= 4)
			image = EquipmentJewelleryArtifactDict.ARTIFACT_ROSE2;

		//For upgrade transferring via well of transmutation
		droppedPetals = Math.max( level(), droppedPetals );
		
		return super.upgrade();
	}
	
	public Weapon ghostWeapon(){
		return weapon;
	}
	
	public Armor ghostArmor(){
		return armor;
	}

	private static final String TALKEDTO =      "talkedto";
	private static final String FIRSTSUMMON =   "firstsummon";
	private static final String GHOSTID =       "ghostID";
	private static final String PETALS =        "petals";
	
	private static final String WEAPON =        "weapon";
	private static final String ARMOR =         "armor";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);

		bundle.put( TALKEDTO, talkedTo );
		bundle.put( FIRSTSUMMON, firstSummon );
		bundle.put( PETALS, droppedPetals );
		
		if (weapon != null) bundle.put( WEAPON, weapon );
		if (armor != null)  bundle.put( ARMOR, armor );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);

		talkedTo = bundle.getBoolean( TALKEDTO );
		firstSummon = bundle.getBoolean( FIRSTSUMMON );
		// Read early SPS-SPD saves without writing the Shattered-only field again.
		ghostID = bundle.contains(GHOSTID) ? bundle.getInt(GHOSTID) : 0;
		droppedPetals = bundle.getInt( PETALS );
		
		if (bundle.contains(WEAPON)) weapon = (MeleeWeapon)bundle.get( WEAPON );
		if (bundle.contains(ARMOR))  armor = (Armor)bundle.get( ARMOR );
	}

	public class roseRecharge extends ArtifactBuff {

		@Override
		public boolean act() {
			
			spend( TICK );
			
			if (charge < chargeCap && !cursed) {
				partialCharge += 2/5f;
				if (partialCharge > 1){
					charge++;
					partialCharge--;
					if (charge == chargeCap){
						partialCharge = 0f;
						GLog.p( Messages.get(DriedRose.class, "charged") );
					}
				}
			} else if (cursed && Random.Int(100) == 0) {

				ArrayList<Integer> spawnPoints = new ArrayList<>();

				for (int i = 0; Dungeon.level != null && i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = target.pos + PathFinder.NEIGHBOURS8[i];
					if (Dungeon.level.insideMap(p) && Actor.findChar(p) == null
							&& (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
						spawnPoints.add(p);
					}
				}

				if (spawnPoints.size() > 0) {
					RedWraith.spawnAt(Random.element(spawnPoints));
					Sample.INSTANCE.play(Assets.Sounds.CURSED);
				}

			}

			updateQuickslot();

			return true;
		}
	}
	
	public CellSelector.Listener ghostDirector = new CellSelector.Listener(){
		
		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			
			Sample.INSTANCE.play( Assets.Sounds.GHOST );

			ghost.directTocell(cell);

		}
		
		@Override
		public String prompt() {
			return  "\"" + Messages.get(GhostHero.class, "direct_prompt") + "\"";
		}
	};

	public static class Petal extends Item {

		{
			stackable = true;
			dropsDownHeap = true;
			
			image = GroundFunctionalFallingDict.PETAL_0;
		}

		@Override
		public boolean doPickUp(Hero hero, int pos) {
			Catalog.setSeen(getClass());
			Statistics.itemTypesDiscovered.add(getClass());
			DriedRose rose = hero.belongings.getItem( DriedRose.class );

			if (rose == null){
				GLog.w( Messages.get(this, "no_rose") );
				return false;
			} if ( rose.level() >= rose.levelCap ){
				GLog.i( Messages.get(this, "no_room") );
				hero.spendAndNext(pickupDelay());
				return true;
			} else {

				rose.upgrade();
				Catalog.countUse(rose.getClass());
				if (rose.level() == rose.levelCap) {
					GLog.p( Messages.get(this, "maxlevel") );
				} else
					GLog.i( Messages.get(this, "levelup") );

				Sample.INSTANCE.play( Assets.Sounds.DEWDROP );
				GameScene.pickUp(this, pos);
				hero.spendAndNext(pickupDelay());
				return true;

			}
		}

		@Override
		public boolean isUpgradable() {
			return false;
		}

		@Override
		public boolean isIdentified() {
			return true;
		}

	}

	public static class GhostHero extends DirectableAlly {

		{
			spriteClass = GhostSprite.class;

			flying = true;
			
			state = WANDERING;
			
			loot = WraithBreath.class;
			lootChance = .2f;
		}
		
		protected DriedRose rose = null;
		
		public GhostHero(){
			super();
		}

		public GhostHero(DriedRose rose){
			super();
			this.rose = rose;
			updateRose();
			HP = HT;
		}

		@Override
		public void defendPos(int cell) {
			yell(Messages.get(this, "directed_position_" + Random.IntRange(1, 5)));
			super.defendPos(cell);
		}

		@Override
		public void followHero() {
			yell(Messages.get(this, "directed_follow_" + Random.IntRange(1, 5)));
			super.followHero();
		}

		@Override
		public void targetChar(Char ch) {
			yell(Messages.get(this, "directed_attack_" + Random.IntRange(1, 5)));
			super.targetChar(ch);
		}

		protected void updateRose(){
			if (rose == null) {
				rose = Dungeon.hero == null ? null : Dungeon.hero.belongings.getItem(DriedRose.class);
				if (rose != null) {
					rose.ghost = this;
					rose.ghostID = id();
				}
			}
			
			defenseSkill = baseDefenseSkill();
			if (rose == null) return;
			HT = baseHealth() + healthPerLevel()*rose.level();
		}

		protected int baseHealth() { return 20; }
		protected int healthPerLevel() { return 10; }
		protected int baseDefenseSkill() { return 5; }
		protected int baseAttackSkill() { return 10; }
		protected int decayDamage() {
			return rose == null || Dungeon.hero == null || !rose.isEquipped(Dungeon.hero) ? 5 : 1;
		}

		public MeleeWeapon weapon(){
			if (rose != null)   return rose.weapon;
			else                return null;
		}

		public void clearWeapon(){
			if (rose != null) rose.weapon = null;
		}

		public Armor armor(){
			if (rose != null)   return rose.armor;
			else                return null;
		}

		@Override
		protected boolean act() {
			updateRose();
			int decay = decayDamage();
			if (decay > 0) damage(decay, new NoRoseDamage());
			
			if (!isAlive()) {
				return true;
			}
			if (Dungeon.hero == null || !Dungeon.hero.isAlive()) {
				if (sprite != null) sprite.die();
				destroy();
				return true;
			}
			return super.act();
		}

		public static class NoRoseDamage{}

		@Override
		public int attackSkill(Char target) {
			
			int acc = baseAttackSkill();
			
			if (weapon() != null){
				acc *= weapon().accuracyFactor( this, target );
			}
			
			return acc;
		}
		
		@Override
		public float attackDelay() {
			float delay = super.attackDelay();
			if (weapon() != null){
				delay *= weapon().delayFactor(this);
			}
			return delay;
		}
		
		@Override
		protected boolean canAttack(Char enemy) {
			return super.canAttack(enemy) || (weapon() != null && weapon().canReach(this, enemy.pos));
		}
		
		@Override
		public int damageRoll() {
			int dmg = 0;
			if (weapon() != null){
				dmg += Random.NormalIntRange(weapon().min(), weaponDamageMax());
			} else {
				dmg += Random.NormalIntRange(0, unarmedDamageMax());
			}
			
			return dmg;
		}

		protected int weaponDamageMax() { return weapon().max(); }
		protected int unarmedDamageMax() { return 5; }
		
		@Override
		public int attackProc(Char enemy, int damage) {
			if (weapon() != null) {
				weapon().proc(this, enemy, damage);
				return damage;
			}
			return super.attackProc(enemy, damage);
		}
		
		@Override
		public int defenseProc(Char enemy, int damage) {
			return super.defenseProc(enemy, damage);
		}
		
		@Override
		public void damage(int dmg, Object src) {
			super.damage( dmg, src );
			
			//for the rose status indicator
			Item.updateQuickslot();
		}
		
		@Override
		public float speed() {
			return super.speed();
		}
		
		@Override
		public int defenseSkill(Char enemy) {
			int defense = baseDefenseSkill();
			return armor() == null ? defense : Math.round(armor().evasionFactor(this, defense));
		}
		
		@Override
		public int drRoll() {
			int dr = 0;
			if (armor() != null){
				int min = armorDamageMin();
				int max = armor().DRMax();
				dr += Random.NormalIntRange(Math.min(min, max), Math.max(min, max));
			}
			return dr;
		}

		protected int armorDamageMin() { return armor().DRMin(); }

		@Override
		public int glyphLevel(Class<? extends Armor.Glyph> cls) {
			return super.glyphLevel(cls);
		}

		@Override
		public boolean interact(Char c) {
			return super.interact(c);
		}

		@Override
		public void die(Object cause) {
			if (Dungeon.level != null && Dungeon.level.insideMap(pos)) {
				Dungeon.level.drop(new Petal(), pos);
			}
			super.die(cause);
		}

		@Override
		public void destroy() {
			super.destroy();
		}
		
		public void sayAppeared(){
			if (Dungeon.hero.buff(AscensionChallenge.class) != null){
				yell( Messages.get( this, "dialogue_ascension_" + Random.IntRange(1, 6) ));

			} else {

				int legacyDepth = Dungeon.legacyDepth();
				int depth = (legacyDepth - 1) / 5;

				//only some lines are said on the first floor of a depth
				int variant = legacyDepth % 5 == 1 ? Random.IntRange(1, 3) : Random.IntRange(1, 6);

				switch (depth) {
					case 0:
						yell(Messages.get(this, "dialogue_sewers_" + variant));
						break;
					case 1:
						yell(Messages.get(this, "dialogue_prison_" + variant));
						break;
					case 2:
						yell(Messages.get(this, "dialogue_caves_" + variant));
						break;
					case 3:
						yell(Messages.get(this, "dialogue_city_" + variant));
						break;
					case 4:
					default:
						yell(Messages.get(this, "dialogue_halls_" + variant));
						break;
				}
			}
			if (ShatteredPixelDungeon.scene() instanceof GameScene) {
				Sample.INSTANCE.play( Assets.Sounds.GHOST );
			}
		}
		
		public void sayBoss(){
			int depth = (Dungeon.legacyDepth() - 1) / 5;
			
			switch(depth){
				case 0:
					yell( Messages.get( this, "seen_goo_" + Random.IntRange(1, 3) ));
					break;
				case 1:
					yell( Messages.get( this, "seen_tengu_" + Random.IntRange(1, 3) ));
					break;
				case 2:
					yell( Messages.get( this, "seen_dm300_" + Random.IntRange(1, 3) ));
					break;
				case 3:
					yell( Messages.get( this, "seen_king_" + Random.IntRange(1, 3) ));
					break;
				case 4: default:
					yell( Messages.get( this, "seen_yog_" + Random.IntRange(1, 3) ));
					break;
			}
			Sample.INSTANCE.play( Assets.Sounds.GHOST );
		}
		
		public void sayDefeated(){
			if (BossHealthBar.isAssigned()){
				yell( Messages.get( this, "defeated_by_boss_" + Random.IntRange(1, 3) ));
			} else {
				yell( Messages.get( this, "defeated_by_enemy_" + Random.IntRange(1, 3) ));
			}
			Sample.INSTANCE.play( Assets.Sounds.GHOST );
		}
		
		public void sayHeroKilled(){
			yell( Messages.get( this, "player_killed_" + Random.IntRange(1, 3) ));
			GLog.newLine();
			Sample.INSTANCE.play( Assets.Sounds.GHOST );
		}
		
		public void sayAnhk(){
			yell( Messages.get( this, "blessed_ankh_" + Random.IntRange(1, 3) ));
			Sample.INSTANCE.play( Assets.Sounds.GHOST );
		}
		
		{
			immunities.add( ToxicGas.class );
			immunities.add( Burning.class );
			immunities.add( ScrollOfPsionicBlast.class );
		}

		@Override
		public synchronized boolean add(Buff buff) {
			return false;
		}

	}

	/** SPS-PD's stronger rose ghost for the Leader subclass. */
	public static class SuperGhostHero extends GhostHero {
		{
			spriteClass = NewGhostSprite.class;
		}

		public SuperGhostHero() {
			super();
		}

		public SuperGhostHero(DriedRose rose) {
			super(rose);
			updateRose();
			HP = HT;
		}

		@Override protected int baseHealth() { return 40; }
		@Override protected int healthPerLevel() { return 15; }
		@Override protected int baseDefenseSkill() { return 10; }
		@Override protected int baseAttackSkill() { return 25; }
		@Override protected int decayDamage() {
			return rose == null || Dungeon.hero == null || !rose.isEquipped(Dungeon.hero) ? 5 : 0;
		}
		@Override protected int weaponDamageMax() { return 2 * weapon().max(); }
		@Override protected int unarmedDamageMax() { return 10; }
		@Override protected int armorDamageMin() { return 2 * armor().DRMin(); }
	}
	
	private static class WndGhostHero extends Window{
		
		private static final int BTN_SIZE	= 32;
		private static final float GAP		= 2;
		private static final float BTN_GAP	= 12;
		private static final int WIDTH		= 116;
		
		private ItemButton btnWeapon;
		private ItemButton btnArmor;
		
		WndGhostHero(final DriedRose rose){
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon( new ItemSprite(rose) );
			titlebar.label( Messages.get(this, "title") );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextBlock message =
					PixelScene.renderTextBlock(Messages.get(this, "desc", rose.ghostStrength()), 6);
			message.maxWidth( WIDTH );
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );
			
			btnWeapon = new ItemButton(){
				@Override
				protected void onClick() {
					if (rose.weapon != null){
						item(new WndBag.Placeholder(SpecificPlaceHolderDict.SOMETHING_0));
						if (!rose.weapon.doPickUp(Dungeon.hero)){
							Dungeon.level.drop( rose.weapon, Dungeon.hero.pos);
						}
						rose.weapon = null;
					} else {
						GameScene.selectItem(new WndBag.ItemSelector() {

							@Override
							public String textPrompt() {
								return Messages.get(WndGhostHero.class, "weapon_prompt");
							}

							@Override
							public Class<?extends Bag> preferredBag(){
								return Belongings.Backpack.class;
							}

							@Override
							public boolean itemSelectable(Item item) {
								return item instanceof MeleeWeapon;
							}

							@Override
							public void onSelect(Item item) {
								if (!(item instanceof MeleeWeapon)) {
									//do nothing, should only happen when window is cancelled
								} else if (item.unique) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_unique"));
									hide();
									} else if (!item.isIdentified()) {
										GLog.w(Messages.get(WndGhostHero.class, "cant_unidentified"));
										hide();
									} else if (item.cursed) {
										GLog.w(Messages.get(WndGhostHero.class, "cant_cursed"));
										hide();
									} else if (((MeleeWeapon)item).STRReq() > rose.ghostStrength()) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_strength"));
									hide();
								} else {
									if (item.isEquipped(Dungeon.hero)){
										((MeleeWeapon) item).doUnequip(Dungeon.hero, false, false);
									} else {
										item.detach(Dungeon.hero.belongings.backpack);
									}
									rose.weapon = (MeleeWeapon) item;
									item(rose.weapon);
								}
								
							}
						});
					}
				}

				@Override
				protected boolean onLongClick() {
					if (item() != null && item().name() != null){
						GameScene.show(new WndInfoItem(item()));
						return true;
					}
					return false;
				}
			};
			btnWeapon.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + GAP, BTN_SIZE, BTN_SIZE );
			if (rose.weapon != null) {
				btnWeapon.item(rose.weapon);
			} else {
				btnWeapon.item(new WndBag.Placeholder(SpecificPlaceHolderDict.SOMETHING_0));
			}
			add( btnWeapon );
			
			btnArmor = new ItemButton(){
				@Override
				protected void onClick() {
					if (rose.armor != null){
						item(new WndBag.Placeholder(SpecificPlaceHolderDict.SOMETHING_0));
						if (!rose.armor.doPickUp(Dungeon.hero)){
							Dungeon.level.drop( rose.armor, Dungeon.hero.pos);
						}
						rose.armor = null;
					} else {
						GameScene.selectItem(new WndBag.ItemSelector() {

							@Override
							public String textPrompt() {
								return Messages.get(WndGhostHero.class, "armor_prompt");
							}

							@Override
							public Class<?extends Bag> preferredBag(){
								return Belongings.Backpack.class;
							}

							@Override
							public boolean itemSelectable(Item item) {
								return item instanceof Armor;
							}

							@Override
							public void onSelect(Item item) {
								if (!(item instanceof Armor)) {
									//do nothing, should only happen when window is cancelled
									} else if (item.unique) {
										GLog.w( Messages.get(WndGhostHero.class, "cant_unique"));
										hide();
									} else if (!item.isIdentified()) {
										GLog.w(Messages.get(WndGhostHero.class, "cant_unidentified"));
										hide();
									} else if (item.cursed) {
										GLog.w(Messages.get(WndGhostHero.class, "cant_cursed"));
										hide();
									} else if (((Armor)item).STRReq() > rose.ghostStrength()) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_strength"));
									hide();
								} else {
									if (item.isEquipped(Dungeon.hero)){
										((Armor) item).doUnequip(Dungeon.hero, false, false);
									} else {
										item.detach(Dungeon.hero.belongings.backpack);
									}
									rose.armor = (Armor) item;
									item(rose.armor);
								}
								
							}
						});
					}
				}

				@Override
				protected boolean onLongClick() {
					if (item() != null && item().name() != null){
						GameScene.show(new WndInfoItem(item()));
						return true;
					}
					return false;
				}
			};
			btnArmor.setRect( btnWeapon.right() + BTN_GAP, btnWeapon.top(), BTN_SIZE, BTN_SIZE );
			if (rose.armor != null) {
				btnArmor.item(rose.armor);
			} else {
				btnArmor.item(new WndBag.Placeholder(SpecificPlaceHolderDict.SOMETHING_0));
			}
			add( btnArmor );
			
			resize(WIDTH, (int)(btnArmor.bottom() + GAP));
		}
	
	}
}
