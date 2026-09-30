/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.StenchGas;
import pd.actors.blobs.TarGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.HighLight;
import pd.actors.buffs.Hot;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Rhythm;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Shocked;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Wet;
import pd.actors.hero.Hero;
import pd.effects.Splash;
import pd.effects.particles.ElmoParticle;
import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.sprites.MissileSprite;
import pd.ui.QuickSlotButton;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class PotionOfMage extends Item {
	public static final String AC_USE = "USE";
	public static final String AC_DRINK = "DRINK";
	public static final String AC_SHATTERED = "SHATTERED";
	private static final String CHARGE = "charge";
	public static final int FULL_CHARGE = 100;
	private int charge;

	{
		image = ItemSpriteSheet.MIX_BOTTLE;
		defaultAction = AC_USE;
		unique = true;
		usesTargeting = true;
	}

	public int charge() { return charge; }
	public void gainCharge() {
		if (charge < FULL_CHARGE) {
			charge++;
			updateQuickslot();
		}
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= 70) {
			actions.add(AC_USE);
			actions.add(AC_SHATTERED);
		}
		if (charge >= 50) actions.add(AC_DRINK);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_USE.equals(action) || AC_SHATTERED.equals(action)) {
			curUser = hero;
			if (charge < 70) GLog.i(Messages.get(this, "break"));
			else GameScene.selectCell(AC_USE.equals(action) ? shooter : shattered);
			return;
		}
		if (AC_DRINK.equals(action)) {
			if (charge < 50) {
				GLog.i(Messages.get(this, "break"));
				return;
			}
			drink(hero);
			return;
		}
		super.execute(hero, action);
	}

	private void drink(Hero hero) {
		switch (Random.Int(7)) {
			case 0: Buff.affect(hero, HighLight.class, 10f); break;
			case 1: Buff.affect(hero, AttackUp.class, 10f).level(35); break;
			case 2: Buff.affect(hero, DefenceUp.class, 10f).level(35); break;
			case 3: Buff.affect(hero, Recharging.class, 10f); break;
			case 4: Buff.affect(hero, Arcane.class, 5f); break;
			case 5: Buff.affect(hero, BerryRegeneration.class).level(hero.HP / 2); break;
			case 6: Buff.affect(hero, Rhythm.class, 10f); break;
			default: break;
		}
		if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
		charge -= 50;
		updateQuickslot();
		hero.spendAndNext(1f);
	}

	private final CellSelector.Listener shattered = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || charge < 70 || !Dungeon.level.insideMap(target)) return;
			if (!Dungeon.level.visited[target] && !Dungeon.level.mapped[target]) return;
			GameScene.add(Blob.seed(target, 15, ToxicGas.class));
			GameScene.add(Blob.seed(target, 15, ConfusionGas.class));
			GameScene.add(Blob.seed(target, 15, ParalyticGas.class));
			GameScene.add(Blob.seed(target, 15, DarkGas.class));
			GameScene.add(Blob.seed(target, 15, TarGas.class));
			GameScene.add(Blob.seed(target, 15, StenchGas.class));
			charge -= 70;
			updateQuickslot();
			curUser.spendAndNext(1f);
		}
		@Override public String prompt() { return Messages.get(PotionOfMage.class, "prompt"); }
	};

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || charge < 70) return;
			int cell = new pd.mechanics.Ballistica(
					curUser.pos, target, pd.mechanics.Ballistica.PROJECTILE).collisionPos;
			Char enemy = Actor.findChar(cell);
			charge -= 70;
			updateQuickslot();
			curUser.sprite.zap(cell);
			curUser.busy();
			QuickSlotButton.target(enemy);
			Item projectile = new MageProjectile();
			((MissileSprite)curUser.sprite.parent.recycle(MissileSprite.class)).reset(
					curUser.sprite, cell, projectile, () -> {
						if (enemy == null || enemy == curUser) Splash.at(cell, 0xCC99FFFF, 1);
						else applyDebuffs(enemy);
						curUser.spendAndNext(1f);
					});
		}
		@Override public String prompt() { return Messages.get(PotionOfMage.class, "prompt"); }
	};

	private static void applyDebuffs(Char target) {
		Buff.affect(target, AttackDown.class, 10f).level(35);
		Buff.affect(target, ArmorBreak.class, 10f).level(35);
		Buff.affect(target, Slow.class, 10f);
		Buff.affect(target, Hot.class, 10f);
		Buff.affect(target, Wet.class, 10f);
		Buff.affect(target, Shocked.class).level(10);
		Buff.affect(target, Roots.class, 10f);
	}

	private static class MageProjectile extends Item {
		{ image = ItemSpriteSheet.SLIME_BALL; }
	}

	@Override public String status() { return Integer.toString(charge / 70); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE)));
	}
}
