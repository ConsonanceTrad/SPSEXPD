/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.KindOfWeapon;

/**
 * SPSEXPD: 「点击敌人自动开火」能力。装备型远程武器（枪械 / 灵能弓 / 各类特殊枪）实现本接口后，
 * 在 {@code Hero.handle} 的敌人分支里统一调度：有弹药 → 自动开火；近战范围外且无弹药 → 触发装填/等待；
 * 近战可击打范围内 → 交给正常近战。各类只需保留自己的载弹形式与附加特效。
 */
public interface SpsAutoFire {

	/** 装备中且有弹药时对敌人自动开火；返回 true 表示已接管本次点击（内部自行调度回合）。 */
	boolean autoFire(Hero hero, Char enemy);

	/** 是否具备「装填」动作（决定无弹时能否回落装填）。 */
	default boolean canReloadAction() { return false; }

	/** 近战范围外且无弹药时触发装填/等待；返回 true 表示已接管本次点击。 */
	default boolean reloadOnEmpty(Hero hero) { return false; }

	/**
	 * 调度点：主手优先、其次副手；任一把有弹药则开火；都打不到且敌人在近战范围外时，
	 * 用「有装填能力」的那把触发装填。近战范围由 {@link Hero#canAttack(Char)} 判定
	 * （相邻 + 武器 reach），不是严格相邻。
	 */
	static boolean tryAutoFire(Hero hero, Char enemy) {
		if (hero == null || enemy == null || Dungeon.level == null || !hero.ready) return false;
		boolean canMelee = hero.canAttack(enemy);
		SpsAutoFire reloadable = null;
		KindOfWeapon[] slots = { hero.belongings.weapon(), hero.belongings.secondWep() };
		for (KindOfWeapon w : slots) {
			if (!(w instanceof SpsAutoFire)) continue;
			SpsAutoFire r = (SpsAutoFire) w;
			if (!(r instanceof Item) || !((Item) r).isEquipped(hero)) continue;
			if (r.autoFire(hero, enemy)) return true;
			if (reloadable == null && r.canReloadAction()) reloadable = r;
		}
		if (!canMelee && reloadable != null) return reloadable.reloadOnEmpty(hero);
		return false;
	}
}
