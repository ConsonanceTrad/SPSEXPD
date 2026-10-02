package pd.windows;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.mobs.npcs.Tinkerer1;
import pd.items.Waterskin;
import pd.items.specific.keys.SpsSkeletonKey;
import pd.items.quest.Mushroom;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.messages.InlineText;

public class WndTinkerer extends WndOptions {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndTinkerer.class)
			.t("info1", "嗯……这就是露珠菌孢。作为回报，我可以改进你的水袋。请选择露珠强化装备的方式。")
			.t("water", "祝福强化")
			.t("draw", "精确强化")
			.t("spinfo", "告诉我它们之间的区别")
			.t("details", "露珠强化会在进入普通楼层时给予_露珠爆炸_。效果持续期间，击杀目标会在尸体周围产生大量露珠。若在本层步数目标内清除全部普通敌人，节省的步数会加入下一层的充能。_祝福强化_会随机强化背包中的装备；_精确强化_会将一件指定道具强化数次。")
			.t("close", "明白了")
			.t("dungeon", "你感觉背包里充满了奇异的能量。")
			.t("farewell", "祝你好运，%s！");
	}


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
