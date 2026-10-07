/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 获得新特质：每 3 级发一点，从特质池抽取 3（或 5）个候选供选择。
 * 重随消耗「重随机会」，且只重抽当前选中的那一格候选。
 */

package pd.windows;

import java.util.ArrayList;
import java.util.HashSet;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.perks.Perk;
import pd.actors.hero.perks.PerkGain;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.utils.GLog;

public class WndGainNewPerk extends WndSelectPerk {

	static {
		InlineText.of(WndGainNewPerk.class)
				.t("title", "选择特质")
				.t("reroll", "重随所选")
				.t("no_reroll", "没有剩余的重随机会了（「神意启发合剂」可以补充）")
				.t("no_candidate", "已没有可换的候选特质")
				.t("maxed", "%s 已达上限，已重新抽取候选");
	}

	private RedButton reroll;
	private RenderedTextBlock rerollCount;

	public WndGainNewPerk(String title, ArrayList<Perk> perks, int initialSelection) {
		super(title, perks, initialSelection);

		reroll = new RedButton(Messages.get(WndGainNewPerk.class, "reroll")) {
			@Override
			protected void onClick() {
				super.onClick();
				rerollSelected();
			}
		};
		add(reroll);

		rerollCount = PixelScene.renderTextBlock("", 6);
		rerollCount.hardlight(Window.TITLE_COLOR);
		add(rerollCount);

		refreshReroll();

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
		if (reroll != null && rerollCount != null) {
			//SPSXPD: 剩余次数显示在重随按钮的右上角（与特质格子角标同一风格）
			rerollCount.setPos(reroll.right() - rerollCount.width() - 3, reroll.top() + 2);
		}
	}

	/** 刷新剩余次数显示与按钮可用状态 */
	private void refreshReroll() {
		Hero hero = Dungeon.hero;
		int left = hero == null ? 0 : Math.max(0, hero.perkRerolls);
		if (rerollCount != null) rerollCount.text(String.valueOf(left));
		if (reroll != null) reroll.enable(left > 0);
		relayout();
	}

	/** 只重抽当前选中的那一格候选 */
	private void rerollSelected() {
		Hero hero = Dungeon.hero;
		if (hero == null || hero.spawnedPerks == null) return;

		if (hero.perkRerolls <= 0) {
			GLog.w(Messages.get(WndGainNewPerk.class, "no_reroll"));
			refreshReroll();
			return;
		}

		int index = selectedIndex();
		if (index < 0 || index >= hero.spawnedPerks.size()) return;

		//排除当前所有候选（其它格 + 本格原来的类），避免白花一次机会抽到同一个
		HashSet<Class<? extends Perk>> exclude = new HashSet<>();
		for (Perk p : hero.spawnedPerks) {
			if (p != null) exclude.add(p.getClass());
		}

		Perk next = Perk.Companion.randomPositiveExcluding(hero, exclude);
		if (next == null) {
			//可选池已空：不消耗次数
			GLog.w(Messages.get(WndGainNewPerk.class, "no_candidate"));
			return;
		}

		//SPSXPD: 已拥有的特质按「升级后等级」预览，描述里能看到升级后的数值
		Perk owned = hero.heroPerk == null ? null : hero.heroPerk.get(next.getClass());
		if (owned != null) next.setLevel(Math.min(next.maxLevel(), owned.level() + 1));

		hero.spawnedPerks.set(index, next);
		hero.perkRerolls--;

		//重开窗口，保持停留在重随的那一格
		hide();
		Show(hero, index);
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
		Show(hero, 0);
	}

	/** 打开选择窗并预先选中某格（重随后就地刷新用） */
	public static void Show(Hero hero, int initialSelection) {
		if (hero == null) return;
		GameScene.show(new WndGainNewPerk(
				Messages.get(WndGainNewPerk.class, "title"),
				roll(hero),
				initialSelection));
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
