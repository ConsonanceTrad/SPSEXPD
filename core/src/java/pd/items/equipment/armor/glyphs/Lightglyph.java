/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Terror;
import pd.actors.buffs.armorbuff.GlyphLight;
import pd.items.equipment.armor.Armor;
import pd.sprites.ItemSprite;
import render.utils.math.GameMath;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Lightglyph extends SpsGlyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Lightglyph.class)
			.t("name", "神圣%s")
			.t("desc", "神圣刻印可以增加使用者的光照抗性，并有几率魅惑、恐吓或使攻击者狂乱。");
	}



	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0xFFFF44);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		setElementalMarker(defender, GlyphLight.class);
		if (attacker == null) return damage;
		int level = (int)GameMath.gate(0, armor.level(), 6);
		int bound = level / 2 + 5;
		if (Random.Int(bound) >= 4) {
			Buff.affect(attacker, Charm.class, Random.IntRange(4, 7)).object = defender.id();
			Buff.affect(attacker, Amok.class, 10f);
		} else if (Random.Int(bound) >= 3 || lucky(defender) && Random.Int(bound) >= 1) {
			Buff.affect(attacker, Terror.class, 10f).object = defender.id();
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}
