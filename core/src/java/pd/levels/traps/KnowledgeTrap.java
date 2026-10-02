package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import render.noosa.Game;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class KnowledgeTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(KnowledgeTrap.class)
			.t("name", "知识陷阱")
			.t("desc", "触发后会鉴定你携带的装备。");
	}

	{ color = RED; shape = STARS; }

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[pos] && Game.instance != null && Game.scene() != null) {
			CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10);
			Sample.INSTANCE.play(Assets.Sounds.LEVELUP);
		}
		if (Dungeon.hero != null && Dungeon.hero.belongings != null) Dungeon.hero.belongings.observe();
	}
}
