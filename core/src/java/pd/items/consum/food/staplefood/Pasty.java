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

/** SPS-PD's date-sensitive staple food. */
public class Pasty extends StapleFood {

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
