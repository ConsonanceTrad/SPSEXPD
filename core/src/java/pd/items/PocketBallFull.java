/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HiddenShadow;
import pd.actors.buffs.WatchOut;
import pd.actors.hero.Hero;
import pd.actors.mobs.pets.*;
import pd.items.consum.eggs.*;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/** Legacy soul lantern. It preserves a pet's species, health, and reward cooldown. */
public class PocketBallFull extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PocketBallFull.class)
			.t("name", "容魂灯")
			.t("ac_use", "使用")
			.t("no_place", "这里无法召回宠物。")
			.t("no_pet", "其中没有能够召唤的宠物灵魂。")
			.t("desc", "一只宠物的灵魂被保存在里面。当前没有宠物时使用，可以按保存的生命与奖励冷却将它释放出来。");
	}

	public static final String AC_USE = "USE";
	private static final String PET_TYPE = "pet_type";
	private static final String PET_HP = "pet_hp";
	private static final String PET_COOLDOWN = "pet_cooldown";

	public int pet_type;
	public int pet_hp;
	public int pet_cooldown;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_USE;
	}

	public PocketBallFull() { this(1, 5, 50); }
	public PocketBallFull(int type, int hp) { this(type, hp, 50); }
	public PocketBallFull(int type, int hp, int cooldown) {
		pet_type = type;
		pet_hp = hp;
		pet_cooldown = cooldown;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if ((LegacyPet.active() == null || petHomeDepth()) && canReleaseHere()) actions.add(AC_USE);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (!AC_USE.equals(action)) { super.execute(hero, action); return; }
		release(hero);
	}

	public boolean release(Hero hero) {
		if (hero == null || Dungeon.level == null || (LegacyPet.active() != null && !petHomeDepth())
				|| !canReleaseHere()
				|| hero.belongings == null || !hero.belongings.contains(this)) return false;
		LegacyPet pet = createPet(pet_type);
		if (pet == null) {
			GLog.n(Messages.get(this, "no_pet"));
			return false;
		}
		int spawn = spawnCell(hero.pos);
		if (spawn < 0) {
			GLog.n(Messages.get(this, "no_place"));
			return false;
		}
		pet.restoreRuntimeState(pet_hp, pet_cooldown);
		pet.pos = spawn;
		pet.state = pet.HUNTING;
		GameScene.add(pet);
		Dungeon.level.occupyCell(pet);
		detach(hero.belongings.backpack);
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		hero.spendAndNext(1f);
		return true;
	}

	public static boolean teleportPet(Hero hero) {
		if (hero == null || Dungeon.level == null || petHomeDepth()) {
			GLog.n(Messages.get(PocketBallFull.class, "no_place"));
			return false;
		}
		LegacyPet pet = LegacyPet.active();
		if (pet == null) {
			GLog.n(Messages.get(PocketBallFull.class, "no_pet"));
			return false;
		}
		int destination = spawnCell(hero.pos);
		if (destination < 0) {
			GLog.n(Messages.get(PocketBallFull.class, "no_place"));
			return false;
		}
		int old = pet.pos;
		pet.pos = destination;
		Dungeon.level.occupyCell(pet);
		if (pet.sprite != null) pet.sprite.move(old, destination);
		return true;
	}

	static boolean canReleaseHere() {
		int depth = Dungeon.legacyDepth();
		return depth < 26 || depth == 50;
	}

	public static boolean petHomeDepth() {
		return Dungeon.legacyDepth() == 50;
	}

	/** Captures the active pet without losing health or its reward timer. */
	public static PocketBallFull removePet(Hero hero) {
		LegacyPet pet = LegacyPet.active();
		if (hero == null || pet == null) return null;
		PocketBallFull lantern = new PocketBallFull(pet.legacyType(), pet.HP, pet.rewardCooldown());
		pet.destroy();
		if (pet.sprite != null) pet.sprite.killAndErase();
		if (!lantern.collect(hero.belongings.backpack) && Dungeon.level != null) {
			Heap heap = Dungeon.level.drop(lantern, hero.pos);
			if (heap.sprite != null) heap.sprite.drop();
		}
		return lantern;
	}

	public static void target(Hero hero) {
		LegacyPet pet = LegacyPet.active();
		if (pet == null) return;
		Buff.detach(pet, HiddenShadow.class);
		Buff.prolong(pet, WatchOut.class, 9999f);
	}

	public static void distarget(Hero hero) {
		LegacyPet pet = LegacyPet.active();
		if (pet == null) return;
		Buff.detach(pet, WatchOut.class);
		Buff.prolong(pet, HiddenShadow.class, 999f);
	}

	static int spawnCell(int center) {
		if (Dungeon.level == null) return -1;
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (cell >= 0 && cell < Dungeon.level.length()
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])
					&& Actor.findChar(cell) == null) cells.add(cell);
		}
		return cells.isEmpty() ? -1 : Random.element(cells);
	}

	public static LegacyPet createPet(int type) {
		switch (type) {
			case 101: return new Kodora();
			case 102: return new GentleCrab();
			case 103: return new RibbonRat();
			case 104: return new Snake();
			case 105: return new LitDemon();
			case 106: return new StarKid();
			case 201: return new DogPet();
			case 202: return new Chocobo();
			case 203: return new Fly();
			case 204: return new Spider();
			case 205: return new Stone();
			case 206: return new DwarfBoy();
			case 301: return new Datura();
			case 302: return new Monkey();
			case 303: return new PigPet();
			case 304: return new ButterflyPet();
			case 305: return new FoxHelper();
			case 306: return new FrogPet();
			case 401: return new Bunny();
			case 402: return new CocoCat();
			case 403: return new Haro();
			case 404: return new Velocirooster();
			case 405: return new Abi();
			case 501: return new BlueDragon();
			case 502: return new GreenDragon();
			case 503: return new LightDragon();
			case 504: return new RedDragon();
			case 505: return new ShadowDragon();
			case 506: return new VioletDragon();
			case 507: return new Scorpion();
			case 508: return new LeryFire();
			case 509: return new GoldDragon();
			case 510: return new BugDragon();
			case 601: return new BlueGirl();
			case 666: return new YearPet();
			default: return null;
		}
	}

	public static Egg petEgg(int type) {
		switch (type) {
			case 101: return new KodoraEgg();
			case 102: return new GentleCrabEgg();
			case 103: return new RibbonRatEgg();
			case 104: return new SnakeEgg();
			case 105: return new LitDemonEgg();
			case 106: return new StarKidEgg();
			case 201: return new DogpetEgg();
			case 202: return new ChocoboEgg();
			case 203: return new FlyEgg();
			case 204: return new SpiderpetEgg();
			case 205: return new StoneEgg();
			case 206: return new DwarfBoyEgg();
			case 301: return new DaturaEgg();
			case 302: return new MonkeyEgg();
			case 303: return new PigpetEgg();
			case 304: return new ButterflypetEgg();
			case 305: return new FoxHelperEgg();
			case 306: return new FrogpetEgg();
			case 401: return new EasterEgg();
			case 402: return new CocoCatEgg();
			case 403: return new HaroEgg();
			case 404: return new VelociroosterEgg();
			case 405: return new AflyEgg();
			case 501: return new BlueDragonEgg();
			case 502: return new GreenDragonEgg();
			case 503: return new LightDragonEgg();
			case 504: return new RedDragonEgg();
			case 505: return new ShadowDragonEgg();
			case 506: return new VioletDragonEgg();
			case 507: return new ScorpionEgg();
			case 508: return new LeryFireEgg();
			case 509: return new GoldDragonEgg();
			case 510: return new BugDragonEGG();
			case 601: return new BlueGirlEgg();
			case 666: return new YearPetEgg();
			default: return null;
		}
	}

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PET_TYPE, pet_type);
		bundle.put(PET_HP, pet_hp);
		bundle.put(PET_COOLDOWN, pet_cooldown);
	}

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		pet_type = bundle.contains(PET_TYPE) ? bundle.getInt(PET_TYPE) : 1;
		pet_hp = bundle.contains(PET_HP) ? bundle.getInt(PET_HP) : 5;
		pet_cooldown = bundle.contains(PET_COOLDOWN) ? bundle.getInt(PET_COOLDOWN) : 50;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 1000 * quantity; }
}
