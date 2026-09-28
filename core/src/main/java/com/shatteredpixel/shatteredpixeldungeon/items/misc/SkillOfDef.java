package com.shatteredpixel.shatteredpixeldungeon.items.misc;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class SkillOfDef extends SkillBook {
	{ image = ItemSpriteSheet.SKILL_DEF; }
	@Override void apply(Hero hero) { hero.improveDefenseSkill(1); }
	@Override public int value() { return 100 * quantity; }
}
