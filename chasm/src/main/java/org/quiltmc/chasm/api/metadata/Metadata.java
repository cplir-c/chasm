package org.quiltmc.chasm.api.metadata;

import org.quiltmc.chasm.api.util.CowWrapper;
import org.quiltmc.chasm.internal.cow.Copyable;

/**
 * {@link org.quiltmc.chasm.api.tree.Node} metadata, capable of being attached to a {@link MapMetadataProvider}.
 */
public interface Metadata<I extends Metadata<I, C, ? extends I>, C extends Metadata<I, C, C> & CowWrapper, T extends Metadata<I, C, T>>
        extends Copyable {
    /**
     * Creates a deep copy of this {@link Metadata}.
     *
     * @return A deep copy of this instance.
     */
    @Override
    T deepCopy();

    /**
     * @param <T>
     * @param parent
     * @param key
     * @param owned
     *
     * @return
     */
    C asWrapper(CowWrapperMetadataProvider parent, Class<I> key, boolean owned);
}
