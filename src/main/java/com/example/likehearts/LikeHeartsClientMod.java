package com.example.likehearts;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * LikeHearts (клиентский мод).
 *
 *   /like <ник> <0-3>   1 — красное, 2 — жёлтое, 3 — фиолетовое, 0 — убрать сердечко
 *   /like list          показать всех, кому выданы сердечки
 *
 * Сердечко рисуется над ником игрока (отдельной строкой неймплейта) и слева от ника в табе.
 * Видите его только вы — на сервере ничего не меняется.
 */
public class LikeHeartsClientMod implements ClientModInitializer {

    public static final String MOD_ID = "likehearts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final String[] NAMES = {"нет", "красное", "жёлтое", "фиолетовое"};

    @Override
    public void onInitializeClient() {
        HeartStore.load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) ->
            dispatcher.register(ClientCommands.literal("like")
                .then(ClientCommands.literal("list").executes(ctx -> {
                    Map<String, Integer> all = HeartStore.all();
                    if (all.isEmpty()) {
                        ctx.getSource().sendFeedback(Component.literal("Сердечки пока никому не выданы"));
                        return 1;
                    }
                    StringBuilder sb = new StringBuilder("Сердечки:");
                    for (Map.Entry<String, Integer> e : all.entrySet()) {
                        sb.append("\n  ").append(e.getKey()).append(" — ").append(NAMES[e.getValue()]);
                    }
                    ctx.getSource().sendFeedback(Component.literal(sb.toString()));
                    return 1;
                }))
                .then(ClientCommands.argument("nick", StringArgumentType.word())
                    .suggests((ctx, builder) ->
                            SharedSuggestionProvider.suggest(ctx.getSource().getOnlinePlayerNames(), builder))
                    .then(ClientCommands.argument("heart", IntegerArgumentType.integer(0, 3))
                        .executes(ctx -> {
                            String nick = StringArgumentType.getString(ctx, "nick");
                            int type = IntegerArgumentType.getInteger(ctx, "heart");
                            HeartStore.set(nick, type);
                            if (type == 0) {
                                ctx.getSource().sendFeedback(Component.literal("Сердечко у игрока " + nick + " убрано"));
                            } else {
                                ctx.getSource().sendFeedback(Component.literal(
                                        "Игроку " + nick + " назначено сердечко: " + NAMES[type]));
                            }
                            return 1;
                        })))
            )
        );

        LOGGER.info("LikeHearts загружен");
    }
}
