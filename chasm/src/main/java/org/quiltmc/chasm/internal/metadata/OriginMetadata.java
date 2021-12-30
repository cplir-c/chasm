package org.quiltmc.chasm.internal.metadata;

import org.quiltmc.chasm.api.Transformation;
import org.quiltmc.chasm.api.metadata.CowWrapperMetadataProvider;
import org.quiltmc.chasm.api.metadata.Metadata;
import org.quiltmc.chasm.internal.cow.ImmutableCowWrapper;

public class OriginMetadata implements Metadata<OriginMetadata, OriginMetadata, OriginMetadata>, ImmutableCowWrapper {
    private final String transformerId;

    public OriginMetadata(Transformation origin) {
        this(origin.getParent().getId());
    }

    private OriginMetadata(String transformerId) {
        this.transformerId = transformerId;
    }

    @Override
    public OriginMetadata deepCopy() {
        // Absolutely no reason to copy a purely immutable object
        return this;
    }

    @Override
    public Object shallowCopy() {
        // Absolutely no reason to copy a purely immutable object
        return this;
    }

    @Override
    public OriginMetadata asWrapper(CowWrapperMetadataProvider parent, Class<OriginMetadata> key, boolean owned) {
        return this;
    }
}
