package com.shatteredpixel.shatteredpixeldungeon.items.misc;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class SkillOfAtk extends SkillBook {
	{ image = ItemSpriteSheet.SKILL_ATK; }
	@Override void apply(Hero hero) { hero.improveAttackSkill(1); }
	@Override public int value() { return 50 * quantity; }
}
