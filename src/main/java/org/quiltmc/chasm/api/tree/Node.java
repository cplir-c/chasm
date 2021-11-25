package org.quiltmc.chasm.api.tree;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.chasm.internal.metadata.MetadataProvider;
import org.quiltmc.chasm.internal.metadata.PathMetadata;

public interface Node {
    /**
     * Creates a deep copy of this {@link Node}.
     * This means that any containing node should also be copied.
     * This also copies the metadata of this Node.
     *
     * @return A recursive copy of this {@link Node}.
     */
    Node copy();

    /**
     * Return the {@link MetadataProvider} of this node.
     *
     * @return The {@link MetadataProvider} of this node.
     */
    @ApiStatus.Internal
    MetadataProvider getMetadata();

    default String getAsString() {
        throw new ClassCastException("Node " + getMetadata().get(PathMetadata.class) + " is not a value node.");
    }

    default int getAsInt() {
        throw new ClassCastException("Node " + getMetadata().get(PathMetadata.class) + " is not a value node.");
    }

    default boolean getAsBoolean() {
        throw new ClassCastException("Node " + getMetadata().get(PathMetadata.class) + " is not a value node.");
    }

    default Object getAsObject() {
        throw new ClassCastException("Node " + getMetadata().get(PathMetadata.class) + " is not a value node.");
    }

    default ValueNode<Object> getAsValueNode() {
        throw new ClassCastException("Node " + getMetadata().get(PathMetadata.class) + " is not a value node.");
    }

    default MapNode getAsMapNode() {
        throw new ClassCastException("Node " + getMetadata().get(PathMetadata.class) + " is not a map node.");
    }

    default ListNode getAsListNode() {
        throw new ClassCastException("Node " + getMetadata().get(PathMetadata.class) + " is not a list node.");
    }

}
