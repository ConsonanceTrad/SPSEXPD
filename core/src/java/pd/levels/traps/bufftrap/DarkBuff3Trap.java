package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.ShadowGas;
import pd.messages.InlineText;
public class DarkBuff3Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(DarkBuff3Trap.class)
			.t("name", "暗种大陷阱")
			.t("desc", "会释放大范围暗影场的陷阱。");
	}


 public DarkBuff3Trap(){ super(VIOLET, STARS, ShadowGas.class, 2, 9, false); } }
