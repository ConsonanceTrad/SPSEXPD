/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.sprites.BeastYearSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The original slow, durable year-beast companion. */
public class YearPet extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(YearPet.class)
			.t("name", "年兽宝宝")
			.t("desc", "一只年兽宝宝，没什么战斗力。");
	}


	{
		spriteClass = BeastYearSprite.class;
		properties.add(Property.BEAST);
		properties.add(Property.UNKNOW);
		baseSpeed = 0.5f;
		flying = false;
		updateStats(true);
	}

	@Override protected Kind kind() { return Kind.YEAR; }

	@Override
	public void updateStats(boolean refill) {
		int oldHT = HT;
		HT = 500 + petLevel() * 10;
		defenseSkill = 0;
		if (refill) HP = HT;
		else if (HT > oldHT) HP = Math.min(HT, HP + HT - oldHT);
	}

	@Override public int drRoll() { return Random.IntRange(petLevel() * 2, petLevel() * 5); }
	@Override public int attackSkill(Char target) { return petLevel() + 20; }
	@Override public int damageRoll() { return Random.NormalIntRange(10 + petLevel() * 2, 10 + petLevel() * 3); }
	@Override protected boolean canAttack(Char enemy) { return Dungeon.level.distance(pos, enemy.pos) <= 2; }
}
