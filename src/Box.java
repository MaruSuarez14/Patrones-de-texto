public class Box<T> {
    private Object[] objectArray;
    private int nobj = 0;
    private int capacity = 0;

    public Box() {
        objectArray = new Object[2];
        capacity = 2;
    }

    public int size() {
        return this.nobj;
    }

    public Box(int capacity) {
        objectArray = new Object[capacity];
        this.capacity = capacity;
    }

    void addElement(T o) {
        if (this.nobj == this.capacity) {
            int newCapacity = this.capacity*2;
            Object[] newArray = new Object[newCapacity];
            for(int i=0; i<this.capacity; i++) {
                newArray[i] = this.objectArray[i];
            }
            this.objectArray = newArray;
            this.capacity = newCapacity;
        }
        this.objectArray[this.nobj] = o;
        this.nobj++;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < this.nobj; i++) {
            result += this.objectArray[i].toString() + ", ";
        }
        return result;
    }

    public void remove(int pos) {
        if (pos < this.nobj) {
            for (int i = pos; i < nobj-1; i++) {
                objectArray[i] = objectArray[i+1];
            }
            objectArray[this.nobj-1] = null;
            this.nobj--;
        }
    }


    public T get(int pos) {
        if (pos < this.nobj) {
            return (T) objectArray[pos];
        }
        throw new RuntimeException("Posició inexistent");
    }
}
