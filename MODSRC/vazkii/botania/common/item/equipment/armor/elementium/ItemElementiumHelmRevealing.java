/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License
 * (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 * 
 * File Created @ [Jul 28, 2014, 6:16:24 PM (GMT)]
 */
package vazkii.botania.common.item.equipment.armor.elementium;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.lib.LibItemNames;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList({
	@Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.IGoggles", striprefs = true),
	@Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.nodes.IRevealer", striprefs = true)})
public class ItemElementiumHelmRevealing extends ItemElementiumHelm implements IGoggles, IRevealer {

	public ItemElementiumHelmRevealing() {
		super(LibItemNames.ELEMENTIUM_HELM_R);
	}

	@Override
	public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return LibResources.MODEL_ELEMENTIUM_2;
	}

}
