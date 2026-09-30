package pd.actors.mobs.pets;

import pd.sprites.BlueGirlSprite;

public class BlueGirl extends PET {
	{ spriteClass = BlueGirlSprite.class; properties.add(Property.ELF); updateStats(true); }
	@Override protected Kind kind() { return Kind.BLUE_GIRL; }
}
