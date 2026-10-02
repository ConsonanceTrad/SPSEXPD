/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.sprites.CellmobSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The stationary life-cell produced by legacy evolve ammunition. */
public class NormalCell extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(NormalCell.class)
			.t("name", "普通细胞")
			.t("desc", "由不稳定的退化弹转变而成的奇异活细胞。");
	}



	{
		spriteClass = CellmobSprite.class;
		HP = HT = 1;
		defenseSkill = 0;
		baseSpeed = 0.5f;
		properties.add(Property.BOSS);
		properties.add(Property.MINIBOSS);
	}

	@Override public int damageRoll() {
		int heroHT = Dungeon.hero == null ? 20 : Dungeon.hero.HT;
		return Random.NormalIntRange(heroHT / 20, heroHT / 10);
	}
	@Override public int attackSkill(Char target) { return 99; }
	@Override public int drRoll() { return 0; }
}
