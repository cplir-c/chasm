/**
 *
 */
package org.quiltmc.chasm.api.metadata;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import org.quiltmc.chasm.api.tree.CowWrapperNode;
import org.quiltmc.chasm.api.util.CowWrapper;
import org.quiltmc.chasm.internal.cow.AbstractChildCowWrapper;
import org.quiltmc.chasm.internal.cow.UpdatableCowWrapper;
import org.quiltmc.chasm.internal.tree.UpdatableCowWrapperNode;

/**
 *
 */
public class CowWrapperMetadataProvider extends
        AbstractChildCowWrapper<MetadataProvider<MapMetadataProvider>, CowWrapperMetadataProvider, UpdatableCowWrapperNode>
        implements MetadataProvider<CowWrapperMetadataProvider> {
    private Map<Class<? extends Metadata<?, ?, ?>>, Metadata<?, ?, ?>> metadataCache;

    /**
     * @param metadata
     * @param owned
     */
    public CowWrapperMetadataProvider(UpdatableCowWrapperNode parent, MapMetadataProvider metadata, boolean owned) {
        super(parent, CowWrapperNode.SentinelKeys.METADATA, metadata, owned);
        metadataCache = null;
    }

    /**
     * @param cowWrapperMetadataProvider
     */
    protected CowWrapperMetadataProvider(CowWrapperMetadataProvider cowWrapperMetadataProvider) {
        super(cowWrapperMetadataProvider);
        metadataCache = null;
    }

    @SuppressWarnings("unchecked")
    private <I extends Metadata<I, C, ? extends I>, C extends Metadata<I, C, C> & CowWrapper> C getCachedWrapper(
            Class<I> dataClass) {
        if (this.metadataCache == null) {
            return null;
        }
        return (C) this.metadataCache.get(dataClass);
    }

    private <I extends Metadata<I, C, ? extends I>, C extends Metadata<I, C, C> & CowWrapper> I removeCachedWrapper(
            Class<I> dataClass) {
        if (this.metadataCache == null) {
            return null;
        }
        I cached = dataClass.cast(this.metadataCache.remove(dataClass));
        if (cached instanceof CowWrapperMetadata<?, ?, ?>) {
            CowWrapperMetadata<?, ?, ?> wrapper = (CowWrapperMetadata<?, ?, ?>) cached;
            wrapper.unlinkParentWrapper();
        }
        return cached;
    }

    @Override
    public <I extends Metadata<I, C, ? extends I>, C extends Metadata<I, C, C> & CowWrapper, T extends Metadata<I, C, T>> void put(
            Class<I> dataClass, T data) {
        this.toOwned();
        this.removeCachedWrapper(dataClass);
        this.object.put(dataClass, data);
    }

    @Override
    public <I extends Metadata<I, C, ? extends I>, C extends Metadata<I, C, C> & CowWrapper> I get(Class<I> dataClass) {
        C wrapper = this.getCachedWrapper(dataClass);
        if (wrapper == null) {
            I metadata = this.object.get(dataClass);
            wrapper = metadata.asWrapper(this, dataClass, isOwned());
            this.metadataCache.put(dataClass, wrapper);
        }
        return dataClass.cast(wrapper);
    }

    @Override
    protected CowWrapperMetadataProvider castThis() {
        return this;
    }

    @Override
    public CowWrapperMetadataProvider deepCopy() {
        CowWrapperMetadataProvider copy = new CowWrapperMetadataProvider(this);
        copy.toShared();
        copy.toOwned(this.isOwned());
        return copy;
    }

    @Override
    public CowWrapperMetadataProvider shallowCopy() {
        return new CowWrapperMetadataProvider(this);
    }

    private <I extends Metadata<I, C, ? extends I>, C extends Metadata<I, C, C> & CowWrapper, T extends Metadata<I, C, T>> void putWrapperUpdate(
            Class<I> key, C cowWrapper, T contents) {
        this.object.put(key, contents);
        if (this.metadataCache == null) {
            this.metadataCache = new HashMap<>();
        }
        this.metadataCache.put(key, cowWrapper);
    }

    @SuppressWarnings("unchecked")
    private <I extends Metadata<I, C, ? extends I>, C extends Metadata<I, C, C> & CowWrapper, T extends Metadata<I, C, T>> void putWrapperUpdateUnsafe(
            Class<? extends Metadata<?, ?, ?>> key, CowWrapperMetadata<?, ?, ?> cowWrapper,
            Metadata<?, ?, ?> contents) {
        this.putWrapperUpdate((Class<I>) key, (C) cowWrapper, (T) contents);
    }

    @Override
    protected void updateThisWrapper(Object objKey, UpdatableCowWrapper cow, Object contents) {
        if (!(cow instanceof Metadata)) {
            throw new ClassCastException("Invalid metadata provider child: " + cow);
        }
        Class<?> classKey = (Class<?>) objKey;
        @SuppressWarnings("rawtypes")
        Class<? extends Metadata> rawKey = classKey.asSubclass(Metadata.class);
        @SuppressWarnings("unchecked")
        Class<? extends Metadata<?, ?, ?>> key = (Class<? extends Metadata<?, ?, ?>>) rawKey;
        Metadata<?, ?, ?> child = this.object.get(key);
        CowWrapperMetadata<?, ?, ?> cowWrapper = (CowWrapperMetadata<?, ?, ?>) cow;
        if (child != contents || this.isOwned() != cow.isOwned()) {
            if (child != contents) {

                this.putWrapperUpdateUnsafe(key, cowWrapper, (Metadata<?, ?, ?>) contents);
            }
            this.toOwned(cow.isOwned());
        }
    }

}
