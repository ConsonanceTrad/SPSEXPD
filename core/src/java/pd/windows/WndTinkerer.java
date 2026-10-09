package pd.windows;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.hero.Hero;
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
			.t("info1", "嗯……这就是露珠菌孢。作为回报，我可以帮你改进露珠瓶，让它能强化你的装备。")
			.t("water", "祝福强化")
			.t("draw", "精确强化")
			.t("spinfo", "告诉我它们之间的区别")
			.t("details", "露珠强化会在进入普通楼层时给予_露珠爆炸_。效果持续期间，击杀目标会在尸体周围产生大量露珠。_精确强化_会让你选择一件装备，用露珠把它连续强化直到达到门槛或露珠不足。")
			.t("close", "明白了")
			.t("dungeon", "你感觉背包里充满了奇异的能量。")
			.t("farewell", "祝你好运，%s！");
	}




	private final Tinkerer1 tinkerer;

	public WndTinkerer(Tinkerer1 tinkerer) {
		super(tinkerer.sprite(), Messages.titleCase(tinkerer.name()),
				Messages.get(WndTinkerer.class, "info1"),
				Messages.get(WndTinkerer.class, "draw"),
				Messages.get(WndTinkerer.class, "spinfo"));
		this.tinkerer = tinkerer;
	}

	@Override
	protected void onSelect(int index) {
		if (index == 1) {
			GameScene.show(new WndOptions(Messages.get(WndTinkerer.class, "spinfo"),
					Messages.get(WndTinkerer.class, "details"),
					Messages.get(WndTinkerer.class, "close")));
			return;
		}

		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		Waterskin waterskin = Dungeon.hero.belongings.getItem(Waterskin.class);
		if (mushroom == null || waterskin == null) return;

		performUpgrade(tinkerer, Dungeon.hero, waterskin, mushroom);
	}

	//SPSEXPD: 强化流程抽成静态方法——对话框选择与"带任务蘑菇直接完成"共用
	public static void performUpgrade(Tinkerer1 tinkerer, Hero hero, Waterskin waterskin, Mushroom mushroom) {
		mushroom.detach(hero.belongings.backpack);
		//SPSEXPD: 已取消祝福强化分支，强化统一为精确强化
		waterskin.applySpsUpgrade(Waterskin.UpgradeMode.ACCURATE);
		//SPSEXPD: 一阶解锁「照明」；只加不清零，旧档已获得的能力不被剥夺
		Dungeon.dewWater = true;
		Dewcharge.charge(hero, 300f);
		Dungeon.level.drop(new SpsSkeletonKey(Dungeon.depth), tinkerer.pos).sprite.drop();
		tinkerer.yell(Messages.get(WndTinkerer.class, "farewell", hero.name()));
		GLog.p(Messages.get(WndTinkerer.class, "dungeon"));
		tinkerer.destroy();
		tinkerer.sprite.die();
	}
}
