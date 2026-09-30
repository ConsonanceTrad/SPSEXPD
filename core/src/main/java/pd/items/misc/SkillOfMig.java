package pd.items.misc;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
public class SkillOfMig extends SkillBook {
	{ image = ItemSpriteSheet.SKILL_MIG; }
	@Override void apply(Hero hero) { hero.improveMagicSkill(1); }
	@Override public int value() { return 200 * quantity; }
}
