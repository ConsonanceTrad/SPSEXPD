/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GasesImmunity;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Testglyph extends SpsGlyph {
	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0x22CC44);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		clearElementalMarker(defender);
		if (attacker == null || !roll(level(armor) + 5, 4, defender, 2)) return damage;
		Buff.prolong(defender, GasesImmunity.class, GasesImmunity.DURATION);
		Class<? extends Blob>[] gases = new Class[]{ToxicGas.class, ConfusionGas.class, ParalyticGas.class,
				DarkGas.class, TarGas.class, StenchGas.class};
		GameScene.add(Blob.seed(attacker.pos, 25, gases[Random.Int(gases.length)]));
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}
