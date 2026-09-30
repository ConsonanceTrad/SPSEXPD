package pd.actors.mobs.pets;

import pd.sprites.GreenDragonSprite;

public class GreenDragon extends PET {
	{ spriteClass = GreenDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.GREEN_DRAGON; }
}
