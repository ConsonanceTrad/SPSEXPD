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
import pd.utils.GLog;

public class WndGainNewPerk extends WndSelectPerk {

	static {
		InlineText.of(WndGainNewPerk.class)
				.t("title", "选择特质")
				.t("reroll", "重随候选")
				.t("maxed", "%s 已达上限，已重新抽取候选");
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

		boolean owned = hero.heroPerk.has(perk.getClass());
		if (!hero.heroPerk.add(perk)) {
			//SPSXPD: 候选缓存过期（该特质期间已被升满）——不消耗点数，重抽候选
			GLog.w(Messages.get(WndGainNewPerk.class, "maxed", perk.title()));
			hero.spawnedPerks.clear();
			hide();
			Show(hero);
			return;
		}

		hero.reservedPerks = Math.max(0, hero.reservedPerks - 1);
		hero.spawnedPerks.clear();
		hero.perkGained++;
		//SPSXPD: 升级提示由 HeroPerk.add 内部发出，这里只负责「获得」提示
		if (!owned) PerkGain.announce(hero, perk);
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

		//SPSXPD: 剔除已达上限的缓存候选（期间可能通过其它途径升满），避免候选里出现满了的特质
		int before = hero.spawnedPerks.size();
		hero.spawnedPerks.removeIf(p -> p == null || !available(hero, p));
		if (hero.spawnedPerks.isEmpty() || hero.spawnedPerks.size() != before) {
			hero.spawnedPerks.clear();
			hero.spawnedPerks.addAll(
					Perk.Companion.randomPositives(hero, Perk.Companion.candidateCount(hero)));
		}

		//SPSXPD: 已拥有的特质按「升级后等级」预览，描述里直接看到升级收益与变化后的数值
		for (Perk p : hero.spawnedPerks) {
			Perk owned = hero.heroPerk == null ? null : hero.heroPerk.get(p.getClass());
			if (owned != null) p.setLevel(Math.min(p.maxLevel(), owned.level() + 1));
		}

		return new ArrayList<>(hero.spawnedPerks);
	}

	/** 候选是否仍可（获得或升级） */
	private static boolean available(Hero hero, Perk candidate) {
		Perk owned = hero.heroPerk == null ? null : hero.heroPerk.get(candidate.getClass());
		return owned == null || owned.level() < owned.maxLevel();
	}
}
