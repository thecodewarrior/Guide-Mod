package com.thecodewarrior.guides.views;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import com.thecodewarrior.guides.GuideMod;
import com.thecodewarrior.guides.GuideServerInterface;
import com.thecodewarrior.guides.Reference;
import com.thecodewarrior.guides.gui.BasicIcon;
import com.thecodewarrior.guides.gui.BasicIconFactory;
import com.thecodewarrior.guides.gui.GuiBookOfRevealing;
import com.thecodewarrior.guides.gui.GuiButtonTransparent;
import com.thecodewarrior.guides.gui.GuiUtils;

public class ViewSettings extends View {

	public static final ResourceLocation tex = new ResourceLocation(Reference.MODID, "textures/gui/view/view_settings.png");
	GuiButton shouldDownload;
	GuiUtils gu;
	public ViewSettings(int width, int height, GuiBookOfRevealing gui) {
		super(null, width, height, gui);
	}

	@Override
	public void init() {
		this.gu = new GuiUtils(this.zLevel);
		this.shouldDownload = new GuiButtonTransparent(1, 0, 3, 10, 10);//new GuiCheckBox(1, 0, 0, "Automatically download guide packs", GuideServerInterface.enabled);
		this.buttonList.add(this.shouldDownload);
	}

	@Override
	public void actionPerformed(GuiButton guibutton) {
		switch(guibutton.id) {
		case 1:
			GuideServerInterface.enabled = !GuideServerInterface.enabled;
			GuideMod.updateEnabled(GuideServerInterface.enabled);
		}
	};
	
	static BasicIconFactory f = new BasicIconFactory(256, tex);
	
	static BasicIcon checkOff = f.create(0,  0, 10, 10);
	static BasicIcon checkOn  = f.create(0, 10, 10, 10);
	
	@Override
	public void draw(int mX, int mY) {
		mc.fontRenderer.drawSplitString("Automatically download guide packs for installed mods", 12, 0, 200, 0x000000);
		mc.renderEngine.bindTexture(tex);
		GL11.glColor4f(1, 1, 1, 1);
		if(GuideServerInterface.enabled) {
			gu.drawIcon(0, 3, checkOn);
		} else {
			gu.drawIcon(0, 3, checkOff);
		}
	}

	@Override
	public void keyTyped(char par1, int par2) {
	}
	
	@Override
	public boolean onClick(int mX, int mY, int button) {
		return false;
	}

}