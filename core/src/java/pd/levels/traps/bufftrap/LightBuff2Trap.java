package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.HolyLight;
import pd.messages.InlineText;
public class LightBuff2Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(LightBuff2Trap.class)
			.t("name", "光种中陷阱")
			.t("desc", "会释放中等范围圣光场的陷阱。");
	}


 public LightBuff2Trap(){ super(WHITE, WAVES, HolyLight.class, 1, 6, false); } }
