/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items.consum.potions.exotic;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorrosiveGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Corrosion;
import pd.actors.buffs.Feed;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import render.noosa.audio.Sample;

/**
 * SPSEXPD: 强酸药剂的合剂升级（吞星花对应的合剂）。
 *
 * 饮用 = 原「耗竭-盛宴」效果：获得「生命摄取」（击杀永久 +1 生命上限）50 回合；
 * 投掷/摔碎 = 比强酸药剂更强的强酸云，腐蚀范围内的一切生物。
 */
public class PotionOfAcidFeast extends ExoticPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfAcidFeast.class)
			.t("name", "强酸盛宴合剂")
			.t("desc", "吞星花对应的合剂，是强酸药剂的升华。饮用后你能从敌人的死亡中汲取生命（击杀永久 +1 生命上限，持续 50 回合）；砸碎后会迸出比强酸药剂更强的强酸云。")
			.t("feast", "你感到饥渴的食欲从体内涌起。");
	}

	/** 盛宴效果（生命摄取）的持续回合数。 */
	public static final float FEAST_DURATION = 50f;

	{
		//SPSEXPD: 图标暂用腐蚀气体合剂格（同为腐蚀主题；专属贴图待绘）
		icon = ItemIconSheet.POTION_CORROGAS;
	}

	@Override
	public void apply(Hero hero) {
		identify();
		//SPSEXPD: 原「耗竭-盛宴」效果——击杀敌对单位永久 +1 生命上限
		Buff.affect(hero, Feed.class, FEAST_DURATION);
		pd.utils.GLog.p(pd.messages.Messages.get(this, "feast"));
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.HEART), 0.4f, 4);
	}

	@Override
	public void shatter(int cell) {
		splash(cell);
		if (Dungeon.level.heroFOV[cell]) {
			identify();
			Sample.INSTANCE.play(Assets.Sounds.SHATTER);
			Sample.INSTANCE.play(Assets.Sounds.GAS);
		}

		int strength = 3 + Dungeon.scalingDepth() / 4;
		int centerVolume = 30;
		for (int i : PathFinder.NEIGHBOURS8) {
			if (!Dungeon.level.solid[cell + i]) {
				GameScene.add(Blob.seed(cell + i, 30, CorrosiveGas.class).setStrength(strength));
			} else {
				centerVolume += 30;
			}
		}
		GameScene.add(Blob.seed(cell, centerVolume, CorrosiveGas.class).setStrength(strength));

		Char ch = Actor.findChar(cell);
		if (ch != null) Buff.affect(ch, Corrosion.class).set(10f, strength);
	}

	@Override
	public int value() { return 60 * quantity; }
}
