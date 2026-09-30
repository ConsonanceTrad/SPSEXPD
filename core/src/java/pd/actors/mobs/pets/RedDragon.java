package pd.actors.mobs.pets;

import pd.sprites.RedDragonSprite;

public class RedDragon extends PET {
	{ spriteClass = RedDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.RED_DRAGON; }
}
