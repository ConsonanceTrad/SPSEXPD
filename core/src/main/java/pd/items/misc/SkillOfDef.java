package pd.items.misc;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
public class SkillOfDef extends SkillBook {
	{ image = ItemSpriteSheet.SKILL_DEF; }
	@Override void apply(Hero hero) { hero.improveDefenseSkill(1); }
	@Override public int value() { return 100 * quantity; }
}
