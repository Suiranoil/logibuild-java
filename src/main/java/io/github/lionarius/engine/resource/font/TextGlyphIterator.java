package io.github.lionarius.engine.resource.font;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class TextGlyphIterator implements Iterator<Glyph> {
    private final Font font;
    private final String text;
    private int index = 0;

    @Override
    public boolean hasNext() {
        return this.index < this.text.length();
    }

    @Override
    public Glyph next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        var codePoint = this.text.codePointAt(this.index);
        var glyph = this.font.getGlyph(codePoint);
        this.index += 1;

        if (glyph == null)
            glyph = this.font.getGlyph('?');

        return glyph;
    }
}
