/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.ConsumThrowsDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.sprites.MissileSprite;
import pd.ui.QuickSlotButton;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 回旋镖 —— 已从投掷武器改造为**近战武器**，并接入神木圆盾的机制：
 * 充能满时可投掷（AC_CAST）、命中麻痹目标、弹回后站在落点接住立即回满充能。
 * 原有的强化子弹（涂油）机制保留，涂层的命中效果在投掷时生效。
 */
public class Boomerang extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Boomerang.class)
			.t("name", "回旋镖")
			.t("ac_cast", "扔出")
			.t("rest", "回旋镖充能未完成。")
			.t("cast_prompt", "选择瞄准目标")
			.t("charge", "剩余体力%d / %d。")
			.t("returned", "你接住了弹回的回旋镖。")
			.t("return_bounce", "弹回的回旋镖击中了%s。")
			.t("desc", "一具扁平的曲状木制投掷物：既能作为近战武器挥击，也能在充能完成后投掷出去击晕远处的敌人。弹回后站到落点接住它，即可立即再次扔出。此外，这个回旋镖可以使用强化子弹强化。");
	}



	public static final String AC_CAST = "CAST";
	private static final String CHARGE = "charge";

	/** SPSEXPD: 充满才能投掷（与神木圆盾一致）。 */
	public static final int FULL_CHARGE = 15;
	/** SPSEXPD: 抛掷后弹回所需回合数。 */
	public static final int RETURN_TURNS = 3;
	/** SPSEXPD: 弹回位置的搜索半径（以英雄为中心，格内空地）。 */
	public static final int RETURN_RANGE = 2;
	/** SPSEXPD: 命中后的击晕回合数。 */
	public static final float PARALYSIS_TURNS = 1f;

	private int charge;

	{
		image = ConsumThrowsDict.BOOMERANG_0;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1f;
		tier = 1;
		unique = true;
		reinforced = true;
	}

	@Override public int min(int lvl) { return 3 + lvl; }
	@Override public int max(int lvl) { return 6 + 2 * lvl; }
	@Override public int STRReq(int lvl) { return 10; }

	public int charge() { return charge; }
	public void gainCharge() {
		if (charge < FULL_CHARGE) {
			charge++;
			updateQuickslot();
		}
	}

	@Override public String status() { return charge + "/" + FULL_CHARGE; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge >= FULL_CHARGE) actions.add(AC_CAST);
		//回旋镖没有决斗家武技，避免出现点了没有效果的按钮
		actions.remove(AC_ABILITY);
		return actions;
	}

	@Override public String defaultAction() {
		if (charge >= FULL_CHARGE) return AC_CAST;
		return super.defaultAction();
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CAST.equals(action)) {
			curUser = hero;
			if (!isEquipped(hero) || charge < FULL_CHARGE) {
				//SPSEXPD: 没充好只提示，且必须清掉 usesTargeting（否则快捷栏点击会进入选目标→普攻）
				usesTargeting = false;
				GLog.i(Messages.get(this, "rest"));
				return;
			}
			usesTargeting = true;
			GameScene.selectCell(shooter);
			return;
		}
		super.execute(hero, action);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String desc() {
		return super.desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE);
	}

	/** SPSEXPD: 在英雄两格内挑一个可通行的空地作为弹回位置（排除英雄自身与本次投掷的落点）。 */
	private int pickReturnCell(int excluded) {
		Hero hero = Dungeon.hero;
		if (hero == null) return 0;
		if (Dungeon.level == null) return hero.pos;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (cell == hero.pos || cell == excluded) continue;
			if (Dungeon.level.distance(hero.pos, cell) > RETURN_RANGE) continue;
			if (!Dungeon.level.passable[cell]) continue;
			candidates.add(cell);
		}
		return candidates.isEmpty() ? hero.pos : Random.element(candidates);
	}

	/** SPSEXPD: 投掷流程（充能归零 → 命中 → 标记弹回位置）。 */
	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || charge < FULL_CHARGE || curUser == null) return;
			BoomerangAmmo thrown = new BoomerangAmmo();
			int cell = thrown.throwPos(curUser, target);
			Char enemy = Actor.findChar(cell);
			int returnCell = pickReturnCell(cell);
			charge = 0;
			updateQuickslot();
			curUser.sprite.zap(cell);
			curUser.busy();
			thrown.throwSound();
			QuickSlotButton.target(enemy);
			((MissileSprite)curUser.sprite.parent.recycle(MissileSprite.class)).reset(
					curUser.sprite, cell, thrown, () -> {
						if (enemy == null || enemy == curUser) Splash.at(cell, 0xCC99FFFF, 1);
						else if (!curUser.shoot(enemy, thrown)) Splash.at(cell, 0xCC99FFFF, 1);
						launchReturn(thrown, cell, returnCell);
						curUser.spendAndNext(1f);
					});
		}
		@Override public String prompt() { return Messages.get(Boomerang.class, "cast_prompt"); }
	};

	/** SPSEXPD: 标记弹回位置，并安排数回合后的弹回。 */
	private void launchReturn(MissileWeapon thrown, int thrownPos, int returnPos) {
		if (Dungeon.hero == null || Dungeon.level == null) return;
		GameScene.targetedCell(returnPos, RETURN_TURNS);
		Buff.append(Dungeon.hero, ReturnShot.class).setup(this, thrown, thrownPos, returnPos,
				Dungeon.depth, Dungeon.branch);
	}

	/** SPSEXPD: 投掷用的内嵌回旋镖（继承 MissileWeapon，仅作为抛射物存在）。 */
	private class BoomerangAmmo extends MissileWeapon {
		{ image = ConsumThrowsDict.BOOMERANG_0; tier = 1; spawnedForEffect = true; setID = 0; }
		@Override public int defaultQuantity() { return 1; }
		@Override public int damageRoll(Char owner) { return Boomerang.this.damageRoll(owner); }
		@Override public float accuracyFactor(Char owner, Char target) { return 1000f; }
		@Override public int proc(Char attacker, Char defender, int damage) {
			//SPSEXPD: 命中麻痹
			Buff.prolong(defender, Paralysis.class, PARALYSIS_TURNS);
			return super.proc(attacker, defender, damage);
		}
	}

	/** SPSEXPD: 回旋镖的弹回计时（仿神木圆盾 MissileShield.ReturnShot）。 */
	public static class ReturnShot extends Buff {

		{
			revivePersists = true;
		}

		private Boomerang boomerang;
		private MissileWeapon thrown;
		private int thrownPos;
		private int returnPos;
		private int returnDepth;
		private int returnBranch;
		/** SPSEXPD: 弹回的目标全局时间（绝对时间，见 setup 里的说明）。 */
		private float returnAt;

		public void setup( Boomerang boomerang, MissileWeapon thrown, int thrownPos, int returnPos,
				int returnDepth, int returnBranch ) {
			this.boomerang = boomerang;
			this.thrown = thrown;
			this.thrownPos = thrownPos;
			this.returnPos = returnPos;
			this.returnDepth = returnDepth;
			this.returnBranch = returnBranch;
			//SPSEXPD: 用「绝对时间」而不是「剩余次数」计时——Buff.append 后时间戳会落后于英雄，
			//Actor.process 会让它连续 act 追平时间，按次数倒数会在同一回合内耗尽。
			returnAt = Actor.now() + Math.max(1, RETURN_TURNS);
		}

		public int returnPos() { return returnPos; }

		@Override public boolean act() {
			if (returnDepth == Dungeon.depth && returnBranch == Dungeon.branch
					&& Actor.now() >= returnAt) {
				final Hero hero = Dungeon.hero;
				final Char landing = Actor.findChar(returnPos);
				if (hero == null || hero.sprite == null || thrown == null) {
					finishReturn(hero, landing);
					return false;
				}
				MissileSprite visual = (MissileSprite) hero.sprite.parent.recycle(MissileSprite.class);
				visual.reset(thrownPos, returnPos, thrown, () -> finishReturn(hero, landing));
				return false;
			}
			spend( TICK );
			return true;
		}

		private void finishReturn( Hero hero, Char landing ) {
			detach();
			if (hero != null) {
				if (landing != null && landing != hero && thrown != null && hero.shoot(landing, thrown)) {
					GLog.w(Messages.get(Boomerang.class, "return_bounce", landing.name()));
				}
				Boomerang item = boomerang != null ? boomerang : hero.belongings.getItem(Boomerang.class);
				//SPSEXPD: 英雄站在弹回位置接住回旋镖时可以立即再次扔出
				if (item != null && hero.pos == returnPos) {
					item.charge = FULL_CHARGE;
					item.updateQuickslot();
					GLog.p(Messages.get(Boomerang.class, "returned"));
				}
			}
			next();
		}

		private static final String BOOMERANG = "boomerang";
		private static final String THROWN = "thrown";
		private static final String THROWN_POS = "thrown_pos";
		private static final String RETURN_POS = "return_pos";
		private static final String RETURN_DEPTH = "return_depth";
		private static final String RETURN_BRANCH = "return_branch";
		private static final String RETURN_AT = "return_at";

		@Override public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(BOOMERANG, boomerang);
			bundle.put(THROWN, thrown);
			bundle.put(THROWN_POS, thrownPos);
			bundle.put(RETURN_POS, returnPos);
			bundle.put(RETURN_DEPTH, returnDepth);
			bundle.put(RETURN_BRANCH, returnBranch);
			bundle.put(RETURN_AT, returnAt);
		}

		@Override public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			boomerang = (Boomerang) bundle.get(BOOMERANG);
			thrown = (MissileWeapon) bundle.get(THROWN);
			thrownPos = bundle.getInt(THROWN_POS);
			returnPos = bundle.getInt(RETURN_POS);
			returnDepth = bundle.getInt(RETURN_DEPTH);
			returnBranch = bundle.getInt(RETURN_BRANCH);
			returnAt = bundle.getFloat(RETURN_AT);
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE)));
	}
}
