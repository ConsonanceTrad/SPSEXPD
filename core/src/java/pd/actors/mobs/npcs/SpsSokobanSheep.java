/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.specific.sellitem.SheepFur;
import pd.levels.SpsSokobanLevel;
import pd.messages.Messages;
import pd.sprites.SheepSprite;
import pd.sprites.SpsSokobanSheepSprite;
import pd.messages.InlineText;

/** Pushable sheep used by the original SPS Sokoban maps. */
public class SpsSokobanSheep extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpsSokobanSheep.class)
			.t("name", "推箱绵羊")
			.t("desc", "这只特殊的绵羊可以被推过地板，用于解开机关。")
			.t("corner.name", "斜推绵羊")
			.t("corner.desc", "这只绵羊也可以沿斜线推动。")
			.t("switch.name", "换位绵羊")
			.t("switch.desc", "与这只绵羊互动会交换你们的位置。")
			.t("black.name", "黑色绵羊")
			.t("black.desc", "这只绵羊会跳向附近的剪毛陷阱。")
			.t("stop.name", "停驻绵羊")
			.t("stop.desc", "这只绵羊已经无法推动。");
	}


	@Override public Item SupercreateLoot() { return new SheepFur(); }

	{
		spriteClass = SpsSokobanSheepSprite.class;
		HP = HT = 1;
		properties.add(Property.UNKNOW);
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

	protected boolean validDirection(int direction) {
		return direction == 1 || direction == -1
				|| direction == Dungeon.level.width() || direction == -Dungeon.level.width();
	}

	@Override
	public boolean interact(Char ch) {
		if (!(ch instanceof Hero)) return true;
		int direction = pos - ch.pos;
		int destination = pos + direction;
		if (!validDirection(direction) || destination < 0 || destination >= Dungeon.level.length()
				|| (!Dungeon.level.passable[destination] && !Dungeon.level.avoid[destination])
				|| Actor.findChar(destination) != null) {
			((Hero) ch).spendAndNext(1f / ch.speed());
			return true;
		}

		int sheepFrom = pos;
		int heroFrom = ch.pos;
		move(destination);
		ch.move(sheepFrom);
		if (sprite != null) sprite.move(sheepFrom, destination);
		if (ch.sprite != null) ch.sprite.move(heroFrom, sheepFrom);
		if (Dungeon.level instanceof SpsSokobanLevel) {
			((SpsSokobanLevel) Dungeon.level).afterSheepMoved(this);
		}
		((Hero) ch).spendAndNext(1f / ch.speed());
		return true;
	}

	public static class Corner extends SpsSokobanSheep {
		{
			spriteClass = SpsSokobanSheepSprite.Corner.class;
		}

		@Override
		protected boolean validDirection(int direction) {
			int width = Dungeon.level.width();
			return super.validDirection(direction) || direction == width + 1 || direction == width - 1
					|| direction == -width + 1 || direction == -width - 1;
		}
	}

	public static class Switch extends SpsSokobanSheep {
		{
			spriteClass = SpsSokobanSheepSprite.Switch.class;
		}

		@Override
		public boolean interact(Char ch) {
			if (!(ch instanceof Hero) || !Dungeon.level.adjacent(pos, ch.pos)) return true;
			int sheepFrom = pos;
			int heroFrom = ch.pos;
			move(heroFrom);
			ch.move(sheepFrom);
			if (sprite != null) sprite.move(sheepFrom, heroFrom);
			if (ch.sprite != null) ch.sprite.move(heroFrom, sheepFrom);
			if (Dungeon.level instanceof SpsSokobanLevel) {
				((SpsSokobanLevel) Dungeon.level).afterSheepMoved(this);
			}
			((Hero) ch).spendAndNext(1f / ch.speed());
			return true;
		}
	}

	public static class Black extends SpsSokobanSheep {
		{
			spriteClass = SpsSokobanSheepSprite.Black.class;
		}

		@Override
		public boolean interact(Char ch) {
			if (!(ch instanceof Hero) || !(Dungeon.level instanceof SpsSokobanLevel)) return true;
			int sheepFrom = pos;
			int destination = ((SpsSokobanLevel) Dungeon.level).randomFleecingCell(pos, 5);
			if (destination < 0) {
				destroy();
				if (sprite != null) sprite.killAndErase();
			} else {
				move(destination);
				if (sprite != null) sprite.move(sheepFrom, destination);
			}
			int heroFrom = ch.pos;
			ch.move(sheepFrom);
			if (ch.sprite != null) ch.sprite.move(heroFrom, sheepFrom);
			if (destination >= 0) ((SpsSokobanLevel) Dungeon.level).afterSheepMoved(this);
			((Hero) ch).spendAndNext(1f / ch.speed());
			return true;
		}
	}

	public static class Stop extends SpsSokobanSheep {
		{
			spriteClass = SheepSprite.class;
		}

		@Override
		public boolean interact(Char ch) {
			return false;
		}
	}
}
