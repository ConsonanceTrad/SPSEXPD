/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.artifacts;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Levitation;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.bombs.BuildBomb;
import pd.items.bombs.DarkBomb;
import pd.items.bombs.DungeonBomb;
import pd.items.bombs.EarthBomb;
import pd.items.bombs.FishingBomb;
import pd.items.bombs.HugeBomb;
import pd.items.bombs.IceBomb;
import pd.items.bombs.LightBomb;
import pd.items.bombs.SpsFireBomb;
import pd.items.bombs.StormBomb;
import pd.items.weapon.missiles.fusion.RocketMissile;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndIronMaker;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

public class AlienBag extends Artifact {

	public static final String AC_SHIELD = "SHIELD", AC_BOMB = "BOMB", AC_BUILD = "BUILD";
	private static final Class<?>[] BOMB_SUPPLY_CLASSES = {BuildBomb.class, DungeonBomb.class, HugeBomb.class,
			RocketMissile.class, SpsFireBomb.class, IceBomb.class, EarthBomb.class, StormBomb.class,
			LightBomb.class, DarkBomb.class, FishingBomb.class};
	private static final float[] BOMB_SUPPLY_WEIGHTS = {0, 3, 0, 1, 1, 1, 1, 1, 1, 1, 1};

	{
		image = EquipmentJewelleryArtifactDict.LEGACY_ALIEN_BAG_0;
		levelCap = 10;
		chargeCap = 100;
		charge = 0;
		defaultAction = AC_BUILD;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge == chargeCap && !cursed) actions.add(AC_SHIELD);
		if (level() > 2) actions.add(AC_BOMB);
		actions.add(AC_BUILD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SHIELD.equals(action)) {
			if (!shield(hero)) GLog.i(Messages.get(this, "no_charge"));
		} else if (AC_BOMB.equals(action)) {
			if (!bombs(hero)) GLog.i(Messages.get(this, "no_charge"));
		} else if (AC_BUILD.equals(action)) {
			if (isEquipped(hero)) GameScene.show(new WndIronMaker());
			else GLog.i(Messages.get(Artifact.class, "need_to_equip"));
		} else {
			super.execute(hero, action);
		}
	}

	public boolean shield(Hero hero) {
		if (hero == null || !isEquipped(hero) || charge != chargeCap || cursed) return false;
		charge = 0;
		Buff.affect(hero, EnergyArmor.class).level(level() * 10);
		Buff.affect(hero, DefenceUp.class, 20f).level(level() * 3);
		Buff.affect(hero, Invisibility.class, level() * 3f);
		Buff.affect(hero, Levitation.class, level() * 3f);
		Buff.affect(hero, HasteBuff.class, level() * 3f);
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public boolean bombs(Hero hero) {
		if (hero == null || Dungeon.level == null || level() <= 2) return false;
		for (int i = 0; i < level() / 4; i++) {
			dropAtHero(randomBombSupply(), hero);
			dropAtHero(Generator.random(Generator.Category.HIGHFOOD), hero);
		}
		level(level() - 2);
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	private static void dropAtHero(Item item, Hero hero) {
		if (item == null) return;
		Heap heap = Dungeon.level.drop(item, hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
	}

	public static Item randomBombSupply() {
		int index = Random.chances(BOMB_SUPPLY_WEIGHTS);
		return ((Item)Reflection.newInstance(BOMB_SUPPLY_CLASSES[index])).random();
	}

	public static Class<?>[] bombSupplyClasses() { return BOMB_SUPPLY_CLASSES.clone(); }
	public static float[] bombSupplyWeights() { return BOMB_SUPPLY_WEIGHTS.clone(); }

	public void gainExp() {
		if (cursed || Dungeon.hero == null || !isEquipped(Dungeon.hero)) return;
		exp++;
		if (exp > 10 + level() * 5 && level() < levelCap) {
			exp -= 10 + level() * 5;
			upgrade();
			GLog.p(Messages.get(BagRecharge.class, "levelup"));
		}
	}

	@Override protected ArtifactBuff passiveBuff() { return new BagRecharge(); }
	public int charge() { return charge; }

	private static final String CURRENT_PARTIAL_CHARGE = "partialcharge";
	private static final String LEGACY_PARTIAL_CHARGE = "partialCharge";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEGACY_PARTIAL_CHARGE, partialCharge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (!bundle.contains(CURRENT_PARTIAL_CHARGE) && bundle.contains(LEGACY_PARTIAL_CHARGE)) {
			partialCharge = bundle.getFloat(LEGACY_PARTIAL_CHARGE);
		}
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero) && charge == chargeCap) {
			desc += "\n\n" + Messages.get(this, "full_charge");
		}
		return desc;
	}

	public class BagRecharge extends ArtifactBuff {
		@Override public boolean act() {
			if (charge < chargeCap) {
				partialCharge += 1f + level();
				if (partialCharge >= 10) {
					charge++;
					partialCharge = 0;
				}
			} else {
				partialCharge = 0;
			}
			updateQuickslot();
			spend(TICK);
			return true;
		}

		public void gainExp() {
			AlienBag.this.gainExp();
		}
	}
}
