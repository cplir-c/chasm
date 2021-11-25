package org.quiltmc.chasm.internal.asm.writer;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.quiltmc.chasm.api.tree.ListNode;
import org.quiltmc.chasm.api.tree.MapNode;
import org.quiltmc.chasm.api.tree.Node;
import org.quiltmc.chasm.api.tree.ValueNode;
import org.quiltmc.chasm.internal.LazyClassNode;
import org.quiltmc.chasm.internal.util.NodeConstants;

@SuppressWarnings("unchecked")
public class ChasmClassWriter {
    private final MapNode classNode;

    public ChasmClassWriter(MapNode classNode) {
        this.classNode = classNode;
    }

    public static Object[] getArguments(ListNode argumentNode) {
        Object[] arguments = new Object[argumentNode.size()];
        for (int i = 0; i < arguments.length; i++) {
            Node argNode = argumentNode.get(i);
            if (argNode instanceof ValueNode<?>) {
                arguments[i] = argNode.getAsObject();
            } else if (argNode.getAsMapNode().containsKey(NodeConstants.TAG)) {
                arguments[i] = getHandle((MapNode) argNode);
            } else {
                MapNode constDynamicNode = (MapNode) argNode;
                String name = constDynamicNode.get(NodeConstants.NAME).getAsString();
                String descriptor = constDynamicNode.get(NodeConstants.DESCRIPTOR).getAsString();
                Handle handle = getHandle((MapNode) constDynamicNode.get(NodeConstants.HANDLE));
                Object[] args = getArguments((ListNode) constDynamicNode.get(NodeConstants.ARGS));
                arguments[i] = new ConstantDynamic(name, descriptor, handle, args);
            }
        }

        return arguments;
    }

    public static Handle getHandle(MapNode handleNode) {
        int tag = handleNode.get(NodeConstants.TAG).getAsInt();
        String owner = handleNode.get(NodeConstants.OWNER).getAsString();
        String name = handleNode.get(NodeConstants.NAME).getAsString();
        String descriptor = handleNode.get(NodeConstants.DESCRIPTOR).getAsString();
        boolean isInterface = handleNode.get(NodeConstants.IS_INTERFACE).getAsBoolean();

        return new Handle(tag, owner, name, descriptor, isInterface);
    }

    public void accept(ClassVisitor visitor) {
        // Unmodified class
        if (classNode instanceof LazyClassNode) {
            ((LazyClassNode) classNode).getClassReader().accept(visitor, 0);
            return;
        }

        // visit
        int version = classNode.get(NodeConstants.VERSION).getAsInt();
        int access = classNode.get(NodeConstants.ACCESS).getAsInt();
        String name = classNode.get(NodeConstants.NAME).getAsString();
        Node signatureNode = classNode.get(NodeConstants.SIGNATURE);
        String signature = signatureNode == null ? null : signatureNode.getAsString();

        Node superClassNode = classNode.get(NodeConstants.SUPER);
        String superClass = superClassNode == null ? "java/lang/Object" : superClassNode.getAsString();

        Node interfacesNode = classNode.get(NodeConstants.INTERFACES);
        String[] interfaces = interfacesNode == null ? new String[0]
                : interfacesNode.getAsListNode().stream().map(n -> ((ValueNode<String>) n).getValue())
                        .toArray(String[]::new);

        visitor.visit(version, access, name, signature, superClass, interfaces);

        // visitSource
        visitSource(visitor);

        // visitModule
        if (classNode.containsKey(NodeConstants.MODULE)) {
            ChasmModuleWriter moduleWriter = new ChasmModuleWriter((MapNode) classNode.get(NodeConstants.MODULE));
            moduleWriter.visitModule(visitor);
        }
        // visitNestHost
        visitNestHost(visitor);

        // visitOuterClass
        visitOuterClass(visitor);

        // visitAnnotation/visitTypeAnnotation
        visitAnnotations(visitor);

        // visitAttribute
        visitAttributes(visitor);

        // visitNestMember
        visitNestMembers(visitor);

        //visitPermittedSubclass
        visitPermittedSubclasses(visitor);

        // visitInnerClass
        visitInnerClasses(visitor);

        // visitRecordComponent
        Node recordComponentListNode = classNode.get(NodeConstants.RECORD_COMPONENTS);
        if (recordComponentListNode != null) {
            for (Node node : recordComponentListNode.getAsListNode()) {
                ChasmRecordComponentWriter chasmRecordComponentWriter = new ChasmRecordComponentWriter((MapNode) node);
                chasmRecordComponentWriter.visitRecordComponent(visitor);
            }
        }

        // visitField
        Node fieldListNode = classNode.get(NodeConstants.FIELDS);
        if (fieldListNode != null) {
            for (Node node : fieldListNode.getAsListNode()) {
                ChasmFieldWriter chasmFieldWriter = new ChasmFieldWriter((MapNode) node);
                chasmFieldWriter.visitField(visitor);
            }
        }

        // visitMethod
        Node methodListNode = classNode.get(NodeConstants.METHODS);
        if (methodListNode != null) {
            for (Node node : methodListNode.getAsListNode()) {
                ChasmMethodWriter chasmMethodWriter = new ChasmMethodWriter((MapNode) node);
                chasmMethodWriter.visitMethod(visitor);
            }
        }

        // visitEnd
        visitor.visitEnd();
    }

