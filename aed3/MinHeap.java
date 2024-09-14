package aed3;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class MinHeap<T extends Comparable<T>> {
    private ArrayList<T> heap;

    public MinHeap() {
        heap = new ArrayList<>();
    }

    // Retorna o índice do pai
    private int parent(int index) {
        return (index - 1) / 2;
    }

    // Retorna o índice do filho esquerdo
    private int leftChild(int index) {
        return 2 * index + 1;
    }

    // Retorna o índice do filho direito
    private int rightChild(int index) {
        return 2 * index + 2;
    }

    // Adiciona um elemento ao heap
    public void insert(T value) {
        heap.add(value);
        heapifyUp(heap.size() - 1);
    }

    // Remove e retorna o menor elemento do heap
    public T extractMin() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }

        T min = heap.get(0);
        T lastElement = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, lastElement);
            heapifyDown(0);
        }

        return min;
    }

    // Corrige a propriedade de heap de baixo para cima
    private void heapifyUp(int index) {
        T temp = heap.get(index);
        while (index > 0 && temp.compareTo(heap.get(parent(index))) < 0) {
            heap.set(index, heap.get(parent(index)));
            index = parent(index);
        }
        heap.set(index, temp);
    }

    // Corrige a propriedade de heap de cima para baixo
    private void heapifyDown(int index) {
        int left = leftChild(index);
        int right = rightChild(index);
        int smallest = index;

        if (left < heap.size() && heap.get(left).compareTo(heap.get(smallest)) < 0) {
            smallest = left;
        }

        if (right < heap.size() && heap.get(right).compareTo(heap.get(smallest)) < 0) {
            smallest = right;
        }

        if (smallest != index) {
            T temp = heap.get(index);
            heap.set(index, heap.get(smallest));
            heap.set(smallest, temp);
            heapifyDown(smallest);
        }
    }

    // Verifica se o heap está vazio
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // Retorna o tamanho do heap
    public int size() {
        return heap.size();
    }

    // Retorna o menor elemento sem remover
    public T peek() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        return heap.get(0);
    }
}
