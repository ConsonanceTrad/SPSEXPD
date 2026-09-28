package com.shatteredpixel.shatteredpixeldungeon.items.misc;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class SkillOfMig extends SkillBook {
	{ image = ItemSpriteSheet.SKILL_MIG; }
	@Override void apply(Hero hero) { hero.improveMagicSkill(1); }
	@Override public int value() { return 200 * quantity; }
}
