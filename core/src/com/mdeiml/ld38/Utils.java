package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Utils {

    public static void drawNumber(int number, float x, float y, SpriteBatch batch, TextureRegion[] digits) {
        x += (int)Math.log10(number) * 4;
        do {
            int digit = number % 10;
            batch.draw(digits[digit], x, y);
            x -= 4;
            number /= 10;
        }while (number > 0);
    }

}
