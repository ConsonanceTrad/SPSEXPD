package pd.items.misc;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
public class SkillOfAtk extends SkillBook {
	{ image = ItemSpriteSheet.SKILL_ATK; }
	@Override void apply(Hero hero) { hero.improveAttackSkill(1); }
	@Override public int value() { return 50 * quantity; }
}
