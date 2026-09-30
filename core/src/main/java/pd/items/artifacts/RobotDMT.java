/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.artifacts;

import pd.Assets;
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Dewcharge;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.MoonFury;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.Heap;
import pd.items.armor.normalarmor.ErrorArmor;
import pd.items.wands.WandOfError;
import pd.items.weapon.melee.special.ErrorW;
import pd.items.weapon.missiles.throwing.ErrorAmmo;
import pd.messages.Messages;
import pd.scenes.MemorySaveScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import watabou.noosa.Game;
import watabou.noosa.audio.Sample;
import watabou.utils.Bundle;
import watabou.utils.Random;

import java.io.IOException;
import java.util.ArrayList;

/** SPS-PD's self-charging mechanical determination core. */
public class RobotDMT extends Artifact {

	public static final String AC_HEART = "HEART";
	public static final String AC_MEMORY = "MEMORY";
	public static final String AC_ERROR = "ERROR";
	public static final int FULL_CHARGE = 100;
	public static final int ANALYSIS_COUNT = 10;

	private boolean error;

	{
		image = ItemSpriteSheet.SPS_ROBOT_HEART;
		levelCap = 10;
		chargeCap = FULL_CHARGE;
		defaultAction = AC_HEART;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge == chargeCap && !cursed) actions.add(AC_HEART);
		if (level() > 9 && !isEquipped(hero)) actions.add(AC_MEMORY);
		if (error && !isEquipped(hero)) actions.add(AC_ERROR);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_HEART.equals(action)) {
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (charge != chargeCap) {
				GLog.i(Messages.get(this, "no_charge"));
			} else if (!cursed) {
				charge = 0;
				if (level() < levelCap) level(level() + 1);
				resolveAnalysis(hero, Random.Int(Math.max(1, level())));
				hero.spend(1f);
				updateQuickslot();
			}
		} else if (AC_MEMORY.equals(action) && level() > 9 && !isEquipped(hero)) {
			detach(hero.belongings.backpack);
			try {
				Dungeon.saveAll();
				Game.switchScene(MemorySaveScene.class);
			} catch (IOException exception) {
				ShatteredPixelDungeon.reportException(exception);
			}
		} else if (AC_ERROR.equals(action) && error && !isEquipped(hero)) {
			detach(hero.belongings.backpack);
			hero.spendAndNext(1f);
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			if (hero.sprite != null) hero.sprite.emitter().burst(
					pd.effects.particles.ElmoParticle.FACTORY, 12);
			Heap heap = Dungeon.level.drop(errorReward(Random.Int(4)), hero.pos);
			if (heap.sprite != null) heap.sprite.drop();
		}
	}

	void resolveAnalysis(Hero hero, int result) {
		switch (result) {
			case 0:
				Buff.prolong(hero, Invisibility.class, 50f);
				GLog.w(Messages.get(this, "patience"));
				break;
			case 1:
				Buff.prolong(hero, AttackUp.class, 100f).level(20);
				Buff.prolong(hero, DefenceUp.class, 100f).level(20);
				GLog.w(Messages.get(this, "bravery"));
				break;
			case 2:
				Buff.prolong(hero, MindVision.class, 80f);
				GLog.w(Messages.get(this, "integrity"));
				break;
			case 3:
				Buff.prolong(hero, Bless.class, 50f);
				GLog.w(Messages.get(this, "preseverance"));
				break;
			case 4:
				Buff.affect(hero, BerryRegeneration.class).level(50);
				GLog.w(Messages.get(this, "kindness"));
				break;
			case 5:
				Buff.affect(hero, MoonFury.class);
				GLog.w(Messages.get(this, "justice"));
				break;
			case 6:
				Buff.prolong(hero, Dewcharge.class, 100f);
				GLog.w(Messages.get(this, "soul"));
				break;
			case 7:
				GLog.w(Messages.get(this, "friendship"));
				break;
			case 8:
				error = true;
				GLog.w(Messages.get(this, "chaos"));
				break;
			default:
				GLog.w(Messages.get(this, "determination"));
				break;
		}
	}

	static Item errorReward(int result) {
		switch (result) {
			case 0: return new ErrorW();
			case 1: return new WandOfError();
			case 2: return new ErrorArmor();
			default: return new ErrorAmmo(3);
		}
	}

	void advanceCharge() {
		if (charge >= chargeCap) {
			partialCharge = 0;
			return;
		}
		partialCharge++;
		if (partialCharge >= 5f) {
			charge++;
			partialCharge = 0;
		}
	}

	public int charge() { return charge; }
	public boolean error() { return error; }

	@Override
	protected ArtifactBuff passiveBuff() {
		return new DmtRecharge();
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero) && charge == chargeCap) {
			desc += "\n\n" + Messages.get(this, "full_charge");
		}
		return desc;
	}

	public class DmtRecharge extends ArtifactBuff {
		@Override
		public boolean act() {
			advanceCharge();
			updateQuickslot();
			spend(TICK);
			return true;
		}
	}

	private static final String ERROR = "error";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ERROR, error);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		error = bundle.getBoolean(ERROR);
	}
}
