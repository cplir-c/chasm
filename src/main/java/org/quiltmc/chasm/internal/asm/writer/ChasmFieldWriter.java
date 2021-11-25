package org.quiltmc.chasm.internal.asm.writer;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.quiltmc.chasm.api.tree.ListNode;
import org.quiltmc.chasm.api.tree.MapNode;
import org.quiltmc.chasm.api.tree.Node;
import org.quiltmc.chasm.api.tree.ValueNode;
import org.quiltmc.chasm.internal.util.NodeConstants;

@SuppressWarnings("unchecked")
public class ChasmFieldWriter {
    private final MapNode fieldNode;

    public ChasmFieldWriter(MapNode fieldNode) {
        this.fieldNode = fieldNode;
    }

    private void visitAttributes(FieldVisitor fieldVisitor) {
        ListNode attributesListNode = (ListNode) fieldNode.get(NodeConstants.ATTRIBUTES);
        if (attributesListNode == null) {
            return;
        }
        for (Node n : attributesListNode) {
            fieldVisitor.visitAttribute(((ValueNode<Attribute>) n).getValue());
        }
    }

    private void visitAnnotations(FieldVisitor fieldVisitor) {
        ListNode annotationsListNode = (ListNode) fieldNode.get(NodeConstants.ANNOTATIONS);
        if (annotationsListNode == null) {
            return;
        }
        for (Node n : annotationsListNode) {
            ChasmAnnotationWriter annotationWriter = new ChasmAnnotationWriter((MapNode) n);
            annotationWriter.visitAnnotation(fieldVisitor::visitAnnotation, fieldVisitor::visitTypeAnnotation);
        }
    }

    public void visitField(ClassVisitor visitor) {
        int access = fieldNode.get(NodeConstants.ACCESS).getAsInt();
        String name = fieldNode.get(NodeConstants.NAME).getAsString();
        String descriptor = fieldNode.get(NodeConstants.DESCRIPTOR).getAsString();

        Node signatureNode = fieldNode.get(NodeConstants.SIGNATURE);
        String signature = signatureNode == null ? null : signatureNode.getAsString();

        Node valueNode = fieldNode.get(NodeConstants.VALUE);
        Object value = valueNode == null ? null : valueNode.getAsObject();

        FieldVisitor fieldVisitor = visitor.visitField(access, name, descriptor, signature, value);

        // visitAnnotation/visitTypeAnnotation
        visitAnnotations(fieldVisitor);

        // visitAttribute
        visitAttributes(fieldVisitor);

        // visitEnd
        fieldVisitor.visitEnd();
    }
}
