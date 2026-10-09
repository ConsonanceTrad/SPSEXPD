/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.GroundFunctionalFallingDict;

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
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The original SPS green dew, whose healing and stored value are randomized per pickup. */
public class GreenDewdrop extends Dewdrop {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GreenDewdrop.class)
			.t("name", "绿色露珠")
			.t("desc", "绿色的露珠。没有露珠瓶时恢复10至39点生命，否则可储存10至29点露水能量。");
	}



	{
		//SPSEXPD: 原先误用普通露珠图标（DEWDROP_0），改为绿色露珠专用图标
		image = GroundFunctionalFallingDict.DEWDROP_4;
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
				if (!pd.actors.hero.perks.BloodShield.convert(hero, healing)) hero.HP += healing;
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
