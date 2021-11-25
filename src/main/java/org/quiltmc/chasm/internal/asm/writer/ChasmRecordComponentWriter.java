package org.quiltmc.chasm.internal.asm.writer;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.quiltmc.chasm.api.tree.ListNode;
import org.quiltmc.chasm.api.tree.MapNode;
import org.quiltmc.chasm.api.tree.Node;
import org.quiltmc.chasm.api.tree.ValueNode;
import org.quiltmc.chasm.internal.util.NodeConstants;

@SuppressWarnings("unchecked")
public class ChasmRecordComponentWriter {
    private final MapNode componentNode;

    public ChasmRecordComponentWriter(MapNode componentNode) {
        this.componentNode = componentNode;
    }

    private void visitAttributes(RecordComponentVisitor componentVisitor) {
        Node attributesListNode = componentNode.get(NodeConstants.ATTRIBUTES);
        if (attributesListNode == null) {
            return;
        }
        for (Node n : attributesListNode.getAsListNode()) {
            componentVisitor.visitAttribute((Attribute) n.getAsObject());
        }
    }

    private void visitAnnotations(RecordComponentVisitor componentVisitor) {
        Node annotationsListNode = componentNode.get(NodeConstants.ANNOTATIONS);
        if (annotationsListNode == null) {
            return;
        }
        for (Node n : annotationsListNode.getAsListNode()) {
            ChasmAnnotationWriter writer = new ChasmAnnotationWriter(n);
            writer.visitAnnotation(componentVisitor::visitAnnotation, componentVisitor::visitTypeAnnotation);
        }
    }

    public void visitRecordComponent(ClassVisitor visitor) {
        String name = componentNode.get(NodeConstants.NAME).getAsString();
        String descriptor = componentNode.get(NodeConstants.DESCRIPTOR).getAsString();

        Node signatureNode = componentNode.get(NodeConstants.SIGNATURE);
        String signature = signatureNode == null ? null : signatureNode.getAsString();

        RecordComponentVisitor componentVisitor = visitor.visitRecordComponent(name, descriptor, signature);

        // visitAnnotation/visitTypeAnnotation
        visitAnnotations(componentVisitor);

        // visitAttribute
        visitAttributes(componentVisitor);

        // visitEnd
        componentVisitor.visitEnd();
    }
}
