/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Dungeon;
import pd.actors.hero.HeroClass;

/** Original soldier mirror sprite, including the selected SPS hero skin. */
public class BMirrorSprite extends MirrorSprite {
	@Override
	public void updateArmor(int tier) {
		if (Dungeon.hero != null) {
			int skin = Dungeon.hero.skin;
			texture(Dungeon.hero.heroClass.spritesheet(skin));
			//SPSEXPD: 留空主题（skin >= IMPLEMENTED_SKINS）与无皮肤内容的职业都用经典形象
			if (Dungeon.hero.heroClass.supportsSkins() && skin < HeroClass.IMPLEMENTED_SKINS) tier = skin;
		}
		super.updateArmor(tier);
	}
}
