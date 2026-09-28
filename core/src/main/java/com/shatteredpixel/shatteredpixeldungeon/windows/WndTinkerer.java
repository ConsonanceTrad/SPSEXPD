package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dewcharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Tinkerer1;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SpsSkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Mushroom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class WndTinkerer extends WndOptions {

	private final Tinkerer1 tinkerer;

	public WndTinkerer(Tinkerer1 tinkerer) {
		super(tinkerer.sprite(), Messages.titleCase(tinkerer.name()),
				Messages.get(WndTinkerer.class, "info1"),
				Messages.get(WndTinkerer.class, "water"),
				Messages.get(WndTinkerer.class, "draw"),
				Messages.get(WndTinkerer.class, "spinfo"));
		this.tinkerer = tinkerer;
	}

	@Override
	protected void onSelect(int index) {
		if (index == 2) {
			GameScene.show(new WndOptions(Messages.get(WndTinkerer.class, "spinfo"),
					Messages.get(WndTinkerer.class, "details"),
					Messages.get(WndTinkerer.class, "close")));
			return;
		}

		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		Waterskin waterskin = Dungeon.hero.belongings.getItem(Waterskin.class);
		if (mushroom == null || waterskin == null) return;

		mushroom.detach(Dungeon.hero.belongings.backpack);
		Waterskin.UpgradeMode mode = index == 0
				? Waterskin.UpgradeMode.RANDOM_BLESS
				: Waterskin.UpgradeMode.ACCURATE;
		waterskin.applySpsUpgrade(mode);
		Dungeon.dewWater = mode == Waterskin.UpgradeMode.RANDOM_BLESS;
		Dungeon.dewDraw = mode == Waterskin.UpgradeMode.ACCURATE;
		Statistics.previousFloorMoves = 500;
		Buff.affect(Dungeon.hero, Dewcharge.class, 300f);
		Dungeon.level.drop(new SpsSkeletonKey(Dungeon.depth), tinkerer.pos).sprite.drop();
		tinkerer.yell(Messages.get(WndTinkerer.class, "farewell", Dungeon.hero.name()));
		GLog.p(Messages.get(WndTinkerer.class, "dungeon"));
		tinkerer.destroy();
		tinkerer.sprite.die();
	}
}
