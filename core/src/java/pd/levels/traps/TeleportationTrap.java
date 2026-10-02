package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.Item;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.messages.Messages;
import pd.plants.Fadeleaf;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class TeleportationTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(TeleportationTrap.class)
			.t("name", "传送陷阱")
			.t("desc", "当这种陷阱被触发时，其周遭的所有东西都会被各自传送到本层的随机地点。");
	}


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
