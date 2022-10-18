package xyz.wagyourtail.jsmacros.client.api.render.shared.classes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.util.math.MatrixStack;

import xyz.wagyourtail.jsmacros.client.api.render.shared.interfaces.IDraw2D;

import java.util.Locale;

/**
 * @author Wagyourtail
 * @since 1.2.3
 */
@SuppressWarnings("unused")
public class RenderCommon {

    /**
     * @author Wagyourtail
     */
    public interface RenderElement extends Drawable {

        MinecraftClient mc = MinecraftClient.getInstance();

        int getZIndex();

        default void render3D(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            render(matrices, mouseX, mouseY, delta);
        }
    }

    /**
     * @param <T> the type of the render element for this builder
     * @author Etheradon
     * @since 1.8.4
     */
    public abstract static class RenderElementBuilder<T extends RenderElement> {

        protected final IDraw2D<?> parent;

        protected RenderElementBuilder(IDraw2D<?> parent) {
            this.parent = parent;
        }

        /**
         * @return the newly created element.
         *
         * @since 1.8.4
         */
        public T build() {
            return createElement();
        }

        /**
         * Builds and adds the element to the draw2D the builder was created from.
         *
         * @return the newly created element.
         *
         * @since 1.8.4
         */
        public T buildAndAdd() {
            T element = createElement();
            parent.reAddElement(element);
            return element;
        }

        protected abstract T createElement();

    }

    /**
     * @param <B> the builder class
     * @since 1.8.4
     */
    public interface Alignable<B extends Alignable<B>> {

        /**
         * @param other     the element to align to
         * @param alignment the alignment to use
         * @return self for chaining.
         *
         * @see #alignHorizontally(Alignable, String, int)
         * @since 1.8.4
         */
        default B alignHorizontally(Alignable<?> other, String alignment) {
            return alignHorizontally(other, alignment, 0);
        }

        /**
         * The alignment must be of the format
         * {@code [left|center|right|x%]On[left|center|right|x%]}. The input is case-insensitive.
         * The first alignment is for the element this method is called on and the second is for the
         * other element. As an example, {@code LeftOnCenter} would align the left side of this
         * element to the center of the other element.
         *
         * @param other     the element to align to
         * @param alignment the alignment to use
         * @param offset    the offset to use
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B alignHorizontally(Alignable<?> other, String alignment, int offset) {
            String[] alignments = alignment.toLowerCase(Locale.ROOT).split("on");
            String thisAlignment = alignments[0];
            String toAlignment = alignments[1];
            int alignToX = switch (toAlignment) {
                case "left" -> other.getScaledLeft();
                case "center" -> other.getScaledLeft() + other.getScaledWidth() / 2;
                case "right" -> other.getScaledRight();
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        yield other.getScaledLeft() + (other.getScaledWidth() * percent / 100);
                    }
                    throw new IllegalArgumentException("Invalid alignment: " + alignment);
                }
            };
            switch (thisAlignment) {
                case "left" -> moveToX(alignToX + offset);
                case "center" -> moveToX(alignToX - getScaledWidth() / 2 + offset);
                case "right" -> moveToX(alignToX - getScaledWidth() + offset);
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        moveToX(alignToX - (getScaledWidth() * percent / 100) + offset);
                    }
                    throw new IllegalArgumentException("Invalid alignment: " + alignment);
                }
            }
            return (B) this;
        }

        /**
         * @param alignment the alignment to use
         * @return self for chaining.
         *
         * @see #alignHorizontally(String, int)
         * @since 1.8.4
         */
        default B alignHorizontally(String alignment) {
            return alignHorizontally(alignment, 0);
        }

        /**
         * Possible alignments are {@code left}, {@code center}, {@code right} or {@code y%} where y
         * is a number between 0 and 100.
         *
         * @param alignment the alignment to use
         * @param offset    the offset to use
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B alignHorizontally(String alignment, int offset) {
            int parentWidth = getParentWidth();
            int width = getScaledWidth();

            switch (alignment.toLowerCase(Locale.ROOT)) {
                case "left" -> moveToX(offset);
                case "center" -> moveToX((parentWidth - width) / 2 + offset);
                case "right" -> moveToX(parentWidth - width + offset);
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        moveToX((parentWidth - width) * percent / 100 + offset);
                    }
                }
            }
            return (B) this;
        }

        /**
         * @param other     the element to align to
         * @param alignment the alignment to use
         * @return self for chaining.
         *
         * @see #alignVertically(Alignable, String, int)
         * @since 1.8.4
         */
        default B alignVertically(Alignable<?> other, String alignment) {
            return alignVertically(other, alignment, 0);
        }

