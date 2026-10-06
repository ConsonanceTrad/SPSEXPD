/*
 * 宠物能力特质基类 —— 由「献祭」魂石获得：把该生物的战斗特性永久加到英雄身上。
 * 分发点见 Hero.attackProc（命中）。
 */

package pd.actors.hero.perks.pets;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.hero.perks.Perk;

public abstract class PetAbilityPerk extends Perk {

	public PetAbilityPerk() {
		super(1);
	}

	public PetAbilityPerk(int maxLevel) {
		super(maxLevel);
	}

	/** 命中敌人时触发 */
	public void onHit(Hero hero, Char enemy, int damage) {
	}

	/** 受到攻击时触发 */
	public void onHurt(Hero hero, Char enemy, int damage) {
	}

	/** 英雄每回合触发 */
	public void onTurn(Hero hero) {
	}

	/** 命中分发 */
	public static void dispatchHit(Hero hero, Char enemy, int damage) {
		for (PetAbilityPerk p : owned(hero)) p.onHit(hero, enemy, damage);
	}

	/** 受击分发 */
	public static void dispatchHurt(Hero hero, Char enemy, int damage) {
		for (PetAbilityPerk p : owned(hero)) p.onHurt(hero, enemy, damage);
	}

	/** 回合分发 */
	public static void dispatchTurn(Hero hero) {
		for (PetAbilityPerk p : owned(hero)) p.onTurn(hero);
	}

	private static java.util.ArrayList<PetAbilityPerk> owned(Hero hero) {
		java.util.ArrayList<PetAbilityPerk> list = new java.util.ArrayList<>();
		if (hero == null || hero.heroPerk == null) return list;
		for (Perk p : hero.heroPerk.perks) {
			if (p instanceof PetAbilityPerk) list.add((PetAbilityPerk) p);
		}
		return list;
	}
}