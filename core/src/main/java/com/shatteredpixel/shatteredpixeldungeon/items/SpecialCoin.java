/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Preserves the unfinished SPS S-coin pickup behavior: feedback only, with no persistent wallet. */
public class SpecialCoin extends Item {
	{
		image = ItemSpriteSheet.SPS_SPECIAL_COIN;
		stackable = true;
	}
	public SpecialCoin() { this(1); }
	public SpecialCoin(int value) { quantity = value; }

	@Override public ArrayList<String> actions(Hero hero) { return new ArrayList<>(); }

	@Override public boolean doPickUp(Hero hero, int pos) {
		GameScene.pickUp(this, pos);
		if (hero.sprite != null) hero.sprite.showStatus(CharSprite.NEUTRAL, "+" + quantity);
		hero.spendAndNext(pickupDelay());
		Sample.INSTANCE.play(Assets.Sounds.GOLD, 1, 1, Random.Float(0.9f, 1.1f));
		return true;
	}

	@Override public Item random() { quantity = Random.IntRange(10, 19); return this; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
