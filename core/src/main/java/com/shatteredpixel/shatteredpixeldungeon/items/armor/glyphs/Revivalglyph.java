/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Revivalglyph extends SpsGlyph {
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
