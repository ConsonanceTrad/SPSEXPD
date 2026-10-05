package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Drowsy;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

public class PixieParasol extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PixieParasol.class)
			.t("name", "小精灵伞瓶")
			.t("desc", "存储者数种致幻植物的提取物的混合溶液，泼洒时会使生物振奋或陷入沉睡。");
	}



	{ image = ConsumPotionSeedBasicPotionDict.MONOCHROME_BLOCK; }
	public PixieParasol() { this(1); }
	public PixieParasol(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			Buff.affect(mob, Drowsy.class);
			Buff.prolong(mob, Paralysis.class, Random.IntRange(10, 16));
			Buff.affect(mob, ArmorBreak.class, 50f).level(30);
			if (mob.sprite != null) mob.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
		}
		Buff.affect(hero, Bless.class, 20f);
	}
}
