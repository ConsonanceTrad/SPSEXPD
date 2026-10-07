/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.actors.blobs.Blob;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.mechanics.pathfind.PathFinder;
import java.util.ArrayList;

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
import pd.actors.blobs.ParalyticGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.messages.InlineText;

public class CharmFruit extends SpsFruit {
	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_MAGEROYAL;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CharmFruit.class)
			.t("name", "梦叶果实")
			.t("desc", "人工种植的梦叶花结出的果实。落地会清空周围的有害气体，命中则魅惑目标。");
	}



	public CharmFruit() { this(1); }
	public CharmFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_MAGEROYAL, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		//SPSEXPD: 落地时清空作用范围内的有害气体（与净化药剂同理）——小型 3×3、大型 5×5 圆形
		if (landsAt(cell)) clearGases(cell);
		else super.onThrow(cell);
	}

	/** SPSEXPD: 清除以 center 为中心的作用范围内的有害气体。 */
	private void clearGases(int center) {
		if (Dungeon.level == null) return;
		ArrayList<Blob> blobs = new ArrayList<>();
		for (Class c : new BlobImmunity().immunities()) {
			Blob b = Dungeon.level.blobs.get(c);
			if (b != null && b.volume > 0) blobs.add(b);
		}
		for (int cell : areaCells(center)) clearCell(cell, blobs);
	}

	private static void clearCell(int cell, ArrayList<Blob> blobs) {
		for (Blob blob : blobs) blob.clear(cell);
		if (Dungeon.level.heroFOV[cell]) CellEmitter.get(cell).burst(Speck.factory(Speck.DISCOVER), 2);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (defender != attacker) {
			Buff.prolong(defender, Charm.class, scaled(3f)).object = attacker.id();
		}
		return super.proc(attacker, defender, 0);
	}

	/** SPSEXPD: 食用会麻痹自己——默认动作为「投掷」。 */
	@Override protected boolean harmfulOnEat() { return true; }

	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Paralysis.class, scaled(1f));
	}
}
