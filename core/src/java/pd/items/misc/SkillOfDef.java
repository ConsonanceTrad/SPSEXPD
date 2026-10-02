package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.hero.Hero;
public class SkillOfDef extends SkillBook {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override void apply(Hero hero) { hero.improveDefenseSkill(1); }
	@Override public int value() { return 100 * quantity; }
}
