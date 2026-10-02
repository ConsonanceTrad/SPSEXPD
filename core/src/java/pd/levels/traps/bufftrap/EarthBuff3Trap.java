package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.AcidWater;
import pd.messages.InlineText;
public class EarthBuff3Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(EarthBuff3Trap.class)
			.t("name", "地种大陷阱")
			.t("desc", "会释放大范围酸蚀场的陷阱。");
	}
 public EarthBuff3Trap(){ super(GREEN, STARS, AcidWater.class, 2, 9, false); } }
