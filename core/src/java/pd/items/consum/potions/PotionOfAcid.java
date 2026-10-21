package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorrosiveGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Corrosion;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;

/**
 * SPSEXPD: 由吞星花果酿造的危险药剂。
 * 投掷后崩解为强酸云，落点敌人被酸蚀；误饮则自身也会被强酸灼伤。
 */
public class PotionOfAcid extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfAcid.class)
			.t("name", "强酸药剂")
			.t("desc", "以吞星花果酿成的危险药剂。砸碎后会迸出强酸云，腐蚀范围内的一切生物；若直接饮用，强酸同样会灼伤你自己。");
	}

	{ image = SpecificPlaceHolderDict.POTION_HOLDER_0; }

	@Override public void apply(Hero hero) {
		//SPSEXPD: 危险药剂——误饮会被强酸灼伤（形态折算：粗制 3/4、试剂 1/3）
		Buff.affect(hero, Corrosion.class).set(6f * effectScale(), Math.max(1, hero.HT / 20));
		hero.sprite.emitter().start(Speck.factory(Speck.CORROSION), 0.4f, 4);
	}

	@Override public void shatter(int cell) {
		splash(cell);
		if (Dungeon.level.heroFOV[cell]) {
			identify();
			Sample.INSTANCE.play(Assets.Sounds.SHATTER);
			Sample.INSTANCE.play(Assets.Sounds.GAS);
		}

		//SPSEXPD: 形态折算——腐蚀强度与气体量（作用范围）按比例缩减
		float scale = effectScale();
		int strength = Math.max(1, (int) ((2 + Dungeon.scalingDepth() / 5) * scale));
		int centerVolume = Math.max(1, (int) (25 * scale));
		for (int i : PathFinder.NEIGHBOURS8) {
			if (!Dungeon.level.solid[cell + i]) {
				GameScene.add(Blob.seed(cell + i, centerVolume, CorrosiveGas.class).setStrength(strength));
			} else {
				centerVolume += Math.max(1, (int) (25 * scale));
			}
		}
		GameScene.add(Blob.seed(cell, centerVolume, CorrosiveGas.class).setStrength(strength));

		Char ch = Actor.findChar(cell);
		if (ch != null) Buff.affect(ch, Corrosion.class).set(8f * scale, strength);
	}

	@Override public int value() { return 30 * quantity; }
}
