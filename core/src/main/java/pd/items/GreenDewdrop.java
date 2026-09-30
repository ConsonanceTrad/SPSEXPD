/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.effects.FloatingText;
import pd.journal.Catalog;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.audio.Sample;
import watabou.utils.Random;

/** The original SPS green dew, whose healing and stored value are randomized per pickup. */
public class GreenDewdrop extends Dewdrop {
	{
		image = ItemSpriteSheet.SPS_GREEN_DEWDROP;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		Waterskin waterskin = hero.belongings.getItem(Waterskin.class);
		Catalog.setSeen(getClass());
		Statistics.itemTypesDiscovered.add(getClass());
		if (waterskin != null) {
			waterskin.collectDew(this);
			GameScene.pickUp(this, pos);
		} else {
			int value = Random.IntRange(10, 39);
			if (hero.heroClass == HeroClass.HUNTRESS) value++;
			int healing = Math.min(hero.HT - hero.HP, value * quantity);
			boolean force = Dungeon.level.map[pos] == Terrain.ENTRANCE
					|| Dungeon.level.map[pos] == Terrain.ENTRANCE_SP
					|| Dungeon.level.map[pos] == Terrain.EXIT
					|| Dungeon.level.map[pos] == Terrain.UNLOCKED_EXIT;
			if (healing <= 0 && !force) return false;
			if (healing > 0) {
				hero.HP += healing;
				hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);
			}
			Catalog.countUse(getClass());
		}
		Sample.INSTANCE.play(Assets.Sounds.DEWDROP);
		hero.spendAndNext(pickupDelay());
		return true;
	}

	@Override
	public int dewValue() {
		return quantity * Random.IntRange(10, 29);
	}
}
