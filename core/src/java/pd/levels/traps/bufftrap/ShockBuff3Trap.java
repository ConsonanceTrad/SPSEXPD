package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.messages.InlineText;
public class ShockBuff3Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(ShockBuff3Trap.class)
			.t("name", "雷种大陷阱")
			.t("desc", "会释放大范围雷电场的陷阱。");
	}
 public ShockBuff3Trap(){ super(YELLOW, STARS, ElectriShock.class, 2, 9, false); } }
