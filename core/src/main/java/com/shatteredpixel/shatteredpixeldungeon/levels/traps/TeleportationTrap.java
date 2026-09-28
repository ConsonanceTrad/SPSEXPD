package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class TeleportationTrap extends Trap {

	{
		color = GREY;
		shape = GRILL;
	}

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
			Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		}

		Char target = Actor.findChar(pos);
		if (target instanceof Hero) {
			ScrollOfTeleportation.teleportChar(target);
			((Hero)target).curAction = null;
		} else if (target != null) {
			int destination = legacyDestination(target);
			if (destination == -1 || Dungeon.bossLevel()) {
				GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));
			} else {
				target.pos = destination;
				if (target.sprite != null) {
					target.sprite.place(destination);
					target.sprite.visible = Dungeon.level.heroFOV[destination];
				}
				Dungeon.level.drop(new Fadeleaf.Seed(), destination);
			}
		}

		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) {
			int destination = Dungeon.level.randomRespawnCell(null);
			if (destination != -1) {
				Item item = heap.pickUp();
				if (item != null) Dungeon.level.drop(item, destination);
			}
		}
	}

	private int legacyDestination(Char target) {
		for (int attempt = 0; attempt < 10; attempt++) {
			int destination = Dungeon.level.randomRespawnCell(target);
			if (destination != -1) return destination;
		}
		return -1;
	}
}
