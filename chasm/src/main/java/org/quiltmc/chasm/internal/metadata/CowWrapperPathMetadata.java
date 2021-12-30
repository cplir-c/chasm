/**
 *
 */
package org.quiltmc.chasm.internal.metadata;

import java.util.Collection;
import java.util.Iterator;
import org.quiltmc.chasm.api.metadata.CowWrapperMetadataProvider;
import org.quiltmc.chasm.api.metadata.CowWrapperMetadata;
import org.quiltmc.chasm.api.metadata.Metadata;
import org.quiltmc.chasm.api.tree.Node;
import org.quiltmc.chasm.internal.collection.MixinRandomAccessListImpl;
import org.quiltmc.chasm.internal.collection.ReadOnlyListWrapperIterator;
import org.quiltmc.chasm.internal.cow.UpdatableCowWrapper;
import org.quiltmc.chasm.internal.metadata.ListPathMetadata.Entry;

/**
 *
 */
public class CowWrapperPathMetadata
        extends CowWrapperMetadata<PathMetadata<?>, CowWrapperPathMetadata, ListPathMetadata>
        implements PathMetadata<CowWrapperPathMetadata>, MixinRandomAccessListImpl<Entry> {

    /**
     * @param parent
     * @param object
     * @param owned
     */
    @SuppressWarnings("unchecked")
    public CowWrapperPathMetadata(CowWrapperMetadataProvider parent, ListPathMetadata object, boolean owned) {
        super(parent, PathMetadata.PATH_METADATA_CLASS, object, owned);
    }

    /**
     * @param other
     */
    public CowWrapperPathMetadata(CowWrapperPathMetadata other) {
        super(other);
    }

    @Override
    public int size() {
        return this.object.size();
    }

    @Override
    public boolean isEmpty() { return this.object.isEmpty(); }

    @Override
    public boolean contains(Object o) {
        return this.object.contains(o);
    }

    @Override
    public Iterator<Entry> iterator() {
        return new ReadOnlyListWrapperIterator<>(this.object);
    }

    @Override
    public Object[] toArray() {
        return this.object.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.object.toArray(a);
    }

    @Override
    public boolean add(Entry e) {
        this.toOwned();
        return this.object.add(e);
    }

    @Override
    public boolean remove(Object o) {
        this.toOwned();
        return this.object.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.object.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Entry> c) {
        this.toOwned();
        return this.object.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Entry> c) {
        this.toOwned();
        return this.object.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.toOwned();
        return this.object.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        this.toOwned();
        return this.object.retainAll(c);
    }

    @Override
    public void clear() {
        this.toOwned();
        this.object.clear();
    }

    @Override
    public Entry get(int index) {
        return this.object.get(index);
    }

    @Override
    public Entry set(int index, Entry element) {
        this.toOwned();
        return this.object.set(index, element);
    }

    @Override
    public void add(int index, Entry element) {
        this.toOwned();
        this.object.add(index, element);
    }

    @Override
    public Entry remove(int index) {
        this.toOwned();
        return this.object.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.object.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.object.lastIndexOf(o);
    }

    @Override
    public CowWrapperPathMetadata deepCopy() {
        return new CowWrapperPathMetadata(this);
    }

    @Override
    public CowWrapperPathMetadata shallowCopy() {
        return new CowWrapperPathMetadata(this);
    }

    @Override
    public CowWrapperPathMetadata append(String name) {
        CowWrapperPathMetadata copy = new CowWrapperPathMetadata(this);
        copy.add(new Entry(name));
        return copy;
    }

    @Override
    public CowWrapperPathMetadata append(int index) {
        CowWrapperPathMetadata copy = new CowWrapperPathMetadata(this);
        copy.add(new Entry(index));
        return copy;
    }

    @Override
    public CowWrapperPathMetadata parent() {
        CowWrapperPathMetadata copy = new CowWrapperPathMetadata(this);
        copy.remove(copy.size());
        return copy;
    }

    @Override
    public boolean startsWith(PathMetadata other) {
        if (other.size() > this.size()) {
            return false;
        }
        for (int i = 0; i < other.size(); ++i) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Node resolve(Node root) {
        return this.object.resolve(root);
    }

    @Override
    protected void updateThisWrapper(Object objKey, UpdatableCowWrapper child, Object objContents) {
        int key = (Integer) objKey;
        Entry current = this.get(key);
        Entry contents = (Entry) objContents;
        if (contents != current || child.isOwned() != this.isOwned()) {
            this.set(key, contents);
            this.toOwned(child.isOwned());
        }
    }

    @Override
    public CowWrapperPathMetadata asWrapper(CowWrapperMetadataProvider parent, Class<PathMetadata<?>> key,
            boolean owned) {
        if (key != PathMetadata.PATH_METADATA_CLASS) {
            throw new IllegalArgumentException("Illegal agument" + key);
        }
        return new CowWrapperPathMetadata(parent, this.object, owned);
    }

}
