package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.HolyLight;
import pd.messages.InlineText;
public class LightBuff3Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(LightBuff3Trap.class)
			.t("name", "光种大陷阱")
			.t("desc", "会释放大范围圣光场的陷阱。");
	}


 public LightBuff3Trap(){ super(WHITE, STARS, HolyLight.class, 2, 9, false); } }
