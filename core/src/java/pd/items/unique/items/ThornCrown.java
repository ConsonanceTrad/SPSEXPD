/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 荆棘冠冕 —— 信徒二档（王冠 B 路线）的徽章（饰物）。
 *
 * <p>拥有十字架的全部效果；此外，_累计受到 21 次伤害_后会驱散自己身上的负面状态与诅咒。</p>
 */
public class ThornCrown extends Cross {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.FOLLOWER_CUSHION_T2_2_THORN;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ThornCrown.class)
			.t("name", "荆棘冠冕")
			.t("stats_desc", "有害魔力的出现率与伤害降低_%1$d%%_（武器诅咒、恶性陷阱、邪恶法术）；累计受到 %2$d 次伤害后，驱散自身负面状态与诅咒。")
			.t("cleansed", "冠冕上的荆棘一齐收紧，你身上的污秽与诅咒被洗净了。")
			.t("desc", "一顶编着荆棘的冠冕：谦卑、痛苦、牺牲。\n\n只要_带在身上_就生效：_有害魔力_（武器诅咒、恶性陷阱、邪恶法术）的出现概率与伤害都会被压低。\n\n此外，_累计受到 %1$d 次伤害_之后，它会_驱散你身上的负面状态与诅咒_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
	}

	@Override
	public String statsDesc() {
		return Messages.get(this, "stats_desc",
				Math.round(HARM_REDUCTION * 100f), pd.actors.buffs.ThornCrownGrace.HITS_REQUIRED);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", pd.actors.buffs.ThornCrownGrace.HITS_REQUIRED);
	}

	/** SPSEXPD: 计入一次受击；满 21 次则驱散负面与诅咒。 */
	public static void countHit(Hero hero) {
		if (hero == null || hero.belongings.getItem(ThornCrown.class) == null) return;
		pd.actors.buffs.ThornCrownGrace grace = Buff.affect(hero, pd.actors.buffs.ThornCrownGrace.class);
		grace.addHit();
		if (!grace.ready()) return;
		grace.reset();

		//清除负面状态
		ArrayList<Buff> negatives = new ArrayList<>();
		for (Buff b : hero.buffs()) {
			if (b.type == Buff.buffType.NEGATIVE) negatives.add(b);
		}
		for (Buff b : negatives) b.detach();

		//解除装备与背包里的诅咒
		if (hero.belongings != null) {
			for (Item item : hero.belongings) {
				if (item != null && item.cursed) {
					item.cursed = false;
					item.cursedKnown = true;
				}
			}
		}

		GLog.p(Messages.get(ThornCrown.class, "cleansed"));
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
