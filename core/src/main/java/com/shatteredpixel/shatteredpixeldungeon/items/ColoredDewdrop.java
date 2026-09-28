package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.audio.Sample;

public abstract class ColoredDewdrop extends Dewdrop {

	protected abstract int baseHealing();

	int healingValue(Hero hero) {
		int value = baseHealing() + Math.max(0, Dungeon.legacyDepth() - 1) / 5;
		if (hero.heroClass == HeroClass.HUNTRESS) value++;
		return Math.min(hero.HT - hero.HP, value * quantity);
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
			int healing = healingValue(hero);
			boolean force = Dungeon.level.map[pos] == Terrain.ENTRANCE
					|| Dungeon.level.map[pos] == Terrain.ENTRANCE_SP
					|| Dungeon.level.map[pos] == Terrain.EXIT
					|| Dungeon.level.map[pos] == Terrain.UNLOCKED_EXIT;
			if (healing <= 0 && !force) return false;
			if (healing > 0) {
				hero.HP += healing;
				if (hero.sprite != null) {
					hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);
				}
			}
			Catalog.countUse(getClass());
		}

		Sample.INSTANCE.play(Assets.Sounds.DEWDROP);
		hero.spendAndNext(pickupDelay());
		return true;
	}
}