        /**
         * The alignment must be of the format
         * {@code [top|center|bottom|y%]On[top|center|bottom|y%]}. The input is case-insensitive.
         * The first alignment is for the element this method is called on and the second is for the
         * other element. As an example, {@code BottomOnTop} would align the bottom side of this
         * element to the top of the other element. Thus, the element would be placed above the
         * other one.
         *
         * @param other     the element to align to
         * @param alignment the alignment to use
         * @param offset    the offset to use
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B alignVertically(Alignable<?> other, String alignment, int offset) {
            String[] alignments = alignment.toLowerCase(Locale.ROOT).split("on");
            String thisAlignment = alignments[0];
            String toAlignment = alignments[1];
            int alignToY = switch (toAlignment) {
                case "top" -> other.getScaledTop();
                case "center" -> other.getScaledTop() + other.getScaledHeight() / 2;
                case "bottom" -> other.getScaledBottom();
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        yield other.getScaledTop() + (other.getScaledHeight() * percent / 100);
                    }
                    throw new IllegalArgumentException("Invalid alignment: " + alignment);
                }
            };
            switch (thisAlignment) {
                case "top" -> moveToY(alignToY + offset);
                case "center" -> moveToY(alignToY - getScaledHeight() / 2 + offset);
                case "bottom" -> moveToY(alignToY - getScaledHeight() + offset);
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        moveToY(alignToY - (getScaledHeight() * percent / 100) + offset);
                    }
                    throw new IllegalArgumentException("Invalid alignment: " + alignment);
                }
            }
            return (B) this;
        }

        /**
         * @param alignment the alignment to use
         * @return self for chaining.
         *
         * @see #alignVertically(String, int)
         * @since 1.8.4
         */
        default B alignVertically(String alignment) {
            return alignVertically(alignment, 0);
        }

        /**
         * Possible alignments are {@code top}, {@code center}, {@code bottom} or {@code x%} where x
         * is a number between 0 and 100.
         *
         * @param alignment the alignment to use
         * @param offset    the offset to use
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B alignVertically(String alignment, int offset) {
            int parentHeight = getParentHeight();
            int height = getScaledHeight();

            switch (alignment.toLowerCase(Locale.ROOT)) {
                case "top" -> moveToY(offset);
                case "center" -> moveToY((parentHeight - height) / 2 + offset);
                case "bottom" -> moveToY(parentHeight - height + offset);
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        moveToY((parentHeight - height) * percent / 100 + offset);
                    }
                }
            }
            return (B) this;
        }

        /**
         * @param horizontal the horizontal alignment to use
         * @param vertical   the vertical alignment to use
         * @return self for chaining.
         *
         * @see #align(String, int, String, int)
         * @since 1.8.4
         */
        default B align(String horizontal, String vertical) {
            return align(horizontal, 0, vertical, 0);
        }

        /**
         * @param horizontal       the horizontal alignment to use
         * @param horizontalOffset the horizontal offset to use
         * @param vertical         the vertical alignment to use
         * @param verticalOffset   the vertical offset to use
         * @return self for chaining.
         *
         * @see #alignHorizontally(String, int)
         * @see #alignVertically(String, int)
         * @since 1.8.4
         */
        default B align(String horizontal, int horizontalOffset, String vertical, int verticalOffset) {
            return alignHorizontally(horizontal, horizontalOffset).alignVertically(vertical, verticalOffset);
        }

        /**
         * @param other      the element to align to
         * @param horizontal the horizontal alignment to use
         * @param vertical   the vertical alignment to use
         * @return self for chaining.
         *
         * @see #align(Alignable, String, int, String, int)
         * @since 1.8.4
         */
        default B align(Alignable<?> other, String horizontal, String vertical) {
            return align(other, horizontal, 0, vertical, 0);
        }

        /**
         * @param other            the element to align to
         * @param horizontal       the horizontal alignment to use
         * @param horizontalOffset the horizontal offset to use
         * @param vertical         the vertical alignment to use
         * @param verticalOffset   the vertical offset to use
         * @return self for chaining.
         *
         * @see #alignHorizontally(Alignable, String, int)
         * @see #alignVertically(Alignable, String, int)
         * @since 1.8.4
         */
        default B align(Alignable<?> other, String horizontal, int horizontalOffset, String vertical, int verticalOffset) {
            return alignHorizontally(other, horizontal, horizontalOffset).alignVertically(other, vertical, verticalOffset);
        }

        /**
         * @param x the new x position
         * @param y the new y position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        B moveTo(int x, int y);

        /**
         * @param x the new x position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B moveToX(int x) {
            return moveTo(x, getScaledTop());
        }

        /**
         * @param y the new y position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B moveToY(int y) {
            return moveTo(getScaledLeft(), y);
        }

        /**
         * @return the scaled width of the element.
         *
         * @since 1.8.4
         */
        int getScaledWidth();

        /**
         * @return the width of the parent element.
         *
         * @since 1.8.4
         */
        int getParentWidth();

        /**
         * @return the scaled height of the element.
         *
         * @since 1.8.4
         */
        int getScaledHeight();

        /**
         * @return the height of the parent element.
         *
         * @since 1.8.4
         */
        int getParentHeight();

        /**
         * @return the position of the scaled element's left side.
         *
         * @since 1.8.4
         */
        int getScaledLeft();

        /**
         * @return the position of the scaled element's top side.
         *
         * @since 1.8.4
         */
        int getScaledTop();

        /**
         * @return the position of the scaled element's right side.
         *
         * @since 1.8.4
         */
        default int getScaledRight() {
            return getScaledLeft() + getScaledWidth();
        }

        /**
         * @return the position of the scaled element's bottom side.
         *
         * @since 1.8.4
         */
        default int getScaledBottom() {
            return getScaledTop() + getScaledHeight();
        }

        /**
         * Parse the string containing a percentage of the form {@code x%} and return its value.
         *
         * @param string the string to parse
         * @return the percentage or {@code -1} if the string is not a valid percentage.
         *
         * @since 1.8.4
         */
        private static int parsePercentage(String string) {
            if (string.endsWith("%")) {
                int percent = Integer.parseInt(string.substring(0, string.length() - 1));
                if (percent >= 0 && percent <= 100) {
                    return percent;
                }
            }
            return -1;
        }

    }

}
