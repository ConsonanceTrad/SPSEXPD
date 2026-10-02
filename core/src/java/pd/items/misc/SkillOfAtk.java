package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.hero.Hero;
public class SkillOfAtk extends SkillBook {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override void apply(Hero hero) { hero.improveAttackSkill(1); }
	@Override public int value() { return 50 * quantity; }
}
