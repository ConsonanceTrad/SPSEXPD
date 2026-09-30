/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.artifacts.fusion;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Frost;
import pd.actors.buffs.FrostIce;
import pd.actors.buffs.Poison;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.particles.ElmoParticle;
import pd.effects.particles.SnowParticle;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.artifacts.Artifact;
import pd.items.artifacts.EtherealChains;
import pd.items.nornstone.NornStone;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import pd.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** SPS-PD 0.9.8's ore-fed ice artifact. */
public class EyeOfSkadi extends Artifact {

	public static final String AC_BLAST = "BLAST";
	public static final String AC_ADD = "ADD";
	public static final String AC_CURSE = "CURSE";
	public static final int FULL_CHARGE = 100;
	public static final int MAX_LEVEL = 10;

	private int consumedPoints;

	{
		image = ItemSpriteSheet.ARTIFACT_ICE_EYE;
		levelCap = MAX_LEVEL;
		chargeCap = FULL_CHARGE;
		defaultAction = AC_CURSE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge == chargeCap && !cursed) actions.add(AC_CURSE);
		if (isEquipped(hero) && level() < levelCap && !cursed) actions.add(AC_ADD);
		if (isEquipped(hero) && level() > 1 && !cursed) actions.add(AC_BLAST);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_BLAST.equals(action) && !AC_ADD.equals(action) && !AC_CURSE.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (AC_BLAST.equals(action)) {
			curUser = hero;
			if (!canUse(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (level() > 1) {
				blast();
				level(level() - 1);
				exp -= level();
				playBurning();
				emitSnow(hero);
				updateQuickslot();
			}
		} else if (AC_ADD.equals(action)) {
			curUser = hero;
			if (canUse(hero) && level() < levelCap) GameScene.selectItem(itemSelector);
		} else if (AC_CURSE.equals(action)) {
			curUser = hero;
			if (!canUse(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (charge != chargeCap) {
				GLog.i(Messages.get(this, "no_charge"));
			} else {
				GameScene.selectCell(curser);
			}
		}
	}

	private boolean canUse(Hero hero) {
		return hero != null && isEquipped(hero) && !cursed;
	}

	private final CellSelector.Listener curser = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			curse(target, curUser);
		}

		@Override
		public String prompt() {
			return Messages.get(EtherealChains.class, "prompt");
		}
	};

	boolean curse(Integer target, Hero hero) {
		if (target == null || hero == null || Dungeon.level == null
				|| !Dungeon.level.insideMap(target)
				|| (!Dungeon.level.visited[target] && !Dungeon.level.mapped[target])) return false;
		Char victim = Actor.findChar(target);
		if (victim == null) {
			GLog.i(Messages.get(EtherealChains.class, "nothing_to_grab"));
			return false;
		}
		Buff.affect(victim, Poison.class).set(level() * 2f);
		Buff.affect(victim, FrostIce.class).level(level() * 4);
		Buff.affect(victim, ArmorBreak.class, level() * 4f).level(80);
		Buff.affect(victim, Chill.class, level() * 4f);
		charge = 0;
		emitElmo(hero);
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public int blast() {
		if (Dungeon.level == null || Dungeon.level.mobs == null) return 0;
		int hit = 0;
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (mob == null || !mob.isAlive()) continue;
			emitSnow(mob);
			int minimum = Math.max(0, mob.HP / 4);
			int maximum = Math.max(minimum, mob.HP / 2);
			int damage = maximum > minimum ? Random.Int(minimum, maximum) : minimum;
			mob.damage(damage, DamageType.ICE_DAMAGE);
			if (mob.isAlive()) {
				Buff.prolong(mob, Frost.class,
						Frost.DURATION * Random.Float(level(), 1.5f * level()));
			}
			hit++;
		}
		updateQuickslot();
		return hit;
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(EyeOfSkadi.this, "prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof StoneOre || item instanceof NornStone;
		}

		@Override
		public void onSelect(Item item) {
			sacrifice(curUser, item);
		}
	};

	boolean sacrifice(Hero hero, Item item) {
		if (hero == null || level() >= levelCap || cursed
				|| (!(item instanceof StoneOre) && !(item instanceof NornStone))) return false;
		consumedPoints += item instanceof NornStone ? 5 : 1;
		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		}
		hero.busy();
		hero.spend(2f);
		playBurning();
		item.detach(hero.belongings.backpack);
		GLog.h(Messages.get(this, "exp", consumedPoints));
		if (consumedPoints > level() + 1 && level() < levelCap) {
			upgrade();
			GLog.p(Messages.get(this, "infuse_ore"));
		}
		updateQuickslot();
		return true;
	}

	void advanceCharge() {
		if (charge < chargeCap && !cursed) {
			partialCharge += 1 + level();
			if (partialCharge >= 10) {
				charge++;
				partialCharge = 0;
				if (charge >= chargeCap) {
					charge = chargeCap;
					partialCharge = 0;
				}
			}
		} else {
			partialCharge = 0;
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new EyeRecharge();
	}

	public class EyeRecharge extends ArtifactBuff {
		@Override
		public boolean act() {
			if (cursed && Random.Int(100) == 0) {
				Buff.prolong(target, Frost.class, 5f);
			} else {
				advanceCharge();
			}
			updateQuickslot();
			spend(TICK);
			return true;
		}
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero)) {
			desc += "\n\n" + Messages.get(this, charge < chargeCap ? "need_charge" : "full_charge");
		}
		return desc;
	}

	public int charge() { return charge; }
	public int consumedPoints() { return consumedPoints; }

	private static final String CONSUMED = "consumedpts";
	private static final String LEGACY_PARTIAL_CHARGE = "partialCharge";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CONSUMED, consumedPoints);
		bundle.put(LEGACY_PARTIAL_CHARGE, partialCharge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		consumedPoints = Math.max(0, bundle.getInt(CONSUMED));
		if (bundle.contains(LEGACY_PARTIAL_CHARGE)) {
			partialCharge = bundle.getFloat(LEGACY_PARTIAL_CHARGE);
		}
		charge = Math.max(0, Math.min(chargeCap, charge));
		partialCharge = Math.max(0, partialCharge);
	}

	private static void emitSnow(Char ch) {
		if (ch != null && ch.sprite != null) ch.sprite.emitter().start(SnowParticle.FACTORY, 0.2f, 6);
	}

	private static void emitElmo(Hero hero) {
		if (hero != null && hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
	}

	private static void playBurning() {
		if (Sample.INSTANCE != null) Sample.INSTANCE.play(Assets.Sounds.BURNING);
	}
}
