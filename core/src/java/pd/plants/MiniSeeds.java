/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.plants;

import pd.atlas.items.ConsumPotionSeedSeedDict;
import pd.messages.InlineText;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 品种化迷你种子——每种种子都有一个「营养不良」的迷你版本。
 *
 * <p>图标是各品种正常种子的 8x8 小号版（{@code ConsumPotionSeedSeedDict.SEED_EXTRA_*}）。
 * 无论玩家把迷你种子种在地上还是花盆里，都只会长出一株**该品种的野生植株**
 * （迷你种子不设 {@code explantClass}，因此永远不会长成可反复收获的果丛 Ex* 形态），
 * 也不会享受到精心种植的额外产出。
 *
 * <p>产出途径：踩踏高草（见 {@code HighGrass#dropSpsHarvest}）随机掉一种。
 */
public final class MiniSeeds {

	private MiniSeeds() { }

	/** 全部品种（等高随机，供高草掉落使用）。 */
	public static final Class<? extends Plant.Seed>[] ALL = new Class[]{
			MiniFirebloomSeed.class, MiniIcecapSeed.class, MiniSorrowmossSeed.class,
			MiniBlindweedSeed.class, MiniSungrassSeed.class, MiniEarthrootSeed.class,
			MiniFadeleafSeed.class, MiniDreamfoilSeed.class, MiniStormvineSeed.class,
			MiniNutPlantSeed.class, MiniStarflowerSeed.class, MiniStarEaterSeed.class,
			MiniReNepenthSeed.class, MiniDewcatcherSeed.class, MiniSeedpodSeed.class,
			MiniSwiftthistleSeed.class, MiniFreshberrySeed.class, MiniRotberrySeed.class,
			MiniBlandfruitSeed.class, MiniSiOtwoFlowerSeed.class
	};

	/** 随机一种品种化迷你种子。 */
	public static Plant.Seed random() {
		Class<? extends Plant.Seed> type = Random.element( ALL );
		return Reflection.newInstance( type );
	}

	public static class MiniFirebloomSeed extends MiniSeed {
		static {
			InlineText.of(MiniFirebloomSeed.class)
				.t("name", "迷你烈焰花之种")
				.t("desc", "一粒干瘪的烈焰花种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的烈焰花，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_2; plantClass = Firebloom.class; }
	}

	public static class MiniIcecapSeed extends MiniSeed {
		static {
			InlineText.of(MiniIcecapSeed.class)
				.t("name", "迷你冰冠花之种")
				.t("desc", "一粒干瘪的冰冠花种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的冰冠花，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_5; plantClass = Icecap.class; }
	}

	public static class MiniSorrowmossSeed extends MiniSeed {
		static {
			InlineText.of(MiniSorrowmossSeed.class)
				.t("name", "迷你断肠苔之种")
				.t("desc", "一粒干瘪的断肠苔种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的断肠苔，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_7; plantClass = Sorrowmoss.class; }
	}

	public static class MiniBlindweedSeed extends MiniSeed {
		static {
			InlineText.of(MiniBlindweedSeed.class)
				.t("name", "迷你致盲草之种")
				.t("desc", "一粒干瘪的致盲草种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的致盲草，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_3; plantClass = Blindweed.class; }
	}

	public static class MiniSungrassSeed extends MiniSeed {
		static {
			InlineText.of(MiniSungrassSeed.class)
				.t("name", "迷你阳春草之种")
				.t("desc", "一粒干瘪的阳春草种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的阳春草，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_4; plantClass = Sungrass.class; }
	}

	public static class MiniEarthrootSeed extends MiniSeed {
		static {
			InlineText.of(MiniEarthrootSeed.class)
				.t("name", "迷你地缚根之种")
				.t("desc", "一粒干瘪的地缚根种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的地缚根，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_9; plantClass = Earthroot.class; }
	}

	public static class MiniFadeleafSeed extends MiniSeed {
		static {
			InlineText.of(MiniFadeleafSeed.class)
				.t("name", "迷你消逝草之种")
				.t("desc", "一粒干瘪的消逝草种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的消逝草，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_10; plantClass = Fadeleaf.class; }
	}

	public static class MiniDreamfoilSeed extends MiniSeed {
		static {
			InlineText.of(MiniDreamfoilSeed.class)
				.t("name", "迷你梦叶花之种")
				.t("desc", "一粒干瘪的梦叶花种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的梦叶花，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_8; plantClass = Dreamfoil.class; }
	}

	public static class MiniStormvineSeed extends MiniSeed {
		static {
			InlineText.of(MiniStormvineSeed.class)
				.t("name", "迷你风暴藤之种")
				.t("desc", "一粒干瘪的风暴藤种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的风暴藤，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_6; plantClass = Stormvine.class; }
	}

	public static class MiniNutPlantSeed extends MiniSeed {
		static {
			InlineText.of(MiniNutPlantSeed.class)
				.t("name", "迷你坚果藤之种")
				.t("desc", "一粒干瘪的坚果藤种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的坚果藤，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_13; plantClass = NutPlant.class; }
	}

	public static class MiniStarflowerSeed extends MiniSeed {
		static {
			InlineText.of(MiniStarflowerSeed.class)
				.t("name", "迷你星陨花之种")
				.t("desc", "一粒干瘪的星陨花种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的星陨花，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_12; plantClass = Starflower.class; }
	}

	public static class MiniStarEaterSeed extends MiniSeed {
		static {
			InlineText.of(MiniStarEaterSeed.class)
				.t("name", "迷你吞星花之种")
				.t("desc", "一粒干瘪的吞星花种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的吞星花，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_14; plantClass = StarEater.class; }
	}

	public static class MiniReNepenthSeed extends MiniSeed {
		static {
			InlineText.of(MiniReNepenthSeed.class)
				.t("name", "迷你转换笼之种")
				.t("desc", "一粒干瘪的转换笼种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的转换笼，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_15; plantClass = ReNepenth.class; }
	}

	public static class MiniDewcatcherSeed extends MiniSeed {
		static {
			InlineText.of(MiniDewcatcherSeed.class)
				.t("name", "迷你集露草之种")
				.t("desc", "一粒干瘪的集露草种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的集露草，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_17; plantClass = Dewcatcher.class; }
	}

	public static class MiniSeedpodSeed extends MiniSeed {
		static {
			InlineText.of(MiniSeedpodSeed.class)
				.t("name", "迷你种子荚之种")
				.t("desc", "一粒干瘪的种子荚种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的种子荚，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_18; plantClass = Seedpod.class; }
	}

	public static class MiniSwiftthistleSeed extends MiniSeed {
		static {
			InlineText.of(MiniSwiftthistleSeed.class)
				.t("name", "迷你速行蓟之种")
				.t("desc", "一粒干瘪的速行蓟种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的速行蓟，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_19; plantClass = Swiftthistle.class; }
	}

	public static class MiniFreshberrySeed extends MiniSeed {
		static {
			InlineText.of(MiniFreshberrySeed.class)
				.t("name", "迷你鲜莓之种")
				.t("desc", "一粒干瘪的鲜莓丛种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的鲜莓丛，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_1; plantClass = Freshberry.class; }
	}

	public static class MiniRotberrySeed extends MiniSeed {
		static {
			InlineText.of(MiniRotberrySeed.class)
				.t("name", "迷你腐莓之种")
				.t("desc", "一粒干瘪的腐莓种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的腐莓，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_1; plantClass = Rotberry.class; }
	}

	public static class MiniBlandfruitSeed extends MiniSeed {
		static {
			InlineText.of(MiniBlandfruitSeed.class)
				.t("name", "迷你无味果之种")
				.t("desc", "一粒干瘪的无味果种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的无味果，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_11; plantClass = BlandfruitBush.class; }
	}

	public static class MiniSiOtwoFlowerSeed extends MiniSeed {
		static {
			InlineText.of(MiniSiOtwoFlowerSeed.class)
				.t("name", "迷你水晶花之种")
				.t("desc", "一粒干瘪的水晶花种子，看起来营养不良，肯定长不大。无论种在地上还是花盆里，都只能长出一株野生的水晶花，不会结成能反复收获的果丛。");
		}
		{ image = ConsumPotionSeedSeedDict.SEED_EXTRA_16; plantClass = SiOtwoFlower.class; }
	}
}
