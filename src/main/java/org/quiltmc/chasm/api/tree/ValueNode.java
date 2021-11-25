package org.quiltmc.chasm.api.tree;

import org.quiltmc.chasm.internal.metadata.MetadataProvider;

public class ValueNode<T> implements Node {
    private MetadataProvider metadataProvider = new MetadataProvider();

    private final T value;

    public ValueNode(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public ValueNode<T> copy() {
        ValueNode<T> copy = new ValueNode<>(value);
        copy.metadataProvider = metadataProvider.copy();
        return copy;
    }

    @Override
    public MetadataProvider getMetadata() {
        return metadataProvider;
    }

    @Override
    public String getAsString() {
        return (String) value;
    }

    @Override
    public int getAsInt() {
        return (Integer) value;
    }

    @Override
    public boolean getAsBoolean() {
        return (Boolean) value;
    }

    @Override
    public Object getAsObject() {
        return this.getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ValueNode<Object> getAsValueNode() {
        return (ValueNode<Object>) this;
    }
}
