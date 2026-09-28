/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Sokoban block adapted from Special Surprise Pixel Dungeon.
 * Distributed under the GNU General Public License v3 or later.
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.levels.AdventureLevel;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GolemSprite;
import com.watabou.utils.Bundle;

public class SokobanBlock extends NPC {

	private int homePos = -1;

	{
		spriteClass = GolemSprite.class;
		HP = HT = 1;
		properties.add(Property.INORGANIC);
	}

	@Override
	protected boolean act() {
		spend(TICK);
		return true;
	}

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	@Override
	public int defenseSkill(Char enemy) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage(int dmg, Object src) {
	}

	@Override
	public boolean add(Buff buff) {
		return false;
	}

	@Override
	public boolean interact(Char c) {
		if (!(c instanceof Hero) || !Dungeon.level.adjacent(pos, c.pos)) return true;
		int direction = pos - c.pos;
		if (direction != 1 && direction != -1
				&& direction != Dungeon.level.width() && direction != -Dungeon.level.width()) {
			return true;
		}

		int destination = pos + direction;
		if (destination < 0 || destination >= Dungeon.level.length()
				|| !Dungeon.level.passable[destination]
				|| Dungeon.level.pit[destination]
				|| Actor.findChar(destination) != null) {
			return true;
		}

		int blockFrom = pos;
		int heroFrom = c.pos;
		move(destination);
		c.move(blockFrom);
		if (sprite != null) sprite.move(blockFrom, destination);
		if (c.sprite != null) c.sprite.move(heroFrom, blockFrom);
		if (Dungeon.level instanceof AdventureLevel) {
			((AdventureLevel)Dungeon.level).afterBlockPushed(this);
		}
		((Hero)c).spendAndNext(1f / c.speed());
		return true;
	}

	public void setHomePos(int homePos) {
		this.homePos = homePos;
	}

	public int homePos() {
		return homePos;
	}

	private static final String HOME_POS = "home_pos";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HOME_POS, homePos);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		homePos = bundle.contains(HOME_POS) ? bundle.getInt(HOME_POS) : -1;
	}
}
