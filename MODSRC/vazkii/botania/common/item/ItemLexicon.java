/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License
 * (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 * 
 * File Created @ [Jan 14, 2014, 5:53:00 PM (GMT)]
 */
package vazkii.botania.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.ILexicon;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.KnowledgeType;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.recipe.IElvenItem;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.core.helper.MathHelper;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibGuiIDs;
import vazkii.botania.common.lib.LibItemNames;
import vazkii.botania.common.lib.LibMisc;

public class ItemLexicon extends ItemMod implements ILexicon, IElvenItem {

	private static final String TAG_KNOWLEDGE_PREFIX = "knowledge.";
	private static final String TAG_FORCED_MESSAGE = "forcedMessage";

	public ItemLexicon() {
		super();
		setMaxStackSize(1);
		setUnlocalizedName(LibItemNames.LEXICON);
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		if(par2EntityPlayer.isSneaking()) {
			Block block = par3World.getBlock(par4, par5, par6);

			if(block != null) {
				if(block instanceof ILexiconable) {
					LexiconEntry entry = ((ILexiconable) block).getEntry(par3World, par4, par5, par6, par2EntityPlayer, par1ItemStack);
					if(entry != null) {
						Botania.proxy.setEntryToOpen(entry);
						par2EntityPlayer.openGui(Botania.instance, LibGuiIDs.LEXICON, par3World, 0, 0, 0);
						if(!par3World.isRemote)
							par3World.playSoundAtEntity(par2EntityPlayer, "botania:lexiconOpen", 0.5F, 1F);
						return true;
					}
				} else if(par3World.isRemote) {
					MovingObjectPosition pos = new MovingObjectPosition(par4, par5, par6, par7, Vec3.createVectorHelper(par8, par9, par10));
					return Botania.proxy.openWikiPage(par3World, block, pos);
				}
			}
		}

		return false;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item));
		ItemStack creative = new ItemStack(item);
		for(String s : BotaniaAPI.knowledgeTypes.keySet()) {
			KnowledgeType type = BotaniaAPI.knowledgeTypes.get(s);
			unlockKnowledge(creative, type);
		}
		list.add(creative);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if(GuiScreen.isShiftKeyDown()) {
			String edition = EnumChatFormatting.GOLD + String.format(StatCollector.translateToLocal("botaniamisc.edition"), getEdition());
			if(!edition.isEmpty())
				par3List.add(edition);

			List<KnowledgeType> typesKnown = new ArrayList();
			for(String s : BotaniaAPI.knowledgeTypes.keySet()) {
				KnowledgeType type = BotaniaAPI.knowledgeTypes.get(s);
				if(isKnowledgeUnlocked(par1ItemStack, type))
					typesKnown.add(type);
			}

			String format = typesKnown.size() == 1 ? "botaniamisc.knowledgeTypesSingular" : "botaniamisc.knowledgeTypesPlural";
			addStringToTooltip(String.format(StatCollector.translateToLocal(format), typesKnown.size()), par3List);

			for(KnowledgeType type : typesKnown)
				addStringToTooltip(" \u2022 " + StatCollector.translateToLocal(type.getUnlocalizedName()), par3List);

		} else addStringToTooltip(StatCollector.translateToLocal("botaniamisc.shiftinfo"), par3List);
	}

	private void addStringToTooltip(String s, List<String> tooltip) {
		tooltip.add(s.replaceAll("&", "\u00a7"));
	}

	public static String getEdition() {
		String version = LibMisc.BUILD;
		int build = version.contains("ANT") ? 0 : Integer.parseInt(version);
		return build == 0 ? StatCollector.translateToLocal("botaniamisc.devEdition") : MathHelper.numberToOrdinal(build);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if(isMessageForced(par1ItemStack)) {
			Botania.proxy.setEntryToOpen(LexiconData.elvenMessage);
			forceMessage(par1ItemStack, false);
		}
		par3EntityPlayer.openGui(Botania.instance, LibGuiIDs.LEXICON, par2World, 0, 0, 0);
		if(!par2World.isRemote)
			par2World.playSoundAtEntity(par3EntityPlayer, "botania:lexiconOpen", 0.5F, 1F);
		return par1ItemStack;
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.uncommon;
	}

	@Override
	public boolean isKnowledgeUnlocked(ItemStack stack, KnowledgeType knowledge) {
		return knowledge.autoUnlock || ItemNBTHelper.getBoolean(stack, TAG_KNOWLEDGE_PREFIX + knowledge.id, false);
	}

	@Override
	public void unlockKnowledge(ItemStack stack, KnowledgeType knowledge) {
		ItemNBTHelper.setBoolean(stack, TAG_KNOWLEDGE_PREFIX + knowledge.id, true);
	}

	public static void forceMessage(ItemStack stack, boolean forced) {
		ItemNBTHelper.setBoolean(stack, TAG_FORCED_MESSAGE, forced);
	}

	public static boolean isMessageForced(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_FORCED_MESSAGE, false);
	}

	@Override
	public boolean isElvenItem(ItemStack stack) {
		return isKnowledgeUnlocked(stack, BotaniaAPI.elvenKnowledge);
	}

}
