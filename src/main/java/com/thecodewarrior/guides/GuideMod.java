package com.thecodewarrior.guides;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.thecodewarrior.guides.api.GuideProvider;
import com.thecodewarrior.guides.api.GuideRegistry;
import com.thecodewarrior.guides.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid=Reference.MODID, version=Reference.VERSION)
public class GuideMod {
	
	@Instance(Reference.MODID)
	public static GuideMod instance;
	
	public static Item bookOfRevealing;
	
	@SidedProxy(clientSide="com.thecodewarrior.guides.proxy.ClientProxy", serverSide="com.thecodewarrior.guides.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		bookOfRevealing = new BookOfRevealing();
		GameRegistry.registerItem(bookOfRevealing, bookOfRevealing.getUnlocalizedName().substring(5));
		
		GuideRegistry.registerBlockGuide(BlockGrass.class, "minecraft:grass");
		GuideRegistry.registerBlockGuide(BlockStone.class, "minecraft:stone");
		GuideProvider waterGuide = new GuideProvider(){
			public String getBlockGuide(ItemStack stack) {
				if(stack.getItem() instanceof ItemBlock){
					Block blk = ( (ItemBlock) stack.getItem() ).field_150939_a;
					if(blk.getMaterial() == Material.lava) {
						return "minecraft:lava";
					} else {
						return "minecraft:water";
					}
				}
				return "";
			}
		};
		GuideRegistry.registerBlockGuide(BlockStaticLiquid.class, waterGuide);
		GuideRegistry.registerBlockGuide(BlockDynamicLiquid.class, waterGuide);
		//GuideRegistry.registerBlockGuide(BlockGrass.class, "minecraft:grass");
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.registerProxies();
		proxy.registerEvents();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
	}
}