    private void visitInnerClasses(ClassVisitor visitor) {
        Node innerClassesListNode = classNode.get(NodeConstants.INNER_CLASSES);
        if (innerClassesListNode == null) {
            return;
        }
        for (Node n : innerClassesListNode.getAsListNode()) {
            MapNode innerClass = (MapNode) n;
            Node nameNode = innerClass.get(NodeConstants.NAME);
            Node outerNameNode = innerClass.get(NodeConstants.OUTER_NAME);
            Node innerNameNode = innerClass.get(NodeConstants.INNER_NAME);
            Node accessNode = innerClass.get(NodeConstants.ACCESS);

            String name = nameNode.getAsString();
            String outerName = outerNameNode == null ? null : outerNameNode.getAsString();
            String innerName = innerNameNode == null ? null : innerNameNode.getAsString();
            int access = accessNode.getAsInt();

            visitor.visitInnerClass(name, outerName, innerName, access);
        }
    }

    private void visitPermittedSubclasses(ClassVisitor visitor) {
        Node permittedSubclassesListNode = classNode.get(NodeConstants.PERMITTED_SUBCLASSES);
        if (permittedSubclassesListNode == null) {
            return;
        }
        for (Node n : permittedSubclassesListNode.getAsListNode()) {
            visitor.visitPermittedSubclass(n.getAsString());
        }
    }

    private void visitNestMembers(ClassVisitor visitor) {
        Node nestMembersListNode = classNode.get(NodeConstants.NEST_MEMBERS);
        if (nestMembersListNode == null) {
            return;
        }
        for (Node n : nestMembersListNode.getAsListNode()) {
            visitor.visitNestMember(n.getAsString());
        }
    }

    private void visitAttributes(ClassVisitor visitor) {
        Node attributesListNode = classNode.get(NodeConstants.ATTRIBUTES);
        if (attributesListNode == null) {
            return;
        }
        for (Node n : attributesListNode.getAsListNode()) {
            visitor.visitAttribute((Attribute) n.getAsObject());
        }
    }

    private void visitAnnotations(ClassVisitor visitor) {
        Node annotationsListNode = classNode.get(NodeConstants.ANNOTATIONS);
        if (annotationsListNode == null) {
            return;
        }
        for (Node n : annotationsListNode.getAsListNode()) {
            ChasmAnnotationWriter writer = new ChasmAnnotationWriter(n);
            writer.visitAnnotation(visitor::visitAnnotation, visitor::visitTypeAnnotation);
        }
    }

    private void visitOuterClass(ClassVisitor visitor) {
        if (classNode.containsKey(NodeConstants.OWNER_CLASS)) {
            String ownerClass = classNode.get(NodeConstants.OWNER_CLASS).getAsString();
            String ownerMethod = classNode.get(NodeConstants.OWNER_METHOD).getAsString();
            String ownerDescriptor = classNode.get(NodeConstants.OWNER_DESCRIPTOR).getAsString();
            visitor.visitOuterClass(ownerClass, ownerMethod, ownerDescriptor);
        }
    }

    private void visitNestHost(ClassVisitor visitor) {
        if (classNode.containsKey(NodeConstants.NEST_HOST)) {
            visitor.visitNestHost(classNode.get(NodeConstants.NEST_HOST).getAsString());
        }
    }

    private void visitSource(ClassVisitor visitor) {
        String source = null;
        if (classNode.containsKey(NodeConstants.SOURCE)) {
            source = classNode.get(NodeConstants.SOURCE).getAsString();
        }

        String debug = null;
        if (classNode.containsKey(NodeConstants.DEBUG)) {
            debug = classNode.get(NodeConstants.DEBUG).getAsString();
        }

        visitor.visitSource(source, debug);
    }
}
