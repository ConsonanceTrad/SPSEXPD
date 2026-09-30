package pd.actors.mobs.pets;

import pd.sprites.ScorpionSprite;

public class Scorpion extends PET {
	{ spriteClass = ScorpionSprite.class; properties.add(Property.BEAST); updateStats(true); }
	@Override protected Kind kind() { return Kind.SCORPION; }
}
