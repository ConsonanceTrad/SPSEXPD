/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;

/** Original soldier mirror sprite, including the selected SPS hero skin. */
public class BMirrorSprite extends MirrorSprite {
	@Override
	public void updateArmor(int tier) {
		if (Dungeon.hero != null) {
			texture(Dungeon.hero.heroClass.spritesheet(Dungeon.hero.skin));
			if (Dungeon.hero.heroClass.supportsSkins()) tier = Dungeon.hero.skin;
		}
		super.updateArmor(tier);
	}
}
