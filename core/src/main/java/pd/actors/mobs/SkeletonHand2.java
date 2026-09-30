/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Terror;
import pd.items.potions.PotionOfLiquidFlame;
import pd.sprites.SkeletonHand2Sprite;
import watabou.utils.Random;

public class SkeletonHand2 extends Mob {
	{
		spriteClass = SkeletonHand2Sprite.class; HP = HT = 1000; defenseSkill = 30;
		EXP = 10; maxLvl = 20; flying = true; loot = PotionOfLiquidFlame.class; lootChance = 0.1f;
		properties.add(Property.UNDEAD); properties.add(Property.BOSS); properties.add(Property.BOSS_MINION); immunities.add(Burning.class);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(10, 30); }
	@Override public int attackSkill(Char target) { return 25; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 15); }
	@Override protected boolean canAttack(Char enemy) { return Dungeon.level.distance(pos, enemy.pos) <= 2; }
	@Override protected boolean act() { boolean result = super.act(); if (state == FLEEING && buff(Terror.class) == null && enemy != null && enemySeen && enemy.buff(Roots.class) == null) state = HUNTING; return result; }
	@Override public int attackProc(Char enemy, int damage) { if (enemy == Dungeon.hero && Random.Int(2) == 0) { Buff.prolong(enemy, Roots.class, 20f); state = FLEEING; } return super.attackProc(enemy, damage); }
}
