/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.equipment.bombs.BuildBomb;
import pd.sprites.CocoCatSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class CocoCat extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(CocoCat.class)
			.t("name", "椰子猫")
			.t("desc", "椰子所培养的宠物猫。它和椰子一样携带着大量的炸弹。");
	}



	{
		spriteClass = CocoCatSprite.class;
		cooldown = 50;
		flying = false;
		properties.add(Property.BEAST);
		updateStats(true);
	}
	@Override protected Kind kind() { return Kind.COCO_CAT; }
	@Override public void updateStats(boolean refill) {
		int old = HT;
		HT = 200 + petLevel() * 2;
		defenseSkill = 3 + petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int drRoll() { return Random.IntRange(2 + petLevel(), 5 + petLevel()); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }
	@Override public int damageRoll() { return Random.NormalIntRange(2 + petLevel(), 5 + petLevel() * 3); }
	@Override protected boolean canAttack(Char enemy) {
		return Dungeon.level != null && Dungeon.level.distance(pos, enemy.pos) <= 4;
	}
	@Override public int attackProc(Char enemy, int damage) {
		if (cooldown > 0) cooldown--;
		if (cooldown == 0 && enemy != null && Dungeon.level != null) {
			new BuildBomb().explode(enemy.pos);
			cooldown = Math.max(5, 50 - petLevel());
		}
		return super.attackProc(enemy, damage);
	}
}
