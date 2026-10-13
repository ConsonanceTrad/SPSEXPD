/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.MissileSprite;
import pd.ui.QuickSlotButton;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentNonEquipDict;

/** SPSXPD: 神木圆盾——只能装备在副武器栏的投掷盾牌。 */
public class MissileShield extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MissileShield.class)
			.t("name", "神木圆盾")
			.t("ac_cast", "扔出")
			.t("rest", "圆盾充能未完成。")
			.t("prompt", "选择瞄准目标")
			.t("desc", "一块普通的木质盾牌，被打磨得十分光滑，可以投掷出去击晕远处的敌人，接住弹回的圆盾即可以立即再次扔出。")
			.t("attack_block", "投掷时造成_%1$d-%2$d点伤害_并击晕目标 _%3$d 回合_。\n\n副手专用，装备时能格挡 _%4$d-%5$d 点伤害_。")
			.t("charge", "剩余体力%d / %d。")
			.t("mark", "圆盾将在3回合后弹回被标记的位置。")
			.t("returned", "你接住了弹回的圆盾。")
			.t("return_bounce", "弹回的圆盾击中了%s。");
	}



	public static final String AC_CAST = "CAST";
	private static final String CHARGE = "charge";
	public static final int FULL_CHARGE = 15;
	/** SPSEXPD: 抛掷后弹回所需回合数。 */
	public static final int RETURN_TURNS = 3;
	/** SPSEXPD: 弹回位置的搜索半径（以英雄为中心，格内空地）。 */
	public static final int RETURN_RANGE = 2;
	/** SPSEXPD: 命中后的击晕回合数（statsInfo 里的回合数也取自这里）。 */
	public static final float PARALYSIS_TURNS = 1f;
	private int charge;

	{
		image = EquipmentNonEquipDict.DIVINE_WOOD_SHIELD;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;
		tier = 1;
		unique = true;
		defaultAction = AC_CAST;
		//SPSEXPD: 不在这里固定 usesTargeting —— 只有充能满、真的要扔时才置 true。
		//固定为 true 会让「没充好」时快捷栏点击仍然进入选目标流程，点怪物就变成走过去普攻。
	}

	/** SPSEXPD: 神木圆盾只能装备在副武器栏，主/副互换会跳过它。 */
	@Override public boolean canEquipPrimary() { return false; }

	public int charge() { return charge; }
	public void gainCharge() {
		if (charge < FULL_CHARGE) {
			charge++;
			updateQuickslot();
		}
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= FULL_CHARGE) actions.add(AC_CAST);
		//圆盾没有决斗家武技，避免出现点了没有效果的按钮
		actions.remove(AC_ABILITY);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override public String defaultAction() { return AC_CAST; }

	@Override public void execute(Hero hero, String action) {
		if (AC_CAST.equals(action)) {
			curUser = hero;
			if (charge < FULL_CHARGE) {
				//SPSEXPD: 没充好只提示，且必须清掉 usesTargeting（否则快捷栏点击会进入选目标→普攻）
				usesTargeting = false;
				GLog.i(Messages.get(this, "rest"));
				return;
			}
			usesTargeting = true;
			GameScene.selectCell(shooter);
			return;
		}
		if (AC_EQUIP.equals(action)) {
			//SPSEXPD: 装备动作直接进副武器栏（不走勇士的主/副选择窗）
			curUser = hero;
			usesTargeting = false;
			equipSecondary(hero);
			return;
		}
		super.execute(hero, action);
	}

	@Override public boolean doEquip(Hero hero) {
		//SPSEXPD: 神木圆盾只能装备在副武器栏
		return equipSecondary(hero);
	}

	private static int heroLevel() {
		return Dungeon.hero == null ? 1 : Math.max(1, Dungeon.hero.lvl);
	}

	/** SPSEXPD: 投掷伤害下限 = 英雄等级 / 6（向下取整）。形参 lvl 是物品等级，圆盾不可升级，故忽略。 */
	@Override public int min(int lvl) { return heroLevel() / 6; }
	/** SPSEXPD: 投掷伤害上限 = 英雄等级 / 4 + 2（向下取整）。 */
	@Override public int max(int lvl) { return heroLevel() / 4 + 2; }
	@Override public int damageRoll(Char owner) { return Random.Int(min(), max()); }

	/** SPSEXPD: 副手装备时提供的额外防护上限（英雄等级/4 + 2），由 Hero.drRoll 掷点使用。 */
	@Override public int defenseFactor(Char owner) { return DRMax(); }

	/** SPSEXPD: 格挡下限 = 英雄等级 / 6（向下取整）；Hero 的格挡掷点在 [drMin, defenseFactor] 间取值。 */
	@Override public int drMin(Char owner) { return heroLevel() / 6; }

	public int DRMax() {
		return heroLevel() / 4 + 2;
	}

	@Override public String statsInfo() {
		return Messages.get(this, "attack_block", min(), max(), (int)PARALYSIS_TURNS, drMin(null), DRMax())
				+ "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE);
	}

	@Override public String status() { return charge + "/" + FULL_CHARGE; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }

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

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || charge < FULL_CHARGE || curUser == null) return;
			MissileShieldAmmo ammo = new MissileShieldAmmo();
			int cell = ammo.throwPos(curUser, target);
			Char enemy = Actor.findChar(cell);
			int returnCell = pickReturnCell(cell);
			charge = 0;
			updateQuickslot();
			curUser.sprite.zap(cell);
			curUser.busy();
			ammo.throwSound();
			QuickSlotButton.target(enemy);
			((MissileSprite)curUser.sprite.parent.recycle(MissileSprite.class)).reset(
					curUser.sprite, cell, ammo, () -> {
						if (enemy == null || enemy == curUser) Splash.at(cell, 0xCC99FFFF, 1);
						else if (!curUser.shoot(enemy, ammo)) Splash.at(cell, 0xCC99FFFF, 1);
						launchReturn(ammo, cell, returnCell);
						curUser.spendAndNext(1f);
					});
		}
		@Override public String prompt() { return Messages.get(MissileShield.class, "prompt"); }
	};

	/** SPSEXPD: 标记弹回位置，并安排 3 回合后的弹回。 */
	private void launchReturn(MissileWeapon ammo, int thrownPos, int returnPos) {
		if (Dungeon.hero == null || Dungeon.level == null) return;
		GameScene.targetedCell(returnPos, RETURN_TURNS);
		Buff.append(Dungeon.hero, ReturnShot.class).setup(this, ammo, thrownPos, returnPos,
				Dungeon.depth, Dungeon.branch);
        // 不需要日志提示
//		if (Dungeon.hero.sprite != null) GLog.i(Messages.get(this, "mark"));
	}

	private class MissileShieldAmmo extends MissileWeapon {
		{ image = EquipmentNonEquipDict.DIVINE_WOOD_SHIELD; tier = 1; spawnedForEffect = true; setID = 0; }
		@Override public int defaultQuantity() { return 1; }
		@Override public int damageRoll(Char owner) { return MissileShield.this.damageRoll(owner); }
		@Override public float accuracyFactor(Char owner, Char target) { return 1000f; }
		@Override public int proc(Char attacker, Char defender, int damage) {
			//SPSEXPD: 原为「对首领/小首领额外造成等量伤害」，按用户要求去掉——只保留击晕
			Buff.prolong(defender, Paralysis.class, PARALYSIS_TURNS);
			return super.proc(attacker, defender, damage);
		}
	}

	/** SPSEXPD: 圆盾的弹回计时（仿 HeavyBoomerang.CircleBack）。 */
	public static class ReturnShot extends Buff {

		{
			revivePersists = true;
		}

		private MissileShield shield;
		private MissileWeapon ammo;
		private int thrownPos;
		private int returnPos;
		private int returnDepth;
		private int returnBranch;
		/** SPSEXPD: 弹回的目标全局时间（绝对时间，见 setup 里的说明）。 */
		private float returnAt;

		public void setup( MissileShield shield, MissileWeapon ammo, int thrownPos, int returnPos,
				int returnDepth, int returnBranch ) {
			this.shield = shield;
			this.ammo = ammo;
			this.thrownPos = thrownPos;
			this.returnPos = returnPos;
			this.returnDepth = returnDepth;
			this.returnBranch = returnBranch;
			//SPSEXPD: 用「绝对时间」而不是「剩余次数」计时。Buff.append 之后，这个新 Buff 的
			//时间戳落后于宿主英雄，Actor.process 会让它连续 act 把时间追平——按次数倒数会在
			//同一回合内就被耗尽（表现为「刚走一步就弹回」），与 RETURN_TURNS 完全不符。
			returnAt = Actor.now() + Math.max(1, RETURN_TURNS);
		}

		public int returnPos() { return returnPos; }

		@Override public boolean act() {
			if (returnDepth == Dungeon.depth && returnBranch == Dungeon.branch
					&& Actor.now() >= returnAt) {
				final Hero hero = Dungeon.hero;
				final Char landing = Actor.findChar(returnPos);
				if (hero == null || hero.sprite == null || ammo == null) {
					finishReturn(hero, landing);
					return false;
				}
				MissileSprite visual = (MissileSprite) hero.sprite.parent.recycle(MissileSprite.class);
				visual.reset(thrownPos, returnPos, ammo, () -> finishReturn(hero, landing));
				return false;
			}
			spend( TICK );
			return true;
		}

		private void finishReturn( Hero hero, Char landing ) {
			detach();
			if (hero != null) {
				if (landing != null && landing != hero && ammo != null && hero.shoot(landing, ammo)) {
					GLog.w(Messages.get(MissileShield.class, "return_bounce", landing.name()));
				}
				MissileShield item = shield != null ? shield : hero.belongings.getItem(MissileShield.class);
				//SPSEXPD: 英雄站在弹回位置接住圆盾时可以立即再次扔出
				if (item != null && hero.pos == returnPos) {
					item.charge = FULL_CHARGE;
					item.updateQuickslot();
					GLog.p(Messages.get(MissileShield.class, "returned"));
				}
			}
			next();
		}

		private static final String SHIELD = "shield";
		private static final String AMMO = "ammo";
		private static final String THROWN_POS = "thrown_pos";
		private static final String RETURN_POS = "return_pos";
		private static final String RETURN_DEPTH = "return_depth";
		private static final String RETURN_BRANCH = "return_branch";
		/** SPSEXPD: 旧存档的「剩余次数」键，只用于读档兼容（新版改用绝对时间）。 */
		private static final String LEGACY_LEFT = "left";
		private static final String RETURN_AT = "return_at";

		@Override public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SHIELD, shield);
			bundle.put(AMMO, ammo);
			bundle.put(THROWN_POS, thrownPos);
			bundle.put(RETURN_POS, returnPos);
			bundle.put(RETURN_DEPTH, returnDepth);
			bundle.put(RETURN_BRANCH, returnBranch);
			bundle.put(RETURN_AT, returnAt);
		}

		@Override public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			shield = (MissileShield) bundle.get(SHIELD);
			ammo = (MissileWeapon) bundle.get(AMMO);
			thrownPos = bundle.getInt(THROWN_POS);
			returnPos = bundle.getInt(RETURN_POS);
			returnDepth = bundle.getInt(RETURN_DEPTH);
			returnBranch = bundle.getInt(RETURN_BRANCH);
			if (bundle.contains(RETURN_AT)) {
				returnAt = bundle.getFloat(RETURN_AT);
			} else {
				//SPSEXPD: 旧档只有剩余次数，按「还差这么多时间」折算成绝对时间
				returnAt = Actor.now() + Math.max(1, bundle.getInt(LEGACY_LEFT));
			}
		}
	}

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE)));
	}
}
