package org.quiltmc.chasm.transformer;

import java.util.ArrayList;
import java.util.List;

import org.quiltmc.chasm.tree.Node;

public interface Target {
    /**
     * True if this target fully contains the other.
     *
     * @param other The other target to check against.
     *
     * @return True if this target fully contains the other.
     */
    boolean contains(Target other);

    /**
     * True if the targets overlap, but neither fully contains the other.
     *
     * @param other The other target to check against.
     *
     * @return True if this target fully contains the other.
     */
    boolean overlaps(Target other);

    Node resolve(Node root);

    default NodePath getPath() {
        class RecordingNode implements Node {
            List<Object> path = new ArrayList<>();

            @Override
            public void initializePath(NodePath nodePath) {
                this.path.clear();
                nodePath.resolve(this);
            }

            @Override
            public NodePath getPath() {
                return NodePath.fromList(path);
            }

            @Override
            public Node toImmutable() {
                return null;
            }

            @Override
            public boolean isImmutable() {
                return false;
            }
        }
        RecordingNode recording = new RecordingNode();
        this.resolve(recording);
        return recording.getPath();
    }
    Target span(Target other);
}
