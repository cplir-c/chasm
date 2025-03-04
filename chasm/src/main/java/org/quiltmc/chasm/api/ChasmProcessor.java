package org.quiltmc.chasm.api;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.quiltmc.chasm.api.tree.ArrayListNode;
import org.quiltmc.chasm.api.tree.ListNode;
import org.quiltmc.chasm.api.tree.MapNode;
import org.quiltmc.chasm.api.tree.Node;
import org.quiltmc.chasm.api.util.SuperClassProvider;
import org.quiltmc.chasm.internal.ChasmSuperClassProvider;
import org.quiltmc.chasm.internal.LazyClassNode;
import org.quiltmc.chasm.internal.TransformationApplier;
import org.quiltmc.chasm.internal.TransformationSorter;
import org.quiltmc.chasm.internal.TransformerSorter;
import org.quiltmc.chasm.internal.asm.ChasmClassWriter;
import org.quiltmc.chasm.internal.metadata.PathMetadata;
import org.quiltmc.chasm.internal.tree.reader.ClassNodeReader;
import org.quiltmc.chasm.internal.util.PathInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms the added classes using the added {@link Transformer}s.
 */
public class ChasmProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChasmProcessor.class);

    private final SuperClassProvider superClassProvider;

    private final ListNode classes;
    private final List<Transformer> transformers = new ArrayList<>();

    /**
     * Creates a new {@link ChasmProcessor} that uses the given {@link SuperClassProvider}.
     *
     * @param superClassProvider A {@code SuperClassProvider} to supply parents of classes that are not being
     *            transformed.
     */
    public ChasmProcessor(SuperClassProvider superClassProvider) {
        this.superClassProvider = superClassProvider;
        classes = new ArrayListNode();
    }

    /**
     * Adds the passed {@link Transformer} to this {@link ChasmProcessor}'s
     * list of {@code Transformer}s.
     *
     * @param transformer A {@code Transformer} to add to this {@code ChasmProcessor}'s
     *            list of {@code Transformer}s to transform classes with.
     */
    public void addTransformer(Transformer transformer) {
        transformers.add(transformer);
    }

    /**
     * Adds the passed class {@code byte[]} to this {@link ChasmProcessor}'s
     * list of classes to transform.
     *
     * @param classBytes A transformable class as a {@code byte[]}.
     */
    public void addClass(byte[] classBytes) {
        ClassReader classReader = new ClassReader(classBytes);
        LazyClassNode classNode = new LazyClassNode(classReader);
        classes.add(classNode);
    }

    /**
     * Transforms this {@link ChasmProcessor}'s list of classes according
     * to this {@code ChasmProcessor}'s list of {@link Transformer}s.
     *
     * @return The resulting list of classes as {@code byte[]}s.
     */
    public List<byte[]> process() {
        LOGGER.info("Processing {} classes...", classes.size());

        LOGGER.info("Initializing paths...");
        PathInitializer.initialize(classes, new PathMetadata());

        LOGGER.info("Sorting {} transformers...", transformers.size());
        List<List<Transformer>> rounds = TransformerSorter.sort(transformers);

        LOGGER.info("Applying transformers in {} rounds:", rounds.size());
        for (List<Transformer> round : rounds) {
            LOGGER.info("Applying {} transformers...", round.size());
            List<Transformation> transformations = applyTransformers(round, classes);

            LOGGER.info("Sorting {} transformations...", transformations.size());
            List<Transformation> sorted = TransformationSorter.sort(transformations);

            LOGGER.info("Applying transformations...");
            TransformationApplier transformationApplier = new TransformationApplier(classes, sorted);
            transformationApplier.applyAll();
        }

        LOGGER.info("Writing {} classes...", classes.size());
        List<byte[]> classBytes = new ArrayList<>();
        for (Node node : classes) {
            MapNode classNode = Node.asMap(node);

            ClassNodeReader chasmWriter = new ClassNodeReader(classNode);
            ClassWriter classWriter = new ChasmClassWriter(
                    new ChasmSuperClassProvider(superClassProvider, classes));
            chasmWriter.accept(classWriter);
            classBytes.add(classWriter.toByteArray());
        }

        LOGGER.info("Processing done!");
        return classBytes;
    }

    private List<Transformation> applyTransformers(List<Transformer> transformers, ListNode classes) {
        List<Transformation> transformations = new ArrayList<>();

        for (Transformer transformer : transformers) {
            // TODO: Replace copy with immutability
            transformations.addAll(transformer.apply(classes.copy()));
        }

        return transformations;
    }
}
