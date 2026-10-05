/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.brewed;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.*;
import pd.actors.hero.Hero;
import pd.effects.SpellSprite;
import pd.items.Item;
import pd.items.StrBottle;
import pd.items.YellowDewdrop;
import pd.items.consum.food.Food;
import pd.items.consum.potions.*;
import pd.items.consum.potions.elixirs.ElixirOfMight;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import pd.messages.InlineText;

/** SPS-PD's cooked blandfruit, whose effects intentionally differ from Shattered's. */
public class Brewed extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Brewed.class)
			.t("name", "酿制无味果")
			.t("ac_eat", "食用")
			.t("raw", "这个果实还需要和一颗种子一起烹煮。")
			.t("sunfruit", "阳光果")
			.t("rotfruit", "鲜美果")
			.t("earthfruit", "大地果")
			.t("blindfruit", "目盲果")
			.t("firefruit", "火焰果")
			.t("icefruit", "冰霜果")
			.t("fadefruit", "渐隐果")
			.t("sorrowfruit", "忧伤果")
			.t("stormfruit", "暴风果")
			.t("dreamfruit", "梦乡果")
			.t("starfruit", "星陨果")
			.t("mightfruit", "决心果")
			.t("heartfruit", "生命果")
			.t("nutfruit", "坚果果")
			.t("mixfruit", "混合果")
			.t("strfruit", "力量果")
			.t("glassfruit", "水晶玻璃果")
			.t("ice_msg", "这个冰霜果尝起来有点冰冻生肉片的感觉。")
			.t("fire_msg", "你感觉到体内有一股烈焰在熊熊燃烧！")
			.t("toxic_msg", "你被灌注了强大的毒素力量！")
			.t("para_msg", "你感到大地的力量在体内奔腾！")
			.t("desc", "尚未烹制的无味果。")
			.t("desc_cooked", "这个果实已经因为吸收锅中的汤而鼓胀，并且吸收了其中种子的属性。\n\n看起来已经可以吃了！");
	}




	public static final String AC_EAT = "EAT";
	private static final float TIME_TO_EAT = 2f;
	private static final String POTION_ATTRIB = "potionattrib";

	public Item potionAttrib;
	private ItemSprite.Glowing potionGlow;
	public float energy = Hunger.HUNGRY;

	{
		stackable = true;
		image = ConsumFoodFoodDict.BLANDFRUIT;
		defaultAction = AC_EAT;
	}

	@Override
	public boolean isSimilar(Item item) {
		if (!(item instanceof Brewed)) return false;
		Brewed other = (Brewed) item;
		return potionAttrib == null ? other.potionAttrib == null
				: other.potionAttrib != null && other.potionAttrib.getClass() == potionAttrib.getClass();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (hero.buff(Locked.class) == null) actions.add(AC_EAT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_EAT.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (hero.buff(Locked.class) != null) {
			GLog.w(Messages.get(Food.class, "locked"));
			return;
		}
		if (potionAttrib == null) {
			GLog.w(Messages.get(this, "raw"));
			return;
		}

		applyPotionEffect(hero);
		if (hero.heroClass != pd.actors.hero.HeroClass.FOLLOWER
				|| Random.Int(10) >= 1) {
			detach(hero.belongings.backpack);
		}
		Buff.affect(hero, Hunger.class).satisfy(energy);
		applyClassEffect(hero);

		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			SpellSprite.show(hero, SpellSprite.FOOD);
		}
		Sample.INSTANCE.play(Assets.Sounds.EAT);
		hero.busy();
		hero.spend(TIME_TO_EAT);
		Statistics.foodEaten++;
		Badges.validateFoodEaten();
	}

	protected void applyPotionEffect(Hero hero) {
		if (potionAttrib instanceof PotionOfLiquidFlame) {
			GLog.i(Messages.get(this, "fire_msg"));
			Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
		} else if (potionAttrib instanceof PotionOfToxicGas) {
			GLog.i(Messages.get(this, "toxic_msg"));
			Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION);
		} else if (potionAttrib instanceof PotionOfParalyticGas) {
			GLog.i(Messages.get(this, "para_msg"));
			Buff.prolong(hero, EarthImbue.class, 50f);
		} else if (potionAttrib instanceof PotionOfFrost) {
			GLog.i(Messages.get(this, "ice_msg"));
			Buff.prolong(hero, FrostImbue.class, FrostImbue.DURATION);
		} else if (potionAttrib instanceof PotionOfMixing) {
			Buff.prolong(hero, FrostImbue.class, FrostImbue.DURATION);
			Buff.prolong(hero, EarthImbue.class, 50f);
			Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION);
			Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
		} else if (potionAttrib instanceof PotionOfExperience) {
			Buff.prolong(hero, Bless.class, 120f);
			Buff.prolong(hero, HasteBuff.class, 120f);
			Buff.affect(hero, MoonFury.class);
		} else if (potionAttrib instanceof PotionOfHealing) {
			hero.HP = hero.HT;
			PotionOfHealing.cure(hero);
		} else if (potionAttrib instanceof PotionOfStrength) {
			Buff.affect(hero, AttackUp.class, 240f).level(30);
			Buff.prolong(hero, Muscle.class, 240f);
		} else if (potionAttrib instanceof PotionOfShield) {
			Buff.affect(hero, ShieldArmor.class).level(hero.HT / 4);
			Buff.affect(hero, MagicArmor.class).level(hero.HT / 4);
		} else if (potionAttrib instanceof PotionOfPurity) {
			Buff.prolong(hero, GasesImmunity.class, 50f);
			Buff.prolong(hero, HighLight.class, 100f);
		} else if (potionAttrib instanceof PotionOfMindVision) {
			Buff.prolong(hero, MindVision.class, 30f);
			Buff.prolong(hero, Awareness.class, 15f);
		} else if (potionAttrib instanceof ElixirOfMight) {
			Buff.affect(hero, DefenceUp.class, 240f).level(30);
			Buff.prolong(hero, HTimprove.class, 240f);
			hero.updateHT(true);
		} else if (potionAttrib instanceof PotionOfLevitation) {
			Buff.prolong(hero, Levitation.class, Levitation.DURATION);
			Buff.affect(hero, DefenceUp.class, 50f).level(20);
		} else if (potionAttrib instanceof PotionOfInvisibility) {
			Buff.prolong(hero, Invisibility.class, Invisibility.DURATION * 2f);
			Buff.affect(hero, AttackUp.class, 100f).level(20);
		} else if (potionAttrib instanceof StrBottle) {
			hero.STR++;
			hero.HP = hero.HT;
			Badges.validateStrengthAttained();
		}
	}

	protected void applyClassEffect(Hero hero) {
		int healEnergy = Math.max(7, Math.round(energy / 40f));
		switch (hero.heroClass) {
			case WARRIOR:
				if (hero.HP < hero.HT) {
					int heal = Random.Int(3, healEnergy);
					hero.HP = Math.min(hero.HT, hero.HP + heal);
				}
				break;
			case MAGE:
				Buff.prolong(hero, Recharging.class, 4f);
				ScrollOfRecharging.charge(hero);
				break;
			case ROGUE:
				Buff.affect(hero, AttackUp.class, 10f).level(30);
				Buff.prolong(hero, Light.class, 5f);
				break;
			case HUNTRESS:
				if (Dungeon.level != null) Dungeon.level.drop(new YellowDewdrop(), hero.pos).sprite.drop();
				break;
			case PERFORMER:
				Buff.affect(hero, WarGroove.class);
				break;
			case SOLDIER:
				Buff.prolong(hero, HasteBuff.class, 5f);
				break;
			case FOLLOWER:
				Dungeon.gold += 10;
				break;
			case ASCETIC:
				Buff.affect(hero, MagicArmor.class).level(10);
				break;
		}
	}

	public Brewed imbuePotion(Item item) {
		potionAttrib = item;
		if (item instanceof PotionOfHealing) potionGlow = new ItemSprite.Glowing(0x2EE62E);
		else if (item instanceof PotionOfStrength) potionGlow = new ItemSprite.Glowing(0xCC0022);
		else if (item instanceof PotionOfParalyticGas) potionGlow = new ItemSprite.Glowing(0x67583D);
		else if (item instanceof PotionOfInvisibility) potionGlow = new ItemSprite.Glowing(0xE5D273);
		else if (item instanceof PotionOfLiquidFlame) potionGlow = new ItemSprite.Glowing(0xFF7F00);
		else if (item instanceof PotionOfFrost) potionGlow = new ItemSprite.Glowing(0x66B3FF);
		else if (item instanceof PotionOfMindVision) potionGlow = new ItemSprite.Glowing(0xB8E6CF);
		else if (item instanceof PotionOfToxicGas) potionGlow = new ItemSprite.Glowing(0xA15CE5);
		else if (item instanceof PotionOfLevitation) potionGlow = new ItemSprite.Glowing(0x1C3A57);
		else if (item instanceof PotionOfPurity) potionGlow = new ItemSprite.Glowing(0x8E2975);
		else if (item instanceof PotionOfExperience) potionGlow = new ItemSprite.Glowing(0xA79400);
		else if (item instanceof PotionOfMixing
				|| item instanceof ElixirOfMight || item instanceof StrBottle) potionGlow = new ItemSprite.Glowing(0xB20000);
		else if (item instanceof PotionOfShield) potionGlow = new ItemSprite.Glowing(0x67583D);
		return this;
	}

	public Brewed cook(Plant.Seed seed) {
		Class<? extends Potion> potion = Potion.SeedToPotion.types.get(seed.getClass());
		return potion == null ? null : imbuePotion(Reflection.newInstance(potion));
	}

	@Override
	public String name() {
		String key = null;
		if (potionAttrib instanceof PotionOfHealing) key = "sunfruit";
		else if (potionAttrib instanceof PotionOfStrength) key = "rotfruit";
		else if (potionAttrib instanceof PotionOfParalyticGas) key = "earthfruit";
		else if (potionAttrib instanceof PotionOfInvisibility) key = "blindfruit";
		else if (potionAttrib instanceof PotionOfLiquidFlame) key = "firefruit";
		else if (potionAttrib instanceof PotionOfFrost) key = "icefruit";
		else if (potionAttrib instanceof PotionOfMindVision) key = "fadefruit";
		else if (potionAttrib instanceof PotionOfToxicGas) key = "sorrowfruit";
		else if (potionAttrib instanceof PotionOfLevitation) key = "stormfruit";
		else if (potionAttrib instanceof PotionOfPurity) key = "dreamfruit";
		else if (potionAttrib instanceof PotionOfExperience) key = "starfruit";
		else if (potionAttrib instanceof ElixirOfMight) key = "mightfruit";
		else if (potionAttrib instanceof PotionOfMixing) key = "mixfruit";
		else if (potionAttrib instanceof StrBottle) key = "strfruit";
		else if (potionAttrib instanceof PotionOfShield) key = "glassfruit";
		return key == null ? super.name() : Messages.get(this, key);
	}

	@Override
	public String desc() {
		return potionAttrib == null ? super.desc() : Messages.get(this, "desc_cooked");
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return potionGlow;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(POTION_ATTRIB, potionAttrib);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(POTION_ATTRIB)) imbuePotion((Item) bundle.get(POTION_ATTRIB));
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return quantity; }
}
