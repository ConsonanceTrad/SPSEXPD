/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.actors.hero.Hero;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.*;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.potions.PotionOfTransmute;
import pd.messages.Messages;
import render.utils.math.Random;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.items.Heap;
import pd.items.consum.food.fruit.Durian;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class NutFruit extends SpsFruit {
	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_NUTVINE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NutFruit.class)
			.t("name", "坚果果实")
			.t("desc", "人工种植的坚果藤结出的果实。落地会留下一颗坚果，命中时则按主武器两成伤害重击目标。");
	}



	public NutFruit() { this(1); }
	public NutFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_NUTVINE, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			Heap heap = Dungeon.level.drop(new Nut(), cell);
			if (heap.sprite != null) heap.sprite.drop(cell);
		} else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		int extra = 0;
		if (attacker instanceof Hero) {
			Hero hero = (Hero) attacker;
			//SPSEXPD: 投掷时 attackingWeapon 指的是果实自己（果实伤害为 0），
			//所以这里取真正装备的主武器来算两成伤害
			KindOfWeapon wep = hero.belongings.weapon();
			if (wep != null) {
				int roll = wep.damageRoll(attacker);
				if (roll > 0) extra = Math.round(roll * 0.2f);
			}
		}
		if (extra > 0) defender.damage(extra, this);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Hunger.class).satisfy(50f);
	}
}
