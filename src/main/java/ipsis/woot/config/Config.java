package ipsis.woot.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import ipsis.woot.commands.CommandConfiguration;
import ipsis.woot.modules.anvil.AnvilConfiguration;
import ipsis.woot.modules.factory.FactoryConfiguration;
import ipsis.woot.modules.fluidconvertor.FluidConvertorConfiguration;
import ipsis.woot.modules.infuser.InfuserConfiguration;
import ipsis.woot.modules.layout.LayoutConfiguration;
import ipsis.woot.simulator.MobSimulatorConfiguration;
import ipsis.woot.modules.squeezer.SqueezerConfiguration;
import ipsis.woot.policy.PolicyConfiguration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


import java.nio.file.Path;

public class Config {

    public static ConfigOverride OVERRIDE = new ConfigOverride();

    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    static {
        setupGeneralConfig();
        CommandConfiguration.init(COMMON_BUILDER, CLIENT_BUILDER);
        AnvilConfiguration.init(COMMON_BUILDER, CLIENT_BUILDER);
        FactoryConfiguration.init(COMMON_BUILDER, CLIENT_BUILDER);
        InfuserConfiguration.init(COMMON_BUILDER, CLIENT_BUILDER);
        FluidConvertorConfiguration.init(COMMON_BUILDER, CLIENT_BUILDER);
        LayoutConfiguration.init(COMMON_BUILDER, CLIENT_BUILDER);
        MobSimulatorConfiguration.init(COMMON_BUILDER, CLIENT_BUILDER);
        SqueezerConfiguration.init(COMMON_BUILDER, CLIENT_BUILDER);
        PolicyConfiguration.init(COMMON_BUILDER, CLIENT_BUILDER);

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();

    }

    private static void setupGeneralConfig() {
        COMMON_BUILDER.comment("General settings").push("general");
        CLIENT_BUILDER.comment("General settings").push("general");
        {
        }
        CLIENT_BUILDER.pop();
        COMMON_BUILDER.pop();
    }

    public static void register(ModConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();;
        spec.correct(configData);
    }


}
