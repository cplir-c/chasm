/**
 *
 */
package org.quiltmc.chasm.api.metadata;

import org.quiltmc.chasm.api.util.CowWrapper;
import org.quiltmc.chasm.internal.cow.Copyable;

/**
 *
 */
public interface MetadataProvider<P extends MetadataProvider<P>> extends Copyable {

    /**
     * Attach {@link Metadata} of a given type.
     *
     * @param dataClass The class of the type to add.
     * @param data The instance of the specified type to attach.
     * @param <T> The type of the metadata.
     */
    <I extends Metadata<I, C, ? extends I>, C extends Metadata<I, C, C> & CowWrapper, T extends Metadata<I, C, T>> void put(
            Class<I> dataClass, T data);

    /**
     * Retrieves {@link Metadata} of a given type.
     *
     * @param dataClass The class of the type to retrieve.
     * @param <T> The type of the metadata.
     * @return The attached metadata of the specified type, or {@code null} if it doesn't exist.
     */
    <I extends Metadata<I, C, ? extends I>, C extends Metadata<I, C, C> & CowWrapper> I get(
            Class<I> dataClass);

    /**
     * Creates a deep copy of this {@link MetadataProvider}.
     *
     * <p>This means that all the contained {@link Metadata} will also be copied.
     *
     * @return A deep copy of this instance.
     */
    @Override
    P deepCopy();

    @Override
    P shallowCopy();

}
