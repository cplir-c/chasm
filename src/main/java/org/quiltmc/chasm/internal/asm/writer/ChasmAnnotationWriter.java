package org.quiltmc.chasm.internal.asm.writer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.TypePath;
import org.quiltmc.chasm.api.tree.ListNode;
import org.quiltmc.chasm.api.tree.MapNode;
import org.quiltmc.chasm.api.tree.Node;
import org.quiltmc.chasm.api.tree.ValueNode;
import org.quiltmc.chasm.internal.util.NodeConstants;


public class ChasmAnnotationWriter {
    private final Node annotationNode;

    public ChasmAnnotationWriter(Node annotationNode) {
        this.annotationNode = annotationNode;
    }

    public void visitAnnotation(AnnotationVisitor visitor) {
        Node values;
        if (annotationNode instanceof MapNode) {
            values = annotationNode.getAsMapNode().get(NodeConstants.VALUES);
        } else {
            values = annotationNode;
        }
        if (values == null) {
            visitor.visitEnd();
            return;
        }

        for (Node value : values.getAsListNode()) {
            String name = null;
            if (value instanceof MapNode && ((MapNode) value).containsKey(NodeConstants.NAME)) {
                MapNode mapNode = (MapNode) value;
                // Name-value pairs
                name = mapNode.get(NodeConstants.NAME).getAsString();
                value = mapNode.get(NodeConstants.VALUE);
            }

            if (value instanceof ValueNode) {
                visitor.visit(name, value.getAsObject());
            } else if (value instanceof ListNode) {
                AnnotationVisitor arrayVisitor = visitor.visitArray(name);

                new ChasmAnnotationWriter(value).visitAnnotation(arrayVisitor);
            } else {
                MapNode mapNode = value.getAsMapNode();
                if (mapNode.containsKey(NodeConstants.VALUE)) {
                    String descriptor = mapNode.get(NodeConstants.DESCRIPTOR).getAsString();
                    String enumValue = mapNode.get(NodeConstants.VALUE).getAsString();

                    visitor.visitEnum(name, descriptor, enumValue);
                } else {
                    String descriptor = mapNode.get(NodeConstants.DESCRIPTOR).getAsString();
                    ListNode annotationValues = mapNode.get(NodeConstants.VALUES).getAsListNode();

                    AnnotationVisitor annotationVisitor = visitor.visitAnnotation(name, descriptor);
                    new ChasmAnnotationWriter(annotationValues).visitAnnotation(annotationVisitor);
                }
            }
        }

        visitor.visitEnd();
    }

    public void visitAnnotation(VisitAnnotation visitAnnotation, VisitTypeAnnotation visitTypeAnnotation) {
        String annotationDesc = annotationNode.getAsMapNode().get(NodeConstants.DESCRIPTOR).getAsString();
        boolean visible = annotationNode.getAsMapNode().get(NodeConstants.VISIBLE).getAsBoolean();
        Node typeRef = annotationNode.getAsMapNode().get(NodeConstants.TYPE_REF);
        Node typePath = annotationNode.getAsMapNode().get(NodeConstants.TYPE_PATH);
        AnnotationVisitor annotationVisitor;
        if (typeRef == null) {
            annotationVisitor = visitAnnotation.visitAnnotation(annotationDesc, visible);
        } else {
            annotationVisitor = visitTypeAnnotation.visitTypeAnnotation(typeRef.getAsInt(),
                    TypePath.fromString(typePath.getAsString()), annotationDesc, visible);
        }
        visitAnnotation(annotationVisitor);
    }

    interface VisitAnnotation {
        AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible);
    }

    interface VisitTypeAnnotation {
        AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor,
                                              final boolean visible);
    }
}
