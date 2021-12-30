/**
 *
 */
package org.quiltmc.chasm.internal.metadata;

import java.util.List;

import org.quiltmc.chasm.api.metadata.CowWrapperMetadataProvider;
import org.quiltmc.chasm.api.metadata.Metadata;
import org.quiltmc.chasm.api.tree.Node;

/**
 *
 */
public interface PathMetadata<T extends PathMetadata<T>> extends
        Metadata<PathMetadata<?>, CowWrapperPathMetadata, T>, List<ListPathMetadata.Entry> {
    @SuppressWarnings("unchecked")
    Class<PathMetadata<?>> PATH_METADATA_CLASS = (Class<PathMetadata<?>>) (Class<?>) PathMetadata.class;

    @Override
    T deepCopy();

    T append(String name);

    T append(int index);

    T parent();

    <P extends PathMetadata<P>> boolean startsWith(P other);

    Node resolve(Node root);

    @Override
    CowWrapperPathMetadata asWrapper(CowWrapperMetadataProvider parent, Class<PathMetadata<?>> key, boolean owned);

}
