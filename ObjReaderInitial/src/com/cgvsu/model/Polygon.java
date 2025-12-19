package com.cgvsu.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Polygon {
    private ArrayList<Integer> vertexIndices;
    private ArrayList<Integer> textureVertexIndices;
    private ArrayList<Integer> normalIndices;

    public Polygon() {
        vertexIndices = new ArrayList<>();
        textureVertexIndices = new ArrayList<>();
        normalIndices = new ArrayList<>();
    }

    // кнструктор для удобного создания треугольников
    public Polygon(int v1, int v2, int v3) {
        this();
        vertexIndices.add(v1);
        vertexIndices.add(v2);
        vertexIndices.add(v3);
    }

    // конструктор для треугольников с текстурами и нормалями
    public Polygon(int v1, int v2, int v3,
                   int t1, int t2, int t3,
                   int n1, int n2, int n3) {
        this(v1, v2, v3);
        textureVertexIndices.add(t1);
        textureVertexIndices.add(t2);
        textureVertexIndices.add(t3);
        normalIndices.add(n1);
        normalIndices.add(n2);
        normalIndices.add(n3);
    }

    public void setVertexIndices(ArrayList<Integer> vertexIndices) {
        if (vertexIndices.size() < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 vertices");
        }
        this.vertexIndices = vertexIndices;
    }

    public void setVertexIndices(Integer... indices) {
        setVertexIndices(new ArrayList<>(Arrays.asList(indices)));
    }

    public void setTextureVertexIndices(ArrayList<Integer> textureVertexIndices) {
        if (!textureVertexIndices.isEmpty() && textureVertexIndices.size() < 3) {
            throw new IllegalArgumentException("Texture indices must be empty or have at least 3 elements");
        }
        this.textureVertexIndices = textureVertexIndices;
    }

    public void setTextureVertexIndices(Integer... indices) {
        setTextureVertexIndices(new ArrayList<>(Arrays.asList(indices)));
    }

    public void setNormalIndices(ArrayList<Integer> normalIndices) {
        if (!normalIndices.isEmpty() && normalIndices.size() < 3) {
            throw new IllegalArgumentException("Normal indices must be empty or have at least 3 elements");
        }
        this.normalIndices = normalIndices;
    }

    public void setNormalIndices(Integer... indices) {
        setNormalIndices(new ArrayList<>(Arrays.asList(indices)));
    }

    public ArrayList<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public ArrayList<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public ArrayList<Integer> getNormalIndices() {
        return normalIndices;
    }

    // полезные методы
    public boolean isTriangle() {
        return vertexIndices.size() == 3;
    }

    public boolean hasTextureCoordinates() {
        return !textureVertexIndices.isEmpty();
    }

    public boolean hasNormals() {
        return !normalIndices.isEmpty();
    }

    @Override
    public String toString() {
        return "Polygon[" + vertexIndices.size() + " vertices]";
    }
}