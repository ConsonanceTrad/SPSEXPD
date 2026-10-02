/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.staplefood;

import pd.atlas.IconEntry;

import pd.atlas.items.ConsumFoodFoodDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Light;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.effects.particles.FlameParticle;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.Calendar;
import pd.messages.InlineText;

/** SPS-PD's date-sensitive staple food. */
public class Pasty extends StapleFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Pasty.class)
			.t("pasty", "肉馅饼")
			.t("pasty_desc", "这是份正宗康郡肉烘饼，内含土豆加牛肉的传统馅料。")
			.t("assorted", "春什锦")
			.t("assorted_desc", "年糕、汤圆和饺子塞满了这个蒸笼。作为新春佳节的传统食物，它能完全消除你的饥饿感并让你充满决心。\n\n春节快乐！")
			.t("book", "暑假作业")
			.t("book_desc", "这是一份暑假作业，注意劳逸结合。\n\n暑假快乐！")
			.t("egg", "七彩蛋")
			.t("egg_desc", "虽然这个东西看起来不大，但吃下它能够完全消除你的饥饿感，并让你精神抖擞。\n\n复活节快乐！")
			.t("pie", "南瓜派")
			.t("pie_desc", "好大的一块南瓜派！甘甜又微辣，它会填饱你的肚子并让你恢复少量生命。\n\n万圣节快乐！")
			.t("turkey", "烤火鸡")
			.t("turkey_desc", "刚烤好的一只感恩节火鸡。吃下它可以恢复你的饥饿值并加快你的速度。\n\n感恩节快乐！")
			.t("cane", "拐杖糖")
			.t("cane_desc", "甜度爆表的巨型拐杖糖！大到够你一次吃饱，其中的糖分还能让你的法杖获得一点额外充能。\n\n节日快乐！")
			.t("bread", "砖头糕")
			.t("bread_desc", "由面粉和奶油烧制成的砖头形糕点。没准里面有金子呢。\n\n劳动节快乐！")
			.t("jelly", "软糖剑")
			.t("jelly_desc", "一个做成剑形状的软糖，估计只有小孩才喜欢这种味道。\n\n儿童节快乐！")
			.t("worker", "辛苦了，这是报酬。");
	}


	enum Holiday {
		NONE, SPRING, STUDENT, EASTER, HWEEN, THANK, XMAS, CHILD, WORKER
	}

	private static final Holiday holiday = holidayFor(Calendar.getInstance());

	{
		image = imageFor(holiday);
		energy = 400f;
	}

	static Holiday holidayFor(Calendar calendar) {
		Holiday result = Holiday.NONE;
		switch (calendar.get(Calendar.MONTH)) {
			case Calendar.JANUARY:
				if (calendar.get(Calendar.WEEK_OF_MONTH) == 1) result = Holiday.XMAS;
				if (calendar.get(Calendar.DAY_OF_MONTH) >= 18) result = Holiday.SPRING;
				break;
			case Calendar.FEBRUARY:
				if (calendar.get(Calendar.DAY_OF_MONTH) <= 28) result = Holiday.SPRING;
				break;
			case Calendar.APRIL:
				result = Holiday.EASTER;
				break;
			case Calendar.MAY:
				if (calendar.get(Calendar.DAY_OF_MONTH) <= 7) result = Holiday.WORKER;
				break;
			case Calendar.JUNE:
				if (calendar.get(Calendar.DAY_OF_MONTH) <= 3) result = Holiday.CHILD;
				break;
			case Calendar.JULY:
			case Calendar.AUGUST:
				result = Holiday.STUDENT;
				break;
			case Calendar.OCTOBER:
				if (calendar.get(Calendar.WEEK_OF_MONTH) >= 2) result = Holiday.HWEEN;
				break;
			case Calendar.NOVEMBER:
				if (calendar.get(Calendar.DAY_OF_MONTH) == 1) result = Holiday.HWEEN;
				if (calendar.get(Calendar.WEEK_OF_MONTH) >= 2) result = Holiday.THANK;
				break;
			case Calendar.DECEMBER:
				if (calendar.get(Calendar.WEEK_OF_MONTH) <= 1) result = Holiday.THANK;
				if (calendar.get(Calendar.WEEK_OF_MONTH) >= 3) result = Holiday.XMAS;
				break;
			default:
				break;
		}
		return result;
	}

	static IconEntry imageFor(Holiday value) {
		switch (value) {
			case SPRING: return SpecificPlaceHolderDict.SOMETHING_0;
			case STUDENT: return SpecificPlaceHolderDict.SOMETHING_0;
			case EASTER: return SpecificPlaceHolderDict.SOMETHING_0;
			case HWEEN: return ConsumFoodFoodDict.PUMPKIN_PIE;
			case THANK: return ConsumFoodFoodDict.SPS_TURKEY_MEAT_0;
			case XMAS: return ConsumFoodFoodDict.CANDY_CANE_0;
			case CHILD: return SpecificPlaceHolderDict.SOMETHING_0;
			case WORKER: return SpecificPlaceHolderDict.SOMETHING_0;
			case NONE:
			default: return SpecificPlaceHolderDict.SOMETHING_0;
		}
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		applyHoliday(hero, holiday);
	}

	static void applyHoliday(Hero hero, Holiday value) {
		switch (value) {
			case SPRING:
				Buff.affect(hero, BerryRegeneration.class).level(10);
				if (hero.sprite != null) hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
				break;
			case EASTER:
				Buff.affect(hero, Bless.class, 5f);
				if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
				break;
			case STUDENT:
				Buff.affect(hero, Light.class, 50f);
				Buff.affect(hero, MindVision.class, 50f);
				if (hero.sprite != null) hero.sprite.emitter().start(FlameParticle.FACTORY, 0.2f, 3);
				break;
			case HWEEN:
				hero.HP = Math.min(hero.HT, hero.HP + hero.HT / 10);
				if (hero.sprite != null) hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
				break;
			case THANK:
				Buff.affect(hero, HasteBuff.class, 5f);
				Buff.affect(hero, Levitation.class, 5f);
				break;
			case XMAS:
				Buff.affect(hero, Recharging.class, 2f);
				ScrollOfRecharging.charge(hero);
				break;
			case WORKER:
				Dungeon.gold += 500;
				GLog.p(Messages.get(Pasty.class, "worker"));
				break;
			case CHILD:
				hero.HP = hero.permanentHT();
				hero.HTBoost += 3;
				hero.updateHT(true);
				Buff.affect(hero, Blindness.class, 20f);
				Buff.affect(hero, Vertigo.class, 20f);
				break;
			case NONE:
			default:
				break;
		}
	}

	@Override
	public String name() {
		return Messages.get(this, nameKey(holiday));
	}

	@Override
	public String desc() {
		return Messages.get(this, nameKey(holiday) + "_desc");
	}

	private static String nameKey(Holiday value) {
		switch (value) {
			case SPRING: return "assorted";
			case STUDENT: return "book";
			case EASTER: return "egg";
			case HWEEN: return "pie";
			case THANK: return "turkey";
			case XMAS: return "cane";
			case CHILD: return "jelly";
			case WORKER: return "bread";
			case NONE:
			default: return "pasty";
		}
	}

	@Override
	public int value() {
		return 100 * quantity;
	}
}
