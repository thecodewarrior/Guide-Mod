package com.thecodewarrior.guides.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.thecodewarrior.guides.GuideMod;
import com.thecodewarrior.guides.gui.GuiBookOfRevealing;
import com.thecodewarrior.guides.guides.Guide;
import com.thecodewarrior.guides.guides.GuideText;
import com.thecodewarrior.guides.views.View;
import com.thecodewarrior.guides.views.ViewGuide;
import com.thecodewarrior.guides.views.ViewNull;

import cpw.mods.fml.common.registry.GameRegistry;


public class GuideRegistry {
	
	private static HashMap<String, GuideGenerator> blocksID     = new HashMap<String, GuideGenerator>();
	private static HashMap<String, GuideGenerator> blocksIDMeta = new HashMap<String, GuideGenerator>();
	private static HashMap<IBlockMatcher, GuideGenerator> blocksCustom = new HashMap<IBlockMatcher, GuideGenerator>();

	private static HashMap<String, GuideGenerator> itemsID     = new HashMap<String, GuideGenerator>();
	private static HashMap<String, GuideGenerator> itemsIDMeta = new HashMap<String, GuideGenerator>();
	private static HashMap<IItemMatcher, GuideGenerator> itemsCustom = new HashMap<IItemMatcher, GuideGenerator>();
	
	private static HashMap<String, View> views = new HashMap<String, View>();
	
	public static GuideGenerator findItemGuide(ItemStack stack) {
		if(stack == null) {
			return GuideRegistry.NULL_GUIDE;
		}
		
		GuideGenerator guide = null;
		
		guide = GuideRegistry.findItemByCustom(stack);
		if(guide != null) {
			return guide;
		}
		
		GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(stack.getItem());
		
		guide = GuideRegistry.findItemByIDMeta(id.modId + ":" + id.name, stack.getItemDamage());
		if(guide != null) {
			return guide;
		}
		
		guide = GuideRegistry.findItemByID(id.modId + ":" + id.name);
		if(guide != null) {
			return guide;
		}
		
		return GuideRegistry.NOTFOUND_GUIDE;
	}
	
