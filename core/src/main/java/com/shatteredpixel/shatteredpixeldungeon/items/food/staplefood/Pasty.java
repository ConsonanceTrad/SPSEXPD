/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

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

	static int imageFor(Holiday value) {
		switch (value) {
			case SPRING: return ItemSpriteSheet.SPS_SPRING_ASSORTED;
			case STUDENT: return ItemSpriteSheet.SPS_KNOWLEDGE_FOOD;
			case EASTER: return ItemSpriteSheet.SPS_PASTY_EASTER_EGG;
			case HWEEN: return ItemSpriteSheet.SPS_PUMPKIN_PIE;
			case THANK: return ItemSpriteSheet.SPS_TURKEY_MEAT;
			case XMAS: return ItemSpriteSheet.SPS_CANDY_CANE;
			case CHILD: return ItemSpriteSheet.SPS_JELLY_SWORD;
			case WORKER: return ItemSpriteSheet.SPS_BRICK_FOOD;
			case NONE:
			default: return ItemSpriteSheet.SPS_PASTY;
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
