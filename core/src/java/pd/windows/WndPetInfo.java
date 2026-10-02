/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.Dungeon;
import pd.actors.mobs.pets.LegacyPet;
import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.messages.InlineText;

/** The five-action interaction window used by SPS-PD's summoned pets. */
public class WndPetInfo extends WndOptions {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndPetInfo.class)
			.t("title", "宠物信息")
			.t("info", "生命值：%1$d / %2$d\n技能冷却：%3$d")
			.t("change", "交换")
			.t("stay", "留守")
			.t("follow", "跟随")
			.t("feed", "喂食")
			.t("recover", "收获")
			.t("staying", "%s会留守在这里。")
			.t("following", "%s会继续跟随你。")
			.t("choose_food", "选择喂给宠物的物品")
			.t("refuses", "你的宠物不吃这个。")
			.t("fed", "%1$s吃掉了%2$s，恢复%3$d点生命。")
			.t("not_ready", "产物还没有准备好，当前冷却为%d。")
			.t("no_reward", "这只宠物目前没有可收获的产物。")
			.t("reward", "你的宠物产出了%s。");
	}




	private final LegacyPet pet;

	public WndPetInfo(LegacyPet pet) {
		super(pet.sprite(), Messages.get(WndPetInfo.class, "title"),
				Messages.get(WndPetInfo.class, "info", pet.HP, pet.HT, pet.rewardCooldown()),
				Messages.get(WndPetInfo.class, "change"),
				Messages.get(WndPetInfo.class, "stay"),
				Messages.get(WndPetInfo.class, "follow"),
				Messages.get(WndPetInfo.class, "feed"),
				Messages.get(WndPetInfo.class, "recover"));
		this.pet = pet;
	}

	@Override
	protected void onSelect(int index) {
		switch (index) {
			case 0:
				pet.swapPlaces(Dungeon.hero);
				break;
			case 1:
				pet.stayHere();
				GLog.i(Messages.get(WndPetInfo.class, "staying", pet.name()));
				break;
			case 2:
				pet.followHero();
				GLog.i(Messages.get(WndPetInfo.class, "following", pet.name()));
				break;
			case 3:
				GameScene.selectItem(foodSelector);
				break;
			case 4:
				collectReward();
				break;
		}
	}

	private void collectReward() {
		if (!pet.rewardReady()) {
			GLog.w(Messages.get(WndPetInfo.class, "not_ready", pet.rewardCooldown()));
			return;
		}
		Item reward = pet.claimReward();
		if (reward == null || Dungeon.level == null) {
			GLog.w(Messages.get(WndPetInfo.class, "no_reward"));
			return;
		}
		Dungeon.level.drop(reward, pet.pos).sprite.drop();
		GLog.p(Messages.get(WndPetInfo.class, "reward", reward.name()));
	}

	private final WndBag.ItemSelector foodSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(WndPetInfo.class, "choose_food"); }
		@Override public boolean itemSelectable(Item item) { return !item.isEquipped(Dungeon.hero); }
		@Override public void onSelect(Item item) {
			if (item == null) return;
			int healed = pet.feed(item, Dungeon.hero);
			if (healed < 0) GLog.w(Messages.get(WndPetInfo.class, "refuses"));
			else GLog.p(Messages.get(WndPetInfo.class, "fed", pet.name(), item.name(), healed));
		}
	};
}
