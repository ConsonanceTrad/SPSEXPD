/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenceUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dewcharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MoonFury;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ErrorArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfError;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.ErrorW;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.ErrorAmmo;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.MemorySaveScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

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
					com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle.FACTORY, 12);
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
