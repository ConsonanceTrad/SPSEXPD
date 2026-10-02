package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.hero.Hero;
public class SkillOfMig extends SkillBook {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override void apply(Hero hero) { hero.improveMagicSkill(1); }
	@Override public int value() { return 200 * quantity; }
}
