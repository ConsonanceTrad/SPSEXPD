package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.AcidWater;
import pd.messages.InlineText;
public class EarthBuff2Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(EarthBuff2Trap.class)
			.t("name", "地种中陷阱")
			.t("desc", "会释放中等范围酸蚀场的陷阱。");
	}
 public EarthBuff2Trap(){ super(GREEN, WAVES, AcidWater.class, 1, 6, false); } }
