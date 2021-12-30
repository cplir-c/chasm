/**
 *
 */
package org.quiltmc.chasm.api.metadata;

import org.quiltmc.chasm.internal.cow.AbstractChildCowWrapper;
import org.quiltmc.chasm.internal.cow.UpdatableCowWrapper;

/**
 * @param <I> Interface type
 * @param <C> CowWrapper type
 * @param <T> Type (the real type of the implementing class)
 *
 */
public abstract class CowWrapperMetadata<I extends Metadata<I, C, ? extends I>, C extends CowWrapperMetadata<I, C, T> & Metadata<I, C, C>, T extends Metadata<I, C, T>>
        extends AbstractChildCowWrapper<T, C, CowWrapperMetadataProvider>
        implements Metadata<I, C, C> {

    /**
     * @param object
     * @param owned
     */
    protected CowWrapperMetadata(CowWrapperMetadataProvider parent, Class<I> key, T object,
            boolean owned) {
        super(parent, key, object, owned);
    }

    /**
     * @param other
     */
    protected CowWrapperMetadata(CowWrapperMetadata<I, C, T> other) {
        super(other);
    }

    @Override
    protected abstract C castThis();

    @Override
    public abstract C deepCopy();

    @Override
    protected void updateThisWrapper(Object key, UpdatableCowWrapper child, Object contents) {
        throw new UnsupportedOperationException("Metadata has no children.");
    }

    @Override
    public abstract C asWrapper(CowWrapperMetadataProvider parent, Class<I> key, boolean owned);

}
