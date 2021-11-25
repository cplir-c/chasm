package org.quiltmc.chasm.internal.asm.writer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.quiltmc.chasm.api.tree.ListNode;
import org.quiltmc.chasm.api.tree.MapNode;
import org.quiltmc.chasm.api.tree.Node;
import org.quiltmc.chasm.api.tree.ValueNode;
import org.quiltmc.chasm.internal.util.NodeConstants;

@SuppressWarnings("unchecked")
public class ChasmModuleWriter {
    private final MapNode moduleNode;

    public ChasmModuleWriter(MapNode moduleNode) {
        this.moduleNode = moduleNode;
    }

    public void visitModule(ClassVisitor visitor) {
        String name = moduleNode.get(NodeConstants.NAME).getAsString();
        int access = moduleNode.get(NodeConstants.ACCESS).getAsInt();
        Node versionNode = moduleNode.get(NodeConstants.VERSION);
        String version = versionNode == null ? null : versionNode.getAsString();

        ModuleVisitor moduleVisitor = visitor.visitModule(name, access, version);

        // visitMainClass
        visitMainClass(moduleVisitor);

        // visitPackage
        visitPackages(moduleVisitor);

        // visitRequire
        visitRequires(moduleVisitor);

        // visitExport
        visitExports(moduleVisitor);

        // visitOpen
        visitOpens(moduleVisitor);

        // visitUse
        visitUses(moduleVisitor);

        // visitProvide
        visitProvides(moduleVisitor);

        // visitEnd
        moduleVisitor.visitEnd();
    }

    private void visitMainClass(ModuleVisitor moduleVisitor) {
        if (moduleNode.containsKey(NodeConstants.MAIN)) {
            moduleVisitor.visitMainClass(moduleNode.get(NodeConstants.MAIN).getAsString());
        }
    }

    private void visitPackages(ModuleVisitor moduleVisitor) {
        // https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.26
        Node packagesListNode = moduleNode.get(NodeConstants.PACKAGES);
        if (packagesListNode == null) {
            return;
        }
        for (Node n : packagesListNode.getAsListNode()) {
            moduleVisitor.visitPackage(n.getAsString());
        }
    }

    private void visitRequires(ModuleVisitor moduleVisitor) {
        Node moduleRequiresListNode = moduleNode.get(NodeConstants.REQUIRES);
        if (moduleRequiresListNode == null) {
            return;
        }
        for (Node n : moduleRequiresListNode.getAsListNode()) {
            MapNode requireNode = (MapNode) n;
            String reqModule = requireNode.get(NodeConstants.MODULE).getAsString();
            int reqAccess = requireNode.get(NodeConstants.ACCESS).getAsInt();

            Node versionNode = requireNode.get(NodeConstants.VERSION);
            String reqVersion = versionNode == null ? null : versionNode.getAsString();
            moduleVisitor.visitRequire(reqModule, reqAccess, reqVersion);
        }
    }

    private void visitExports(ModuleVisitor moduleVisitor) {
        Node moduleExportsListNode = moduleNode.get(NodeConstants.EXPORTS);
        if (moduleExportsListNode == null) {
            return;
        }
        for (Node n : moduleExportsListNode.getAsListNode()) {
            MapNode exportNode = n.getAsMapNode();
            String expPackage = exportNode.get(NodeConstants.PACKAGE).getAsString();
            int expAcccess = exportNode.get(NodeConstants.ACCESS).getAsInt();
            ListNode reqModules = (ListNode) exportNode.get(NodeConstants.MODULES);
            String[] modules = null;
            if (reqModules != null) {
                modules = new String[reqModules.size()];
                for (int i = 0; i < reqModules.size(); i++) {
                    modules[i] = reqModules.get(i).getAsString();
                }
            }
            moduleVisitor.visitExport(expPackage, expAcccess, modules);
        }
    }

    private void visitOpens(ModuleVisitor moduleVisitor) {
        Node moduleOpensListNode = moduleNode.get(NodeConstants.OPENS);
        if (moduleOpensListNode == null) {
            return;
        }
        for (Node n : moduleOpensListNode.getAsListNode()) {
            MapNode openNode = n.getAsMapNode();
            String openPackage = openNode.get(NodeConstants.PACKAGE).getAsString();
            int openAccess = openNode.get(NodeConstants.ACCESS).getAsInt();
            ListNode openModules = (ListNode) openNode.get(NodeConstants.MODULES);
            String[] modules = null;
            if (openModules != null) {
                modules = new String[openModules.size()];
                for (int i = 0; i < openModules.size(); i++) {
                    modules[i] = openModules.get(i).getAsString();
                }
            }

            moduleVisitor.visitOpen(openPackage, openAccess, modules);
        }
    }

    private void visitUses(ModuleVisitor moduleVisitor) {
        Node moduleUsesListNode = moduleNode.get(NodeConstants.USES);
        if (moduleUsesListNode == null) {
            return;
        }
        for (Node n : moduleUsesListNode.getAsListNode()) {
            moduleVisitor.visitUse(n.getAsString());
        }
    }

    private void visitProvides(ModuleVisitor moduleVisitor) {
        Node moduleProvidesListNode = moduleNode.get(NodeConstants.PROVIDES);
        if (moduleProvidesListNode == null) {
            return;
        }
        for (Node n : moduleProvidesListNode.getAsListNode()) {
            MapNode providesNode = (MapNode) n;
            String service = providesNode.get(NodeConstants.SERVICE).getAsString();
            ListNode providers = (ListNode) providesNode.get(NodeConstants.PROVIDERS);
            String[] prov = new String[providers.size()];
            for (int i = 0; i < providers.size(); i++) {
                prov[i] = providers.get(i).getAsString();
            }
            moduleVisitor.visitProvide(service, prov);
        }
    }
}
