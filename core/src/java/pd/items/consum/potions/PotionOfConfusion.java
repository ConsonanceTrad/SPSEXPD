package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Vertigo;
import pd.actors.buffs.Weakness;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;
import render.utils.math.Random;

/**
 * SPSEXPD: 由种子荚果实酿造。
 * 饮用后随机获得 3 种源自药水的正面或负面效果，效果之间不重复。
 */
public class PotionOfConfusion extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfConfusion.class)
			.t("name", "混乱药剂")
			.t("desc", "以种子荚果实酿成的无序药剂。饮用后会随机获得三种源自药水的效果——是福是祸，全凭运气。");
	}

	private static final int POOL = 12;

	{ image = SpecificPlaceHolderDict.POTION_HOLDER_0; }

	@Override public void apply(Hero hero) {
		boolean[] used = new boolean[POOL];
		int applied = 0;
		int guard = 0;
		while (applied < 3 && guard++ < 100) {
			int pick = Random.Int(POOL);
			if (used[pick]) continue;
			used[pick] = true;
			applyOne(hero, pick);
			applied++;
		}
		hero.sprite.emitter().start(Speck.factory(Speck.CONFUSION), 0.4f, 4);
	}

	private static void applyOne(Hero hero, int index) {
		switch (index) {
			case 0:
				Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.HT / 10));
				break;
			case 1:
				Buff.affect(hero, AttackUp.class, 60f).level(15);
				break;
			case 2:
				Buff.affect(hero, DefenceUp.class, 60f).level(15);
				break;
			case 3:
				Buff.prolong(hero, HasteBuff.class, 40f);
				break;
			case 4:
				Buff.prolong(hero, Bless.class, 40f);
				break;
			case 5:
				Buff.prolong(hero, Recharging.class, 40f);
				break;
			case 6:
				Buff.affect(hero, Poison.class).set(2 + hero.lvl / 4);
				break;
			case 7:
				Buff.affect(hero, Burning.class);
				break;
			case 8:
				Buff.prolong(hero, Cripple.class, 15f);
				break;
			case 9:
				Buff.prolong(hero, Weakness.class, 15f);
				break;
			case 10:
				Buff.prolong(hero, Blindness.class, 15f);
				break;
			default:
				Buff.prolong(hero, Vertigo.class, 15f);
				break;
		}
	}

	@Override public int value() { return 50 * quantity; }
}
