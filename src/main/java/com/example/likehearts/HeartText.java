package com.example.likehearts;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

/** Собирает текстовый компонент с символом сердечка из нашего шрифта (картинки heart_1..3.png). */
public final class HeartText {

    private static final Identifier NAMEPLATE_FONT = Identifier.fromNamespaceAndPath(LikeHeartsClientMod.MOD_ID, "hearts");
    private static final Identifier TAB_FONT = Identifier.fromNamespaceAndPath(LikeHeartsClientMod.MOD_ID, "tab");

    private HeartText() {
    }

    /** Крупное сердечко для строки над ником. */
    public static MutableComponent nameplate(int type) {
        return glyph(type, NAMEPLATE_FONT);
    }

    /** Мелкое сердечко для списка игроков (таба). */
    public static MutableComponent tab(int type) {
        return glyph(type, TAB_FONT);
    }

    private static MutableComponent glyph(int type, Identifier font) {
        char symbol = (char) (0xE000 + type); // 1 -> \uE001, 2 -> \uE002, 3 -> \uE003
        return Component.literal(String.valueOf(symbol))
                .setStyle(Style.EMPTY.withFont(new FontDescription.Resource(font)));
    }
}
