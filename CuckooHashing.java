package pxc190029;

import java.util.*;

public class CuckooHashing<T> {
    private int size = 0;
    private int capacity;
    private double loadFactor;
    private Entry[][] hashTable;
    private int threshold;
    private HashSet<T> secondaryTable; //secondaryTable

    class Entry<E> {
        T element;
        public Entry(T element) {
            this.element = element;
        }
    }

    CuckooHashing(int capacity, double loadFactor){
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        this.hashTable = new Entry[2][capacity];
        this.threshold = (int)  Math.floor(Math.log(2*capacity));
        this.secondaryTable = new HashSet<>();
    }

    public boolean add(T key){
        if(contains(key)) return false;
        boolean reachTothreshold = true;
        double load = (double) size/(2*capacity);
        if(load >= loadFactor){
            reHash(key);
            return true;
        }
            int tableNumber = 1;
            Entry newEntry = new Entry(key);
            for(int i = 0; i < threshold; i++){
                tableNumber = tableNumber == 0? 1 : 0;
                int hashcode = hash(tableNumber+1, key);
                newEntry = new Entry(key);
                if(hashTable[tableNumber][hashcode] == null){
                    hashTable[tableNumber][hashcode] = newEntry;
                    reachTothreshold = false;
                    break;
                }else{
                    T kick = (T)hashTable[tableNumber][hashcode].element;
                    hashTable[tableNumber][hashcode] = newEntry;
                    key = kick;
                }
            }
            if(reachTothreshold == true){secondaryTable.add(key);return true;}
            size++;//!!!!!!!!!
            return true;
    }

    public void reHash(T key){
        Entry[][] oldTable = hashTable;
        this.capacity = 2*capacity;
        this.size = 0;
        hashTable = new Entry[2][capacity];
        this.threshold = (int)  Math.floor(Math.log(2*capacity));
        for(int i = 0; i < oldTable[0].length; i++){
            if(oldTable[0][i] != null){
                add((T)oldTable[0][i].element);
            }
            if(oldTable[1][i] != null){
                add((T)oldTable[1][i].element);
            }
        }
        add(key);
    }
    private int hash(int numOfTable, T x) {
        switch (numOfTable) {
            case 1:
                return hash1(x);
            default:
                return hash2(x);
        }
    }
    int hash1(T key){
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        int h = key.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        h = h ^ (h >>> 7) ^ (h >>> 4);
        return Math.abs(h%capacity);
    }
    int hash2(T key){
        int hashValue = key.hashCode();
        //int key =  (hashValue/capacity)%capacity;
        int res = hash1(key);
        res += (1+hashValue%9);
        res = res%capacity;
        return Math.abs(res);
    }

    public boolean contains(T x){
        int hashcode1 = hash(1,x);
        int hashcode2 = hash(2,x);
        if((hashTable[0][hashcode1] != null && (T)hashTable[0][hashcode1].element == x)
                || (hashTable[1][hashcode2] != null && (T)hashTable[1][hashcode2].element == x) || secondaryTable.contains(x)){
            return true;
        }
        return false;
    }
    public boolean remove(T x){
        int hashcode1 = hash(1,x);
        int hashcode2 = hash(2,x);
        if(!contains(x) && !secondaryTable.contains(x)){
            return false;
        }else{
            if(secondaryTable.remove(x)){
               size++;
            }
            else if((T)hashTable[0][hashcode1].element == x){
                hashTable[0][hashcode1] = null;
            }else{
                hashTable[1][hashcode2] = null;
            }
        }
        size--;
        return true;
    }


    public static void main(String[] args) {


        int[] arr = new int[2000000];
        Random rand = new Random();
        for (int i = 0; i < 2000000; i++) {
            arr[i] = rand.nextInt();
        }

        double[] loadFac = {0.5, 0.75, 0.9};

        for (double loadFactor : loadFac) {


            // Test Cuckoo hashing
            CuckooHashing cc = new CuckooHashing(8, loadFactor);

            Timer timer1 = new Timer();
            for(int num : arr){
                cc.add(num);
            }

            for(int num : arr){
                cc.contains(num);
            }

            for(int num : arr){
                cc.remove(num);
            }

            timer1.end();
            System.out.println("Performance of Cuckoo Hashing with load factor = " + loadFactor + "\n" + timer1 + "\n-----------");

            // Test HashSet
            HashSet<Integer> set = new HashSet<Integer>(8, (float) loadFactor);
            Timer timer2 = new Timer();
            for(int num : arr){
                set.add(num);
            }

            for(int num : arr){
                set.contains(num);
            }

            for(int num : arr){
                set.remove(num);
            }

            timer2.end();
            System.out.println("Performance of HashSet with load factor = " + loadFactor + "\n" + timer2 + "\n-----------");



            // Test HashTable
            Hashtable<Integer, Integer> ht = new Hashtable(8, (float) loadFactor);

            Timer timer3 = new Timer();
            for(int num : arr){
                ht.put(num, 0);
            }

            for(int num : arr){
                ht.containsKey(num);
            }

            for(int num : arr){
                ht.remove(num);
            }

            timer3.end();
            System.out.println("Performance of HashTable with load factor = " + loadFactor + "\n" + timer3 + "\n-----------");

            // Test hashmap
            HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>(8, (float) loadFactor);
            Timer timer4 = new Timer();
            for(int num : arr){
                hashMap.put(num, 0);
            }

            for(int num : arr){
                hashMap.containsKey(num);
            }

            for(int num : arr){
                hashMap.remove(num);
            }

            timer4.end();
            System.out.println("Performance of HashMap with load factor = " + loadFactor + "\n" + timer4 + "\n-----------");

        }






    }
}
