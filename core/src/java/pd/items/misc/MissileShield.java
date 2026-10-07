/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
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
			.t("rest", "圆盾还没有准备好。")
			.t("prompt", "选择要瞄准的地方")
			.t("desc", "一块普通的木质盾牌，上面有一些刮痕。\n这块盾牌被打磨得十分光滑，可以将其投掷出去击晕远处的敌人，并在3回合后弹回被标记的位置。\n如果弹回的圆盾被你接住，可以立即再次扔出。\n它只能装备在副武器栏，装备时会为你提供基于等级的额外防护。")
			.t("damage", "圆盾可以造成_%d-%d点伤害_，并对首领再次造成等量伤害。")
			.t("charge", "剩余体力%d / %d。")
			.t("blocking", "装备在副手时能格挡0~%d点伤害。")
			.t("mark", "圆盾将在3回合后弹回被标记的位置。")
			.t("returned", "弹回的圆盾被你接住，可以立即再次扔出。")
			.t("return_bounce", "弹回的圆盾再次击中了%s。");
	}



	public static final String AC_CAST = "CAST";
	private static final String CHARGE = "charge";
	public static final int FULL_CHARGE = 15;
	/** SPSEXPD: 抛掷后弹回所需回合数。 */
	public static final int RETURN_TURNS = 3;
	/** SPSEXPD: 弹回位置的搜索半径。 */
	public static final int RETURN_RANGE = 4;
	private int charge;

	{
		image = EquipmentNonEquipDict.DIVINE_WOOD_SHIELD;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;
		tier = 1;
		unique = true;
		defaultAction = AC_CAST;
		usesTargeting = true;
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
			if (charge < FULL_CHARGE) GLog.i(Messages.get(this, "rest"));
			else GameScene.selectCell(shooter);
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

	@Override public int min(int lvl) { return 1 + heroLevel() / 5; }
	@Override public int max(int lvl) { return 1 + heroLevel() / 2; }
	@Override public int damageRoll(Char owner) { return Random.Int(min(), max()); }

	/** SPSEXPD: 副手装备时提供的额外防护上限（0~英雄等级/2），由 Hero.drRoll 掷点使用。 */
	@Override public int defenseFactor(Char owner) { return DRMax(); }

	public int DRMax() {
		return Dungeon.hero == null ? 0 : Dungeon.hero.lvl / 2;
	}

	@Override public String statsInfo() {
		return Messages.get(this, "damage", min(), max())
				+ "\n\n" + Messages.get(this, "blocking", DRMax())
				+ "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE);
	}

	@Override public String status() { return charge + "/" + FULL_CHARGE; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }

	/** SPSEXPD: 在英雄 4 格范围内挑一个安全的空地作为弹回位置。 */
	private int pickReturnCell() {
		Hero hero = Dungeon.hero;
		if (hero == null) return 0;
		if (Dungeon.level == null) return hero.pos;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (cell == hero.pos) continue;
			if (Dungeon.level.distance(hero.pos, cell) > RETURN_RANGE) continue;
			if (!Dungeon.level.passable[cell] || Dungeon.level.solid[cell]) continue;
			if (Actor.findChar(cell) != null) continue;
			if (hazardous(cell)) continue;
			candidates.add(cell);
		}
		return candidates.isEmpty() ? hero.pos : Random.element(candidates);
	}

	/** 有气体/火焰等 blob 的格子不选作弹回位置。 */
	private static boolean hazardous(int cell) {
		if (Dungeon.level == null || Dungeon.level.blobs == null) return false;
		for (Blob blob : Dungeon.level.blobs.values()) {
			if (blob != null && blob.cur != null && cell < blob.cur.length && blob.cur[cell] > 0) {
				return true;
			}
		}
		return false;
	}

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || charge < FULL_CHARGE || curUser == null) return;
			MissileShieldAmmo ammo = new MissileShieldAmmo();
			int cell = ammo.throwPos(curUser, target);
			Char enemy = Actor.findChar(cell);
			int returnCell = pickReturnCell();
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
		GameScene.targetedCell(returnPos, RETURN_TURNS + 1f);
		Buff.append(Dungeon.hero, ReturnShot.class).setup(this, ammo, thrownPos, returnPos,
				Dungeon.depth, Dungeon.branch);
		if (Dungeon.hero.sprite != null) GLog.i(Messages.get(this, "mark"));
	}

	private class MissileShieldAmmo extends MissileWeapon {
		{ image = SpecificPlaceHolderDict.SOMETHING_0; tier = 1; spawnedForEffect = true; setID = 0; }
		@Override public int defaultQuantity() { return 1; }
		@Override public int damageRoll(Char owner) { return MissileShield.this.damageRoll(owner); }
		@Override public float accuracyFactor(Char owner, Char target) { return 1000f; }
		@Override public int proc(Char attacker, Char defender, int damage) {
			if (Char.hasProp(defender, Char.Property.BOSS) || Char.hasProp(defender, Char.Property.MINIBOSS)) {
				defender.damage(damage, this);
			}
			Buff.prolong(defender, Paralysis.class, 2f);
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
		private int left;

		public void setup( MissileShield shield, MissileWeapon ammo, int thrownPos, int returnPos,
				int returnDepth, int returnBranch ) {
			this.shield = shield;
			this.ammo = ammo;
			this.thrownPos = thrownPos;
			this.returnPos = returnPos;
			this.returnDepth = returnDepth;
			this.returnBranch = returnBranch;
			left = RETURN_TURNS;
		}

		public int returnPos() { return returnPos; }

		@Override public boolean act() {
			if (returnDepth == Dungeon.depth && returnBranch == Dungeon.branch) {
				left--;
				if (left <= 0) {
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
		private static final String LEFT = "left";

		@Override public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SHIELD, shield);
			bundle.put(AMMO, ammo);
			bundle.put(THROWN_POS, thrownPos);
			bundle.put(RETURN_POS, returnPos);
			bundle.put(RETURN_DEPTH, returnDepth);
			bundle.put(RETURN_BRANCH, returnBranch);
			bundle.put(LEFT, left);
		}

		@Override public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			shield = (MissileShield) bundle.get(SHIELD);
			ammo = (MissileWeapon) bundle.get(AMMO);
			thrownPos = bundle.getInt(THROWN_POS);
			returnPos = bundle.getInt(RETURN_POS);
			returnDepth = bundle.getInt(RETURN_DEPTH);
			returnBranch = bundle.getInt(RETURN_BRANCH);
			left = bundle.getInt(LEFT);
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
