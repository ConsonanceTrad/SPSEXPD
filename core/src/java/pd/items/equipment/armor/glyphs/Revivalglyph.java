/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.glyphs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.*;
import pd.items.equipment.armor.Armor;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Revivalglyph extends SpsGlyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Revivalglyph.class)
			.t("name", "复生%s")
			.t("desc", "复生刻印有几率使植被生长，并驱散使用者的负面效果。");
	}

	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0xCC0000);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		clearElementalMarker(defender);
		int level = level(armor);
		if ((level > 10 && Random.Int(50) < level) || Random.Int(4) == 0) plantGrass(defender.pos);
		if (level > 0 && Random.Int(level) >= 5) cleanse(defender);
		return damage;
	}
	private static void cleanse(Char defender) {
		Class<?>[] negative = {Paralysis.class, Burning.class, Ooze.class, Tar.class, STRDown.class,
				Vertigo.class, Poison.class, Cripple.class, Bleeding.class, Slow.class, AttackDown.class,
				ArmorBreak.class, CountDown.class, GrowSeed.class};
		for (Class<?> type : negative) Buff.detach(defender, (Class<? extends Buff>)type);
	}
	private static boolean plantGrass(int cell) {
		if (Dungeon.level == null || cell < 0 || cell >= Dungeon.level.length()) return false;
		int terrain = Dungeon.level.map[cell];
		if (terrain != Terrain.EMPTY && terrain != Terrain.EMPTY_DECO && terrain != Terrain.EMBERS
				&& terrain != Terrain.GRASS && terrain != Terrain.WATER) return false;
		Level.set(cell, Terrain.HIGH_GRASS);
		GameScene.updateMap(cell);
		return true;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}
