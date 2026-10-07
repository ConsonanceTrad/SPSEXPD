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

package pd.actors.hero;

import pd.Assets;
import pd.Badges;
import pd.Bones;
import pd.Challenges;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SacrificialFire;
import pd.actors.buffs.AdrenalineSurge;
import pd.actors.buffs.ArtifactRecharge;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.Awareness;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Berserk;
import pd.actors.buffs.Blasphemy;
import pd.actors.buffs.Bless;
import pd.actors.buffs.BloodAngry;
import pd.actors.buffs.Buff;
import pd.actors.buffs.BunnyCombo;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Combo;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.DarkFallen;
import pd.actors.buffs.DeadRaise;
import pd.actors.buffs.DewScatter;
import pd.actors.buffs.Disarm;
import pd.actors.buffs.Drowsy;
import pd.actors.buffs.Foresight;
import pd.actors.buffs.GoldTouch;
import pd.actors.buffs.GreaterHaste;
import pd.actors.buffs.HTimprove;
import pd.actors.buffs.HeroDisguise;
import pd.actors.buffs.HighAttack;
import pd.actors.buffs.HighVoice;
import pd.actors.buffs.HoldFast;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Invulnerability;
import pd.actors.buffs.ItemSteal;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.LingBless;
import pd.actors.buffs.LostInventory;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.Momentum;
import pd.actors.buffs.MonkEnergy;
import pd.actors.buffs.MoonFury;
import pd.actors.buffs.Muscle;
import pd.actors.buffs.NewCombo;
import pd.actors.buffs.Notice;
import pd.actors.buffs.OnePunch;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.ParyAttack;
import pd.actors.buffs.PhysicalEmpower;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Regeneration;
import pd.actors.buffs.Rhythm2;
import pd.actors.buffs.SnipersMark;
import pd.actors.buffs.SpeedImbue;
import pd.actors.buffs.SuperArcane;
import pd.actors.buffs.TargetShoot;
import pd.actors.buffs.TimeStasis;
import pd.actors.buffs.TrinityStance;
import pd.actors.buffs.Vertigo;
import pd.actors.buffs.faithbuff.BalanceFaith;
import pd.actors.buffs.faithbuff.DemonFaith;
import pd.actors.buffs.faithbuff.FaithBuff;
import pd.actors.buffs.faithbuff.HumanFaith;
import pd.actors.buffs.faithbuff.LifeFaith;
import pd.actors.buffs.faithbuff.MechFaith;
import pd.actors.buffs.mindbuff.AmokMind;
import pd.actors.buffs.mindbuff.CrazyMind;
import pd.actors.buffs.mindbuff.HopeMind;
import pd.actors.buffs.mindbuff.LoseMind;
import pd.actors.buffs.mindbuff.TerrorMind;
import pd.actors.buffs.mindbuff.WeakMind;
import pd.actors.hero.abilities.ArmorAbility;
import pd.actors.hero.abilities.cleric.AscendedForm;
import pd.actors.hero.abilities.duelist.Challenge;
import pd.actors.hero.abilities.duelist.ElementalStrike;
import pd.actors.hero.abilities.huntress.NaturesPower;
import pd.actors.hero.abilities.warrior.Endure;
import pd.actors.hero.spells.BodyForm;
import pd.actors.hero.spells.HallowedGround;
import pd.actors.hero.spells.HolyWard;
import pd.actors.hero.spells.HolyWeapon;
import pd.actors.hero.spells.Smite;
import pd.actors.mobs.Mimic;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Monk;
import pd.actors.mobs.Snake;
import pd.actors.mobs.SommonSkeleton;
import pd.actors.mobs.npcs.Imp;
import pd.actors.mobs.pets.LegacyPet;
import pd.effects.CellEmitter;
import pd.effects.FloatingText;
import pd.effects.Speck;
import pd.effects.SpellSprite;
import pd.effects.Splash;
import pd.items.Ankh;
import pd.items.BrokenSeal;
import pd.items.Dewdrop;
import pd.items.EquipableItem;
import pd.items.Generator;
import pd.items.Heap.Type;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.misc.DewBadge;
import pd.items.Item;
import pd.items.KindOfWeapon;
import pd.items.OrbOfZot;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.ClassArmor;
import pd.items.equipment.armor.ClothArmor;
import pd.items.equipment.armor.glyphs.Stone;
import pd.items.equipment.armor.glyphs.Viscosity;
import pd.items.equipment.artifacts.CloakOfShadows;
import pd.items.equipment.artifacts.DriedRose;
import pd.items.equipment.artifacts.EtherealChains;
import pd.items.equipment.artifacts.FlyChains;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.artifacts.SkeletonKey;
import pd.items.equipment.artifacts.TalismanOfForesight;
import pd.items.equipment.artifacts.TimekeepersHourglass;
import pd.items.equipment.bags.MagicalHolster;
import pd.items.consum.eggs.Egg;
import pd.items.specific.journal.Guidebook;
import pd.items.specific.keys.CrystalKey;
import pd.items.specific.keys.GoldenKey;
import pd.items.specific.keys.GoldenSkeletonKey;
import pd.items.specific.keys.IronKey;
import pd.items.specific.keys.Key;
import pd.items.specific.keys.SpsSkeletonKey;
import pd.items.specific.keys.WornKey;
import pd.items.misc.Ankhshield;
import pd.items.misc.AttackShield;
import pd.items.misc.BShovel;
import pd.items.misc.BigBattery;
import pd.items.misc.CopyBall;
import pd.items.misc.DanceLion;
import pd.items.misc.DiceTower;
import pd.items.misc.FishBone;
import pd.items.misc.FourClover;
import pd.items.misc.GhostGirlRose;
import pd.items.misc.GunOfSoldier;
import pd.items.misc.HealBag;
import pd.items.misc.HorseTotem;
import pd.items.misc.JumpA;
import pd.items.misc.JumpF;
import pd.items.misc.JumpH;
import pd.items.misc.JumpM;
import pd.items.misc.JumpP;
import pd.items.misc.JumpR;
import pd.items.misc.JumpS;
import pd.items.misc.JumpW;
import pd.items.misc.LeaderFlag;
import pd.items.misc.MissileShield;
import pd.items.misc.PotionOfMage;
import pd.items.misc.RangeBag;
import pd.items.misc.SavageHelmet;
import pd.items.misc.SeriousPunch;
import pd.items.misc.Shovel;
import pd.items.misc.UndeadBook;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionOfExperience;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.potions.elixirs.ElixirOfMight;
import pd.items.consum.potions.exotic.PotionOfDivineInspiration;
import pd.items.quest.ChallengeJournal;
import pd.items.quest.DarkGold;
import pd.items.quest.DwarfToken;
import pd.items.quest.EscapeCrystal;
import pd.items.quest.Pickaxe;
import pd.items.equipment.rings.RingOfAccuracy;
import pd.items.equipment.rings.RingOfElements;
import pd.items.equipment.rings.RingOfEvasion;
import pd.items.equipment.rings.RingOfForce;
import pd.items.equipment.rings.RingOfFuror;
import pd.items.equipment.rings.RingOfHaste;
import pd.items.equipment.rings.RingOfMight;
import pd.items.equipment.rings.RingOfTenacity;
import pd.items.equipment.rings.fusion.RingOfMagic;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.consum.scrolls.exotic.ScrollOfChallenge;
import pd.items.equipment.trinkets.ThirteenLeafClover;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.wands.WandOfLivingEarth;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.Crossbow;
import pd.items.equipment.weapon.melee.Flail;
import pd.items.equipment.weapon.melee.Gloves;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.melee.Quarterstaff;
import pd.items.equipment.weapon.melee.RoundShield;
import pd.items.equipment.weapon.melee.Sai;
import pd.items.equipment.weapon.melee.Scimitar;
import pd.items.equipment.weapon.melee.WornShortsword;
import pd.items.equipment.weapon.missiles.MegaCannon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.journal.Catalog;
import pd.journal.Document;
import pd.journal.Notes;
import pd.levels.CellFlags;
import pd.levels.FieldOfView;
import pd.levels.Level;
import pd.levels.MiningLevel;
import pd.levels.Terrain;
import pd.levels.Transitions;
import pd.levels.VaultLevel;
import pd.levels.features.Chasm;
import pd.levels.features.LevelTransition;
import pd.levels.features.Sign;
import pd.levels.rooms.special.WeakFloorRoom;
import pd.levels.traps.Trap;
import pd.mechanics.Ballistica;
import pd.mechanics.ShadowCaster;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.plants.Earthroot;
import pd.scenes.AlchemyScene;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.scenes.WelcomeScene;
import pd.sprites.CharSprite;
import pd.sprites.HeroSprite;
import pd.sprites.ImpSprite;
import pd.ui.AttackIndicator;
import pd.ui.BuffIndicator;
import pd.ui.QuickSlotButton;
import pd.ui.StatusPane;
import pd.utils.GLog;
import pd.windows.WndHero;
import pd.windows.WndIronMaker;
import pd.windows.WndLifeTradeItem;
import pd.windows.WndOptions;
import pd.windows.WndResurrect;
import pd.windows.WndTent;
import pd.windows.WndTradeItem;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.noosa.tweeners.Delayer;
import render.utils.data.BArray;
import render.utils.data.Callback;
import render.utils.geom.Point;
import render.utils.math.GameMath;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import pd.messages.InlineText;

