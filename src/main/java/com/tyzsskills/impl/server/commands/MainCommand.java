package com.tyzsskills.impl.server.commands;

import net.minecraft.server.permissions.Permissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class MainCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("skills")
                .then(XpCommand.xp().requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_ADMIN)))
                .then(LevelCommand.level().requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_ADMIN)))
                .then(SpCommand.sp().requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_ADMIN)))
                .then(SkillCommand.skill().requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_ADMIN)))
                .then(ReloadCommand.reload().requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_ADMIN)))
                .then(ResetCommand.reset().requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_ADMIN)))
                .then(PeekCommand.peek().requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_ADMIN)))
                .then(DebugCommands.debug());
    }
}
