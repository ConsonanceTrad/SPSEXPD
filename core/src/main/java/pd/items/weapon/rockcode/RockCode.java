/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.effects.MagicMissile;
import pd.items.Item;
import pd.items.wands.Wand;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.ui.QuickSlotButton;
import pd.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public abstract class RockCode extends Item {
	public static final String AC_ZAP = "ZAP";
	private static final String CUR_ENERGY = "energy";
	private static final float TIME_TO_ZAP = 1f;

	public String sname;
	public int maxEnergy = initialEnergy();
	public int curEnergy = maxEnergy;
	protected int collisionProperties = Ballistica.MAGIC_BOLT;

	public static void dropForPerformer(RockCode code) {
		if (code == null || Dungeon.hero == null || Dungeon.level == null
				|| Dungeon.hero.heroClass != HeroClass.PERFORMER || Dungeon.hero.skin != 7) return;
		pd.items.Heap heap = Dungeon.level.drop(code, Dungeon.hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
	}

	{
		image = ItemSpriteSheet.POCKET_BALL_EMPTY;
		defaultAction = AC_ZAP;
		usesTargeting = true;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (curEnergy > 0 && !actions.contains(AC_ZAP)) actions.add(AC_ZAP);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (!AC_ZAP.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (curEnergy <= 0) {
			GLog.w(Messages.get(Wand.class, "fizzles"));
			return;
		}
		curUser = hero;
		GameScene.selectCell(shooter);
	}

	@Override public String status() { return curEnergy + "/" + maxEnergy; }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "stats_desc"); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }

	protected int initialEnergy() { return 4; }
	protected int energyPerCast() { return 1; }
	protected abstract int missileType();
	protected abstract void onZap(Ballistica attack);
	public void onMeleeHit(pd.items.weapon.melee.MeleeWeapon weapon,
			Char attacker, Char defender, int damage) { }

	protected void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, missileType(), curUser.sprite,
				bolt.collisionPos, callback);
		Sample.INSTANCE.play(pd.Assets.Sounds.ZAP);
	}

	public boolean zapAt(Hero user, int target) {
		if (user == null || Dungeon.level == null || curEnergy < energyPerCast()
				|| !Dungeon.level.insideMap(target) || target == user.pos) return false;
		Ballistica shot = new Ballistica(user.pos, target, collisionProperties);
		if (shot.collisionPos == user.pos) return false;
		curUser = user;
		resolveZap(shot);
		Invisibility.dispel();
		user.spendAndNext(TIME_TO_ZAP);
		return true;
	}

	private void resolveZap(Ballistica shot) {
		onZap(shot);
		curEnergy = Math.max(0, curEnergy - energyPerCast());
		updateQuickslot();
	}

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || curEnergy < energyPerCast()) return;
			Ballistica shot = new Ballistica(curUser.pos, target, collisionProperties);
			int cell = shot.collisionPos;
			if (target == curUser.pos || cell == curUser.pos) {
				GLog.i(Messages.get(Wand.class, "self_target"));
				return;
			}
			curUser.sprite.zap(cell);
			Char aimed = Actor.findChar(target);
			QuickSlotButton.target(aimed != null ? aimed : Actor.findChar(cell));
			curUser.busy();
			fx(shot, () -> {
				resolveZap(shot);
				curUser.spendAndNext(TIME_TO_ZAP);
			});
			Invisibility.dispel();
		}
		@Override public String prompt() { return Messages.get(Wand.class, "prompt"); }
	};

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CUR_ENERGY, curEnergy);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		curEnergy = Math.max(0, Math.min(maxEnergy, bundle.getInt(CUR_ENERGY)));
	}
}
