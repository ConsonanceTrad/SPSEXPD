/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 获得新特质：每 3 级发一点，从特质池抽取 3（或 5）个候选供选择，可重随。
 */

package pd.windows;

import java.util.ArrayList;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.perks.Perk;
import pd.actors.hero.perks.PerkGain;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.ui.RedButton;

public class WndGainNewPerk extends WndSelectPerk {

	static {
		InlineText.of(WndGainNewPerk.class)
				.t("title", "选择特质")
				.t("reroll", "重随候选");
	}

	private RedButton reroll;

	public WndGainNewPerk(String title, ArrayList<Perk> perks) {
		super(title, perks);

		reroll = new RedButton(Messages.get(WndGainNewPerk.class, "reroll")) {
			@Override
			protected void onClick() {
				super.onClick();
				Hero hero = Dungeon.hero;
				if (hero == null) return;
				hero.spawnedPerks.clear();
				hide();
				Show(hero);
			}
		};
		add(reroll);

		//父类在构造期已布局过一次（那时还没有重随按钮），这里带上它重新布局
		relayout();
	}

	@Override
	protected float extraHeight() {
		return BTN_H + 2;
	}

	@Override
	protected void layoutExtra(float top) {
		if (reroll != null) reroll.setRect(0, top, WIDTH, BTN_H);
	}

	@Override
	protected void onPerkSelected(Perk perk) {
		Hero hero = Dungeon.hero;
		if (hero == null || perk == null) return;

		hero.heroPerk.add(perk);
		hero.reservedPerks = Math.max(0, hero.reservedPerks - 1);
		hero.spawnedPerks.clear();
		hero.perkGained++;
		PerkGain.announce(hero, perk);
		hide();

		//还有剩余特质点则继续选择
		if (hero.reservedPerks > 0) {
			Show(hero);
		}
	}

	/** 打开选择窗（候选缓存在 hero.spawnedPerks，中途退出/读档不丢失） */
	public static void Show(Hero hero) {
		if (hero == null) return;
		GameScene.show(new WndGainNewPerk(
				Messages.get(WndGainNewPerk.class, "title"),
				roll(hero)));
	}

	private static ArrayList<Perk> roll(Hero hero) {
		if (hero.spawnedPerks == null) hero.spawnedPerks = new ArrayList<>();
		if (hero.spawnedPerks.isEmpty()) {
			hero.spawnedPerks.addAll(
					Perk.Companion.randomPositives(hero, Perk.Companion.candidateCount(hero)));
		}
		return new ArrayList<>(hero.spawnedPerks);
	}
}
