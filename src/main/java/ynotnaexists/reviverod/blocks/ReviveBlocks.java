package ynotnaexists.reviverod.blocks;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import ynotnaexists.reviverod.ReviveRod;

public class ReviveBlocks {

    public static final Block REVIVE_ROD = registerBlock("revive_rod", new ReviveRodBlock(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ReviveRod.MOD_ID, "revive_rod")))
            .mapColor(MapColor.DARK_AQUA)
            .requiresTool()
            .strength(3.0F, 6.0F)
            .sounds(BlockSoundGroup.COPPER)
            .nonOpaque()
    ));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(ReviveRod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(ReviveRod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ReviveRod.MOD_ID, name))).useBlockPrefixedTranslationKey()));
    }

    public static void registerClass() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ReviveBlocks.REVIVE_ROD);
        });
    }
}
