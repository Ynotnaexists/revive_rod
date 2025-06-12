package ynotnaexists.reviverod;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ReviveSoundEffects {

    public static final RegistryEntry<SoundEvent> REVIVE_ROD_ACTIVATES = registerSoundEvent("block.revive_rod.activate");

    private static RegistryEntry.Reference<SoundEvent> registerSoundEvent(String name) {
        Identifier id = Identifier.of(ReviveRod.MOD_ID, name);
        return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerClass() {}
}
