/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.GlassShield;
import pd.actors.buffs.HighLight;
import pd.actors.buffs.Levitation;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.particles.ElmoParticle;
import pd.items.Ankh;
import pd.items.Heap;
import pd.items.Item;
import pd.items.summon.FairyCard;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import watabou.noosa.audio.Sample;
import watabou.utils.Bundle;
import watabou.utils.PathFinder;
import watabou.utils.Random;

import java.util.ArrayList;

public class UndeadBook extends Item {
	public static final String AC_READ = "READ";
	public static final String AC_READ2 = "READ2";
	public static final String AC_BLESS = "BLESS";
	public static final int HOLY_COST = 10;
	public static final int SOUL_COST = 50;
	private static final String CHARGE = "charge";
	private static final String PRAYERS = "prayers";

	private int charge;
	private int prayers;

	{
		image = ItemSpriteSheet.SPS_UNDEAD_BOOK;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge > HOLY_COST) actions.add(AC_READ);
		if (hero != null && hero.permanentHT() > 10) actions.add(AC_READ2);
		if (hero != null && prayers < hero.lvl) actions.add(AC_BLESS);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_READ.equals(action)) {
			if (holyBless(hero)) {
				if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
				Sample.INSTANCE.play(Assets.Sounds.BURNING);
				hero.spendAndNext(1f);
			}
		} else if (AC_READ2.equals(action)) {
			if (soulSacrifice(hero)) GLog.p(Messages.get(this, "bless"));
		} else if (AC_BLESS.equals(action)) {
			if (pray(hero)) GLog.p(Messages.get(this, "1up"));
		} else {
			super.execute(hero, action);
		}
	}

	public boolean holyBless(Hero hero) {
		if (hero == null || charge <= HOLY_COST) return false;
		charge -= HOLY_COST;
		applyHolyBless(hero, Random.Int(4));
		updateQuickslot();
		return true;
	}

	public void applyHolyBless(Hero hero, int outcome) {
		switch (outcome) {
			case 0:
				Buff.affect(hero, Levitation.class, 10f);
				Buff.affect(hero, HighLight.class, 10f);
				break;
			case 1:
				Buff.affect(hero, GlassShield.class).turns(2);
				Buff.affect(hero, EnergyArmor.class).level(hero.lvl * 2);
				break;
			case 2:
				if (Dungeon.level != null) {
					for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
						if (Dungeon.level.heroFOV[mob.pos]) mob.damage(mob.HT / 2, this);
					}
				}
				break;
			case 3:
				summonFairy(hero);
				break;
			default:
				throw new IllegalArgumentException("Unknown holy blessing outcome: " + outcome);
		}
	}

	public FairyCard.Fairy summonFairy(Hero hero) {
		if (hero == null || Dungeon.level == null) return null;
		ArrayList<Integer> spawnPoints = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = hero.pos + offset;
			if (Dungeon.level.insideMap(cell) && Actor.findChar(cell) == null
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) spawnPoints.add(cell);
		}
		if (spawnPoints.isEmpty()) return null;
		FairyCard.Fairy fairy = new FairyCard.Fairy();
		fairy.HP = fairy.HT = hero.lvl * 3;
		fairy.pos = Random.element(spawnPoints);
		GameScene.add(fairy);
		return fairy;
	}

	public boolean soulSacrifice(Hero hero) {
		if (hero == null || hero.permanentHT() <= 10) return false;
		if (charge > SOUL_COST) charge -= SOUL_COST;
		else if (!hero.spendPermanentHT(5)) return false;
		Buff.affect(hero, Dewcharge.class, 100f);
		updateQuickslot();
		return true;
	}

	public boolean pray(Hero hero) {
		if (hero == null || Dungeon.level == null || prayers >= hero.lvl) return false;
		prayers++;
		Heap heap = Dungeon.level.drop(new Ankh(), hero.pos);
		if (heap.sprite != null) heap.sprite.drop(hero.pos);
		updateQuickslot();
		return true;
	}

	public void gainCharge() { charge++; updateQuickslot(); }
	public void gainCharge(int amount) { charge += Math.max(0, amount); updateQuickslot(); }
	public int charge() { return charge; }
	public int prayers() { return prayers; }
	@Override public String status() { return Integer.toString(charge); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge)
			+ "\n\n" + Messages.get(this, "charge2", prayers); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 50 * quantity; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
		bundle.put(PRAYERS, prayers);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, bundle.getInt(CHARGE));
		prayers = Math.max(0, bundle.getInt(PRAYERS));
	}
}