public class Hero extends Char {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Hero.class)
			.t("name", "你")
			.t("leave", "从没有人愿意这么轻易地离开像素地牢。")
			.t("level_up", "升级！")
			.t("new_level", "升级！精准+，闪避+，最大生命值+5！")
			.t("new_talent", "天赋点+1！")
			.t("new_perk", "获得特质点！请前往选择新特质。")
			.t("unspent", "你还有尚未使用的天赋点！")
			.t("level_cap", "你不能变得更强了，不过你的经验给了你一股力量！")
			.t("you_now_have", "你获得了：%s。")
			.t("you_cant_have", "你无法携带：%s。")
			.t("locked_chest", "箱子锁着而你没有对应的钥匙。")
			.t("locked_door", "你没有对应的钥匙。")
			.t("noticed_smth", "你注意到了些什么。")
			.t("wait", "...")
			.t("fuurai_change", "你的武器变化成了另一种形态！")
			.t("search", "搜索")
			.t("search_distracted", "你没办法集中精力，探索周边异常费力。")
			.t("key_distracted", "你没能打开这把锁，还得再试试。")
			.t("pain_resist", "疼痛使你得以抵抗睡意。")
			.t("revive", "重生十字章迸裂出苏生的能量！");
	}




	{
		actPriority = HERO_PRIO;
		
		alignment = Alignment.ALLY;
	}
	
	public static final int MAX_LEVEL = 30;

	public static final int STARTING_STR = 10;
	
	private static final float TIME_TO_REST		    = 1f;
	private static final float TIME_TO_SEARCH	    = 2f;
	private static final float HUNGER_FOR_SEARCH	= 6f;
	private static final float TIME_TO_PICK_UP      = 1f;   //SPSXPD: 搜索顺带拾取时每件物品的耗时

	//SPSEXPD: 搜索顺带拾取期间，物品自身的拾取结算一律不计时/不统计；最终由 search() 统一只加一个回合
	private boolean spsPickingUp = false;
	
	public HeroClass heroClass = HeroClass.ROGUE;
	public HeroSubClass subClass = HeroSubClass.NONE;
	public int skin = 0;
	public CombatStyle combatStyle = CombatStyle.BALANCED;
	public ArmorAbility armorAbility = null;
	public ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<>();
	public LinkedHashMap<Talent, Talent> metamorphedTalents = new LinkedHashMap<>();

	//SPSXPD: 特质（Perk）体系 —— 取代破碎天赋
	public pd.actors.hero.perks.HeroPerk heroPerk = new pd.actors.hero.perks.HeroPerk();
	/** 每多少级发放一个特质点 */
	public static final int PERK_LEVEL_STEP = 3;

	/** 未使用的特质点（每 PERK_LEVEL_STEP 级 +1） */
	public int reservedPerks = 0;
	/** 开局默认的重随机会次数 */
	public static final int DEFAULT_PERK_REROLLS = 2;
	/** 剩余的重随机会（初始 2 次；「神意启发合剂」每次 +3，测试时间给 999） */
	public int perkRerolls = DEFAULT_PERK_REROLLS;
	/** 本次升级抽出的候选特质（存档安全） */
	public ArrayList<pd.actors.hero.perks.Perk> spawnedPerks = new ArrayList<>();
	/** 已获得的特质数量（用于徽章等统计） */
	public int perkGained = 0;
	/** 暴击几率（0-1），由特质与装备修改 */
	public float criticalChance = 0f;
	/** 生命回复加成（累加），由特质修改 */
	public float regenerationBonus = 0f;
	/** 特质条件计数器 */
	public pd.actors.hero.TraitCounters traitCounters = new pd.actors.hero.TraitCounters();

	public static final String PERK_POINTS    = "sps_perk_points";
	public static final String PERK_REROLLS   = "sps_perk_rerolls";
	public static final String PERK_SPAWNED   = "sps_perk_spawned";
	public static final String PERK_GAINED    = "sps_perk_gained";
	public static final String CRITICAL_CHANCE= "sps_critical_chance";
	public static final String REGEN_BONUS    = "sps_regen_bonus";
	
	private int attackSkill = 10;
	private int defenseSkill = 5;
	private int magicSkill = 0;

	public boolean ready = false;
	public boolean damageInterrupt = true;
	public HeroAction curAction = null;
	public HeroAction lastAction = null;

	//reference to the enemy the hero is currently in the process of attacking
	private Char attackTarget;
	
	public boolean resting = false;
	
	public Belongings belongings;
	
	public int STR;
	
	public float awareness;
	
	public int lvl = 1;
	public int exp = 0;
	public int petLevel = 0;
	public int petExperience = 0;
	/** Legacy SPS resource used by several skin-specific weapons. */
	public int spp = 0;
	
	public int HTBoost = 0;
	public static final int STARTING_HT = 30;
	
	private ArrayList<Mob> visibleEnemies;

	//This list is maintained so that some logic checks can be skipped
	// for enemies we know we aren't seeing normally, resulting in better performance
	public ArrayList<Mob> mindVisionEnemies = new ArrayList<>();

	public Hero() {
		super();

		HP = HT = STARTING_HT;
		STR = STARTING_STR;
		
		belongings = new Belongings( this );
		
		visibleEnemies = new ArrayList<>();
	}
	
	public void updateHT( boolean boostHP ){
		int curHT = HT;
		
		HT = baseLevelHT() + HTBoost;
		if (buff(HTimprove.class) != null) HT += Math.round(permanentHT() * 0.2f);
		float multiplier = RingOfMight.HTMultiplier(this);
		HT = Math.round(multiplier * HT);
		
		if (buff(ElixirOfMight.HTBoost.class) != null){
			HT += buff(ElixirOfMight.HTBoost.class).boost();
		}

		//SPSEXPD: 撒旦之心满级——额外生命上限，让自然回复能突破血肉极限
		pd.items.equipment.artifacts.HeartOfSatan.Regeneration heartOfSatan =
				buff(pd.items.equipment.artifacts.HeartOfSatan.Regeneration.class);
		if (heartOfSatan != null) HT += heartOfSatan.extraCap();
		
		HT = Math.round(combatStyle.healthMultiplier() * HT);

		if (boostHP){
			if (!pd.actors.hero.perks.BloodShield.convert(this, Math.max(HT - curHT, 0))) HP += Math.max(HT - curHT, 0);
		}
		HP = Math.min(HP, HT);
	}

	/** Permanent, unmodified maximum health used by SPS life-cost effects. */
	public int permanentHT() {
		return baseLevelHT() + HTBoost;
	}

	public int baseLevelHT() {
		int healthPerLevel = Dungeon.isChallenged(Challenges.LISTLESS) ? 2 : 5;
		return STARTING_HT + healthPerLevel * (lvl - 1);
	}

	public boolean spendPermanentHT(int amount) {
		if (amount <= 0 || permanentHT() <= amount) return false;
		HTBoost -= amount;
		updateHT(false);
		return true;
	}

	public void improveCombatSkills(int amount) {
		attackSkill += amount;
		defenseSkill += amount;
	}

	public void improveAttackSkill(int amount) { attackSkill += amount; }
	public void improveDefenseSkill(int amount) { defenseSkill += amount; }
	public void improveMagicSkill(int amount) { magicSkill += amount; }
	public int magicSkill() {
		SuperArcane arcane = buff(SuperArcane.class);
		return magicSkill + RingOfMagic.magicSkillBonus(this)
				- (buff(LoseMind.class) == null ? 0 : 5)
				+ (arcane == null ? 0 : Math.max(5, arcane.level()));
	}

	//SPSXPD: 魔法抗性（目前由特质提供，后续可扩展装备/护甲加成）
	public float magicalResistance() {
		float r = 0f;
		if (heroPerk != null) {
			pd.actors.hero.perks.ExtraMagicalResistance m =
					heroPerk.get(pd.actors.hero.perks.ExtraMagicalResistance.class);
			if (m != null) r += m.ratio();
		}
		return Math.min(0.9f, r);
	}

	public int STR() {
		int strBonus = 0;

		strBonus += RingOfMight.strengthBonus( this );
		
		AdrenalineSurge buff = buff(AdrenalineSurge.class);
		if (buff != null){
			strBonus += buff.boost();
		}

		if (hasTalent(Talent.STRONGMAN)){
			strBonus += (int)Math.floor(STR * (0.03f + 0.05f*pointsInTalent(Talent.STRONGMAN)));
		}
		if (subClass == HeroSubClass.ARTISAN) strBonus++;
		if (buff(pd.actors.buffs.AflyBless.class) != null) strBonus++;
		if (buff(pd.actors.buffs.STRDown.class) != null) strBonus -= 3;
		if (buff(Muscle.class) != null) strBonus += 2;

		return STR + strBonus;
	}

	private static final String CLASS       = "class";
	private static final String SUBCLASS    = "subClass";
	private static final String SKIN        = "fusion_skin";
	private static final String STYLE       = "fusion_combat_style";
	private static final String ABILITY     = "armorAbility";

	private static final String ATTACK		= "attackSkill";
	private static final String DEFENSE		= "defenseSkill";
	private static final String MAGIC_SKILL = "magicSkill";
	private static final String STRENGTH	= "STR";
	private static final String LEVEL		= "lvl";
	private static final String EXPERIENCE	= "exp";
	private static final String PET_LEVEL    = "sps_pet_level";
	private static final String PET_EXP      = "sps_pet_experience";
	private static final String SPP          = "sps_power_points";
	private static final String HTBOOST     = "htboost";
	
	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );

		bundle.put( CLASS, heroClass );
		bundle.put( SUBCLASS, subClass );
		bundle.put( SKIN, skin );
		bundle.put( STYLE, combatStyle );
		bundle.put( ABILITY, armorAbility );
		Talent.storeTalentsInBundle( bundle, this );

		//SPSXPD: 特质体系存档
		heroPerk.storeInBundle( bundle );
		bundle.put( PERK_POINTS, reservedPerks );
		bundle.put( PERK_REROLLS, perkRerolls );
		bundle.put( PERK_SPAWNED, spawnedPerks );
		bundle.put( PERK_GAINED, perkGained );
		bundle.put( CRITICAL_CHANCE, criticalChance );
		bundle.put( REGEN_BONUS, regenerationBonus );
		traitCounters.storeInBundle( bundle );
		
		bundle.put( ATTACK, attackSkill );
		bundle.put( DEFENSE, defenseSkill );
		bundle.put( MAGIC_SKILL, magicSkill );
		
		bundle.put( STRENGTH, STR );
		
		bundle.put( LEVEL, lvl );
		bundle.put( EXPERIENCE, exp );
		bundle.put( PET_LEVEL, petLevel );
		bundle.put( PET_EXP, petExperience );
		bundle.put( SPP, spp );
		
		bundle.put( HTBOOST, HTBoost );

		belongings.storeInBundle( bundle );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {

		lvl = bundle.getInt( LEVEL );
		exp = bundle.getInt( EXPERIENCE );
		petLevel = bundle.getInt(PET_LEVEL);
		petExperience = bundle.getInt(PET_EXP);
		spp = Math.max(0, bundle.getInt(SPP));

		HTBoost = bundle.getInt(HTBOOST);

		super.restoreFromBundle( bundle );

		heroClass = bundle.getEnum( CLASS, HeroClass.class );
		subClass = bundle.getEnum( SUBCLASS, HeroSubClass.class );
		skin = Math.max(0, Math.min(7, bundle.getInt(SKIN)));
		combatStyle = bundle.contains(STYLE) ? bundle.getEnum(STYLE, CombatStyle.class) : CombatStyle.BALANCED;
		if (combatStyle == null) combatStyle = CombatStyle.BALANCED;
		armorAbility = (ArmorAbility)bundle.get( ABILITY );
		Talent.restoreTalentsFromBundle( bundle, this );

		//SPSXPD: 特质体系读档
		if (bundle.contains( PERK_POINTS )) {
			heroPerk.restoreFromBundle( bundle );
			reservedPerks = bundle.getInt( PERK_POINTS );
			//SPSXPD: 旧档没有该键时回落到默认次数（否则会读成 0）
			perkRerolls = bundle.contains( PERK_REROLLS )
					? bundle.getInt( PERK_REROLLS ) : DEFAULT_PERK_REROLLS;
			spawnedPerks.clear();
			if (bundle.contains( PERK_SPAWNED )) {
				for (render.utils.serialize.Bundlable b : bundle.getCollection( PERK_SPAWNED )) {
					if (b instanceof pd.actors.hero.perks.Perk) spawnedPerks.add((pd.actors.hero.perks.Perk) b);
				}
			}
			perkGained = bundle.getInt( PERK_GAINED );
			criticalChance = bundle.getFloat( CRITICAL_CHANCE );
			regenerationBonus = bundle.getFloat( REGEN_BONUS );
			traitCounters.restoreFromBundle( bundle );
		}
		
		attackSkill = bundle.getInt( ATTACK );
		defenseSkill = bundle.getInt( DEFENSE );
		magicSkill = bundle.getInt( MAGIC_SKILL );
		
		STR = bundle.getInt( STRENGTH );

		belongings.restoreFromBundle( bundle );
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		info.level = bundle.getInt( LEVEL );
		info.str = bundle.getInt( STRENGTH );
		info.exp = bundle.getInt( EXPERIENCE );
		info.hp = bundle.getInt( Char.TAG_HP );
		info.ht = bundle.getInt( Char.TAG_HT );
		info.shld = bundle.getInt( Char.TAG_SHLD );
		info.heroClass = bundle.getEnum( CLASS, HeroClass.class );
		info.subClass = bundle.getEnum( SUBCLASS, HeroSubClass.class );
		info.skin = Math.max(0, Math.min(7, bundle.getInt(SKIN)));
		info.combatStyle = bundle.contains(STYLE) ? bundle.getEnum(STYLE, CombatStyle.class) : CombatStyle.BALANCED;
		Belongings.preview( info, bundle );
	}

	public boolean hasTalent( Talent talent ){
		return pointsInTalent(talent) > 0;
	}

	public int pointsInTalent( Talent talent ){
		for (LinkedHashMap<Talent, Integer> tier : talents){
			for (Talent f : tier.keySet()){
				if (f == talent) return tier.get(f);
			}
		}
		return 0;
	}

	public void upgradeTalent( Talent talent ){
		for (LinkedHashMap<Talent, Integer> tier : talents){
			for (Talent f : tier.keySet()){
				if (f == talent) tier.put(talent, tier.get(talent)+1);
			}
		}
		Talent.onTalentUpgraded(this, talent);
	}

	public int talentPointsSpent(int tier){
		int total = 0;
		for (int i : talents.get(tier-1).values()){
			total += i;
		}
		return total;
	}

	/** SPSXPD: 该等级是否发放特质点（每 PERK_LEVEL_STEP 级一次） */
	public static boolean grantsPerkPoint(int level) {
		return level > 0 && level % PERK_LEVEL_STEP == 0;
	}

	public int talentPointsAvailable(int tier){
		//SPSXPD: 破碎天赋体系已停用（改用特质/Perk 体系），天赋点恒为 0。
		//这样所有内联的 hasTalent(...)/pointsInTalent(...) 判定自然失效，
		//无需改动全项目 100+ 处调用点，也便于随时回退。
		return 0;
	}

	public int bonusTalentPoints(int tier){
		if (lvl < (Talent.tierLevelThresholds[tier]-1)
				|| (tier == 3 && subClass == HeroSubClass.NONE)
				|| (tier == 4 && armorAbility == null)) {
			return 0;
		} else if (buff(PotionOfDivineInspiration.DivineInspirationTracker.class) != null
					&& buff(PotionOfDivineInspiration.DivineInspirationTracker.class).isBoosted(tier)) {
			return 2;
		} else {
			return 0;
		}
	}
	
	public String className() {
		return subClass == null || subClass == HeroSubClass.NONE ? heroClass.title() : subClass.title();
	}

	@Override
	public String name(){
		if (buff(HeroDisguise.class) != null) {
			return buff(HeroDisguise.class).getDisguise().title();
		} else {
			return className();
		}
	}

	@Override
	public void hitSound(float pitch) {
		if (!RingOfForce.fightingUnarmed(this)) {
			belongings.attackingWeapon().hitSound(pitch);
		} else if (RingOfForce.getBuffedBonus(this, RingOfForce.Force.class) > 0) {
			//pitch deepens by 2.5% (additive) per point of strength, down to 75%
			super.hitSound( pitch * GameMath.gate( 0.75f, 1.25f - 0.025f*STR(), 1f) );
		} else {
			super.hitSound(pitch * 1.1f);
		}
	}

	@Override
	public boolean blockSound(float pitch) {
		if ( belongings.weapon() != null && belongings.weapon().defenseFactor(this) >= 4 ){
			Sample.INSTANCE.play( Assets.Sounds.HIT_PARRY, 1, pitch);
			return true;
		}
		return super.blockSound(pitch);
	}

	public void live() {
		for (Buff b : buffs()){
			if (!b.revivePersists) b.detach();
		}
		Buff.affect( this, Regeneration.class );
		Buff.affect( this, Hunger.class );
		if (Dungeon.isChallenged(Challenges.SPS_DARKNESS)) {
			Buff.affect(this, DarkFallen.class);
		}
	}
	
	public int tier() {
		Armor armor = belongings.armor();
		if (armor instanceof ClassArmor){
			return 6;
		} else if (armor != null){
			return armor.tier;
		} else {
			return 0;
		}
	}
	
	public boolean shoot( Char enemy, MissileWeapon wep ) {

		attackTarget = enemy;
		boolean wasEnemy = enemy.alignment == Alignment.ENEMY
				|| (enemy instanceof Mimic && enemy.alignment == Alignment.NEUTRAL);

		//temporarily set the hero's weapon to the missile weapon being used
		//TODO improve this!
		belongings.thrownWeapon = wep;
		boolean hit = attack( enemy );
		Invisibility.dispel();
		belongings.thrownWeapon = null;

		if (hit && subClass == HeroSubClass.GLADIATOR && wasEnemy){
			Buff.affect( this, Combo.class ).hit( enemy );
		}

		if (hit && heroClass == HeroClass.DUELIST && wasEnemy){
			Buff.affect( this, Sai.ComboStrikeTracker.class).addHit( attackTarget );
		}

		attackTarget = null;
		return hit;
	}
	
	@Override
	public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
		boolean result = super.attack(enemy, dmgMulti, dmgBonus, accMulti);
		if (!(belongings.attackingWeapon() instanceof MissileWeapon)){
			if (buff(Talent.PreciseAssaultTracker.class) != null){
				buff(Talent.PreciseAssaultTracker.class).detach();
			} else if (buff(Talent.LiquidAgilACCTracker.class) != null
						&& buff(Talent.LiquidAgilACCTracker.class).uses <= 0){
				buff(Talent.LiquidAgilACCTracker.class).detach();
			}
		}
		return result;
	}

	@Override
	public int attackSkill( Char target ) {
		KindOfWeapon wep = belongings.attackingWeapon();
		
		float accuracy = 1;
		accuracy *= RingOfAccuracy.accuracyMultiplier( this );
		
		//precise assault and liquid agility
		if (!(wep instanceof MissileWeapon)) {
			if ((hasTalent(Talent.PRECISE_ASSAULT) || hasTalent(Talent.LIQUID_AGILITY))
					//does not trigger on ability attacks
					&& belongings.abilityWeapon != wep && buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) == null){

				//non-duelist benefit for precise assault, can stack with liquid agility
				if (heroClass != HeroClass.DUELIST) {
					//persistent +10%/20%/30% ACC for other heroes
					accuracy *= 1f + 0.1f * pointsInTalent(Talent.PRECISE_ASSAULT);
				}

				if (wep instanceof Flail && buff(Flail.SpinAbilityTracker.class) != null){
					//do nothing, this is not a regular attack so don't consume talent fx
				} else if (wep instanceof Crossbow && buff(Crossbow.ChargedShot.class) != null){
					//do nothing, this is not a regular attack so don't consume talent fx
				} else if (buff(Talent.PreciseAssaultTracker.class) != null) {
					// 2x/5x/inf. ACC for duelist if she just used a weapon ability
					switch (pointsInTalent(Talent.PRECISE_ASSAULT)){
						default: case 1:
							accuracy *= 2; break;
						case 2:
							accuracy *= 5; break;
						case 3:
							accuracy *= Float.POSITIVE_INFINITY; break;
					}
				} else if (buff(Talent.LiquidAgilACCTracker.class) != null){
					// 3x/inf. ACC, depending on talent level
					accuracy *= pointsInTalent(Talent.LIQUID_AGILITY) == 2 ? Float.POSITIVE_INFINITY : 3f;
					Talent.LiquidAgilACCTracker buff = buff(Talent.LiquidAgilACCTracker.class);
					buff.uses--;
				}
			}
		} else {
			if (buff(Momentum.class) != null && buff(Momentum.class).freerunning()){
				accuracy *= 1f + pointsInTalent(Talent.PROJECTILE_MOMENTUM)/2f;
			}
			if (subClass == HeroSubClass.AGENT) accuracy *= 1.15f;
		}
		if (heroClass == HeroClass.SOLDIER) accuracy *= 1.05f;
		accuracy *= combatStyle.accuracyMultiplier();

		if (buff(Scimitar.SwordDance.class) != null){
			accuracy *= 1.50f;
		}
		
		if (!RingOfForce.fightingUnarmed(this)) {
			return Math.max(1, Math.round(attackSkill * accuracy * wep.accuracyFactor( this, target )));
		} else {
			return Math.max(1, Math.round(attackSkill * accuracy));
		}
	}
	
	@Override
	public int defenseSkill( Char enemy ) {

		if (buff(Combo.ParryTracker.class) != null){
			if (canAttack(enemy) && !isCharmedBy(enemy)){
				Buff.affect(this, Combo.RiposteTracker.class).enemy = enemy;
			}
			return INFINITE_EVASION;
		}

		if (buff(RoundShield.GuardTracker.class) != null){
			return INFINITE_EVASION;
		}
		
		float evasion = defenseSkill;
		
		if (heroClass == HeroClass.SOLDIER) evasion *= 1.05f;
		evasion *= combatStyle.evasionMultiplier();

		if (buff(Talent.LiquidAgilEVATracker.class) != null){
			if (pointsInTalent(Talent.LIQUID_AGILITY) == 1){
				evasion *= 3f;
			} else if (pointsInTalent(Talent.LIQUID_AGILITY) == 2){
				return INFINITE_EVASION;
			}
		}

		if (buff(Quarterstaff.DefensiveStance.class) != null){
			evasion *= 3;
		}
		
		if (paralysed > 0) {
			evasion /= 2;
		}
		if (buff(LingBless.class) != null) evasion *= 1.2f;

		if (belongings.armor() != null) {
			evasion = belongings.armor().evasionFactor(this, evasion);

			//stone specifically overrides to 0 always, guaranteed hit
			if (belongings.armor().hasGlyph(Stone.class, this) && !Stone.testingEvasion()){
				return 0;
			}
		}

		return Math.max(1, Math.round(evasion));
	}

	@Override
	public String defenseVerb() {
		Combo.ParryTracker parry = buff(Combo.ParryTracker.class);
		if (parry != null){
			parry.parried = true;
			if (buff(Combo.class) == null || buff(Combo.class).getComboCount() < 9 || pointsInTalent(Talent.ENHANCED_COMBO) < 2){
				parry.detach();
			}
			return Messages.get(Monk.class, "parried");
		}

		if (buff(RoundShield.GuardTracker.class) != null){
			buff(RoundShield.GuardTracker.class).hasBlocked = true;
			BuffIndicator.refreshHero();
			Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
			return Messages.get(RoundShield.GuardTracker.class, "guarded");
		}

		if (buff(MonkEnergy.MonkAbility.Focus.FocusBuff.class) != null){
			buff(MonkEnergy.MonkAbility.Focus.FocusBuff.class).detach();
			if (sprite != null && sprite.visible) {
				Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
			}
			return Messages.get(Monk.class, "parried");
		}

		return super.defenseVerb();
	}

	@Override
	public int drRoll() {
		int dr = super.drRoll();

		if (belongings.armor() != null) {
			int armDr = Random.NormalIntRange( belongings.armor().DRMin(), belongings.armor().DRMax());
			armDr = belongings.armor().damageReductionFactor(this, armDr);
			if (armDr > 0) dr += armDr;
		}
		if (belongings.weapon() != null && !RingOfForce.fightingUnarmed(this))  {
			int wepDr = Random.NormalIntRange( 0 , belongings.weapon().defenseFactor( this ) );
			if (STR() < ((Weapon)belongings.weapon()).STRReq()){
				wepDr -= 2*(((Weapon)belongings.weapon()).STRReq() - STR());
			}
			if (wepDr > 0) dr += wepDr;
		}
		//SPSEXPD: 装备在副手的神木圆盾提供基于英雄等级的额外防护
		if (belongings.secondWep() instanceof MissileShield) {
			int secondDr = Random.NormalIntRange( 0, belongings.secondWep().defenseFactor( this ) );
			if (secondDr > 0) dr += secondDr;
		}

		if (buff(HoldFast.class) != null){
			dr += buff(HoldFast.class).armorBonus();
		}
		
		return dr + combatStyle.bonusArmor(lvl);
	}
	
	@Override
	public int damageRoll() {
		KindOfWeapon wep = belongings.attackingWeapon();
		int dmg;

		if (!RingOfForce.fightingUnarmed(this)) {
			dmg = wep.damageRoll( this );

			if (!(wep instanceof MissileWeapon)) dmg += RingOfForce.armedDamageBonus(this);
		} else {
			dmg = RingOfForce.damageRoll(this);
			if (RingOfForce.unarmedGetsWeaponAugment(this)){
				dmg = ((Weapon)belongings.attackingWeapon()).augment.damageFactor(dmg);
			}
		}
		dmg += RingOfFuror.damageBonus(this);

		PhysicalEmpower emp = buff(PhysicalEmpower.class);
		if (emp != null){
			dmg += emp.dmgBoost;
			emp.left--;
			if (emp.left <= 0) {
				emp.detach();
			}
			Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
		}

		if (heroClass != HeroClass.DUELIST
				&& hasTalent(Talent.WEAPON_RECHARGING)
				&& (buff(Recharging.class) != null || buff(ArtifactRecharge.class) != null)){
			dmg = Math.round(dmg * 1.025f + (.025f*pointsInTalent(Talent.WEAPON_RECHARGING)));
		}
		if (subClass == HeroSubClass.ASCETIC_MONK && wep instanceof Gloves) dmg += 1;
		if (subClass == HeroSubClass.HACKER && buff(Recharging.class) != null) dmg = Math.round(dmg * 1.10f);
		dmg = Math.round(dmg * combatStyle.damageMultiplier());

		MoonFury moonFury = buff(MoonFury.class);
		if (moonFury != null) {
			dmg *= 3;
			moonFury.detach();
		}
		HighAttack highAttack = buff(HighAttack.class);
		if (highAttack != null) {
			dmg *= highAttack.level();
			highAttack.detach();
		}
		ParyAttack parry = buff(ParyAttack.class);
		if (parry != null) dmg = Math.round(dmg * (1f + parry.level() * 0.4f));
		Blasphemy blasphemy = buff(Blasphemy.class);
		if (blasphemy != null) dmg = Math.round(dmg * (1f + blasphemy.level() * 0.1f));
		if (buff(TargetShoot.class) != null && wep instanceof MissileWeapon) dmg = Math.round(dmg * 1.5f);
		if (buff(SpeedImbue.class) != null) dmg = Math.round(dmg * 1.5f);
		if (buff(HighVoice.class) != null && Random.Int(8) == 0) dmg = Math.round(dmg * 1.2f);
		if (buff(BloodAngry.class) != null) dmg = (int)(dmg * 1.5f);
		if (buff(Rhythm2.class) != null) dmg = Math.round(dmg * 1.2f);

		if (dmg < 0) dmg = 0;
		return dmg;
	}

	//damage rolls that come from the hero can have their RNG influenced by clover
	public static int heroDamageIntRange(int min, int max ){
		if (Random.Float() < ThirteenLeafClover.alterHeroDamageChance()){
			return ThirteenLeafClover.alterDamageRoll(min, max);
		} else {
			return Random.NormalIntRange(min, max);
		}
	}
	
	@Override
	public float speed() {

		float speed = super.speed();

		speed *= RingOfHaste.speedMultiplier(this);
		speed *= combatStyle.speedMultiplier();
		if (buff(SpeedImbue.class) != null) speed *= 2f;
		
		if (belongings.armor() != null) {
			speed = belongings.armor().speedFactor(this, speed);
		}
		
		Momentum momentum = buff(Momentum.class);
		if (momentum != null){
			if (sprite instanceof HeroSprite) {
				((HeroSprite)sprite).sprint(momentum.freerunning() ? 1.5f : 1f);
			}
			speed *= momentum.speedMultiplier();
		} else if (sprite instanceof HeroSprite) {
			((HeroSprite)sprite).sprint( 1f );
		}

		NaturesPower.naturesPowerTracker natStrength = buff(NaturesPower.naturesPowerTracker.class);
		if (natStrength != null){
			speed *= (2f + 0.25f*pointsInTalent(Talent.GROWING_POWER));
		}

		speed = AscensionChallenge.modifyHeroSpeed(speed);
		if (Dungeon.level != null && pos >= 0 && pos < Dungeon.level.length()) {
			speed *= FishBone.waterSpeedMultiplier(this, Dungeon.level.water[pos]);
		}
		if (buff(LingBless.class) != null) speed += 0.2f;

		//SPSEXPD: 三相之力的战舞姿态——冲锋姿态拖慢步伐，防御姿态更慢
		TrinityStance trinityStance = buff(TrinityStance.class);
		if (trinityStance != null) speed *= trinityStance.speedMultiplier(TrinityStance.weaponOf(this));

		return speed;
		
	}

	@Override
	public float stealth() {
		float stealth = super.stealth();
		if (belongings.armor() != null) stealth += belongings.armor().stealthFactor(this);
		else stealth += 1f;
		if (subClass == HeroSubClass.AGENT) stealth += 5f;
		if (heroClass == HeroClass.ROGUE && skin == 2) stealth += 8f;
		return stealth;
	}

	@Override
	public boolean canSurpriseAttack(){
		KindOfWeapon w = belongings.attackingWeapon();
		if (!(w instanceof Weapon))             return true;
		if (RingOfForce.fightingUnarmed(this))  return true;
		if (STR() < ((Weapon)w).STRReq())       return false;
		if (w instanceof Flail)                 return false;

		return super.canSurpriseAttack();
	}

	public boolean canAttack(Char enemy){
		if (enemy == null || pos == enemy.pos || !Actor.chars().contains(enemy)) {
			return false;
		}

		//can always attack adjacent enemies
		if (Dungeon.level.adjacent(pos, enemy.pos)) {
			return true;
		}

		KindOfWeapon wep = Dungeon.hero.belongings.attackingWeapon();

		if (wep != null){
			return wep.canReach(this, enemy.pos);
		} else if (buff(AscendedForm.AscendBuff.class) != null) {
			boolean[] passable = BArray.not(Dungeon.level.solid, null);
			for (Char ch : Actor.chars()) {
				if (ch != this) passable[ch.pos] = false;
			}

			PathFinder.buildDistanceMap(enemy.pos, passable, 3);

			return PathFinder.distance[pos] <= 3;
		} else {
			int reach = 1 + RingOfAccuracy.reachBonus(this);
			if (reach <= 1) return false;
			boolean[] passable = BArray.not(Dungeon.level.solid, null);
			for (Char ch : Actor.chars()) if (ch != this) passable[ch.pos] = false;
			PathFinder.buildDistanceMap(enemy.pos, passable, reach);
			return PathFinder.distance[pos] <= reach;
		}
	}
	
	public float attackDelay() {
		if (buff(Talent.LethalMomentumTracker.class) != null){
			buff(Talent.LethalMomentumTracker.class).detach();
			return 0;
		}

		float delay = 1f;

		if (!RingOfForce.fightingUnarmed(this)) {
			
			return delay * belongings.attackingWeapon().delayFactor( this );
			
		} else {
			//Normally putting furor speed on unarmed attacks would be unnecessary
			//But there's going to be that one guy who gets a furor+force ring combo
			//This is for that one guy, you shall get your fists of fury!
			float speed = RingOfFuror.attackSpeedMultiplier(this);

			//ditto for furor + sword dance!
			if (buff(Scimitar.SwordDance.class) != null){
				speed += 0.6f;
			}

			//and augments + brawler's stance! My goodness, so many options now compared to 2014!
			if (RingOfForce.unarmedGetsWeaponAugment(this)){
				delay = ((Weapon)belongings.weapon).augment.delayFactor(delay);
			}

			return delay/speed;
		}
	}

	@Override
	public void spend( float time ) {
		if (spsPickingUp) return;   //SPSEXPD: 搜索捡拾期间的逐件结算不计时/不统计
		justMoved = false;
		Statistics.advanceSpsTime(time);
		pd.items.quest.AdventureJournal journal =
				belongings.getItem(pd.items.quest.AdventureJournal.class);
		if (journal != null && Dungeon.branch == 0 && Dungeon.depth < 40) journal.gainCharge();
		LeaderFlag leaderFlag = belongings.getItem(LeaderFlag.class);
		if (leaderFlag != null) leaderFlag.advanceTime(this, time);
		super.spend(time);
	}

	@Override
	public void spendConstant(float time) {
		super.spendConstant(time);
	}

	public void spendAndNextConstant(float time ) {
		busy();
		spendConstant( time );
		next();
	}

	public void spendAndNext( float time ) {
		if (spsPickingUp) return;   //SPSEXPD: 搜索捡取期间不推进回合
		busy();
		spend( time );
		next();
	}
	
	@Override
	public boolean act() {
		//SPSXPD: 宠物能力特质（献祭获得）的每回合触发
		pd.actors.hero.perks.pets.PetAbilityPerk.dispatchTurn(this);

		//SPSEXPD: 精制种子作物的成长——每回合推进一次，只作用于英雄所在楼层
		if (Dungeon.level != null && Dungeon.level.plants != null) {
			for (pd.plants.Plant plant : Dungeon.level.plants.valueList()) {
				if (plant.growTurns > 0) plant.growTurns--;
			}
		}
		
		//calls to dungeon.observe will also update hero's local FOV.
		fieldOfView = Dungeon.level.heroFOV;
		MissileShield missileShield = belongings.getItem(MissileShield.class);
		if (missileShield != null) missileShield.gainCharge();
		//SPSEXPD: 奇迹烧瓶不再按回合充能，改为在 earnExp 里按获得的经验抽取（见 earnExp）
		Shovel shovel = belongings.getItem(Shovel.class);
		if (shovel != null) shovel.gainCharge();
		GunOfSoldier soldierGun = belongings.getItem(GunOfSoldier.class);
		if (soldierGun != null) soldierGun.gainCharge();
		if (belongings.weapon instanceof MegaCannon) ((MegaCannon)belongings.weapon).gainCharge();
		BigBattery battery = belongings.getItem(BigBattery.class);
		if (battery != null) battery.gainCharge();
		DanceLion danceLion = belongings.getItem(DanceLion.class);
		if (danceLion != null) danceLion.gainCharge();
		BShovel bShovel = belongings.getItem(BShovel.class);
		if (bShovel != null) bShovel.gainCharge();
		//SPSEXPD: 神圣护盾不再逐回合自然回复——改由重生十字架的「充能」动作消耗安卡补充（见 Ankh）
		JumpW jumpW = belongings.getItem(JumpW.class);
		if (jumpW != null) jumpW.gainCharge();
		JumpM jumpM = belongings.getItem(JumpM.class);
		if (jumpM != null) jumpM.gainCharge();
		JumpR jumpR = belongings.getItem(JumpR.class);
		if (jumpR != null) jumpR.gainCharge();
		JumpH jumpH = belongings.getItem(JumpH.class);
		if (jumpH != null) jumpH.gainCharge();
		JumpP jumpP = belongings.getItem(JumpP.class);
		if (jumpP != null) jumpP.gainCharge();
		CopyBall copyBall = belongings.getItem(CopyBall.class);
		if (copyBall != null) copyBall.gainCharge();
		OrbOfZot orbOfZot = belongings.getItem(OrbOfZot.class);
		if (orbOfZot != null) orbOfZot.gainCharge();
		JumpS jumpS = belongings.getItem(JumpS.class);
		if (jumpS != null) jumpS.gainCharge();
		JumpF jumpF = belongings.getItem(JumpF.class);
		if (jumpF != null) jumpF.gainCharge();
		JumpA jumpA = belongings.getItem(JumpA.class);
		if (jumpA != null) jumpA.gainCharge();
		if (Dungeon.dewDraw || Dungeon.dewWater) {
			Dungeon.level.currentMoves++;
			Statistics.floorMoves = Dungeon.level.currentMoves;
		}
		if (buff(DeadRaise.class) != null && Random.Int(30) == 0) {
			SommonSkeleton.spawnNear(pos);
		}

		if (buff(Endure.EndureTracker.class) != null){
			buff(Endure.EndureTracker.class).endEnduring();
		}
		
		if (!ready) {
			//do a full observe (including fog update) if not resting.
			if (!resting || buff(MindVision.class) != null || buff(Awareness.class) != null) {
				Dungeon.observe();
			} else {
				//otherwise just directly re-calculate FOV
				FieldOfView.update( Dungeon.level, this, fieldOfView);
			}
		}
		
		checkVisibleMobs();
		BuffIndicator.refreshHero();
		BuffIndicator.refreshBoss();
		
		if (paralysed > 0) {
			
			curAction = null;
			
			spendAndNext( TICK );
			return false;
		}
		
		boolean actResult;
		if (curAction == null) {
			
			if (resting) {
				spendConstant( TIME_TO_REST );
				next();
			} else {
				ready();
			}

			//if we just loaded into a level and have a search buff, make sure to process them
			if(Actor.now() == 0){
				if (buff(Foresight.class) != null){
					search(false);
				} else if (buff(TalismanOfForesight.Foresight.class) != null){
					buff(TalismanOfForesight.Foresight.class).checkAwareness();
				}
			}
			
			actResult = false;
			
		} else {
			
			resting = false;
			
			ready = false;
			
			if (curAction instanceof HeroAction.Move) {
				actResult = actMove( (HeroAction.Move)curAction );
				
			} else if (curAction instanceof HeroAction.Interact) {
				actResult = actInteract( (HeroAction.Interact)curAction );
				
			} else if (curAction instanceof HeroAction.Buy) {
				actResult = actBuy( (HeroAction.Buy)curAction );

			} else if (curAction instanceof HeroAction.LifeBuy) {
				actResult = actLifeBuy( (HeroAction.LifeBuy)curAction );
				
			}else if (curAction instanceof HeroAction.PickUp) {
				actResult = actPickUp( (HeroAction.PickUp)curAction );
				
			} else if (curAction instanceof HeroAction.OpenChest) {
				actResult = actOpenChest( (HeroAction.OpenChest)curAction );
				
			} else if (curAction instanceof HeroAction.Unlock) {
				actResult = actUnlock((HeroAction.Unlock) curAction);
				
			} else if (curAction instanceof HeroAction.Mine) {
				actResult = actMine( (HeroAction.Mine)curAction );

			}else if (curAction instanceof HeroAction.LvlTransition) {
				actResult = actTransition( (HeroAction.LvlTransition)curAction );
				
			} else if (curAction instanceof HeroAction.Attack) {
				actResult = actAttack( (HeroAction.Attack)curAction );
				
			} else if (curAction instanceof HeroAction.Alchemy) {
				actResult = actAlchemy( (HeroAction.Alchemy)curAction );
				
			} else {
				actResult = false;
			}
		}
		
		return actResult;
	}
	
	public void busy() {
		ready = false;
	}
	
	private void ready() {
		if (sprite.looping()) sprite.idle();
		curAction = null;
		damageInterrupt = true;
		waitOrPickup = false;
		ready = true;
		canSelfTrample = true;

		//SPS: 还能"继续行动"（右下角有继续按钮）时不收起路径预览；
		//只有彻底停下（含被打断后再也接不上）才清掉。
		if (lastAction == null) {
			GameScene.clearHeroPath();
		}

		AttackIndicator.updateState();
		
		GameScene.ready();
		//check statistics to see if vault warned?
		//or just used shared prefs?
		if (Dungeon.level instanceof VaultLevel
				&& HP < HT*0.334f
				&& !Statistics.vaultInjureWarned
				&& SPDSettings.vaultInjureWarns() < 3){
			SPDSettings.vaultInjureWarns(SPDSettings.vaultInjureWarns()+1);
			Statistics.vaultInjureWarned = true;
			ShatteredPixelDungeon.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					String text = Messages.get(EscapeCrystal.class, "injure_warning_1");
					if (!Dungeon.level.locked) {
						text += "\n\n" + Messages.get(EscapeCrystal.class, "injure_warning_2");
					}
					text += "\n\n" + Messages.get(EscapeCrystal.class, "injure_warning_3");
					GameScene.show(new WndOptions(new ImpSprite(),
							Messages.titleCase(Messages.get(Imp.class, "name")),
							text,
							//recycling this one
							Messages.get(WelcomeScene.class, "controller_okay")){

						@Override
						protected void onSelect(int index) {
							super.onSelect(index);
						}

						@Override
						public void onBackPressed() {
							//do nothing, must close via button
						}
					});
				}
			});
		}
	}
	
	public void interrupt() {
		if (isAlive() && curAction != null &&
			((curAction instanceof HeroAction.Move && curAction.dst != pos) ||
			(curAction instanceof HeroAction.LvlTransition))) {
			lastAction = curAction;
		}
		curAction = null;
		GameScene.resetKeyHold();
		resting = false;
	}
	
	public void resume() {
		curAction = lastAction;
		lastAction = null;
		damageInterrupt = false;
		next();
	}

	private boolean canSelfTrample = false;
	public boolean canSelfTrample(){
		return canSelfTrample && !rooted && !flying &&
				//standing in high grass
				(Dungeon.level.map[pos] == Terrain.HIGH_GRASS ||
				//standing in furrowed grass and not huntress
				(heroClass != HeroClass.HUNTRESS && Dungeon.level.map[pos] == Terrain.FURROWED_GRASS) ||
				//standing on a plant
				Dungeon.level.plants.get(pos) != null);
	}
	
	public boolean justMoved;

	private boolean actMove( HeroAction.Move action ) {

		if (getCloser( action.dst )) {
			justMoved = true;
			canSelfTrample = false;
			return true;

		//Hero moves in place if there is grass to trample
		} else if (pos == action.dst && canSelfTrample()){
			canSelfTrample = false;
			Dungeon.level.pressCell(pos);
			spendAndNext( 1 / speed() );
			return false;
		} else {
			if (pos == action.dst && Dungeon.level.map[pos] == Terrain.SIGN) {
				Sign.read(pos);
			}
			ready();
			return false;
		}
	}
	
	private boolean actInteract( HeroAction.Interact action ) {
		
		Char ch = action.ch;

		if (ch.isAlive() && ch.canInteract(this)) {
			
			ready();
			sprite.turnTo( pos, ch.pos );
			return ch.interact(this);
			
		} else {
			
			if ((fieldOfView[ch.pos] || Char.hasProp(ch, Property.OBJECT)) && getCloser( ch.pos )) {

				return true;

			} else {
				ready();
				return false;
			}
			
		}
	}
	
	private boolean actBuy( HeroAction.Buy action ) {
		int dst = action.dst;
		if (pos == dst) {

			ready();
			
			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && heap.type == Type.FOR_SALE && heap.size() == 1) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndTradeItem( heap ) );
					}
				});
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actLifeBuy( HeroAction.LifeBuy action ) {
		int dst = action.dst;
		if (pos == dst) {
			ready();
			Heap heap = Dungeon.level.heaps.get(dst);
			if (heap != null && heap.type == Type.FOR_LIFE && heap.size() == 1) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndLifeTradeItem(heap));
					}
				});
			}
			return false;
		} else if (getCloser(dst)) {
			return true;
		} else {
			ready();
			return false;
		}
	}

	private boolean actAlchemy( HeroAction.Alchemy action ) {
		final int dst = action.dst;
		if (Dungeon.level.distance(dst, pos) <= 1) {

			ready();

			//SPS: 交互与场景切换必须切回渲染线程（actor 线程构造 UI / switchScene 都会崩）
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					if (Dungeon.level.map[dst] == Terrain.TENT) {
						GameScene.show(new WndTent());
					} else if (Dungeon.level.map[dst] == Terrain.IRON_MAKER) {
						GameScene.show(new WndIronMaker());
					} else {
						AlchemyScene.clearToolkit();
						ShatteredPixelDungeon.switchScene(AlchemyScene.class);
					}
				}
			});
			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	//used to keep track if the wait/pickup action was used
	// so that the hero spends a turn even if the fail to pick up an item
	public boolean waitOrPickup = false;

	private boolean actPickUp( HeroAction.PickUp action ) {
		int dst = action.dst;
		if (pos == dst) {
			
			Heap heap = Dungeon.level.heaps.get( pos );
			if (heap != null) {
				Item item = heap.peek();
				if (item.doPickUp( this )) {
					heap.pickUp();

					//TODO this statement is getting silly, might be better to handle this as a propery of items
					if (item instanceof Dewdrop
							|| (item instanceof DwarfToken && Imp.Quest.mirrorUsed)
							|| item instanceof TimekeepersHourglass.sandBag
							|| item instanceof DriedRose.Petal
							|| item instanceof Key
							|| item instanceof Guidebook
							|| (item instanceof MissileWeapon && !MissileWeapon.UpgradedSetTracker.pickupValid(this, (MissileWeapon) item))) {
						//Do Nothing
					} else if (item instanceof DarkGold) {
						DarkGold existing = belongings.getItem(DarkGold.class);
						if (existing != null){
							if (existing.quantity() >= 40) {
								GLog.p(Messages.get(DarkGold.class, "you_now_have", existing.quantity()));
							} else {
								GLog.i(Messages.get(DarkGold.class, "you_now_have", existing.quantity()));
							}
						}
					} else {

						boolean important = item.unique && item.isIdentified() &&
								(item instanceof Scroll || item instanceof Potion);
						if (important) {
							GLog.p( Messages.capitalize(Messages.get(this, "you_now_have", item.name())) );
						} else {
							GLog.i( Messages.capitalize(Messages.get(this, "you_now_have", item.name())) );
						}
					}
					
					curAction = null;
				} else {

					if (waitOrPickup) {
						spendAndNextConstant(TIME_TO_REST);
					}

					//allow the hero to move between levels even if they can't collect the item
					if (Transitions.get( Dungeon.level, pos) != null){
						throwItems();
					} else {
						heap.sprite.drop();
					}

					if (item instanceof Dewdrop
							|| item instanceof TimekeepersHourglass.sandBag
							|| item instanceof DriedRose.Petal
							|| item instanceof Key) {
						//Do Nothing
					} else {
						GLog.newLine();
						GLog.n(Messages.capitalize(Messages.get(this, "you_cant_have", item.name())));
					}

					ready();
				}
			} else {
				ready();
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actOpenChest( HeroAction.OpenChest action ) {
		int dst = action.dst;
		if (Dungeon.level.adjacent( pos, dst ) || pos == dst) {
			path = null;
			
			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && (heap.type != Type.HEAP && heap.type != Type.FOR_SALE
					&& heap.type != Type.FOR_LIFE)) {

				boolean noKey = false;
				if (heap.type == Type.LOCKED_CHEST){
					noKey = (Dungeon.branch != 0 || Notes.keyCount(new GoldenKey(Dungeon.depth)) < 1)
							&& Notes.keyCount(new GoldenSkeletonKey(0)) < 1;
				} else if (heap.type == Type.CRYSTAL_CHEST){
					noKey = (Dungeon.branch != 0 || (Notes.keyCount(new CrystalKey(Dungeon.depth)) < 1
							&& Notes.keyCount(new GoldenKey(Dungeon.depth)) < 1))
							&& Notes.keyCount(new GoldenSkeletonKey(0)) < 1;
				}

				if (noKey){

						GLog.w( Messages.get(this, "locked_chest") );
						ready();
						return false;

				}
				
				switch (heap.type) {
				case TOMB:
					Sample.INSTANCE.play( Assets.Sounds.TOMB );
					PixelScene.shake( 1, 0.5f );
					break;
				case SKELETON:
				case REMAINS:
					break;
				default:
					Sample.INSTANCE.play( Assets.Sounds.UNLOCK );
				}
				
				sprite.operate( dst );
				
			} else {
				ready();
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actUnlock( HeroAction.Unlock action ) {
		int doorCell = action.dst;
		if (Dungeon.level.adjacent( pos, doorCell )) {
			path = null;
			
			boolean hasKey = false;
			int door = Dungeon.level.map[doorCell];
			
			if (Dungeon.branch != 0) {

				//keys currently do not apply to sub-floors
				hasKey = false;

			} else if (door == Terrain.LOCKED_DOOR
					&& Notes.keyCount(new IronKey(Dungeon.depth)) > 0) {
				
				hasKey = true;

			} else if (door == Terrain.HERO_LKD_DR){

				if (belongings.getItem(SkeletonKey.class) != null
						&& !belongings.getItem(SkeletonKey.class).cursed){
					GLog.i(Messages.get(SkeletonKey.class, "locked_with_key"));
					ready();
					return false;
				} else {
					hasKey = true;
				}
				
			} else if (door == Terrain.CRYSTAL_DOOR
					&& Notes.keyCount(new CrystalKey(Dungeon.depth)) > 0) {

				hasKey = true;

			} else if (door == Terrain.LOCKED_EXIT
					&& (Notes.keyCount(new SpsSkeletonKey(Dungeon.depth)) > 0
					|| Notes.keyCount(new WornKey(Dungeon.depth)) > 0)) {

				hasKey = true;
				
			}
			
			if (hasKey) {
				
				sprite.operate( doorCell );
				
				Sample.INSTANCE.play( Assets.Sounds.UNLOCK );
				
			} else {
				GLog.w( Messages.get(this, "locked_door") );
				ready();
			}

			return false;

		} else if (getCloser( doorCell )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actMine(HeroAction.Mine action){
		if (Dungeon.level.adjacent(pos, action.dst)){
			path = null;
			if ((Dungeon.level.map[action.dst] == Terrain.WALL
					|| Dungeon.level.map[action.dst] == Terrain.WALL_DECO
					|| Dungeon.level.map[action.dst] == Terrain.MINE_CRYSTAL
					|| Dungeon.level.map[action.dst] == Terrain.MINE_BOULDER)
				&& Dungeon.level.insideMap(action.dst)){
				sprite.attack(action.dst, new Callback() {
					@Override
					public void call() {

						boolean crystalAdjacent = false;
						for (int i : PathFinder.NEIGHBOURS8) {
							if (Dungeon.level.map[action.dst + i] == Terrain.MINE_CRYSTAL){
								crystalAdjacent = true;
								break;
							}
						}

						//1 hunger spent total
						if (Dungeon.level.map[action.dst] == Terrain.WALL_DECO){
							DarkGold gold = new DarkGold();
							if (gold.doPickUp( Dungeon.hero )) {
								DarkGold existing = Dungeon.hero.belongings.getItem(DarkGold.class);
								if (existing != null && existing.quantity()%5 == 0){
									if (existing.quantity() >= 40) {
										GLog.p(Messages.get(DarkGold.class, "you_now_have", existing.quantity()));
									} else {
										GLog.i(Messages.get(DarkGold.class, "you_now_have", existing.quantity()));
									}
								}
								spend(-Actor.TICK); //picking up the gold doesn't spend a turn here
							} else {
								Dungeon.level.drop( gold, pos ).sprite.drop();
							}
							PixelScene.shake(0.5f, 0.5f);
							CellEmitter.center( action.dst ).burst( Speck.factory( Speck.STAR ), 7 );
							Sample.INSTANCE.play( Assets.Sounds.EVOKE );
							Level.set( action.dst, Terrain.EMPTY_DECO );

							//mining gold doesn't break crystals
							crystalAdjacent = false;

						//4 hunger spent total
						} else if (Dungeon.level.map[action.dst] == Terrain.WALL){
							buff(Hunger.class).affectHunger(-3);
							PixelScene.shake(0.5f, 0.5f);
							CellEmitter.get( action.dst ).burst( Speck.factory( Speck.ROCK ), 2 );
							Sample.INSTANCE.play( Assets.Sounds.MINE );
							Level.set( action.dst, Terrain.EMPTY_DECO );

						//1 hunger spent total
						} else if (Dungeon.level.map[action.dst] == Terrain.MINE_CRYSTAL){
							Splash.at(action.dst, 0xFFFFFF, 5);
							Sample.INSTANCE.play( Assets.Sounds.SHATTER );
							Level.set( action.dst, Terrain.EMPTY );

						//1 hunger spent total
						} else if (Dungeon.level.map[action.dst] == Terrain.MINE_BOULDER){
							Splash.at(action.dst, 0x555555, 5);
							Sample.INSTANCE.play( Assets.Sounds.MINE, 0.6f );
							Level.set( action.dst, Terrain.EMPTY_DECO );
						}

						for (int i : PathFinder.NEIGHBOURS9) {
							Dungeon.level.discoverable[action.dst + i] = true;
						}
						for (int i : PathFinder.NEIGHBOURS9) {
							GameScene.updateMap( action.dst+i );
						}

						if (crystalAdjacent){
							sprite.parent.add(new Delayer(0.2f){
								@Override
								protected void onComplete() {
									boolean broke = false;
									for (int i : PathFinder.NEIGHBOURS8) {
										if (Dungeon.level.map[action.dst+i] == Terrain.MINE_CRYSTAL){
											Splash.at(action.dst+i, 0xFFFFFF, 5);
											Level.set( action.dst+i, Terrain.EMPTY );
											broke = true;
										}
									}
									if (broke){
										Sample.INSTANCE.play( Assets.Sounds.SHATTER );
									}

									for (int i : PathFinder.NEIGHBOURS9) {
										GameScene.updateMap( action.dst+i );
									}
									spendAndNext(TICK);
									ready();
								}
							});
						} else {
							spendAndNext(TICK);
							ready();
						}

						Dungeon.observe();
					}
				});
			} else {
				ready();
			}
			return false;
		} else if (getCloser( action.dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actTransition(HeroAction.LvlTransition action ) {
		int stairs = action.dst;
		LevelTransition transition = Transitions.get( Dungeon.level, stairs);

		if (rooted) {
			PixelScene.shake(1, 1f);
			ready();
			return false;

		} else if (!Dungeon.level.locked && transition != null && transition.inside(pos)) {

			if (Dungeon.level.activateTransition(this, transition)){
				curAction = null;
			} else {
				ready();
			}

			return false;

		} else if (getCloser( stairs )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actAttack( HeroAction.Attack action ) {

		attackTarget = action.target;
		if (buff(Disarm.class) != null) {
			GLog.w(Messages.get(Disarm.class, "cant_attack"));
			ready();
			attackTarget = null;
			return false;
		}

		if (isCharmedBy(attackTarget)){
			GLog.w( Messages.get(Charm.class, "cant_attack"));
			ready();
			return false;
		}

		if (attackTarget.isAlive() && canAttack(attackTarget) && attackTarget.invisible == 0) {

			if (heroClass != HeroClass.DUELIST
					&& hasTalent(Talent.AGGRESSIVE_BARRIER)
					&& buff(Talent.AggressiveBarrierCooldown.class) == null
					&& (HP / (float)HT) <= 0.5f){
				int shieldAmt = 1 + 2*pointsInTalent(Talent.AGGRESSIVE_BARRIER);
				Buff.affect(this, Barrier.class).setShield(shieldAmt);
				sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldAmt), FloatingText.SHIELDING);
				Buff.affect(this, Talent.AggressiveBarrierCooldown.class, 50f);

			}
			//attack target cleared on onAttackComplete
			sprite.attack( attackTarget.pos );

			return false;

		} else {

			if (fieldOfView[attackTarget.pos] && getCloser( attackTarget.pos )) {

				attackTarget = null;
				return true;

			} else {
				ready();
				attackTarget = null;
				return false;
			}

		}
	}

	public Char attackTarget(){
		return attackTarget;
	}
	
	public void rest( boolean fullRest ) {
		spendAndNextConstant( TIME_TO_REST );
		if (hasTalent(Talent.HOLD_FAST)){
			if (heroClass != HeroClass.WARRIOR || buff(BrokenSeal.WarriorShield.class) != null) {
				Buff.affect(this, HoldFast.class).pos = pos;
			}
		}
		if (hasTalent(Talent.PATIENT_STRIKE)){
			Buff.affect(Dungeon.hero, Talent.PatientStrikeTracker.class).pos = Dungeon.hero.pos;
		}
		if (!fullRest) {
			if (sprite != null) {
				sprite.showStatus(CharSprite.DEFAULT, Messages.get(this, "wait"));
			}
		}
		resting = fullRest;
	}
	
	@Override
	public int attackProc( final Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );

		//SPSXPD: 暴击系统（照暗黑实现，近战/法术/投掷共用 Critical 入口）
		damage = pd.actors.hero.Critical.roll( this, damage );
		//SPSXPD: 宠物能力特质（献祭获得）在命中时生效
		pd.actors.hero.perks.pets.PetAbilityPerk.dispatchHit(this, enemy, damage);

		if (pd.actors.hero.Critical.lastWasCrit()) {
			//「汲血暴击」：暴击时按伤害比例回血
			pd.actors.hero.perks.VampiricCrit.tryProc( this, damage );
		}

		KindOfWeapon wep;
		if (RingOfForce.fightingUnarmed(this) && !RingOfForce.unarmedGetsWeaponEnchantment(this)){
			wep = null;
		} else {
			wep = belongings.attackingWeapon();
		}

		damage = Talent.onAttackProc( this, enemy, damage );

		//SPSEXPD: 蔬菜的“下次攻击”类效果（露珠菌孢）
		DewScatter.onHeroAttack( this, enemy, damage );

		OnePunch onePunch = buff(OnePunch.class);
		if (onePunch != null) {
			damage = onePunch.empower(damage);
			onePunch.detach();
		}

		if (wep != null) {
			damage = wep.proc( this, enemy, damage );
		} else {

			if (buff(BodyForm.BodyFormBuff.class) != null && buff(BodyForm.BodyFormBuff.class).enchant() != null){
				damage = buff(BodyForm.BodyFormBuff.class).enchant().proc(new WornShortsword(), this, enemy, damage);
			}
			if (enemy.isAlive() && buff(HolyWeapon.HolyWepBuff.class) != null) {
				int dmg = subClass == HeroSubClass.PALADIN ? 6 : 2;
				enemy.damage(Math.round(dmg * Weapon.Enchantment.genericProcChanceMultiplier(this)), HolyWeapon.INSTANCE);
			}
			if (enemy.isAlive() && buff(Smite.SmiteTracker.class) != null) {
				enemy.damage(Smite.bonusDmg(this, enemy), Smite.INSTANCE);
			}
		}
		GoldTouch goldTouch = buff(GoldTouch.class);
		if (goldTouch != null && !(wep instanceof MissileWeapon)) {
			int depthDivisor = Math.max(1, 20 - Statistics.deepestFloor);
			if (Dungeon.gold < 1_000_000 / depthDivisor) {
				int earnedGold = Math.max(0, Math.min(1000 * lvl, damage));
				Dungeon.gold += earnedGold;
				if (earnedGold > 0 && sprite != null) {
					sprite.showStatusWithIcon(CharSprite.NEUTRAL, "+" + earnedGold, FloatingText.GOLD);
				}
			}
		}
		ItemSteal steal = buff(ItemSteal.class);
		if (steal != null && enemy instanceof Mob) {
			Mob mob = (Mob) enemy;
			if (mob.firstItem) {
				Item loot = mob.SupercreateLoot();
				if (loot != null) Dungeon.level.drop(loot, pos).sprite.drop();
				mob.firstItem = false;
				steal.detach();
			}
		}

		damage = (int)(damage * FaithBuff.outgoingMultiplier(this, enemy));
		if (buff(CrazyMind.class) != null && Random.Int(10) == 0) damage = 0;
		if (buff(CrazyMind.class) != null) damage = Math.round(damage * 1.2f);
		if (buff(LoseMind.class) != null) damage = Math.round(damage * 1.2f);
		if (buff(AmokMind.class) != null) damage = Math.round(damage * 1.2f);
		if (buff(WeakMind.class) != null) damage = Math.round(damage * 1.2f);
		if (buff(TerrorMind.class) != null) damage = Math.round(damage * 1.2f);
		HorseTotem horseTotem = belongings.getItem(HorseTotem.class);
		if (horseTotem != null && horseTotem.shouldTrigger(this)) {
			damage = horseTotem.empower(this, damage);
		}
		RangeBag rangeBag = belongings.getItem(RangeBag.class);
		if (rangeBag != null && enemy.HP <= damage && rangeBag.shouldDrop(this)) {
			rangeBag.drop(rangeBag.createDrop(), enemy.pos);
		}
		AttackShield attackShield = belongings.getItem(AttackShield.class);
		if (attackShield != null) attackShield.gainCharge();
		HealBag healBag = belongings.getItem(HealBag.class);
		if (healBag != null) healBag.gainCharge();
		DiceTower diceTower = belongings.getItem(DiceTower.class);
		if (diceTower != null) diceTower.gainCharge();
		SeriousPunch seriousPunch = belongings.getItem(SeriousPunch.class);
		if (seriousPunch != null) seriousPunch.gainCharge();
		
		switch (subClass) {
		case SNIPER:
			if (wep instanceof MissileWeapon && !(wep instanceof SpiritBow.SpiritArrow) && enemy != this) {
				Actor.add(new Actor() {
					
					{
						actPriority = VFX_PRIO;
					}
					
					@Override
					protected boolean act() {
						if (enemy.isAlive()) {
							if (hasTalent(Talent.SHARED_UPGRADES)){
								int levelBonus = Math.min( 2*pointsInTalent(Talent.SHARED_UPGRADES), wep.buffedLvl() );
								// bonus dmg is 16.67% x weapon level, max of 2/4/6
								float bonusDmg = levelBonus/6f;
								Buff.prolong(Hero.this, SnipersMark.class, SnipersMark.DURATION + levelBonus).set(enemy.id(), bonusDmg);
							} else {
								Buff.prolong(Hero.this, SnipersMark.class, SnipersMark.DURATION).set(enemy.id(), 0);
							}
						}
						Actor.remove(this);
						return true;
					}
				});
			}
			break;
		case JOKER:
			if (wep instanceof MissileWeapon && Random.Int(5) == 0) {
				Buff.prolong(enemy, Cripple.class, 2f);
			}
			break;
		default:
		}
		
		return damage;
	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {
		if (subClass == HeroSubClass.LEADER) damage = Math.round(damage * 0.90f);
		UndeadBook undeadBook = belongings.getItem(UndeadBook.class);
		if (undeadBook != null) undeadBook.gainCharge();
		SavageHelmet savageHelmet = belongings.getItem(SavageHelmet.class);
		if (savageHelmet != null && savageHelmet.shouldTrigger(this)) {
			damage = savageHelmet.absorb(this, damage);
		}
		Earthroot.MagicPlantArmor naturalArmor = buff(Earthroot.MagicPlantArmor.class);
		if (naturalArmor != null) damage = naturalArmor.absorb(damage);
		
		if (damage > 0 && subClass == HeroSubClass.BERSERKER){
			Berserk berserk = Buff.affect(this, Berserk.class);
			berserk.damage(damage);
		}
		
		if (belongings.armor() != null) {
			damage = belongings.armor().proc( enemy, this, damage );
		} else {
			if (buff(BodyForm.BodyFormBuff.class) != null
				&& buff(BodyForm.BodyFormBuff.class).glyph() != null){
				damage = buff(BodyForm.BodyFormBuff.class).glyph().proc(new ClothArmor(), enemy, this, damage);
			}
			if (buff(HolyWard.HolyArmBuff.class) != null){
				int blocking = subClass == HeroSubClass.PALADIN ? 3 : 1;
				damage -= Math.round(blocking * Armor.Glyph.genericProcChanceMultiplier(enemy));
			}
		}

		WandOfLivingEarth.RockArmor rockArmor = buff(WandOfLivingEarth.RockArmor.class);
		if (rockArmor != null) {
			damage = rockArmor.absorb(damage);
		}
		
		return super.defenseProc( enemy, damage );
	}

	@Override
	public int glyphLevel(Class<? extends Armor.Glyph> cls) {
		if (belongings.armor() != null && belongings.armor().hasGlyph(cls, this)){
			return Math.max(super.glyphLevel(cls), belongings.armor.buffedLvl());
		} else if (buff(BodyForm.BodyFormBuff.class) != null
				&& buff(BodyForm.BodyFormBuff.class).glyph() != null
				&& buff(BodyForm.BodyFormBuff.class).glyph().getClass() == cls){
			return belongings.armor() != null ? belongings.armor.buffedLvl() : 0;
		} else {
			return super.glyphLevel(cls);
		}
	}

	@Override
	public void damage( int dmg, Object src ) {
		//SPSXPD: 宠物能力特质（献祭获得）的受击触发
		pd.actors.hero.perks.pets.PetAbilityPerk.dispatchHurt(this, src instanceof pd.actors.Char ? (pd.actors.Char) src : null, dmg);
		if (buff(TimekeepersHourglass.timeStasis.class) != null
				|| buff(TimeStasis.class) != null) {
			return;
		}

		//regular damage interrupt, triggers on any damage except specific mild DOT effects
		// unless the player recently hit 'continue moving', in which case this is ignored
		if (!(src instanceof Hunger || src instanceof Viscosity.DeferedDamage
				|| src instanceof pd.actors.blobs.NmGas)
				&& damageInterrupt) {
			interrupt();
		}

		if (this.buff(Drowsy.class) != null){
			Buff.detach(this, Drowsy.class);
			GLog.w( Messages.get(this, "pain_resist") );
		}

		//temporarily assign to a float to avoid rounding a bunch
		float damage = dmg;
		if (FishBone.protectsFrom(this, src)) damage = 0;

		Endure.EndureTracker endure = buff(Endure.EndureTracker.class);
		if (!(src instanceof Char)){
			//reduce damage here if it isn't coming from a character (if it is we already reduced it)
			if (endure != null){
				damage = endure.adjustDamageTaken(dmg);
			}
			//the same also applies to challenge scroll damage reduction
			if (buff(ScrollOfChallenge.ChallengeArena.class) != null){
				damage *= 0.67f;
			}
			//and to monk meditate damage reduction
			if (buff(MonkEnergy.MonkAbility.Meditate.MeditateResistance.class) != null){
				damage *= 0.2f;
			}
		}

		//SPSEXPD: 荆棘斗篷已移除，其荆棘反弹钩子一并删除

		if (buff(Talent.WarriorFoodImmunity.class) != null){
			if (pointsInTalent(Talent.IRON_STOMACH) == 1)       damage /= 4f;
			else if (pointsInTalent(Talent.IRON_STOMACH) == 2)  damage = 0;
		}

		if (src instanceof Char) {
			Char enemy = (Char)src;
			damage = (float)Math.ceil(damage * FaithBuff.incomingMultiplier(this, enemy));
		}
		damage = (float)Math.ceil(damage * RingOfElements.damageMultiplier(this, src));
		if (buff(BloodAngry.class) != null) damage = (float)Math.ceil(damage * 0.8f);
		if (buff(Rhythm2.class) != null) damage = (float)Math.ceil(damage * 0.9f);
		if (buff(WeakMind.class) != null) damage = (float)Math.ceil(damage * 1.3f);

		dmg = Math.round(damage);

		//we ceil this one to avoid letting the player easily take 0 dmg from tenacity early
		dmg = (int)Math.ceil(dmg * RingOfTenacity.damageMultiplier( this ));

		int preHP = HP + shielding();
		if (src instanceof Hunger) preHP -= shielding();
		super.damage( dmg, src );
		int postHP = HP + shielding();
		if (src instanceof Hunger) postHP -= shielding();
		int effectiveDamage = preHP - postHP;

		if (effectiveDamage <= 0) return;

		if (buff(Challenge.DuelParticipant.class) != null){
			buff(Challenge.DuelParticipant.class).addDamage(effectiveDamage);
		}

		//flash red when hit for serious damage.
		float percentDMG = effectiveDamage / (float)preHP; //percent of current HP that was taken
		float percentHP = 1 - ((HT - postHP) / (float)HT); //percent health after damage was taken
		// The flash intensity increases primarily based on damage taken and secondarily on missing HP.
		float flashIntensity = 0.25f * (percentDMG * percentDMG) / percentHP;
		//if the intensity is very low don't flash at all
		if (flashIntensity >= 0.05f){
			flashIntensity = Math.min(1/3f, flashIntensity); //cap intensity at 1/3
			GameScene.flash( (int)(0xFF*flashIntensity) << 16 );
			if (isAlive()) {
				if (flashIntensity >= 1/6f) {
					Sample.INSTANCE.play(Assets.Sounds.HEALTH_CRITICAL, 1/3f + flashIntensity * 2f);
				} else {
					Sample.INSTANCE.play(Assets.Sounds.HEALTH_WARN, 1/3f + flashIntensity * 4f);
				}
				//hero gets interrupted on taking serious damage, regardless of any other factor
				interrupt();
				damageInterrupt = true;
			}
		}
	}
	
	public void checkVisibleMobs() {
		ArrayList<Mob> visible = new ArrayList<>();

		boolean newMob = false;

		Mob target = null;
		for (Mob m : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (fieldOfView[ m.pos ] && m.landmark() != null){
				Notes.add(m.landmark());
			}

			if (fieldOfView[ m.pos ] && m.alignment == Alignment.ENEMY) {
				visible.add(m);
				if (!visibleEnemies.contains( m )) {
					newMob = true;
				}

				//only do a simple check for mind visioned enemies, better performance
				if ((!mindVisionEnemies.contains(m) && QuickSlotButton.autoAim(m) != -1)
						|| (mindVisionEnemies.contains(m) && new Ballistica( pos, m.pos, Ballistica.PROJECTILE ).collisionPos == m.pos)) {
					if (target == null) {
						target = m;
					} else if (distance(target) > distance(m)) {
						target = m;
					}
					if (m instanceof Snake && Dungeon.level.distance(m.pos, pos) <= 4
							&& !Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_EXAMINING)){
						GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_EXAMINING);
						//we set to read here to prevent this message popping up a bunch
						Document.ADVENTURERS_GUIDE.readPage(Document.GUIDE_EXAMINING);
					}
				}
			}
		}

		Char lastTarget = QuickSlotButton.lastTarget;
		if (target != null && (lastTarget == null ||
							!lastTarget.isAlive() || !lastTarget.isActive() ||
							lastTarget.alignment == Alignment.ALLY ||
							!fieldOfView[lastTarget.pos])){
			QuickSlotButton.target(target);
		}
		
		if (newMob) {
			if (resting){
				Dungeon.observe();
			}
			interrupt();
		}

		visibleEnemies = visible;

		//we also scan for blob landmarks here
		for (Blob b : Dungeon.level.blobs.values().toArray(new Blob[0])){
			if (b.volume > 0 && b.landmark() != null && !Notes.contains(b.landmark())){
				int cell;
				boolean found = false;
				//if a single cell within the blob is visible, we add the landmark
				for (int i=b.area.top; i < b.area.bottom; i++) {
					for (int j = b.area.left; j < b.area.right; j++) {
						cell = j + i* Dungeon.level.width();
						if (fieldOfView[cell] && b.cur[cell] > 0) {
							Notes.add( b.landmark() );
							found = true;
							break;
						}
					}
					if (found) break;
				}

				//Clear blobs that only exist for landmarks.
				// Might want to make this a properly if it's used more
				if (found && b instanceof WeakFloorRoom.WellID){
					b.fullyClear();
				}
			}
		}
	}
	
	public int visibleEnemies() {
		return visibleEnemies.size();
	}
	
	public Mob visibleEnemy( int index ) {
		return visibleEnemies.get(index % visibleEnemies.size());
	}

	public ArrayList<Mob> getVisibleEnemies(){
		return new ArrayList<>(visibleEnemies);
	}
	
	private boolean walkingToVisibleTrapInFog = false;
	
	private boolean getCloser( final int target ) {

		if (target == pos)
			return false;

		if (rooted) {
			PixelScene.shake( 1, 1f );
			return false;
		}
		
		int step = -1;
		
		if (Dungeon.level.adjacent( pos, target )) {

			path = null;

			if (Actor.findChar( target ) == null) {
				if (Dungeon.level.passable[target] || Dungeon.level.avoid[target]) {
					step = target;
				}
				if (walkingToVisibleTrapInFog
						&& Dungeon.level.traps.get(target) != null
						&& Dungeon.level.traps.get(target).visible
						&& Dungeon.level.traps.get(target).active){
					return false;
				}
			}
			
		} else {

			boolean newPath = false;
			if (path == null || path.isEmpty() || !Dungeon.level.adjacent(pos, path.getFirst()))
				newPath = true;
			else if (path.getLast() != target)
				newPath = true;
			else {
				if (!Dungeon.level.passable[path.get(0)] || Actor.findChar(path.get(0)) != null) {
					newPath = true;
				}
			}

			if (newPath) {

				int len = Dungeon.level.length();
				boolean[] p = Dungeon.level.passable;
				boolean[] v = Dungeon.level.visited;
				boolean[] m = Dungeon.level.mapped;
				boolean[] passable = new boolean[len];
				for (int i = 0; i < len; i++) {
					passable[i] = p[i] && (v[i] || m[i]);
				}

				PathFinder.Path newpath = Dungeon.findPath(this, target, passable, fieldOfView, true);
				if (newpath != null && path != null && newpath.size() > 2*path.size()){
					path = null;
				} else {
					path = newpath;
				}
			}

			if (path == null) return false;
			step = path.removeFirst();

		}

		if (step != -1) {

			float delay = 1;

			if (buff(GreaterHaste.class) != null){
				delay = 0;
			}

			if (Dungeon.level.pit[step] && !Dungeon.level.solid[step]
					&& (!flying || buff(Levitation.class) != null && buff(Levitation.class).detachesWithinDelay(delay / speed()))){
				if (!Chasm.jumpConfirmed){
					Chasm.heroJump(this);
					interrupt();
				} else {
					flying = false;
					remove(buff(Levitation.class)); //directly remove to prevent cell pressing
					Chasm.heroFall(target);
				}
				canSelfTrample = false;
				return false;
			}

			if (buff(GreaterHaste.class) != null){
				buff(GreaterHaste.class).spendMove();
			}

			if (subClass == HeroSubClass.FREERUNNER){
				Buff.affect(this, Momentum.class).gainStack();
			}
			
			sprite.move(pos, step);
			move(step);

			spend( delay / speed() );
			
			search(false);

			return true;

		} else {

			return false;
			
		}

	}
	
	public boolean handle( int cell ) {
		
		if (cell == -1) {
			return false;
		}

		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
			FieldOfView.update( Dungeon.level,  this, fieldOfView );
		}

		if (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell]
				&& Dungeon.level.traps.get(cell) != null
				&& Dungeon.level.traps.get(cell).visible
				&& Dungeon.level.traps.get(cell).active) {
			walkingToVisibleTrapInFog = true;
		} else {
			walkingToVisibleTrapInFog = false;
		}
		
		Char ch = Actor.findChar( cell );
		Heap heap = Dungeon.level.heaps.get( cell );

		//SPSXPD: 点自己的伙伴投影：
		//  - 有 1 级驯兽大师：直接走到它脚下的格子（同格已放行）
		//  - 否则：相邻则交换位置，远处则走到它旁边（寻路自然停在旁边）
		if (ch instanceof pd.actors.mobs.pets.LegacyPet && ch != this) {
			pd.actors.hero.perks.BeastMaster beastMaster = heroPerk.get(pd.actors.hero.perks.BeastMaster.class);
			if (beastMaster != null || !Dungeon.level.adjacent(pos, cell)) {
				curAction = new HeroAction.Move( cell );
			} else {
				curAction = new HeroAction.Interact( ch );
			}
		} else if ((Dungeon.level.map[cell] == Terrain.ALCHEMY
				|| Dungeon.level.map[cell] == Terrain.TENT
				|| Dungeon.level.map[cell] == Terrain.IRON_MAKER) && cell != pos) {
			
			curAction = new HeroAction.Alchemy( cell );
			
		} else if (ch instanceof Mob && (fieldOfView[cell] || Char.hasProp(ch, Property.OBJECT))) {

			if (((Mob) ch).heroShouldInteract()) {
				curAction = new HeroAction.Interact( ch );
			} else {
				curAction = new HeroAction.Attack( ch );
			}

		//TODO perhaps only trigger this if hero is already adjacent? reducing mistaps
		} else if (Dungeon.level instanceof MiningLevel &&
					belongings.getItem(Pickaxe.class) != null &&
				(Dungeon.level.map[cell] == Terrain.WALL
						|| Dungeon.level.map[cell] == Terrain.WALL_DECO
						|| Dungeon.level.map[cell] == Terrain.MINE_CRYSTAL
						|| Dungeon.level.map[cell] == Terrain.MINE_BOULDER)){

			curAction = new HeroAction.Mine( cell );

		} else if (heap != null
				//moving to an item doesn't auto-pickup when enemies are near...
				&& (visibleEnemies.size() == 0 || cell == pos ||
				//...but only for standard heaps. Chests and similar open as normal.
				(heap.type != Type.HEAP && heap.type != Type.FOR_SALE && heap.type != Type.FOR_LIFE))) {

			switch (heap.type) {
			case HEAP:
				curAction = new HeroAction.PickUp( cell );
				break;
			case FOR_SALE:
				curAction = heap.size() == 1 && heap.peek().value() > 0 ?
					new HeroAction.Buy( cell ) :
					new HeroAction.PickUp( cell );
				break;
			case FOR_LIFE:
				curAction = heap.size() == 1 ? new HeroAction.LifeBuy(cell) :
						new HeroAction.PickUp(cell);
				break;
			default:
				curAction = new HeroAction.OpenChest( cell );
			}
			
		} else if (Dungeon.level.map[cell] == Terrain.LOCKED_DOOR
				|| Dungeon.level.map[cell] == Terrain.HERO_LKD_DR
				|| Dungeon.level.map[cell] == Terrain.CRYSTAL_DOOR
				|| Dungeon.level.map[cell] == Terrain.LOCKED_EXIT) {
			
			curAction = new HeroAction.Unlock( cell );
			
		} else if (Transitions.get( Dungeon.level, cell) != null
				//moving to a transition doesn't automatically trigger it when enemies are near
				&& (visibleEnemies.size() == 0 || cell == pos)
				&& !Dungeon.level.locked
				&& !Dungeon.level.plants.containsKey(cell)
				&& (Dungeon.depth < 40 || Transitions.get( Dungeon.level, cell).type == LevelTransition.Type.REGULAR_ENTRANCE) ) {

			curAction = new HeroAction.LvlTransition( cell );
			
		}  else {
			
			curAction = new HeroAction.Move( cell );
			lastAction = null;
			
		}

		return true;
	}
	
	public void earnExp( int exp, Class source ) {

		//SPSEXPD: 奇迹烧瓶抽取本次获得的经验的 50%，并至少为角色保留 1 点
		int absorbed = 0;

		//xp granted by ascension challenge is only for on-exp gain effects
		if (source != AscensionChallenge.class) {
			PotionOfMage flask = belongings.getItem(PotionOfMage.class);
			if (flask != null && exp > 0) {
				absorbed = Math.min(exp - 1, exp / 2);
				flask.addCharge(absorbed);
			}
			int gained = exp - absorbed;
			//SPSXPD: 「快速学习」特质提供额外经验
			this.exp += gained + GhostGirlRose.experienceBonus(this)
					+ pd.actors.hero.perks.QuickLearner.extraExp(this, exp);
		}
		LegacyPet legacyPet = LegacyPet.active();
		if (legacyPet != null && exp > 0) {
			petExperience += exp;
			while (petExperience >= 10 * petLevel + 5) {
				petExperience = 0;
				petLevel++;
				legacyPet.updateStats(false);
				GLog.p(Messages.get(LegacyPet.class, "levelup"));
			}
		}
		//SPSXPD: on-exp 效果按角色实得的经验计算（奇迹烧瓶抽走的部分不算进度）
		float percent = (exp - absorbed)/(float)maxExp();
		if (exp > 0 && heroClass == HeroClass.PERFORMER) {
			Buff.prolong(this, Bless.class, subClass == HeroSubClass.SUPERSTAR ? 5f : 3f);
		}

		EtherealChains.chainsRecharge chains = buff(EtherealChains.chainsRecharge.class);
		if (chains != null) chains.gainExp(percent);

		FlyChains.chainsRecharge2 flyChains = buff(FlyChains.chainsRecharge2.class);
		if (flyChains != null) flyChains.gainExp(percent);

		pd.items.equipment.artifacts.Pylon.BeaconRecharge pylon =
				buff(pd.items.equipment.artifacts.Pylon.BeaconRecharge.class);
		if (pylon != null) pylon.gainExp(percent);

		Berserk berserk = buff(Berserk.class);
		if (berserk != null) berserk.recover(percent);
		
		if (source != PotionOfExperience.class) {
			for (Item i : belongings) {
				i.onHeroGainExp(percent, this);
			}
			if (buff(Talent.RejuvenatingStepsFurrow.class) != null){
				buff(Talent.RejuvenatingStepsFurrow.class).countDown(percent*200f);
				if (buff(Talent.RejuvenatingStepsFurrow.class).count() <= 0){
					buff(Talent.RejuvenatingStepsFurrow.class).detach();
				}
			}
			if (buff(ElementalStrike.ElementalStrikeFurrowCounter.class) != null){
				buff(ElementalStrike.ElementalStrikeFurrowCounter.class).countDown(percent*20f);
				if (buff(ElementalStrike.ElementalStrikeFurrowCounter.class).count() <= 0){
					buff(ElementalStrike.ElementalStrikeFurrowCounter.class).detach();
				}
			}
			if (buff(HallowedGround.HallowedFurrowTracker.class) != null){
				buff(HallowedGround.HallowedFurrowTracker.class).countDown(percent*100f);
				if (buff(HallowedGround.HallowedFurrowTracker.class).count() <= 0){
					buff(HallowedGround.HallowedFurrowTracker.class).detach();
				}
			}
		}
		
		boolean levelUp = false;
		while (this.exp >= maxExp()) {
			this.exp -= maxExp();

			if (buff(Talent.WandPreservationCounter.class) != null
				&& pointsInTalent(Talent.WAND_PRESERVATION) == 2){
				buff(Talent.WandPreservationCounter.class).detach();
			}

			if (lvl < MAX_LEVEL) {
				lvl++;
				levelUp = true;
				
				if (buff(ElixirOfMight.HTBoost.class) != null){
					buff(ElixirOfMight.HTBoost.class).onLevelUp();
				}
				
				if (Dungeon.isChallenged(Challenges.LISTLESS)) {
					int oldHP = HP;
					updateHT(false);
					HP = Math.min(HT, oldHP + 1);
				} else {
					updateHT(true);
				}
				if (buff(HopeMind.class) != null) {
					HTBoost++;
					updateHT(true);
				}
				attackSkill++;
				defenseSkill++;

				if (buff(FourClover.FourCloverBless.class) != null) {
					HTBoost += 5;
					updateHT(true);
					improveMagicSkill(1);
					Dungeon.gold += 1000;
					if (sprite != null) {
						sprite.showStatusWithIcon(CharSprite.POSITIVE, "+1000", FloatingText.GOLD);
					}
				}

			} else {
				Buff.prolong(this, Bless.class, Bless.DURATION);
				this.exp = 0;

				GLog.newLine();
				GLog.p( Messages.get(this, "level_cap"));
				Sample.INSTANCE.play( Assets.Sounds.LEVELUP );
			}
			
		}
		
		if (levelUp) {
			if (heroClass == HeroClass.SOLDIER) {
				HTBoost++;
				updateHT(true);
			}
			if (heroClass == HeroClass.PERFORMER) {
				Buff.affect(this, Barrier.class).incShield(3 + lvl / 5);
			}
			
			if (sprite != null) {
				GLog.newLine();
				GLog.p( Messages.get(this, "new_level") );
				sprite.showStatus( CharSprite.POSITIVE, Messages.get(Hero.class, "level_up") );
				Sample.INSTANCE.play( Assets.Sounds.LEVELUP );
				//SPSXPD: 每 3 级发放一个特质点（取代破碎天赋的 tier 机制）
				if (grantsPerkPoint(lvl)) {
					reservedPerks++;
					GLog.newLine();
					GLog.p( Messages.get(this, "new_perk") );
					//SPSXPD: 不再让左上角头像闪金光（加点提示由左下角快捷按钮承担）
				}
				//「特定等级必然获得」与「满足条件即获得」的特质检查
				pd.actors.hero.perks.PerkGrants.onLevelUp(this, lvl);
			}
			
			Item.updateQuickslot();
			
			Badges.validateLevelReached();
		}
	}
	
	public int maxExp() {
		return maxExp( lvl );
	}
	
	public static int maxExp( int lvl ){
		return 5 + lvl * 5;
	}
	
	public boolean isStarving() {
		return Buff.affect(this, Hunger.class).isStarving();
	}
	
	@Override
	public boolean add( Buff buff ) {

		if (buff.type == Buff.buffType.NEGATIVE &&
				(buff(TimekeepersHourglass.timeStasis.class) != null || buff(TimeStasis.class) != null)) {
			return false;
		}

		boolean added = super.add( buff );

		if (sprite != null && added) {
			String msg = buff.heroMessage();
			if (msg != null){
				GLog.w(msg);
			}

			if (buff instanceof Paralysis || buff instanceof Vertigo) {
				interrupt();
			}

		}
		
		BuffIndicator.refreshHero();

		return added;
	}
	
	@Override
	public boolean remove( Buff buff ) {
		if (super.remove( buff )) {
			BuffIndicator.refreshHero();
			return true;
		}
		return false;
	}
	
	@Override
	protected synchronized void onRemove() {
		//same as super, except we retain charger for rankings purposes
		for (Buff buff : buffs()) {
			if (buff instanceof MeleeWeapon.Charger){
				Actor.remove(buff);
			} else {
				buff.detach();
			}
		}
	}

	@Override
	public void die( Object cause ) {
		
		curAction = null;

		Ankh ankh = null;

		//look for ankhs in player inventory, prioritize ones which are blessed.
		for (Ankh i : belongings.getAllItems(Ankh.class)){
			if (ankh == null || i.isBlessed()) {
				ankh = i;
			}
		}

		if (ankh != null) {
			interrupt();

			if (ankh.isBlessed()) {
				this.HP = HT / 4;

				PotionOfHealing.cure(this);
				Buff.prolong(this, Invulnerability.class, Invulnerability.DURATION);

				SpellSprite.show(this, SpellSprite.ANKH);
				GameScene.flash(0x80FFFF40);
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				GLog.w(Messages.get(this, "revive"));
				Statistics.ankhsUsed++;
				Catalog.countUse(Ankh.class);

				ankh.detach(belongings.backpack);

				for (Char ch : Actor.chars()) {
					if (ch instanceof DriedRose.GhostHero) {
						((DriedRose.GhostHero) ch).sayAnhk();
						return;
					}
				}
			} else {

				//this is hacky, basically we want to declare that a wndResurrect exists before
				//it actually gets created. This is important so that the game knows to not
				//delete the run or submit it to rankings, because a WndResurrect is about to exist
				//this is needed because the actual creation of the window is delayed here
				WndResurrect.instance = new Object();
				Ankh finalAnkh = ankh;
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndResurrect(finalAnkh) );
					}
				});

				if (cause instanceof Hero.Doom) {
					((Hero.Doom)cause).onDeath();
				}

				SacrificialFire.Marked sacMark = buff(SacrificialFire.Marked.class);
				if (sacMark != null){
					sacMark.detach();
				}

			}
			return;
		}
		
		Actor.fixTime();
		super.die( cause );
		reallyDie( cause );
	}
	
	public static void reallyDie( Object cause ) {
		
		int length = Dungeon.level.length();
		int[] map = Dungeon.level.map;
		boolean[] visited = Dungeon.level.visited;
		boolean[] discoverable = Dungeon.level.discoverable;
		
		for (int i=0; i < length; i++) {
			
			int terr = map[i];
			
			if (discoverable[i]) {
				
				visited[i] = true;
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
					CellFlags.discover( Dungeon.level,  i );
				}
			}
		}
		
		Bones.leave();
		
		Dungeon.observe();
		GameScene.updateFog();
				
		Dungeon.hero.belongings.identify();

		int pos = Dungeon.hero.pos;

		ArrayList<Integer> passable = new ArrayList<>();
		for (Integer ofs : PathFinder.NEIGHBOURS8) {
			int cell = pos + ofs;
			if ((Dungeon.level.passable[cell] || Dungeon.level.avoid[cell]) && Dungeon.level.heaps.get( cell ) == null) {
				passable.add( cell );
			}
		}
		Collections.shuffle( passable );

		ArrayList<Item> items = new ArrayList<>(Dungeon.hero.belongings.backpack.items);
		for (Integer cell : passable) {
			if (items.isEmpty()) {
				break;
			}

			Item item = Random.element( items );
			Dungeon.level.drop( item, cell ).sprite.drop( pos );
			items.remove( item );
		}

		for (Char c : Actor.chars()){
			if (c instanceof DriedRose.GhostHero){
				((DriedRose.GhostHero) c).sayHeroKilled();
			}
		}

		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.gameOver();
				Sample.INSTANCE.play( Assets.Sounds.DEATH );
			}
		});

		if (cause instanceof Hero.Doom) {
			((Hero.Doom)cause).onDeath();
		}

		Dungeon.deleteGame( GamesInProgress.curSlot, true );
	}

	//effectively cache this buff to prevent having to call buff(...) a bunch.
	//This is relevant because we call isAlive during drawing, which has both performance
	//and thread coordination implications if that method calls buff(...) frequently
	private Berserk berserk;

	@Override
	public boolean isAlive() {
		
		if (HP <= 0){
			if (berserk == null) berserk = buff(Berserk.class);
			return berserk != null && berserk.berserking();
		} else {
			berserk = null;
			return super.isAlive();
		}
	}

	@Override
	public void move(int step, boolean travelling) {
		boolean wasHighGrass = Dungeon.level.map[step] == Terrain.HIGH_GRASS;

		super.move( step, travelling);
		//SPSXPD: 能量不再靠走路积攒 —— 改为对装备在神器位的魂石喂食（见 Egg.feed）
		
		if (!flying && travelling) {
			if (Dungeon.level.water[pos]) {
				Sample.INSTANCE.play( Assets.Sounds.WATER, 1, Random.Float( 0.8f, 1.25f ) );
			} else if (Dungeon.level.map[pos] == Terrain.EMPTY_SP) {
				Sample.INSTANCE.play( Assets.Sounds.STURDY, 1, Random.Float( 0.96f, 1.05f ) );
			} else if (Dungeon.level.map[pos] == Terrain.GRASS
					|| Dungeon.level.map[pos] == Terrain.EMBERS
					|| Dungeon.level.map[pos] == Terrain.FURROWED_GRASS){
				if (step == pos && wasHighGrass) {
					Sample.INSTANCE.play(Assets.Sounds.TRAMPLE, 1, Random.Float( 0.96f, 1.05f ) );
				} else {
					Sample.INSTANCE.play( Assets.Sounds.GRASS, 1, Random.Float( 0.96f, 1.05f ) );
				}
			} else {
				Sample.INSTANCE.play( Assets.Sounds.STEP, 1, Random.Float( 0.96f, 1.05f ) );
			}
		}

		//SPSEXPD: 佩戴集露徽章时，移动那一刻把落脚点周围 3x3 的露珠收进露珠瓶（不花回合）
		if (step == pos && belongings.badge instanceof DewBadge) {
			DewBadge.collectAround(this);
		}
	}
	
	@Override
	public void onAttackComplete() {

		if (attackTarget == null){
			curAction = null;
			super.onAttackComplete();
			return;
		}
		
		AttackIndicator.target(attackTarget);
		boolean wasEnemy = attackTarget.alignment == Alignment.ENEMY
				|| (attackTarget instanceof Mimic && attackTarget.alignment == Alignment.NEUTRAL);

		boolean hit = attack(attackTarget);
		
		Invisibility.dispel();
		spend( attackDelay() );

		if (hit && subClass == HeroSubClass.GLADIATOR && wasEnemy){
			Buff.affect( this, Combo.class ).hit(attackTarget);
		}

		if (hit && heroClass == HeroClass.DUELIST && wasEnemy){
			Buff.affect( this, Sai.ComboStrikeTracker.class).addHit( attackTarget );
		}

		if (buff(AttackShield.LongBuff.class) != null && belongings.weapon() == null) {
			NewCombo combo = buff(NewCombo.class);
			if (hit) Buff.affect(this, NewCombo.class).hit();
			else if (combo != null) combo.miss();
		}

		if (hit && heroClass == HeroClass.ROGUE && skin == 7) {
			advanceFuuraiWeapon(Random.Int(1, 11));
		}

		if (heroClass == HeroClass.SOLDIER && skin == 7) {
			BunnyCombo combo = buff(BunnyCombo.class);
			if (hit) Buff.affect(this, BunnyCombo.class).hit();
			else if (combo != null) combo.miss();
		}

		curAction = null;
		attackTarget = null;

		super.onAttackComplete();
	}

	public boolean advanceFuuraiWeapon(int amount) {
		if (heroClass != HeroClass.ROGUE || skin != 7 || amount <= 0) return false;
		spp += amount;
		if (spp <= 100 || !(belongings.weapon instanceof Weapon)) return false;
		Weapon oldWeapon = (Weapon)belongings.weapon;
		Weapon replacement = null;
		for (int attempts = 0; attempts < 40; attempts++) {
			Item generated = Generator.random(Generator.Category.MELEEWEAPON);
			if (generated instanceof Weapon && generated.getClass() != oldWeapon.getClass()) {
				replacement = (Weapon)generated;
				break;
			}
		}
		if (replacement == null) return false;
		spp = 0;

		replacement.level(oldWeapon.trueLevel());
		replacement.enchantment = oldWeapon.enchantment;
		replacement.reinforced = oldWeapon.reinforced;
		replacement.levelKnown = oldWeapon.levelKnown;
		replacement.cursedKnown = oldWeapon.cursedKnown;
		replacement.cursed = oldWeapon.cursed;
		int slot = Dungeon.quickslot == null ? -1 : Dungeon.quickslot.getSlot(oldWeapon);
		belongings.weapon = replacement;
		if (slot >= 0) Dungeon.quickslot.setSlot(slot, replacement);
		Item.updateQuickslot();
		GLog.p(Messages.get(this, "fuurai_change"));
		return true;
	}
	
	@Override
	public void onMotionComplete() {
		GameScene.checkKeyHold();
		//SPS: 走完一步后刷新移动路径提示（起点=当前格，已过的点自然消失）
		GameScene.refreshHeroPath();
	}
	
	@Override
	public void onOperateComplete() {
		
		if (curAction instanceof HeroAction.Unlock) {

			int doorCell = ((HeroAction.Unlock)curAction).dst;
			int door = Dungeon.level.map[doorCell];

			SkeletonKey.keyRecharge skele = buff(SkeletonKey.keyRecharge.class);
			SkeletonKey.KeyReplacementTracker keyUseTrack = buff(SkeletonKey.KeyReplacementTracker.class);

			if (skele != null && skele.isCursed() && Random.Int(6) != 0){
				GLog.n(Messages.get(this, "key_distracted"));
				spendAndNext(2*Key.TIME_TO_UNLOCK);
				Buff.affect(this, Hunger.class).affectHunger(-4);
			} else if (Dungeon.level.distance(pos, doorCell) <= 1) {
				boolean hasKey = true;
				if (Dungeon.branch != 0 && !ChallengeJournal.isChallengeBranch(Dungeon.branch)){
					hasKey = false; //keys currently do not work in sub-floors
				} else if (door == Terrain.LOCKED_DOOR) {
					hasKey = Notes.remove(new IronKey(ChallengeJournal.keyDepth(Dungeon.depth, Dungeon.branch)));
					if (hasKey) {
						if (keyUseTrack != null){
							keyUseTrack.processIronLockOpened();
						}
						Level.set(doorCell, Terrain.DOOR);
					}
				} else if (door == Terrain.HERO_LKD_DR) {
					hasKey = true;
					Level.set(doorCell, Terrain.DOOR);
					GLog.i( Messages.get(SkeletonKey.class, "force_lock"));
				} else if (door == Terrain.CRYSTAL_DOOR) {
					hasKey = Notes.remove(new CrystalKey(ChallengeJournal.keyDepth(Dungeon.depth, Dungeon.branch)));
					if (hasKey) {
						if (keyUseTrack != null){
							keyUseTrack.processCrystalLockOpened();
						}
						Level.set(doorCell, Terrain.EMPTY);
						Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
						CellEmitter.get( doorCell ).start( Speck.factory( Speck.DISCOVER ), 0.025f, 20 );
					}
				} else {
					hasKey = Notes.remove(new SpsSkeletonKey(ChallengeJournal.keyDepth(Dungeon.depth, Dungeon.branch)));
					if (!hasKey) hasKey = Notes.remove(new WornKey(ChallengeJournal.keyDepth(Dungeon.depth, Dungeon.branch)));
					if (hasKey) {
						Level.set(doorCell, Terrain.UNLOCKED_EXIT);
					}
				}
				
				if (hasKey) {
					GameScene.updateKeyDisplay();
					GameScene.updateMap(doorCell);
					spend(Key.TIME_TO_UNLOCK);
				}
			}
			
		} else if (curAction instanceof HeroAction.OpenChest) {
			
			Heap heap = Dungeon.level.heaps.get( ((HeroAction.OpenChest)curAction).dst );
			SkeletonKey.keyRecharge skele = buff(SkeletonKey.keyRecharge.class);
			SkeletonKey.KeyReplacementTracker keyUseTrack = buff(SkeletonKey.KeyReplacementTracker.class);

			if (skele != null && skele.isCursed()
					&& (heap.type == Type.LOCKED_CHEST || heap.type == Type.CRYSTAL_CHEST)
					&& Random.Int(6) != 0){
				GLog.n(Messages.get(this, "key_distracted"));
				spend(2*Key.TIME_TO_UNLOCK);
				Buff.affect(this, Hunger.class).affectHunger(-4);
			} else if (Dungeon.level.distance(pos, heap.pos) <= 1){
				boolean hasKey = true;
				if (heap.type == Type.SKELETON || heap.type == Type.REMAINS) {
					Sample.INSTANCE.play( Assets.Sounds.BONES );
				} else if (heap.type == Type.LOCKED_CHEST){
					//keys currently do not work in sub-floors
					hasKey = (Dungeon.branch == 0 || ChallengeJournal.isChallengeBranch(Dungeon.branch))
							&& Notes.remove(new GoldenKey(ChallengeJournal.keyDepth(Dungeon.depth, Dungeon.branch)));
					if (hasKey && keyUseTrack != null){
						keyUseTrack.processGoldLockOpened();
					}
					if (!hasKey) hasKey = Notes.remove(new GoldenSkeletonKey(0));
				} else if (heap.type == Type.CRYSTAL_CHEST){
					//keys currently do not work in sub-floors
					hasKey = (Dungeon.branch == 0 || ChallengeJournal.isChallengeBranch(Dungeon.branch))
							&& Notes.remove(new GoldenKey(ChallengeJournal.keyDepth(Dungeon.depth, Dungeon.branch)));
					if (!hasKey && (Dungeon.branch == 0 || ChallengeJournal.isChallengeBranch(Dungeon.branch))) {
						hasKey = Notes.remove(new CrystalKey(ChallengeJournal.keyDepth(Dungeon.depth, Dungeon.branch)));
					}
					if (hasKey && keyUseTrack != null){
						keyUseTrack.processCrystalLockOpened();
					}
					if (!hasKey) hasKey = Notes.remove(new GoldenSkeletonKey(0));
				}

				if (hasKey) {
					GameScene.updateKeyDisplay();
					heap.open(this);
					spend(Key.TIME_TO_UNLOCK);
				}
			}
			
		}
		curAction = null;

		if (!ready) {
			super.onOperateComplete();
		}
	}

	//SPSEXPD: 搜索时顺手拾取一格的地面物品，返回因此额外消耗的回合数（每件 1 回合）。
	//只处理普通地面堆，宝箱/遗骸等仍需玩家自己打开。
	private float pickUpHeap( int cell ){
		Heap heap = Dungeon.level.heaps.get( cell );
		if (heap == null || heap.type != Heap.Type.HEAP || heap.isEmpty()) return 0f;

		//SPSEXPD: 期间物品自身触发的 spend/spendAndNext 一律不计，最终由 search() 统一加一个回合
		spsPickingUp = true;
		try {
			float time = 0f;
			while (!heap.isEmpty()){
				Item item = heap.pickUp();
				if (!item.doPickUp( this )){
					heap.drop( item );
					GLog.newLine();
					GLog.n( Messages.capitalize(Messages.get(this, "you_cant_have", item.name())) );
					break;
				}
				//露珠的 doPickUp 内部已结算拾取回合，这里不再重复计时
				if (!(item instanceof Dewdrop)) time += TIME_TO_PICK_UP;
				//金币与自动收集类物品不刷屏
				if (!(item instanceof Gold || item instanceof DarkGold || item instanceof Dewdrop
						|| item instanceof Key || item instanceof Guidebook)){
					GLog.i( Messages.capitalize(Messages.get(this, "you_now_have", item.name())) );
				}
			}
			return time;
	} finally {
		spsPickingUp = false;
	}
	}

	//SPSEXPD: 搜索捡拾整体只额外消耗一个回合（不论捡到几件、跨几格）
	private float addedPickUpTime( float current, int cell ){
		return pickUpHeap( cell ) > 0f ? TIME_TO_PICK_UP : current;
	}

	//SPSEXPD: 搜索拾取只作用于「视野内且可抵达」的格子，避免隔墙取物
	private boolean canPickUpAt( int cell ){
		return fieldOfView[cell] && PathFinder.distance[cell] < Integer.MAX_VALUE;
	}

	public boolean search( boolean intentional ) {
		
		if (!isAlive()) return false;
		
		boolean smthFound = false;
		float pickUpTime = 0f;   //SPSXPD: 搜索顺带拾取物品额外消耗的回合

		boolean circular = pointsInTalent(Talent.WIDE_SEARCH) == 1;
		//SPSXPD: 盗贼的搜索距离加成改由「高效搜索」特质体现（裁决），不再硬编码在角色上
		int distance = 1;
		if (hasTalent(Talent.WIDE_SEARCH)) distance++;
		//SPSXPD: 「高效搜索」特质（盗贼/修士的搜索更远）
		if (heroPerk != null && heroPerk.has(pd.actors.hero.perks.EfficientSearch.class)) distance++;
		
		boolean foresight = buff(Foresight.class) != null;
		boolean notice = buff(Notice.class) != null;
		boolean foresightScan = foresight && !Dungeon.level.mapped[pos];

		if (foresightScan){
			Dungeon.level.mapped[pos] = true;
		}

		if (foresight) {
			distance = Foresight.DISTANCE;
			circular = true;
		}

		Point c = Dungeon.level.cellToPoint(pos);

		//SPSEXPD: 先算一次「从英雄出发、限本次搜索距离」的可达范围，供搜索拾取判定（防隔墙取物）
		PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), distance + 1 );

		//SPSEXPD: 脚下的格子也算——搜索时先捡起自己站的那一格
		if (intentional && SPDSettings.searchPickUp() && canPickUpAt( pos )) {
			pickUpTime = addedPickUpTime( pickUpTime, pos );
		}

		TalismanOfForesight.Foresight talisman = buff( TalismanOfForesight.Foresight.class );
		boolean cursed = talisman != null && talisman.isCursed();

		int[] rounding = ShadowCaster.rounding[distance];

		int left, right;
		int curr;
		for (int y = Math.max(0, c.y - distance); y <= Math.min(Dungeon.level.height()-1, c.y + distance); y++) {
			if (!circular){
				left = c.x - distance;
			} else if (rounding[Math.abs(c.y - y)] < Math.abs(c.y - y)) {
				left = c.x - rounding[Math.abs(c.y - y)];
			} else {
				left = distance;
				while (rounding[left] < rounding[Math.abs(c.y - y)]){
					left--;
				}
				left = c.x - left;
			}
			right = Math.min(Dungeon.level.width()-1, c.x + c.x - left);
			left = Math.max(0, left);
			for (curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); curr++){

				if ((foresight || fieldOfView[curr]) && curr != pos) {

					//SPSEXPD: 主动搜索时顺带拾取该格地面物品（设置里可关闭），只限视野内且可达的位置
					if (intentional && SPDSettings.searchPickUp() && canPickUpAt( curr )){
						pickUpTime = addedPickUpTime( pickUpTime, curr );
					}

					if ((foresight && (!Dungeon.level.mapped[curr] || foresightScan))){
						GameScene.checkedCell(curr, foresightScan ? pos : curr);
					} else if (intentional) {
						GameScene.checkedCell(curr, pos);
					}

					if (foresight){
						Dungeon.level.mapped[curr] = true;
					}
					
					if (Dungeon.level.secret[curr]){
						
						Trap trap = Dungeon.level.traps.get( curr );
						float chance;

						//searches aided by foresight always succeed, even if trap isn't searchable
						if (foresight){
							chance = 1f;

						//otherwise if the trap isn't searchable, searching always fails
						} else if (trap != null && !trap.canBeSearched){
							chance = 0f;

						//intentional searches always succeed against regular traps and doors
						} else if (intentional || notice){
							chance = 1f;
						
						//unintentional searches always fail with a cursed talisman
						} else if (cursed) {
							chance = 0f;
							
						//unintentional trap detection scales from 40% at floor 0 to 30% at floor 25
						} else if (Dungeon.level.map[curr] == Terrain.SECRET_TRAP) {
							chance = 0.4f - (Dungeon.legacyDepth() / 250f);
							
						//unintentional door detection scales from 20% at floor 0 to 0% at floor 20
						} else {
							chance = 0.2f - (Dungeon.legacyDepth() / 100f);
						}

						//don't want to let the player search though hidden doors in tutorial
						if (SPDSettings.intro()){
							chance = 0;
						}
						
						if (Random.Float() < chance) {
						
							int oldValue = Dungeon.level.map[curr];
							
							GameScene.discoverTile( curr, oldValue );
							
							CellFlags.discover( Dungeon.level,  curr );
							
							ScrollOfMagicMapping.discover( curr );
							
							if (fieldOfView[curr]) smthFound = true;
	
							if (talisman != null && !talisman.isCursed()) talisman.charge();
						}
					}
				}
			}
		}
		
		if (intentional) {
			sprite.showStatus( CharSprite.DEFAULT, Messages.get(this, "search") );
			sprite.operate( pos );
			if (!Dungeon.level.locked) {
				if (cursed) {
					GLog.n(Messages.get(this, "search_distracted"));
					Buff.affect(this, Hunger.class).affectHunger(TIME_TO_SEARCH + pickUpTime - (2 * HUNGER_FOR_SEARCH));
				} else {
					Buff.affect(this, Hunger.class).affectHunger(TIME_TO_SEARCH + pickUpTime - HUNGER_FOR_SEARCH);
				}
			}
			spendAndNext(TIME_TO_SEARCH + pickUpTime);
			
		}
		
		if (smthFound) {
			GLog.w( Messages.get(this, "noticed_smth") );
			Sample.INSTANCE.play( Assets.Sounds.SECRET );
			interrupt();
		}

		if (foresight){
			GameScene.updateFog(pos, Foresight.DISTANCE+1);
		}

		if (talisman != null){
			talisman.checkAwareness();
		}
		
		return smthFound;
	}
	
	public void resurrect() {
		HP = HT;
		live();

		MagicalHolster holster = belongings.getItem(MagicalHolster.class);

		Buff.affect(this, LostInventory.class);
		Buff.affect(this, Invisibility.class, 3f);
		//lost inventory is dropped in interlevelscene

		//activate items that persist after lost inventory
		//FIXME this is very messy, maybe it would be better to just have one buff that
		// handled all items that recharge over time?
		for (Item i : belongings){
			if (i instanceof EquipableItem && i.isEquipped(this)){
				((EquipableItem) i).activate(this);
			} else if (i instanceof CloakOfShadows && i.keptThroughLostInventory() && hasTalent(Talent.LIGHT_CLOAK)) {
				((CloakOfShadows) i).activate(this);
			} else if (i instanceof HolyTome  && i.keptThroughLostInventory() && hasTalent(Talent.LIGHT_READING)) {
				((HolyTome) i).activate(this);
			} else if (i instanceof Wand && i.keptThroughLostInventory()){
				if (holster != null && holster.contains(i)){
					((Wand) i).charge(this, MagicalHolster.HOLSTER_SCALE_FACTOR);
				} else {
					((Wand) i).charge(this);
				}
			} else if (i instanceof MagesStaff && i.keptThroughLostInventory()){
				((MagesStaff) i).applyWandChargeBuff(this);
			}
		}

		updateHT(false);
	}

	@Override
	public void next() {
		if (isAlive())
			super.next();
	}

	public static interface Doom {
		public void onDeath();
	}
}