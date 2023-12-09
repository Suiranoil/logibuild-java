package io.github.lionarius.engine.resource.font;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.Iterator;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class TextGlyphIterator implements Iterator<TextGlyphIterator.NextGlyph> {
    private final Font font;
    private final String text;
    private int index = 0;
    private int lineCharacters = 0;
    private int prevCodePoint = -1;
    private final Vector2f cursor = new Vector2f(0);
    private final Vector2f kerning = new Vector2f(0);

    @Override
    public boolean hasNext() {
        return this.index < this.text.length();
    }

    @Override
    public NextGlyph next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        var codePoint = this.text.codePointAt(this.index);
        if (this.isLineBreak(codePoint)) {
            this.index += 1;
            this.newLine();
            return null;
        }
        if (this.isTab(codePoint)) {
            this.index += 1;
            this.tab();
            return null;
        }

        var glyph = this.font.getGlyph(codePoint);

        if (glyph == null) {
            glyph = this.font.getGlyph('?');

            if (glyph == null)
                return null;
        }


        if (this.prevCodePoint >= 0) {
            var kerning = this.font.getKerning().get(Pair.with(this.prevCodePoint, codePoint));
            if (kerning != null)
                this.kerning.add(kerning.advance(), 0);
        }


        NextGlyph next = null;
        if (glyph.getPositions() != null)
            next = new NextGlyph(glyph.getPositions(), glyph.getUvs(), new Vector2f(this.cursor).add(this.kerning));

        this.cursor.add(glyph.getAdvance(), 0);
        this.prevCodePoint = codePoint;
        this.lineCharacters += 1;
        this.index += 1;

        return next;
    }

    private boolean isTab(int codePoint) {
        return codePoint == 0x0009;
    }

    private void tab() {
        var spaceGlyph = this.font.getGlyph(' ');
        if (spaceGlyph == null)
            return;
        var missing = 4 - (this.lineCharacters % 4);
        this.cursor.add(missing * spaceGlyph.getAdvance(), 0);
    }

    private boolean isLineBreak(int codePoint) {
        return codePoint == 0x000A;
    }

    private void newLine() {
        var y = this.cursor.y();

        this.kerning.set(0, 0);
        this.cursor.set(0, y + this.font.getMetrics().lineHeight());
        this.lineCharacters = 0;
        this.prevCodePoint = -1;
    }

    public record NextGlyph(@NonNull Vector2fc[] positions, @NonNull Vector2fc[] uvs, @NonNull Vector2fc offset) {
    }
}
