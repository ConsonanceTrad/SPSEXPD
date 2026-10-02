/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Poison;
import pd.items.consum.food.fruit.Blackberry;
import pd.items.consum.potions.PotionOfHealing;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.DwarfLichSprite;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class DwarfLich extends LegacyDualLootMob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DwarfLich.class)
			.t("name", "不朽矮人")
			.t("desc", "为维系亡灵国王而牺牲的强大巫师，能够调用死亡之力伤害敌人。");
	}



	int tombId = -1;
	{ spriteClass = DwarfLichSprite.class; HP = HT = 120 + legacyDepthAdjustment(0) * Random.NormalIntRange(5, 7); defenseSkill = 24 + legacyDepthAdjustment(1); EXP = 14; maxLvl = 30; setupLegacyDualLoot(PotionOfHealing.class, 0.3f, Blackberry.class, 0.3f); properties.add(Property.UNDEAD); properties.add(Property.DWARF); properties.add(Property.MAGICER); resistances.add(Poison.class); }
	@Override public int damageRoll() { return Random.NormalIntRange(20, 32); }
	@Override public int attackSkill(Char target) { return 36 + legacyDepthAdjustment(1); }
	@Override public int drRoll() { return Random.NormalIntRange(5, 15); }
	@Override protected boolean canAttack(Char enemy) {
		return !Dungeon.level.adjacent(pos, enemy.pos) && new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	@Override protected boolean getCloser(int target) { return state == HUNTING && enemySeen ? getFurther(target) : super.getCloser(target); }
	@Override public int attackProc(Char enemy, int damage) {
		enemy.damage(damageRoll() / 2, new LichDancer.EnergyDamage());
		return super.attackProc(enemy, damage / 2);
	}
	@Override public void die(Object cause) {
		super.die(cause);
		if (!(cause instanceof King.TombCleanup)) RedWraith.spawnAt(pos);
	}
	private static final String TOMB_ID = "tomb_id";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(TOMB_ID, tombId); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); tombId = bundle.getInt(TOMB_ID); }
	static void spawnAround(int center, int tombId) {
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = center + offset;
			if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) continue;
			DwarfLich lich = new DwarfLich(); lich.pos = cell; lich.tombId = tombId; lich.state = lich.HUNTING; GameScene.add(lich, 2f);
		}
	}
}
