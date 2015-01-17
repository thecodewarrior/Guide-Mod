package com.thecodewarrior.guides.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import scala.actors.threadpool.Arrays;

import com.thecodewarrior.guides.Reference;

public class GuiBookOfRevealing extends GuiContainer {
	public static final int GUI_ID = 100;

	public static final ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/gui/book_of_revealing_gui.png");
	private static final String seperator = "\u0380";// some random unused code point with size=0 in glyph_sizes.bin
	private GuiContainerBookOfRevealing container;
	
	String[] links;
	
	public ItemStack stack;

	private int lineCount = 0;
	private int topLine   = 0;

	private List<String> lines;

	private int textTop;
	private int textLeft;

	private boolean needsRefresh;
	
	private List<IndexedRect> linkRects;

	private float mouseX;

	private float mouseY;
	
	public GuiBookOfRevealing(EntityPlayer player, ItemStack stack, World w, int x, int y, int z) {
		this(player, stack);
		((GuiContainerBookOfRevealing) this.inventorySlots).refreshGuide(w.getBlock(x, y, z), w,x,y,z);
		refreshGuideReal();
	}
	
	public GuiBookOfRevealing(EntityPlayer player, ItemStack stack) {
		super(new GuiContainerBookOfRevealing(player, stack));
		this.container = (GuiContainerBookOfRevealing) this.inventorySlots;
		this.container.refreshGuide((Item)null);
		this.container.gui = this;
		
		this.stack = stack;
		this.xSize = 255;
		this.ySize = 208;
	}
	
	protected void mouseClicked(int x, int y, int button)
    {
		super.mouseClicked(x,y,button);
		if(fontRendererObj == null) {
			return;
		}
		int left = (width - xSize)  / 2;
		int top  = (height - ySize) / 2;
		
		int scrollOffset = fontRendererObj.FONT_HEIGHT*this.topLine;
		
		int adjX  = x - left;
		int adjY  = y - top + scrollOffset;
		if(button == 0) {
			for(IndexedRect rect : this.linkRects) {
				if(rect.pointInside(adjX, adjY)) {
					handleLinkClick(this.links[rect.id]);
				}
			}
		}
    }
	
	private void handleLinkClick(String link) {
		String[] splitLink = link.split(":");
		String prefix = splitLink[0];
		String data = subsetJoin(splitLink, 1, ":");
		
		if(prefix.equalsIgnoreCase("guide")) {
			this.container.refreshGuide(data);
		}
		
	}
	
	private String subsetJoin(String[] arr, int start, String joinWith) {
		return subsetJoin(arr,start,arr.length, joinWith);
	}
	
	private String subsetJoin(String[] arr, int start, int end, String joinWith) {
		return StringUtils.join( Arrays.copyOfRange(arr, start, end) , joinWith);
	}
	
	public void refreshGuide() {
		this.needsRefresh = true;
	}
	
	private void resetLinks() {
		this.linkRects = new ArrayList<IndexedRect>();
	}
	
	private void addLink(int x, int y, int width, int height, int id) {
		this.linkRects.add(new IndexedRect(id, x,y,width, height));
	}
	
	private void refreshGuideReal() {
		if(!this.needsRefresh) {
			return;
		} else if(fontRendererObj == null) {
			return;
		} else {
			this.needsRefresh = false;
		}
		
		resetLinks();
		
		int left = (width - xSize)  / 2;
		int top  = (height - ySize) / 2;
		
		int textMargin = 5;
		int textAreaWidth = xSize-textMargin-16;
		int textAreaHight = 115;
		
		String rawGuide = this.container.guide;
		if(rawGuide == null)
			return;
		
		
		String[] guideParts = rawGuide.split("<<|>>");
		this.links = new String[(int) Math.floor(guideParts.length/2.0)];
		String guide = "";
		
		int linkNum = 0;
		boolean isLink = false;
		int relX = textMargin;
		int relY = textMargin;
		
		StringBuilder builder = new StringBuilder();
		
		for(String part : guideParts) {
			if(isLink) {
				if(part.matches(".+?\\|.+?:.+?")) { // link to another guide
					String linkText = part.split("\\|")[0];
					
					builder.append( seperator+ linkText +seperator );
					
					this.links[linkNum] = "guide:" + subsetJoin( part.split("\\|"), 1, "|");
				}
			} else {
				builder.append( part );
			}
			isLink = !isLink;
		}
		
		guide = builder.toString();
		
		this.lines = fontRendererObj.listFormattedStringToWidth(guide, textAreaWidth);
		
		this.lineCount = textAreaHight / fontRendererObj.FONT_HEIGHT;
		
		this.topLine = this.container.topLine;
		
		int lineNum = 0;
		int toSkip = topLine;
		linkNum = 0;
		int curY = textMargin;
		
		int height = fontRendererObj.FONT_HEIGHT;
		isLink = false;
		for(String line: lines) {
			String[] lineParts = line.split(seperator);
			int curX=textMargin;
			//isLink = false;
			boolean linkLast = false;
			for(String linePart: lineParts) {
				linkLast = false;
				int len = fontRendererObj.getStringWidth(linePart);
				if(isLink) {
					IndexedRect rect = new IndexedRect(linkNum, curX, curY, len, height);
					linkRects.add(rect);
					linkLast = true;
					linkNum++;
				}
				curX += len;
				isLink = !isLink;
			}
			if(linkLast) {
				linkNum--;
			}
			isLink = linkLast;
			curY += height;
			lineNum++;
		}
		
		
		this.textLeft = left+textMargin;
		this.textTop  = top +textMargin;
	}

