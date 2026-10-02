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

package pd.items.equipment.bombs;

import pd.atlas.items.EquipmentEquipWeaponBombDict;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Electricity;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.effects.CellEmitter;
import pd.effects.Lightning;
import pd.effects.particles.SparkParticle;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.tiles.DungeonTilemap;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.BArray;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class FlashBangBomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FlashBangBomb.class)
			.t("name", "闪光弹")
			.t("desc", "这枚改造过的炸弹在爆炸时会爆发出一阵电闪雷鸣。在2格范围内的所有单位不仅会受到爆炸的_%1$d~%2$d点伤害_和电击的额外25%%伤害，还会被麻痹10回合。")
			.t("discover_hint", "你可通过炼金合成该物品。");
	}

	
	{
		image = EquipmentEquipWeaponBombDict.FLASHBANG_0;
	}

	@Override
	protected int explosionRange() {
		return 2;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);

		ArrayList<Char> affected = new ArrayList<>();
		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), explosionRange() );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE && Actor.findChar(i) != null) {
				affected.add(Actor.findChar(i));
			}
		}

		ArrayList<Lightning.Arc> arcs = new ArrayList<>();
		for (Char ch : affected){
			//25% bonus damage and 10 turns of stun
			int damage = Math.round(Random.NormalIntRange(4 + Dungeon.scalingDepth(), 12 + 3*Dungeon.scalingDepth()) / 4f);
			ch.damage(damage, new Electricity());
			if (ch.isAlive()) Buff.prolong(ch, Paralysis.class, Paralysis.DURATION);
			arcs.add(new Lightning.Arc(DungeonTilemap.tileCenterToWorld(cell), ch.sprite.center()));

			if (ch == Dungeon.hero){
				GameScene.flash(0x80FFFFFF);
			}

			if (ch == Dungeon.hero && !ch.isAlive()) {
				Badges.validateDeathFromFriendlyMagic();
				GLog.n(Messages.get(this, "ondeath"));
				Dungeon.fail(this);
			}
		}

		CellEmitter.center(cell).burst(SparkParticle.FACTORY, 20);
		Dungeon.hero.sprite.parent.addToFront(new Lightning(arcs, null));
		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
	}
	
	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 30);
	}
}
