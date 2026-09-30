/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;
import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.keys.GoldenSkeletonKey;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.SpsHermitCrabSprite;
import pd.utils.GLog;
import render.utils.math.Random;
public class SpsHermitCrab extends Mob {
	private static final float TIME_TO_ZAP = 2f;
	{ spriteClass = SpsHermitCrabSprite.class; HP = HT = 200; defenseSkill = 22; EXP = 60; loot = Generator.Category.BERRY; lootChance = 0.33f; properties.add(Property.BEAST); properties.add(Property.BOSS); properties.add(Property.BOSS_MINION); resistances.add(pd.actors.blobs.Electricity.class); }
	@Override public int damageRoll() { return Random.NormalIntRange(25, 50); }
	@Override public int attackSkill(Char target) { return 25; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 4); }
	@Override public void damage(int damage, Object source) { if (damage > HT/4 && source != this) for (Mob mob : Dungeon.level.mobs) if (mob instanceof Shell && mob.isAlive()) { ((Shell)mob).addCharge(damage - 1); damage = 1; GLog.n(Messages.get(this, "absorb")); break; } super.damage(damage, source); }
	@Override protected boolean canAttack(Char enemy) { return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos; }
	@Override protected boolean doAttack(Char enemy) { if (Dungeon.level.adjacent(pos, enemy.pos)) return super.doAttack(enemy); if (sprite != null && (sprite.visible || enemy.sprite.visible)) { sprite.zap(enemy.pos); return false; } zap(); return true; }
	private void zap() { spend(TIME_TO_ZAP); if (enemy != null && enemy.isAlive() && hit(this, enemy, true)) { int damage = Random.Int(15, 30); if (Dungeon.level.water[enemy.pos] && !enemy.flying) damage = Math.round(damage*1.5f); enemy.damage(damage, this); } }
	public void onZapComplete() { zap(); next(); }
	@Override public void die(Object cause) { int cell = pos; super.die(cause); Heap heap = Dungeon.level.drop(new GoldenSkeletonKey(0), cell); if (heap.sprite != null) heap.sprite.drop(); }
}