	public void initGui() {
		super.initGui();
		int left = ((width - xSize) / 2);
		int top = (height - ySize) / 2;
		//id, x, y, u, v, width, height, texture 224
		GuiButtonCustomTexture buttonUp = new GuiButtonCustomTexture(1, left+236, top+4, 0, 224, 16, 16, texture);
		//buttonUp.visible = false;
		this.buttonList.add(buttonUp);
		this.buttonList.add(new GuiButtonCustomTexture(2, left+236, top+104, 0, 240, 16, 16, texture));
		//.buttonList.add(new GuiButtonLink(3, left+10, top+10, "Click me"));
	}
	
	protected void actionPerformed(GuiButton guibutton) {
        //id is the id you give your button
		GuiContainerBookOfRevealing container = ((GuiContainerBookOfRevealing) this.inventorySlots);
        switch(guibutton.id) {
        case 1:
        	container.topLine -= 1;
            if(container.topLine < 0) {
            	container.topLine = 0;
            }
            this.refreshGuide();
            break;
        case 2:
        	container.topLine += 1;
        	this.refreshGuide();
            break;
//        case 3:
//        	System.out.println("HELLO!");
//        	break;
        }
        //Packet code here
        //PacketDispatcher.sendPacketToServer(packet); //send packet
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		
		refreshGuideReal();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texture);
		int left = (width - xSize)  / 2;
		int top  = (height - ySize) / 2;
//		j = j - 1 + 1;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		
		if(this.lines == null) {
			
		}
		
		boolean[] hovering;
		if(this.links != null && this.linkRects != null) {
			
			hovering = new boolean[this.links.length];
			for(IndexedRect rect : this.linkRects) {
				if(rect.pointInside(
						((int)mouseX)-left,
						((int)mouseY)- top + (fontRendererObj.FONT_HEIGHT*this.topLine)
						)) {
					hovering[rect.id] = true;
				}
			}
		} else {
			hovering = new boolean[1]; // just to prevent errors
		}
		
		int lineNum = 0;
		int toSkip = this.topLine;
		boolean isLink = false;
		int linkNum = 0;
		
		for(String line: lines) {
			
			String[] lineParts = line.split(seperator);
			if(toSkip > 0) {
				toSkip--;
				linkNum += (int)Math.floor(lineParts.length/2.0);
				continue;
			}
			
			int curX=textLeft;
			boolean linkLast = false;
			for(String linePart: lineParts) {
				linkLast = false;
				int color = 0x404040;
				if(isLink) {
					color = 0x0000CC;
					if(hovering[linkNum]) {
						color = 0x550055;
						this.drawHorizontalLine(curX, textTop+(fontRendererObj.FONT_HEIGHT*(lineNum+1))-2, fontRendererObj.getStringWidth(linePart), color);
					}
					linkNum = linkNum+1;
					linkLast = true;
				}
				curX = fontRendererObj.drawString(linePart, curX, textTop+(fontRendererObj.FONT_HEIGHT*(lineNum)), color);
				isLink = !isLink;
			}
			if(linkLast) {
				linkNum--;
			}
			isLink = linkLast;
			lineNum++;
			if(lineNum > lineCount) break;
		}
		
		//drawString(fontRendererObj, , (width - ), top+5, 0x404040);
		
	}
	
	public void drawScreen(int par1, int par2, float par3)
	{
		this.mouseX = (float)par1;
		this.mouseY = (float)par2;
		super.drawScreen(par1, par2, par3);
	}
	
}
