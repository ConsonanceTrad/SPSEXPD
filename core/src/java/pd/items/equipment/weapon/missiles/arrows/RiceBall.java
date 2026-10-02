/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.equipment.weapon.missiles.arrows;

import pd.atlas.items.ConsumThrowsDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Drowsy;
import pd.actors.mobs.npcs.NPC;
import pd.effects.Speck;
import pd.messages.InlineText;

public class RiceBall extends Arrows {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RiceBall.class)
			.t("name", "糯米团")
			.t("desc", "用于投食的食物，由于其量大份足，任何食用它的有生命的怪物都会去寻找一个安全的地方来美美睡上一觉。");
	}


	public static final float DURATION = 10f;

	{
		image = ConsumThrowsDict.RICE_BALL;
	}

	public RiceBall() { this(1); }

	public RiceBall(int number) {
		super(1, 1);
		quantity(number);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (canFeed(defender)) {
			Buff.affect(defender, Drowsy.class);
			if (defender.sprite != null) {
				defender.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
			}
			if (defender.HT > 0 && (float)defender.HP / defender.HT > 0.01f) {
				teleportTarget(defender);
			}
		}
		return super.proc(attacker, defender, damage);
	}

	static boolean canFeed(Char defender) {
		return defender != null
				&& !(defender instanceof NPC)
				&& !Char.hasProp(defender, Char.Property.UNDEAD)
				&& !Char.hasProp(defender, Char.Property.BOSS)
				&& !Char.hasProp(defender, Char.Property.DEMONIC)
				&& !Char.hasProp(defender, Char.Property.UNKNOW)
				&& !Char.hasProp(defender, Char.Property.ELEMENT)
				&& !Char.hasProp(defender, Char.Property.MECH);
	}

	static boolean teleportTarget(Char defender) {
		if (Dungeon.level == null || defender == null) return false;
		for (int attempt = 0; attempt < 20; attempt++) {
			int destination = Dungeon.level.randomRespawnCell(defender);
			if (!Dungeon.level.insideMap(destination)
					|| (!Dungeon.level.passable[destination] && !Dungeon.level.avoid[destination])
					|| Actor.findChar(destination) != null) continue;
			defender.pos = destination;
			Dungeon.level.occupyCell(defender);
			if (defender.sprite != null) {
				defender.sprite.place(destination);
				defender.sprite.visible = Dungeon.level.heroFOV[destination];
			}
			return true;
		}
		return false;
	}

	@Override public int value() { return quantity() * 10; }
}
