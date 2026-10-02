/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.levels.features;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.particles.ElmoParticle;
import pd.levels.Level;
import pd.levels.SpsDew;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.messages.InlineText;

public final class DewBlessRoom {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(DewBlessRoom.class)
			.t("order", "露珠女神赐予了你祝福，本层的规定清理时间为%d回合。");
	}


	private DewBlessRoom() {
	}

	public static void trample(Level level, int pos, Char ch) {
		CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
		if (ch instanceof Hero) {
			Buff.affect((Hero) ch, Dewcharge.class, 720f);
			GLog.h(Messages.get(DewBlessRoom.class, "order"), SpsDew.par( level ));
			Level.set(pos, Terrain.GRASS);
			GameScene.updateMap(pos);
		}
		Dungeon.observe();
	}
}