	private static GuideGenerator findItemByCustom(ItemStack stack) {		
		for(Map.Entry<IItemMatcher, GuideGenerator> entry : GuideRegistry.itemsCustom.entrySet()) {
			if(entry.getKey().matches(stack)) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	private static GuideGenerator findItemByIDMeta(String id, int damage) {
		String fullID = id + "." + damage;
		if(GuideRegistry.itemsIDMeta.containsKey(fullID)) {
			return GuideRegistry.itemsIDMeta.get(fullID);
		}
		
		return null;
	}
	
	private static GuideGenerator findItemByID(String id) {
		if(GuideRegistry.itemsID.containsKey(id)) {
			return GuideRegistry.itemsID.get(id);
		}
		return null;
	}

	
	public static GuideGenerator findBlockGuide(ItemStack stack) {
		if(stack == null) {
			return GuideRegistry.NULL_GUIDE;
		}
		if(!( stack.getItem() instanceof ItemBlock )) {
			return GuideRegistry.NOTBLOCK_GUIDE;
		}
		
		Block block = ( (ItemBlock) stack.getItem() ).field_150939_a;
		
		GuideGenerator guide = null;
		
		guide = GuideRegistry.findBlockByCustom(block);
		if(guide != null) {
			return guide;
		}
		
		GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(block);
		
		guide = GuideRegistry.findBlockByIDMeta(id.modId + ":" + id.name, stack.getItemDamage());
		if(guide != null) {
			return guide;
		}
		
		HashMap<String, GuideGenerator> gen = GuideRegistry.blocksID;
		
		guide = GuideRegistry.findBlockByID(id.modId + ":" + id.name);
		if(guide != null) {
			return guide;
		}
		
		return GuideRegistry.NOTFOUND_GUIDE;
	}
	
	public static GuideGenerator findBlockGuide(World w, int x, int y, int z) {
		for(Map.Entry<IBlockMatcher, GuideGenerator> entry : GuideRegistry.blocksCustom.entrySet()) {
			if(entry.getKey().matches(w,x,y,z)) {
				return entry.getValue();
			}
		}
		return GuideRegistry.findBlockGuide(new ItemStack(w.getBlock(x, y, z)));
	}
	
	private static GuideGenerator findBlockByCustom(Block block) {
		for(Map.Entry<IBlockMatcher, GuideGenerator> entry : GuideRegistry.blocksCustom.entrySet()) {
			if(entry.getKey().matches(block)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private static GuideGenerator findBlockByIDMeta(String id, int meta) {
		String fullID = id + "." + meta;
		if(GuideRegistry.blocksIDMeta.containsKey(fullID)) {
			return GuideRegistry.blocksIDMeta.get(fullID);
		}
		
		return null;
	}

	private static GuideGenerator findBlockByID(String id) {
		if(GuideRegistry.blocksID.containsKey(id)) {
			return GuideRegistry.blocksID.get(id);
		}
		
		return null;
	}
	
	public static View findView(String name) {
		if(GuideRegistry.views.containsKey(name)) {
			return GuideRegistry.views.get(name);
		}
		return null;
	}

	
	
	public static void registerBlockGuideByCustom(IBlockMatcher matcher, GuideGenerator guide) {
		GuideRegistry.blocksCustom.put(matcher, guide);
	}
	
	public static void registerBlockGuideByIDMeta(String id, int meta, GuideGenerator guide) {
		GuideRegistry.blocksIDMeta.put(id+"."+meta, guide);
	}
	
	public static void registerBlockGuideByID(String id, GuideGenerator guide) {
		GuideRegistry.blocksID.put(id, guide);
	}

	public static void registerItemGuideByCustom(IItemMatcher matcher, GuideGenerator guide) {
		GuideRegistry.itemsCustom.put(matcher, guide);
	}
	
	public static void registerItemGuideByIDMeta(String id, int damage, GuideGenerator guide) {
		GuideRegistry.itemsIDMeta.put(id+"."+damage, guide);
	}
	
	public static void registerItemGuideByID(String id, GuideGenerator guide) {
		GuideRegistry.itemsID.put(id, guide);
	}
	
	// Default Guide Generators
	
	public static GuideGenerator newBasicGuide(String name) {
		return new GuideGeneratorBasic(name);
	}
	
	public static GuideGenerator NULL_GUIDE = new GuideGenerator() {

		@Override
		public View generate(int width, int height, GuiBookOfRevealing gui) {
			return new ViewNull(width,height,gui);
		}
		
	};
	
	public static GuideGenerator NOTFOUND_GUIDE = new GuideGeneratorError("guide.text.error.notfound", "Guide not found");
	public static GuideGenerator NOTBLOCK_GUIDE = new GuideGeneratorError("guide.text.error.notblock", "Not a block");
	
	public static ResourceLocation guideLoc(String name) {
		return new ResourceLocation( name.split(":")[0], "guides/" + GuideMod.proxy.getLang() + "/" + name.split(":")[1] + ".txt" );
	}
	// GuideGenerator classes
	
		public static class GuideGeneratorError extends GuideGenerator {
	
			public String name;
			public String text;
			
			public GuideGeneratorError(String name, String text) {
				this.name = name;
				this.text = text;
			}
			
			@Override
			public View generate(int width, int height, GuiBookOfRevealing gui) {
				return new ViewGuide(new ErrorGuide(this.name, this.text), width, height, gui);
			}
			
		}
		
		public static class GuideGeneratorBasic extends GuideGenerator {
	
			public String guideName;
			
			public GuideGeneratorBasic(String guideName) {
				this.guideName = guideName;
			}
			
			@Override
			public View generate(int width, int height, GuiBookOfRevealing gui) {
				
				return new ViewGuide(new GuideText( GuideMod.proxy.getFileText(GuideRegistry.guideLoc(guideName))),
						width, height, gui);
			}
			
		}
		
		public static abstract class GuideGeneratorView extends GuideGenerator {
	
			protected Guide guide;
			
			public GuideGeneratorView(Guide guide) {
				this.guide = guide;
			}
			
			@Override
			public abstract View generate(int width, int height, GuiBookOfRevealing gui);
			
		}
		
		public static class GuideGeneratorViewGuide extends GuideGeneratorView {
	
			public GuideGeneratorViewGuide(Guide guide) {
				super(guide);
			}
	
			@Override
			public View generate(int width, int height, GuiBookOfRevealing gui) {
				return new ViewGuide(this.guide, width, height, gui);
			}
			
		}
	// end GuideGenerator Classes
}
